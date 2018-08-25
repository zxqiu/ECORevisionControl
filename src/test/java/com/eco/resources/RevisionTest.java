package com.eco.resources;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.resources.RevisionResource;
import com.eco.services.ECOConfiguration;
import com.eco.services.ECORevisionControlService;
import com.eco.utils.misc.Dict;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.IOException;
import java.util.Date;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
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

    private Revision testRevision0 = new Revision("testBranch0testRevision0",
                                                 "testBranch0",
                                                 "testRevision0",
                                                 new Date(123),
                                                 "testAuthor0",
                                                 1,
                                                 "testEditor0",
                                                 "testCommitID0",
                                                 new Date(456),
                                                 new RevisionData("testComment0"));

    private Revision testRevision1 = new Revision("testBranch1testRevision1",
            "testBranch1",
            "testRevision1",
            new Date(123),
            "testAuthor1",
            1,
            "testEditor1",
            "testCommitID1",
            new Date(456),
            new RevisionData("testComment1"));

    @ClassRule
    public static final DropwizardAppRule<ECOConfiguration> RULE =
        new DropwizardAppRule<ECOConfiguration>(ECORevisionControlService.class,
                ResourceHelpers.resourceFilePath("ECORevisionControl.yml"));

    @Test
    public void createRevision() throws IOException {
        if (client == null) {
            client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revision test client");
        }

        Response response = client
                .target(String.format("http://localhost:%d%s%s%s/"
                        , RULE.getLocalPort()
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH
                        , RevisionResource.PATH_INSERT_OBJ)
                        )
                .request()
                .post(Entity.entity(testRevision0, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Form form = new Form();
        form.param(Dict.BRANCH_NAME, testRevision1.getBranchName());
        form.param(Dict.REVISION_ID, testRevision1.getRevisionId());
        form.param(Dict.TIME, String.valueOf(testRevision1.getTime().getTime()));
        form.param(Dict.AUTHOR, testRevision1.getAuthor());
        form.param(Dict.COMMENT, testRevision1.getData().getComment());
        form.param(Dict.STATUS, String.valueOf(testRevision1.getStatus()));
        form.param(Dict.EDITOR, testRevision1.getEditor());
        form.param(Dict.COMMIT_ID, testRevision1.getCommitId());
        form.param(Dict.EDIT_TIME, String.valueOf(testRevision1.getEditTime().getTime()));

        response = client
                .target(String.format("http://localhost:%d%s%s%s"
                        , RULE.getLocalPort()
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH
                        , RevisionResource.PATH_INSERT_FORM)
                        )
                .request()
                .post(Entity.form(form));

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
                        testRevision0.getBranchName(),
                        testRevision0.getRevisionId()))
                .request()
                .get(Revision.class);

        assertThat(revision.toString()).isEqualTo(testRevision0.toString());
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
                        testRevision0.getBranchName(),
                        testRevision0.getRevisionId()))
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        response = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH,
                        testRevision1.getBranchName(),
                        testRevision1.getRevisionId()))
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}
