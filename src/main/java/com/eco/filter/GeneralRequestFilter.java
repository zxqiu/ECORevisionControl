package com.eco.filter;

import com.eco.utils.misc.XSSFilter;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by zxqiu on 8/26/18.
 */
public class GeneralRequestFilter implements ContainerRequestFilter {
	private static final Logger _logger = LoggerFactory.getLogger(GeneralRequestFilter.class);
	private static XSSFilter xssFilter = new XSSFilter();

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		ContainerRequest request = (ContainerRequest) containerRequestContext;

		if (containerRequestContext.hasEntity()) {
			request.bufferEntity();
			String in = request.readEntity(String.class);
			//_logger.info("Incoming data in request :" + in);
			String out = xssFilter.stripXSS(in);
			//_logger.info("Stripped data in request :" + out);

			request.setEntityStream(new ByteArrayInputStream(out.getBytes()));
		}
	}
}
