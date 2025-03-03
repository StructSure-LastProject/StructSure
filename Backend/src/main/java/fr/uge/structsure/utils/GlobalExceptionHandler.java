package fr.uge.structsure.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Logger to track invalid query methods
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Oh, it seems like this is a logger... */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<String> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("Request method not supported for @{}/{} ({})", request.getMethod(), getUri(request), request.getRemoteHost());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Get the targeted URI from the request
     * @param request the request to get the URI from
     * @return the URI
     */
    private static String getUri(HttpServletRequest request) {
        var uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        return uri == null ? "" : String.valueOf(uri);
    }
}