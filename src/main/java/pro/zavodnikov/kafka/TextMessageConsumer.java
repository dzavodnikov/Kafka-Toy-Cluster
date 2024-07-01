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

import org.apache.kafka.common.serialization.StringDeserializer;

import com.beust.jcommander.Parameter;

/**
 * Example of Kafka Consumer.
 */
public class TextMessageConsumer extends AbstractConsumer<String, String> {

    @Parameter(names = "--max-messages", description = "Max number of messages. Use -1 (default) for unlimited.")
    private Integer maxMessages = -1;

    private int countMessage = 0;

    public TextMessageConsumer() {
        super(StringDeserializer.class, StringDeserializer.class);
    }

    @Override
    protected boolean processMessage(final String key, final String value) {
        ++this.countMessage;

        System.out.println(String.format("%s: %s", key, value));

        if (this.maxMessages >= 0 && this.countMessage >= this.maxMessages) {
            return false;
        }
        return true;
    }

    public static void main(final String[] args) {
        final TextMessageConsumer consumer = new TextMessageConsumer();
        consumer.init(args);
        consumer.run();
    }
}
