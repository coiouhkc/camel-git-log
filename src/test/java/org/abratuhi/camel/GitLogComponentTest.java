/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.abratuhi.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepository;
import org.junit.Test;

import java.io.File;

public class GitLogComponentTest extends CamelTestSupport {

    @Produce(uri = "direct:input")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:output")
    private MockEndpoint output;


    private File repodir;
    private File repogit;

    @Test
    public void testReadSampleGitRepo() throws Exception {
        output.setExpectedMessageCount(1);

        Exchange exchange = producerTemplate.send(new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(GitLogMessage.HEADER_PATH, "");
            }
        });

        output.assertIsSatisfied();
    }


    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:input").to("git:" + repogit.getAbsolutePath()).to("mock:output");
            }
        };
    }

    public void setUp() throws Exception {
        repodir = File.createTempFile("tmp", "repo");
        repodir.delete();
        repodir.mkdir();

        repogit = new File(repodir, ".git");

        final FileRepository repo = new FileRepository(repogit);
        Git git = new Git(repo);

        repo.create();
        final File repofile = File.createTempFile("tmp", "repofile", repodir);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Test message").call();

        super.setUp();
    }

    public void tearDown() throws Exception {
        repodir.deleteOnExit();

        super.tearDown();
    }



}
