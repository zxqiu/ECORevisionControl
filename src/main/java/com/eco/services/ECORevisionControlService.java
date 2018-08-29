package com.eco.services;

import com.eco.branch.resources.BranchResource;
import com.eco.filter.GeneralRequestFilter;
import com.eco.views.resources.GUI;
import com.fizzed.rocker.runtime.RockerRuntime;
import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.jdbi.DBIFactory;

import org.skife.jdbi.v2.DBI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eco.revision.dao.RevisionDAO;
import com.eco.revision.resources.RevisionResource;

/**
 * Created by neo on 8/12/18.
 */
public class ECORevisionControlService extends Application<ECOConfiguration> {
    private static final Logger _logger = LoggerFactory.getLogger(ECORevisionControlService.class);

    public static void main(String[] args) throws Exception {
        new ECORevisionControlService().run(args);
    }

    @Override
    public String getName() {
        return "ECO revision Control Application";
    }

    @Override
    public void initialize(Bootstrap<ECOConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/r"));
    }

    @Override
    public void run(ECOConfiguration configuration,
                    Environment environment) throws Exception {
		final DBIFactory factory = new DBIFactory();
	    final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "sqlite");

	    final RevisionDAO revisionDAO = jdbi.onDemand(RevisionDAO.class);

        _logger.info("Register all resources");
        /* register resources */
        environment.jersey().register(new RevisionResource(revisionDAO));
        environment.jersey().register(new BranchResource());
        environment.jersey().register(new GUI(revisionDAO));

        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(new RockerMessageBodyWriter());

	    /*
	    dev option: real time rocker reloading
	     */
        RockerRuntime.getInstance().setReloading(true);

		/*
		register all filters
		 */
        environment.jersey().register(new GeneralRequestFilter());
    }
}
