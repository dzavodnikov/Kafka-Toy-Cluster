/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 Dmitry Zavodnikov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pro.zavodnikov.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;

import com.beust.jcommander.Parameter;

/**
 * Base class of Kafka Consumer.
 */
public abstract class AbstractConsumer<K, V> extends AbstractClient {

    @Parameter(names = "--topic", required = true, description = "Name of target topic.")
    protected String topic;

    @Parameter(names = "--partition", description = "Topic partition. Default is 0.")
    protected Integer partition;

    @Parameter(names = "--offset", description = "Partition offset.")
    protected Long offset;

    protected AbstractConsumer(final Class<? extends Deserializer<K>> keyDeserializer,
            final Class<? extends Deserializer<V>> valueDeserializer) {
        super();

        this.props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Can be "earliest" or "latest".
        this.props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer.getName());
        this.props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getName());
    }

    @Override
    protected void customParametersProcess() {
        super.customParametersProcess();

        this.props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, getClass().getSimpleName());
    }

    protected void beforeStart() {
        // Do nothing.
    }

    protected void afterStop() {
        // Do nothing.
    }

    private void printInfoMessage(final String action) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s consumer of topic '%s'", action, this.topic));
        if (this.partition != null) {
            sb.append(String.format(" on partition %d", this.partition));

            if (this.offset != null) {
                sb.append(String.format(" with offset %d", this.offset));
            }
        } else {
            if (this.offset != null) {
                System.out.println("Partition number was not provided, so the offset will be ignored");
            }
        }

        System.out.println(sb.toString());
    }

    /**
     * @param key   that was received;
     * @param value that was received;
     * @return <code>true</code> if we should continue consuming messages.
     */
    protected abstract boolean processMessage(K key, V value);

    /**
     * Start accepting messages.
     */
    public void run() {
        beforeStart();
        printInfoMessage("Start consumer");

        try (KafkaConsumer<K, V> consumer = new KafkaConsumer<>(this.props)) {
            if (this.partition != null) { // Assign specific partition and, maybe, offset.
                final TopicPartition partition = new TopicPartition(topic, this.partition);
                consumer.assign(Collections.singletonList(partition));

                if (this.offset != null) {
                    consumer.seek(partition, offset);
                }
            } else { // Get dynamically assigned partitions.
                consumer.subscribe(Arrays.asList(this.topic));
            }

            stop: while (true) {
                for (ConsumerRecord<K, V> record : consumer.poll(Duration.ofMillis(100))) {
                    System.out.println(String.format("Got message from '%s' Topic (offset %d)",
                            record.topic(), record.offset()));

                    if (!processMessage(record.key(), record.value())) {
                        break stop;
                    }
                }
            }

            System.out.println("All required messages was received");
        }

        printInfoMessage("Stop consumer");
        afterStop();
    }
}
