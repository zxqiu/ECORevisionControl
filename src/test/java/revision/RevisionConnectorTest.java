package revision;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.eco.revision.core.Revision;
import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by neo on 8/16/18.
 */
public class RevisionConnectorTest {
    private static final Logger _logger = LoggerFactory.getLogger(RevisionConnectorTest.class);
    private final DataSourceFactory conf = new DataSourceFactory();

    {
        BootstrapLogging.bootstrap();
        conf.setUrl("jdbc:sqlite:db/eco.db");
        conf.setDriverClass("org.sqlite.JDBC");
        conf.setValidationQuery("SELECT 1");
    }

    private RevisionConnector revisionConnector;

    private final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
    private final LifecycleEnvironment lifecycleEnvironment = mock(LifecycleEnvironment.class);
    private final Environment environment = mock(Environment.class);
    private final DBIFactory factory = new DBIFactory();
    private final List<Managed> managed = new ArrayList<>();
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private DBI dbi = mock(DBI.class);

    @Before
    public void setUp() throws Exception {
        when(environment.healthChecks()).thenReturn(healthChecks);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(environment.metrics()).thenReturn(metricRegistry);
        when(environment.getHealthCheckExecutorService()).thenReturn(Executors.newSingleThreadExecutor());

        this.dbi = factory.build(environment, conf, "sqlite");
        final ArgumentCaptor<Managed> managedCaptor = ArgumentCaptor.forClass(Managed.class);
        verify(lifecycleEnvironment).manage(managedCaptor.capture());
        managed.addAll(managedCaptor.getAllValues());
        for (Managed obj : managed) {
            obj.start();
        }

        /*
        try {
            final RevisionDAO revisionDAO = this.dbi.onDemand(RevisionDAO.class);
            RevisionConnector.init(revisionDAO);

            _logger.info("Testing : RevisionConnector");

            Revision revision = new Revision();
            revision.setId("test.rev");
            revision.setBranchName("test");
            revision.setAuthor("us");
            revision.setRevisionId("rev");
            Date time = new Date();
            revision.setTime(time);
            revision.setCommitted(true);
            revision.setCommitId("id");
            revision.setCommitter("me");
            Date editTime = new Date();
            revision.setEditTime(editTime);

            RevisionConnector rc = RevisionConnector.getInstance();

            rc.createTable();
            rc.insert(revision);

            Revision ret = rc.findByID(revision.getId()).get(0);
            assertEquals(ret.getId(), revision.getId());
            assertEquals(ret.getBranchName(), revision.getBranchName());
            assertEquals(ret.getRevisionId(), revision.getRevisionId());
            assertEquals(ret.getAuthor(), revision.getAuthor());
            assertEquals(ret.getTime(), revision.getTime());
            assertEquals(ret.isCommitted(), revision.isCommitted());
            assertEquals(ret.getCommitId(), revision.getCommitId());
            assertEquals(ret.getCommitter(), revision.getCommitter());
            assertEquals(ret.getEditTime(), revision.getEditTime());
        } catch (IOException e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        }
        */

    }

    @After
    public void tearDown() throws Exception {
        for (Managed obj : managed) {
            obj.stop();
        }
    }

    @Test
    public void
}
