package co.carlier.software.router;

import co.carlier.software.router.exception.ExceptionNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * @param <R> Response object of function
 */
public class Router<R> {

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

    private List<Route> routes = new ArrayList<>();

    public Router put(String url, Function<Request, R> function) {
        addRoute("put", url, function);
        return this;
    }

    public Router post(String url, Function<Request, R> function) {
        addRoute("post", url, function);
        return this;
    }

    public Router patch(String url, Function<Request, R> function) {
        addRoute("get", url, function);
        return this;
    }

    public Router delete(String url, Function<Request, R> function) {
        addRoute("delete", url, function);
        return this;
    }

    public Router get(String url, Function<Request, R> function) {
        addRoute("get", url, function);
        return this;
    }

    public R execute(String method, String url, Request request) {
        method = method.toLowerCase(Locale.ENGLISH).trim();
        url = url.trim();

        //remove trailing /
        String lastChar = url.substring(url.length() - 1);
        if (lastChar.equals("/")) {
            url = url.substring(0, url.length() - 1);
        }

        for (Route route : routes) {
            if (!route.method.equals(method)) {
                continue;
            }

            Map<String, String> match = route.urlMatcher.match(url);
            if (match == null) {
                continue;
            }

            //add parameters to request object
            request.setUrlParameters(match);
            return route.function.apply(request);
        }

        throw new ExceptionNotFound("route '" + method + ":" + url + "' not found");
    }

    public List<Route> routes() {
        return routes;
    }

    private void addRoute(String method, String url, Function<Request, R> function) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("add route '" + method + ":" + url + "'");
        }

        routes.add(new Route(method, url, function));
    }

    public class Route {
        private final String method;
        private final Function<Request, R> function;
        private final UrlMatcher urlMatcher;

        private Route(String method, String url, Function<Request, R> function) {
            this.method = method;
            this.function = function;
            this.urlMatcher = new UrlMatcher(url);
        }

        public String method() {
            return method;
        }
    }
}
