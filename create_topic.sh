#!/usr/bin/env bash

source env.sh

run pro.zavodnikov.kafka.TopicCreator \
    --brokers localhost:9092 \
    --topic Text1
