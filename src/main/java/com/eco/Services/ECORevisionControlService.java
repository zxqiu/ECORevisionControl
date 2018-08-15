package com.eco.Services;

import com.eco.ECOConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import com.eco.Revision.dao.RevisionDAO;

import io.dropwizard.jdbi.DBIFactory;

/**
 * Created by neo on 8/12/18.
 */
public class ECORevisionControlService extends Application<ECOConfiguration> {
    public static void main(String[] args) throws Exception {
        new ECORevisionControlService().run(args);
    }

    @Override
    public String getName() {
        return "ECO Revision Control Application";
    }

    @Override
    public void initialize(Bootstrap<ECOConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(ECOConfiguration configuration,
                    Environment environment) {
		final DBIFactory factory = new DBIFactory();
	    final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");

	    final RevisionDAO revisionDAO = jdbi.onDemand(RevisionDAO.class);

        revisionDAO.createTable();
    }
}
