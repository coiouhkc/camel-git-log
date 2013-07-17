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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The HelloWorld producer.
 */
public class GitLogProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(GitLogProducer.class);
    private GitLogEndpoint endpoint;

    public GitLogProducer(GitLogEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        String repo = endpoint.getEndpointUri().substring("git://".length());
        FileRepositoryBuilder frb = new FileRepositoryBuilder();
        FileRepository fr = frb.setGitDir(new File(repo)).readEnvironment().build();
        Git git = new Git(fr);
        Iterable<RevCommit> commits = git.log().call();

        StringBuffer sb = new StringBuffer();
        for(RevCommit rc : commits) {
            sb.append(rc.getShortMessage() + "\n");
        }

        GitLogMessage msg = new GitLogMessage();
        msg.setBody(sb.toString());

        exchange.setOut(msg);
    }

}
