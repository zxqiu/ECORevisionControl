package com.eco.resources;

import com.codahale.metrics.MetricRegistry;
import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import com.eco.revision.resources.RevisionResource;
import com.eco.services.ECOConfiguration;
import com.eco.services.ECORevisionControlService;
import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.client.JerseyClientBuilder;
import java.util.Date;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.assertj.core.api.Assertions.assertThat;

public class RevisionTest {
    private static final Logger _logger = LoggerFactory.getLogger(RevisionTest.class);

    private Revision testRevision = new Revision("testID",
                                                 "testBranch",
                                                 "testRevision",
                                                 new Date(),
                                                 "testAuthor",
                                                 true,
                                                 "testCommitter",
                                                 "testCommitID",
                                                 new Date(),
                                                 new RevisionData("testComment"));

    @ClassRule
    public static final DropwizardAppRule<ECOConfiguration> RULE =
        new DropwizardAppRule<ECOConfiguration>(ECORevisionControlService.class, ResourceHelpers.resourceFilePath("ECORevisionControl.yml"));;

    @Test
    public void createRevision() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revision test client");

        Response response = client.target(
                String.format("http://localhost:%d/" + Dict.API_V1_PATH + RevisionResource.PATH + RevisionResource.PATH_INSERT_OBJ, RULE.getLocalPort()
                ))
                .request()
                .post(Entity.entity(testRevision, MediaType.APPLICATION_JSON));

        //assertThat(response.getStatus()).isEqualTo(Response.Status.OK);
    }

    /*
    private final DataSourceFactory conf = new DataSourceFactory();
    private MetricRegistry metricRegistry = new MetricRegistry();
    private Environment environment;
    private RevisionConnector revisionConnector;
    private DBI dbi;

    private Revision testRevision;

    {
        BootstrapLogging.bootstrap();
        conf.setUrl("jdbc:sqlite:db/test.db");
        conf.setDriverClass("org.sqlite.JDBC");
        conf.setUser("");
        conf.asSingleConnectionPool();
    }

    @ClassRule
    public static ResourceTestRule resources;

    @Before
    public void setUp() throws Exception {
        environment = new Environment("test", new ObjectMapper(), Validators.newValidator(),
                metricRegistry, ClassLoader.getSystemClassLoader());
        dbi = new DBIFactory().build(environment, conf, "sqlite");

        resources = ResourceTestRule.builder()
                    .addResource(new RevisionResource(dbi.onDemand(RevisionDAO.class)))
                    .build();

        revisionConnector = RevisionConnector.getInstance();

        testRevision = new Revision("testID",
                                    "testBranch",
                                    "testRevision",
                                    new Date(),
                                    "testAuthor",
                                    true,
                                    "testCommitter",
                                    "testCommitID",
                                    new Date(),
                                    new RevisionData("testComment"));

    }

    @After
    public void tearDown() {
    }

    @Test
    public void createRevision() throws Exception {
        final Response response = resources.target(Dict.API_V1_PATH + "/revisions/test")
                                           .request(MediaType.APPLICATION_JSON)
                                           .post(Entity.entity(testRevision, MediaType.APPLICATION_JSON_TYPE));
        //assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        //assertThat(revisionConnector.findByID(testRevision.getId())).isEqualTo(testRevision);
    }
    */
}
