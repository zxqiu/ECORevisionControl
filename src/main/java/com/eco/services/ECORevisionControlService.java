package com.eco.services;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import io.dropwizard.jdbi.DBIFactory;

import org.skife.jdbi.v2.DBI;

import com.eco.revision.dao.RevisionConnector;
import com.eco.revision.dao.RevisionDAO;
import com.eco.revision.resources.RevisionResource;

/**
 * Created by neo on 8/12/18.
 */
public class ECORevisionControlService extends Application<ECOConfiguration> {
    public static void main(String[] args) throws Exception {
        new ECORevisionControlService().run(args);
    }

    @Override
    public String getName() {
        return "ECO revision Control Application";
    }

    @Override
    public void initialize(Bootstrap<ECOConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(ECOConfiguration configuration,
                    Environment environment) throws Exception {
		final DBIFactory factory = new DBIFactory();
	    final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "sqlite");

	    final RevisionDAO revisionDAO = jdbi.onDemand(RevisionDAO.class);
        RevisionConnector.init(revisionDAO);

        /* register resources */
        environment.jersey().register(new RevisionResource());
    }
}
