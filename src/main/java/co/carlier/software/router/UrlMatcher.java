package co.carlier.software.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UrlMatcher {

    private static final String URL_PARAM_REGEX = "\\{(\\w*?)\\}";
    private static final Pattern URL_PARAM_PATTERN = Pattern.compile(URL_PARAM_REGEX);
    private static final String URL_PARAM_MATCH_REGEX = "([^\\/]+)";

    private final String requestUrl;

    private Matcher matcher;
    private List<String> parameterNames = new ArrayList<>();

    UrlMatcher(String requestUrl) {
        Pattern compiledUrl = Pattern.compile(requestUrl.replaceAll(URL_PARAM_REGEX, URL_PARAM_MATCH_REGEX));
        matcher = compiledUrl.matcher("");

        this.requestUrl = requestUrl;
    }

    Map<String, String> match(String url) {
        Matcher urlMatcher = matcher.reset(url);

        if (urlMatcher.matches()) {
            return extractParameters(urlMatcher);
        }

        return null;
    }

    private Map<String, String> extractParameters(Matcher matcher) {
        Map<String, String> values = new HashMap<>();

        if (parameterNames.isEmpty()) {
            Matcher m = URL_PARAM_PATTERN.matcher(requestUrl);
            while (m.find()) {
                parameterNames.add(m.group(1));
            }
        }

        for (int i = 0; i < matcher.groupCount(); i++) {
            String value = matcher.group(i + 1);

            if (value != null) {
                values.put(parameterNames.get(i), value);
            }
        }

        return values;
    }
}