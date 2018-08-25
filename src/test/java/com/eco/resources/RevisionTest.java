package com.eco.resources;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.resources.RevisionResource;
import com.eco.services.ECOConfiguration;
import com.eco.services.ECORevisionControlService;
import com.eco.utils.misc.Dict;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.client.JerseyClientBuilder;

import java.util.Date;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class RevisionTest {
    private static final Logger _logger = LoggerFactory.getLogger(RevisionTest.class);
    private static Client client;

    private Revision testRevision = new Revision("testBranchtestRevision",
                                                 "testBranch",
                                                 "testRevision",
                                                 new Date(123),
                                                 "testAuthor",
                                                 1,
                                                 "testEditor",
                                                 "testCommitID",
                                                 new Date(456),
                                                 new RevisionData("testComment"));

    @ClassRule
    public static final DropwizardAppRule<ECOConfiguration> RULE =
        new DropwizardAppRule<ECOConfiguration>(ECORevisionControlService.class,
                ResourceHelpers.resourceFilePath("ECORevisionControl.yml"));

    @Test
    public void createRevision() {
        if (client == null) {
            client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revision test client");
        }

        Response response = client
                .target(String.format("http://localhost:%d/"
                        + Dict.API_V1_PATH + RevisionResource.PATH
                        + RevisionResource.PATH_INSERT_OBJ, RULE.getLocalPort()))
                .request()
                .post(Entity.entity(testRevision, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void getRevision() {
        if (client == null) {
            client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revision test client");
        }

        Revision revision = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH,
                        testRevision.getBranchName(),
                        testRevision.getRevisionId()))
                .request()
                .get(Revision.class);

        assertThat(revision.toString()).isEqualTo(testRevision.toString());
    }

    @Test
    public void deleteRevision() {
        if (client == null) {
            client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revision test client");
        }

        Response response = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH,
                        testRevision.getBranchName(),
                        testRevision.getRevisionId()))
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    }
