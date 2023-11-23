#!/usr/bin/env python

import argparse
from pathlib import Path
from time import sleep
from typing import Callable

import yaml
from kafka import KafkaConsumer
from kafka.admin import KafkaAdminClient, NewTopic
from kafka.errors import NoBrokersAvailable

# Initialize virtual environment:
#   $ python -m venv .venv
#   $ source .venv/bin/activate
#   (.venv) pip install --upgrade pip
#   (.venv) pip install pyyaml
#   (.venv) pip install kafka-python


REQUEST_TIMEOUT_SEC = 30


def repeat_if(*exceptions: OSError, max_request_repeat: int = 30, repeat_sleep_sec: int = 10, print_info: bool = True):
    def repeat_if_wrapper(func: Callable):
        def wrapper(*args, **kwargs):
            args_str = ",".join([f"{str(val)}" for val in args])
            kwargs_str = ",".join([f"{key}={val}" for key, val in kwargs.items()])
            fn_str = f'#{func.__name__}({",".join([val for val in [args_str, kwargs_str] if val])})'

            happens = set()
            for rep in range(max_request_repeat):
                try:
                    return func(*args, **kwargs)
                except tuple(exceptions) as e:
                    step = rep + 1

                    happens.add(str(e))

                    if print_info:
                        repeat = f"{step}/{max_request_repeat}"
                        print(f"Waiting {repeat_sleep_sec} sec and repeat {repeat} for {fn_str}: {str(e)}")

                    sleep(repeat_sleep_sec)
            raise Exception(f'Fail with {fn_str}: {",".join(happens)}')

        return wrapper

    return repeat_if_wrapper


QUORUM_KEY = "quorum"
TOPICS_KEY = "topics"


# See:
#   https://kafka-python.readthedocs.io/en/master/usage.html
@repeat_if(NoBrokersAvailable)
def add_kafka_topics(servers: list[str], new_topics: list[str]) -> None:
    create_topics = []

    consumer = KafkaConsumer(bootstrap_servers=servers)
    existing_topics = consumer.topics()
    for name in new_topics:
        if name not in existing_topics:
            create_topics.append(NewTopic(name=name, num_partitions=1, replication_factor=1))
            print(f"Create topic '{name}'")

    if create_topics:
        admin = KafkaAdminClient(bootstrap_servers=servers)
        admin.create_topics(new_topics=create_topics, validate_only=False)

    print("Kafka have all required Topics")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(prog="CUP-Init", description="CUP cluster initialization script")
    parser.add_argument("-c", "--config", nargs="?", type=Path, help="Config file", default="init.yml")
    args = parser.parse_args()

    with open(args.config, "r", encoding="UTF-8") as file:
        config = yaml.safe_load(file)

        try:
            kafka_quorum = [server.strip() for server in config.get(QUORUM_KEY).split(",")]
            add_kafka_topics(kafka_quorum, config.get(TOPICS_KEY))
        except Exception as e:
            print(f"Can't initialize cluster! Exception '{type(e)}': {str(e)}")
