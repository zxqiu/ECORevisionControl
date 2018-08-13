package com.eco.Services;

import com.eco.ECOConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
        // nothing to do yet
    }
}
