package com.eco.utils.misc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by neo on 9/7/18.
 */
public class JsonErrorMsg {
    @JsonProperty
    private String message;

    public JsonErrorMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
