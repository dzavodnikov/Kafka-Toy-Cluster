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

import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.StringSerializer;

import com.beust.jcommander.Parameter;

/**
 * Example of Kafka Producer.
 */
public class TextMessageProducer extends AbstractProducer<String, String> {

    @Parameter(names = "--topic", required = true, description = "Name of target topic.")
    protected String topic;

    @Parameter(names = "--max-messages", description = "Max number of messages. Default is 10 messages.")
    private Integer maxMessages = 10;

    @Parameter(names = "--delay-sec", description = "Max number of messages. Default is 5 seconds.")
    private Integer delaySeconds = 5;

    private int messageCounter = 0;

    public TextMessageProducer() {
        super(StringSerializer.class, StringSerializer.class);
    }

    @Override
    protected KafkaMessage<String, String> getMessage() {
        if (this.maxMessages >= 0 && this.messageCounter >= this.maxMessages) {
            return null;
        }

        if (this.delaySeconds >= 0) {
            try {
                TimeUnit.SECONDS.sleep(this.delaySeconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        final String key = String.format("TestKey_%d", this.messageCounter);
        final String value = String.format("TestValue_%d", this.messageCounter);

        ++this.messageCounter;

        return new TestStringMessage(this.topic, key, value);
    }

    public static void main(final String[] args) {
        final TextMessageProducer producer = new TextMessageProducer();
        producer.init(args);
        producer.run();
    }

    private class TestStringMessage extends AbstractKafkaMessage<String, String> {

        protected TestStringMessage(final String topicName, final String key, final String value) {
            super(topicName, key, value);
        }
    }
}
