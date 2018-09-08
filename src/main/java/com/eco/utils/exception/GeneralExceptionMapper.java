package com.eco.utils.exception;

import org.hibernate.engine.spi.SessionImplementor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by neo on 9/7/18.
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<GeneralException> {
    @Override
    public Response toResponse(GeneralException e) {
        return Response.status(e.getStatus().getStatusCode())
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
