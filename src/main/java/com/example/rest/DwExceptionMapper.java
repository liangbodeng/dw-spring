package com.example.rest;

import org.glassfish.jersey.server.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.example.auth.UserException;
import com.example.core.DwException;

@Provider
public class DwExceptionMapper implements ExceptionMapper<Exception> {
	private static final Logger LOG = LoggerFactory.getLogger(DwExceptionMapper.class);

	public DwExceptionMapper() {
	}

	@Override
	public Response toResponse(Exception error) {
		String errorMessage = null;
		Status status = Response.Status.BAD_REQUEST;

		if (error instanceof ParamException) {
			ParamException paramException = (ParamException) error;
			if (paramException.getCause() instanceof DwException) {
				errorMessage = ((DwException) paramException.getCause()).toJson(paramException.getParameterName());
			} else {
				errorMessage = new DwException(DwException.INVALID_PARAMETER, "Invalid value for parameter: '" + paramException.getParameterName() + "'").toJson();
			}
		} else if (error instanceof DwException) {
			errorMessage = ((DwException) error).toJson();
		} else if (error instanceof UserException) {
			errorMessage = ((UserException) error).toJson();
		} else if (error instanceof NotFoundException) {
			errorMessage = new DwException(DwException.RESOURCE_NOT_FOUND, "Resource not found").toJson();
		} else {
			LOG.error(error.getMessage(), error);
			errorMessage = new DwException(DwException.INTERNAL_SYSTEM_ERROR, error.getMessage()).toJson();
		}

		LOG.info("toReponse - responding: " + errorMessage, error);
		return Response.status(status).entity(errorMessage).build();
	}
}
