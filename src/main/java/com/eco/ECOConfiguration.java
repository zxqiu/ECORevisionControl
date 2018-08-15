package com.eco;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by neo on 8/12/18.
 */
public class ECOConfiguration extends Configuration {
    @NotEmpty
    private String projectName;

    @JsonProperty
    public String getProjectName() {
        return projectName;
    }

    @JsonProperty
    public void setProjectName(String name) {
        this.projectName = name;
    }

    /******************** database *************************/
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
}
