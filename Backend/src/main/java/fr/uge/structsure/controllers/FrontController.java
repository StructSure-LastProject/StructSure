package fr.uge.structsure.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller that serves the frontend as a SinglePageApplication, no
 * matters which url is targeted since it does not start with "/api".
 */
@Controller
public class FrontController implements ErrorController {

    /** Load the index.html content in memory to reduce reading time */
    private static final String INDEX = load("static/index.html");

    /** Load the error.html content in memory to reduce reading time */
    private static final String ERROR = load("static/error.html");

    /** Reuse not found response */
    private static final ResponseEntity<InputStreamResource> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

    /** Detects the version from the android app file name */
    private static final Pattern APP_FILE = Pattern.compile("StructSure-(\\d+)\\.(\\d+)\\.(\\d+)\\.[a-z]+", Pattern.CASE_INSENSITIVE);

    /** Correspondance table to find MIME type from a file extension */
    private static final Map<String, MediaType> MIME;
    static {
        var map = new HashMap<String, MediaType>();
        map.put("png", MediaType.IMAGE_PNG);
        map.put("jpg", MediaType.IMAGE_JPEG);
        map.put("jpeg", MediaType.IMAGE_JPEG);
        map.put("icon", MediaType.valueOf("image/x-icon"));
        map.put("ico", MediaType.valueOf("image/x-icon"));
        map.put("js", MediaType.valueOf("application/javascript"));
        map.put("css", MediaType.valueOf("text/css"));
        map.put("svg", MediaType.valueOf("image/svg+xml"));
        map.put("woff", MediaType.valueOf("font/woff"));
        map.put("woff2", MediaType.valueOf("font/woff2"));
        MIME = map;
    }


    /**
     * Mapper for all endpoints (except /api*) that serves the frontend
     * file "index.html". Routing is then handled by the Single Page
     * Application.
     * @param request currently active request to get the targeted url
     * @return the content of the index.html page
     */
    @GetMapping("**")
    public ResponseEntity<?> forwardToIndex(HttpServletRequest request, HttpSession session) {
        var savedRequest = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        ResponseEntity<?> response;
        if (request.getRequestURI().startsWith("/login") && savedRequest != null && savedRequest.getRequestURI().startsWith("/api")){
            // Request targeting the API that got redirected to the login page
            response = new ResponseEntity<>(INDEX, HttpStatus.UNAUTHORIZED);
        } else if (request.getRequestURI().startsWith("/api")) {
            response = null; // Give up and lets another endpoint take care of this request
        } else if (request.getRequestURI().contains("..")) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (request.getRequestURI().contains(".")) {
            response = tryLoad(request.getRequestURI())
                .orElse(ResponseEntity.notFound().build());
        } else {
            response = ResponseEntity.ok(INDEX);
        }
        return response;
    }

    /**
     * Endpoint that displays the error page with the error code.
     * This page works for frontend unknown endpoint by fetch this url
     * with values such as "/error404", and work also internally for
     * unsafe endpoints.
     * @param request the request to get the error code from (internally)
     * @param code the explicit error code (for the frontend redirect)
     * @return the error page
     */
    @GetMapping("/error{code}")
    public ResponseEntity<String> handleError(HttpServletRequest request, @PathVariable(required = false) String code) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (code == null || code.isBlank()) code = status != null ? status.toString() : ":/";
        var httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            httpCode = HttpStatus.valueOf(code);
        } catch (IllegalArgumentException e) {
            // Already have 500 default value
        }
        return new ResponseEntity<>(ERROR.replace("%%", code), httpCode);
    }

    /**
     * Enable users to download the Android application package (apk)
     * from the server.
     * Searches such a file on the server static files and picks the
     * latest version if many are present.
     * @return the latest application file if found
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadApplication() {
        try {
            var resources = new PathMatchingResourcePatternResolver().getResources("static/application/*");
            var file = Arrays.stream(resources)
                .flatMap(r -> Optional.ofNullable(r.getFilename()).stream())
                .max(Comparator.comparing(FrontController::getVersion));
            return file.flatMap(s -> tryLoad("/application/" + s))
                .orElse(NOT_FOUND);
        } catch (IOException e) {
            return NOT_FOUND;
        }
    }

    /**
     * Format the version from the filename with multiple zeros to be
     * able to sort correctly versions in filename.
     * @param fileName the name of the file to sort
     * @return the sort-proof name
     */
    private static String getVersion(String fileName) {
        Matcher matcher = APP_FILE.matcher(fileName);

        if (matcher.find()) {
            try {
                return String.format(
                    "StructSure-%03d.%03d.%03d.apk",
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
                );
            } catch (NumberFormatException e) {
                // Not a big deal, simply return the filename
            }
        }
        return fileName;
    }

    /**
     * Tries to load and returns the bytes from the static file at the
     * given location. If not found, will return an empty response.
     * The path must be checked before calling the method to avoid
     * patterns such as '../', note that Spring already handle this
     * case with malicious URL detection.
     * @param path the path of the file to load
     * @return the response with the file data or empty if not found
     */
    private static Optional<ResponseEntity<InputStreamResource>> tryLoad(String path) {
        ClassPathResource resource = new ClassPathResource("static" + path);
        try {
            var inputStream = new InputStreamResource(resource.getInputStream());
            return Optional.of(
                ResponseEntity.ok()
                    .contentType(getMimeType(path))
                    .contentLength(resource.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(inputStream)
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to detect the MIME type corresponding to the extension of
     * the given path.
     * @param path the path to detect the MIME type for
     * @return the MIME type or 'ALL' if not found
     */
    private static MediaType getMimeType(String path) {
        var point = path.lastIndexOf(".");
        var ext = point == -1 ? "" : path.substring(point + 1);
        return MIME.getOrDefault(ext, MediaType.valueOf("application/octet-stream"));
    }

    /**
     * Loads the given static page content as a string from the
     * resources files.
     * @param file the path of the file to load (ex: static/index.html)
     * @return the content of the requested file
     * @throws IllegalStateException if the file is not readable or not found
     */
    private static String load(String file) {
        try (var input = new ClassPathResource(file).getInputStream()) {
            return new String(input.readAllBytes());
        } catch (IOException e) {
            System.err.println("Failed to load '" + file + "' which is needed in production");
            return "Loading error : " + file;
        }
    }
}
