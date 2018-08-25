package com.eco.resources;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.resources.RevisionResource;
import com.eco.services.ECOConfiguration;
import com.eco.services.ECORevisionControlService;
import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
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

        /*
        Response response = client
                .target(String.format("http://localhost:%d%s%s%s/"
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH
                        , RevisionResource.PATH_INSERT_OBJ
                        , RULE.getLocalPort())
                        )
                .request()
                .post(Entity.entity(testRevision, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        Form form = new Form();
        form.param(Dict.BRANCH_NAME, testRevision.getBranchName());
        form.param(Dict.REVISION_ID, testRevision.getRevisionId());
        form.param(Dict.TIME, testRevision.getTime().toString());
        form.param(Dict.AUTHOR, testRevision.getAuthor());
        form.param(Dict.COMMENT, testRevision.getData().getComment());
        form.param(Dict.STATUS, String.valueOf(testRevision.getStatus()));
        form.param(Dict.EDITOR, testRevision.getEditor());
        form.param(Dict.COMMIT_ID, testRevision.getCommitId());
        form.param(Dict.EDIT_TIME, testRevision.getEditTime().toString());

        */

        ObjectMapper mapper = new ObjectMapper();
        MultivaluedMap<String, Object> map;// = new MultivaluedHashMap<String, Object>();

        Form form = new Form(map);
        map = mapper.readValue(testRevision.toString(), new TypeReference<Map<String, String>>(){});

        Response response = client
                .target(String.format("http://localhost:%d%s%s%s"
                        , RULE.getLocalPort()
                        , Dict.API_V1_PATH
                        , RevisionResource.PATH
                        , RevisionResource.PATH_INSERT_FORM)
                        )
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    /*
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
    */

    }
