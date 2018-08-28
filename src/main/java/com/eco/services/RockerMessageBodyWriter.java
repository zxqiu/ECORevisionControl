package com.eco.services;

/*
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.RockerOutput;
import com.fizzed.rocker.runtime.ArrayOfByteArraysOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_JSON})
public class RockerMessageBodyWriter implements MessageBodyWriter<RockerModel> {
    static private final Logger log = LoggerFactory.getLogger(RockerMessageBodyWriter.class);

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
        return RockerModel.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(RockerModel t, Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
        return -1;
    }

    @Override
    public void writeTo(RockerModel t, Class<?> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, Object> mm, OutputStream out) throws IOException, WebApplicationException {
        log.info("{}", mm);


        RockerOutput output = t.render();

        mm.add("Content-Type", "text/html; charset=" + output.getCharset());

        if (output instanceof ArrayOfByteArraysOutput) {
            ArrayOfByteArraysOutput aobao = (ArrayOfByteArraysOutput)output;
            out.write(aobao.toByteArray());
        }
    }

}
