package com.hotelreservation.msgateway.validator;

import com.hotelreservation.msgateway.constants.ApiPaths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Validator responsible for determining whether an incoming HTTP request matches any pre-configured
 * public route that bypasses authentication.
 */
@Component
public class RouteValidator {

  private final Map<HttpMethod, List<PathPattern>> methodPatterns = new HashMap<>();

  /**
   * Initializes the validator by parsing raw public route strings into compiled {@link PathPattern}
   * instances grouped by HTTP method for optimal performance.
   */
  public RouteValidator() {

    PathPatternParser pathPatternParser = new PathPatternParser();

    ApiPaths.PublicRoutes.ALLOWED_PATHS.forEach(
        (method, paths) -> {
          List<PathPattern> compilated = paths.stream().map(pathPatternParser::parse).toList();

          this.methodPatterns.put(method, compilated);
        });
  }

  /**
   * Checks if the given HTTP request matches any of the registered public endpoints. * @param
   * request the incoming {@link ServerHttpRequest} to evaluate.
   *
   * @return true if the request path and method match a public pattern; false otherwise.
   */
  public boolean isPublic(ServerHttpRequest request) {
    HttpMethod method = request.getMethod();

    if (!methodPatterns.containsKey(method)) {
      return false;
    }

    PathContainer pathContainer = request.getPath().pathWithinApplication();

    return methodPatterns.get(method).stream().anyMatch(pattern -> pattern.matches(pathContainer));
  }
}
