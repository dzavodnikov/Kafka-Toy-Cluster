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

/**
 * Make creation Kafka messages using {@link KafkaMessage} interface easily.
 */
public abstract class AbstractKafkaMessage<K, V> implements KafkaMessage<K, V> {

    private final String topicName;
    private final Integer partition;

    private final K key;
    private final V value;

    protected AbstractKafkaMessage(final String topicName, final Integer partition, final K key, final V value) {
        this.topicName = topicName;
        this.partition = partition;

        this.key = key;
        this.value = value;
    }

    protected AbstractKafkaMessage(final String topicName, final K key, final V value) {
        this(topicName, null, key, value);
    }

    @Override
    public String getTopicName() {
        return this.topicName;
    }

    @Override
    public Integer getPartition() {
        return this.partition;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }
}
