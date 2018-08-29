package com.eco.resources;

import com.eco.revision.core.CommitStatus;
import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.resources.RevisionResource;
import com.eco.services.ECOConfiguration;
import com.eco.services.ECORevisionControlService;
import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;

public class RevisionTest {
    private static final Logger _logger = LoggerFactory.getLogger(RevisionTest.class);
    private static Client client;
    private static final int READ_TIMEOUT = 20 * 1000; // second

    private Revision testRevision0 = new Revision("testBranchtestRevision0",
                                                 "testBranch",
                                                 "testRevision0",
                                                 new Date(123),
                                                 "testAuthor0",
                                                 "testComment0",
                                                 "testEditor0",
                                                 new Date(456),
                                                 new RevisionData());

    private Revision testRevision1 = new Revision("testBranchtestRevision1",
                                                 "testBranch",
                                                 "testRevision1",
                                                 new Date(123),
                                                 "testAuthor1",
                                                 "testComment1",
                                                 "testEditor1",
                                                 new Date(456),
                                                 new RevisionData());

    @ClassRule
    public static final DropwizardAppRule<ECOConfiguration> RULE =
        new DropwizardAppRule<ECOConfiguration>(ECORevisionControlService.class,
                ResourceHelpers.resourceFilePath("ECORevisionControl.yml"));

    @Before
    public void setUp() throws IOException {
        if (client == null) {
            client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revision test client");
            client.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        }
        createRevision();
    }

    @After
    public void cleanUp() {
        deleteRevision();
    }

    //@Test
    public void createRevision() throws IOException {
        Response response = client
                .target(String.format("http://localhost:%d%s%s%s/"
                        , RULE.getLocalPort()
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH_ROOT
                        , RevisionResource.PATH_POST_OBJ)
                        )
                .request()
                .post(Entity.entity(testRevision0, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Form form = new Form();
        form.param(Dict.BRANCH_NAME, testRevision1.getBranchName());
        form.param(Dict.REVISION_ID, testRevision1.getRevisionId());
        form.param(Dict.TIME, String.valueOf(testRevision1.getTime().getTime()));
        form.param(Dict.AUTHOR, testRevision1.getAuthor());
        form.param(Dict.COMMENT, testRevision1.getComment());
        form.param(Dict.EDITOR, testRevision1.getEditor());
        form.param(Dict.EDIT_TIME, String.valueOf(testRevision1.getEditTime().getTime()));

        response = client
                .target(String.format("http://localhost:%d%s%s%s"
                        , RULE.getLocalPort()
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH_ROOT
                        , RevisionResource.PATH_POST_FORM)
                        )
                .request()
                .post(Entity.form(form));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void getByID() {
        Revision revision = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH_ROOT,
                        testRevision0.getBranchName(),
                        testRevision0.getRevisionId()))
                .request()
                .get(Revision.class);

        assertThat(revision.toString()).isEqualTo(testRevision0.toString());
    }

    @Test
    public void getByBranch() {
        List<Revision> revisions = client
                .target(String.format("http://localhost:%d%s%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH_ROOT,
                        testRevision0.getBranchName()))
                .request()
                .get(new GenericType<List<Revision>>(){});

        assertThat(revisions.size()).isGreaterThan(0);
    }

    //@Test
    public void getAll() {
        List<Revision> revisions = client
                .target(String.format("http://localhost:%d%s%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH_ROOT
                        ))
                .request()
                .get(new GenericType<List<Revision>>(){});

        assertThat(revisions.size()).isGreaterThan(1);
    }

    @Test
    public void updateRevision() throws IOException {
        Revision revisionCopy = Revision.toRevision(testRevision1.toString());
        revisionCopy.setEditor("newEditor");
        revisionCopy.setEditTime(new Date(999));

        Form form = new Form();
        form.param(Dict.EDITOR, revisionCopy.getEditor());
        form.param(Dict.EDIT_TIME, String.valueOf(revisionCopy.getEditTime().getTime()));

        List<CommitStatus> commitStatuses = new ArrayList<>();
        commitStatuses.add(new CommitStatus("testCommitBranch2", Revision.STATUS.COMMITTED.getValue(), "committed"));
        commitStatuses.add(new CommitStatus("testCommitBranch3", Revision.STATUS.SKIPPED.getValue(), "skipped"));
        if (revisionCopy.getData() == null) {
            revisionCopy.setData(new RevisionData());
        }
        revisionCopy.getData().setCommitStatuses(commitStatuses);

        form.param(Dict.COMMIT_STATUSES, CommitStatus.toJSONString(revisionCopy.getData().getCommitStatuses()));

        Response response = client
                .target(String.format("http://localhost:%d%s%s"
                        , RULE.getLocalPort()
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH_ROOT)
                )
                .path(testRevision1.getBranchName())
                .path(testRevision1.getRevisionId())
                .request()
                .put(Entity.form(form));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Revision revision = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH_ROOT,
                        testRevision1.getBranchName(),
                        testRevision1.getRevisionId()))
                .request()
                .get(Revision.class);

        assertThat(revision.toString()).isEqualTo(revisionCopy.toString());
    }

    //@Test
    public void deleteRevision() {
        Response response = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH_ROOT,
                        testRevision0.getBranchName(),
                        testRevision0.getRevisionId()))
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        response = client
                .target(String.format("http://localhost:%d%s%s/%s/%s",
                        RULE.getLocalPort(),
                        Dict.API_V1_PATH,
                        RevisionResource.PATH_ROOT,
                        testRevision1.getBranchName(),
                        testRevision1.getRevisionId()))
                .request()
                .delete();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}
