package com.eco.utils.exception;

import javax.ws.rs.core.Response;

/**
 * Created by neo on 9/7/18.
 */
public class GeneralException extends Throwable {
    private Response.Status status;

    public GeneralException() {
        this(Response.Status.INTERNAL_SERVER_ERROR);
    }

    public GeneralException(String message) {
        this(Response.Status.INTERNAL_SERVER_ERROR, message);
    }

    public GeneralException(Response.Status status) {
        this(status, "Error happens when executing requested task");
    }

    public GeneralException(Response.Status status, String message) {
        this(status, message, null);
    }

    public GeneralException(Response.Status status, String message, Throwable throwable) {
        super(message, throwable);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
