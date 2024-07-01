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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

import com.beust.jcommander.Parameter;

import pro.zavodnikov.AbstractParams;

/**
 * Get access to Kafka.
 */
public abstract class AbstractClient extends AbstractParams {

    @Parameter(names = { "--properties-file", }, description = "Any properties-file. Useful for custom configuration.")
    protected File propertiesFile;

    @Parameter(names = { "--brokers", }, description = "Kafka broker servers.")
    protected String brokers;

    protected final Properties props;

    protected AbstractClient() {
        this.props = new Properties();
    }

    @Override
    protected void customParametersProcess() {
        if (this.propertiesFile != null) {
            try (FileInputStream fis = new FileInputStream(this.propertiesFile)) {
                System.out.println(String.format("Load properties-file '%s'", this.propertiesFile.getAbsolutePath()));

                this.props.load(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (this.brokers != null) {
            this.props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.brokers);
        }

        super.customParametersProcess();
    }
}
