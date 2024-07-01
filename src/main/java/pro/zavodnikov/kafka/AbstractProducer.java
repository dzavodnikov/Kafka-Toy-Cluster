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

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Base class of Kafka Producer.
 */
public abstract class AbstractProducer<K, V> extends AbstractClient {

    protected AbstractProducer(final Class<? extends Serializer<K>> keySerializer,
            final Class<? extends Serializer<V>> valueSerializer) {
        super();

        this.props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getName());
        this.props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getName());
    }

    protected void beforeStart() {
        // Do nothing.
    }

    protected void afterStop() {
        // Do nothing.
    }

    /**
     * @return messages that needs to be send. If <code>null</code> received stop
     *         the loop.
     */
    protected abstract KafkaMessage<K, V> getMessage();

    /**
     * Start sending messages.
     */
    public void run() {
        beforeStart();
        System.out.println("Start producer");

        try (KafkaProducer<K, V> producer = new KafkaProducer<>(this.props)) {
            while (true) {
                final KafkaMessage<K, V> message = getMessage();
                if (message == null) {
                    break;
                }

                final ProducerRecord<K, V> record = new ProducerRecord<>(message.getTopicName(), message.getPartition(),
                        message.getKey(), message.getValue());

                producer.send(record);
                producer.flush();

                System.out.println(String.format("Send message to '%s'", message.getTopicName()));
            }

            System.out.println("All required messages was sent");
        }

        System.out.println("Stop producer");
        afterStop();
    }
}
