package com.eco;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

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
}
