package com.eco.DBI;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.StringColumnMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by neo on 8/16/18.
 */
public class JDBITest {
    private static final Logger _logger = LoggerFactory.getLogger(JDBITest.class);
    private final DataSourceFactory conf = new DataSourceFactory();

    {
        BootstrapLogging.bootstrap();
        conf.setUrl("jdbc:sqlite:db/test.db");
        conf.setDriverClass("org.sqlite.JDBC");
        conf.setUser("");
        conf.asSingleConnectionPool();
    }

    private MetricRegistry metricRegistry = new MetricRegistry();
    private Environment environment;
    private DBI dbi;
    private TestDAO dao;

    private final String TEST_STR0 = "walefjadjfowihefoaiwhejf;awjdsfowifaelfiaweihvaoihfpawoiehpfoiwhefaw;hief";
    private final int TEST_INT0 = 46253;
    private final Date TEST_TIME0 = new Date();
    private final String TEST_STR1 = "asdlfkwohgaowiealwsefhwoihapoiwhg;awiehghasdfh";
    private final int TEST_INT1 = 51232;

    @Before
    public void setUp() throws Exception {
        environment = new Environment("test", new ObjectMapper(), Validators.newValidator(),
                metricRegistry, ClassLoader.getSystemClassLoader());

        dbi = new DBIFactory().build(environment, conf, "sqlite");
        dao = dbi.onDemand(TestDAO.class);

        for (LifeCycle lc : environment.lifecycle().getManagedObjects()) {
            lc.start();
        }

        dao.dropTable();
        dao.createTable();
        dao.insert(TEST_STR0, TEST_INT0, TEST_TIME0);
        dao.insert(TEST_STR1, TEST_INT1, null);
    }

    @After
    public void tearDown() throws Exception {
        for (LifeCycle lc : environment.lifecycle().getManagedObjects()) {
            lc.stop();
        }
    }

    @Test
    public void createsValidDBI() {
        final Handle handle = dbi.open();

        final Query<String> strs = handle.createQuery("select strItem from " + TestDAO.TABLE_NAME + " where intItem = :intParam")
                .bind("intParam", TEST_INT0)
                .map(StringColumnMapper.INSTANCE);
        assertThat(strs).containsOnly(TEST_STR0);
    }

    @Test
    public void canReturnImmutableLists() throws Exception {
        assertThat(dao.findAllInt())
                .containsExactly(TEST_INT0, TEST_INT1);
    }

    @Test
    public void canReturnImmutableSets() throws Exception {
        assertThat(dao.findAllDistinctInt())
                .containsOnly(TEST_INT0, TEST_INT1);
    }
}
