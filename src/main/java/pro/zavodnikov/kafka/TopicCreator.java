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

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;

import com.beust.jcommander.Parameter;

/**
 * Create Kafka Topic if it is not exists.
 */
public class TopicCreator extends AbstractClient {

    @Parameter(names = "--topic", required = true, description = "Name of target topic.")
    private String topic;

    @Parameter(names = "--partitions", description = "Topic partitions number. Default is 1.")
    private Integer partitions = 1;

    @Parameter(names = "--replication", description = "Topic replication value. Default is 1.")
    private Short replication = 1;

    public static final int TEST_TOPIC_PARTITIONS = 1;
    public static final short TEST_TOPIC_REPLICATION = 1;

    public void create() throws InterruptedException, ExecutionException {
        try (Admin admin = Admin.create(this.props)) {
            if (admin.listTopics().names().get().contains(this.topic)) {
                System.out.println(String.format("Topic '%s' exists", this.topic));
                return;
            }

            admin.createTopics(Arrays.asList(new NewTopic(this.topic, this.partitions, this.replication)));

            System.out.println(String.format("Topic '%s' created", this.topic));
        }
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        final TopicCreator creator = new TopicCreator();
        creator.init(args);
        creator.create();
    }
}
