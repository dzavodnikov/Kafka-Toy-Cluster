#!/usr/bin/env bash

java -cp target/kafka-examples-1.0.0-SNAPSHOT-jar-with-dependencies.jar: \
    pro.zavodnikov.kafka.TopicCreator \
        --brokers localhost:9092 \
        --topic Text1
