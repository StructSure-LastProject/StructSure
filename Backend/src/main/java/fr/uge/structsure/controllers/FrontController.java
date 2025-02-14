package fr.uge.structsure.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller that serves the frontend as a SinglePageApplication, no
 * matters which url is targeted since it does not start with "/api".
 */
@Controller
public class FrontController {

    /** Load the index.html content in memory to reduce reading time */
    private static final String HTML = load();

    /**
     * Mapper for all endpoints (except /api*) that serves the frontend
     * file "index.html". Routing is then handled by the Single Page
     * Application.
     * @param request currently active request to get the targeted url
     * @return the content of the index.html page
     */
    @GetMapping("*")
    public ResponseEntity<String> forwardToIndex(HttpServletRequest request, HttpSession session) {
        var savedRequest = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (request.getRequestURI().startsWith("/login") && savedRequest != null && savedRequest.getRequestURI().startsWith("/api")){
            // Request targeting the API that got redirected to the login page
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (request.getRequestURI().startsWith("/api")) {
            return null; // Give up and lets another endpoint take care of this request
        }
        return ResponseEntity.ok(HTML);
    }

    /**
     * Loads the "index.html" page content as a string from the
     * resources files.
     * @return the content of the index.html
     * @throws IllegalStateException if the file is not readable or not found
     */
    private static String load() {
        try (var input = new ClassPathResource("static/index.html").getInputStream()) {
            return new String(input.readAllBytes());
        } catch (IOException e) {
            System.err.println("Failed to load 'index.html' which is needed in production");
            return "Loading error : index.html";
        }
    }
}
