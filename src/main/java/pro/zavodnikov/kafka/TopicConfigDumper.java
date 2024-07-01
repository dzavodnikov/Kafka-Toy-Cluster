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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

import com.beust.jcommander.Parameter;

/**
 * Create dump of existing topics with extra details.
 */
public class TopicConfigDumper extends AbstractClient {

    @Parameter(names = { "--out-file", }, required = true, description = "Output filename.")
    protected String outFile;

    private long totalMessages(final Admin admin, final TopicDescription topicDesc)
            throws ExecutionException, InterruptedException {
        final Map<TopicPartition, OffsetSpec> partitionOffsetsBegin = new HashMap<>();
        final Map<TopicPartition, OffsetSpec> partitionOffsetsEnd = new HashMap<>();

        for (TopicPartitionInfo partition : topicDesc.partitions()) {
            partitionOffsetsBegin.put(new TopicPartition(topicDesc.name(), partition.partition()),
                    OffsetSpec.earliest());
            partitionOffsetsEnd.put(new TopicPartition(topicDesc.name(), partition.partition()),
                    OffsetSpec.latest());
        }

        final ListOffsetsResult resultBegin = admin.listOffsets(partitionOffsetsBegin);
        final ListOffsetsResult resultEnd = admin.listOffsets(partitionOffsetsEnd);

        long totalMessages = 0;
        for (TopicPartitionInfo partition : topicDesc.partitions()) {
            final TopicPartition topicPartition = new TopicPartition(topicDesc.name(), partition.partition());

            final long begin = resultBegin.partitionResult(topicPartition).get().offset();
            final long end = resultEnd.partitionResult(topicPartition).get().offset();

            totalMessages += Math.abs(end - begin);
        }
        return totalMessages;
    }

    private void writeLine(final FileWriter writer, final String topic, long totalMessages) throws IOException {
        writer.write(topic);
        writer.write("\n");

        writer.write("    messages: " + totalMessages);
        writer.write("\n");

        writer.write("\n");
    }

    public void dumpConfig() throws InterruptedException, ExecutionException, IOException {
        try (Admin admin = AdminClient.create(this.props)) {
            final List<String> topicNames = new ArrayList<>(admin.listTopics().names().get());
            Collections.sort(topicNames);

            final Map<String, TopicDescription> topicToDesc = admin.describeTopics(topicNames).allTopicNames().get();
            try (FileWriter writer = new FileWriter(this.outFile)) {
                for (String topic : topicNames) {
                    final long totalMessages = totalMessages(admin, topicToDesc.get(topic));
                    writeLine(writer, topic, totalMessages);
                }
            }

        }
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException, IOException {
        final TopicConfigDumper dumper = new TopicConfigDumper();
        dumper.init(args);
        dumper.dumpConfig();
    }
}
