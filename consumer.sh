#!/usr/bin/env bash

source env.sh

run pro.zavodnikov.kafka.TextMessageConsumer \
    --brokers localhost:9092 \
    --topic Text1 \
    --max-messages 10
