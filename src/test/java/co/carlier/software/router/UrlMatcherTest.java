package co.carlier.software.router;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UrlMatcherTest {

    @Test
    void matchWithParameters() {
        Map<String, String> matchingMap = new HashMap<>();

        matchingMap.clear();
        matchingMap.put("param", "part2");
        assertEquals(matchingMap, doMatch("/part1/part2", "/part1/{param}", true));

        matchingMap.clear();
        matchingMap.put("param", "wrong");
        assertNotEquals(matchingMap, doMatch("/part1/part2", "/part1/{param}", true));

        matchingMap.clear();
        matchingMap.put("param", "part2");
        matchingMap.put("param1", "part3");
        assertEquals(matchingMap, doMatch("/part1/part2/part3", "/part1/{param}/{param1}", true));

        matchingMap.clear();
        matchingMap.put("param", "part2");
        matchingMap.put("param1", "{part3}");
        assertEquals(matchingMap, doMatch("part2/{part3}", "{param}/{param1}", true));

        matchingMap.clear();
        matchingMap.put("param", "part1");
        assertEquals(matchingMap, doMatch("/part1/part2", "/{param}/part2", true));

        doMatch("/part1/part2/part3", "/part1/{param}/{param1}", true);
        doMatch("/part1/part2/par-t3", "/part1/{param}/{param1}", true);
        doMatch("/part1/part2/{part3}", "/part1/{param}/{param1}", true);
        doMatch("/part1/part2/{part3", "/part1/{param}/{param1}", true);
        doMatch("/part1/part2/part3}", "/part1/{param}/{param1}", true);
        doMatch("/part1/part2/part3", "/part1/{param}/part3", true);
        doMatch("/part1/part2/part3", "/{param}/{param}/part3", true);
        doMatch("/part1/part2/part3}", "/part1/{param}/{param1}", true);
        doMatch("/part1", "/{param}", true);
    }

    @Test
    void matchNotWithParameters() {
        doMatch("/part1", "{param}", false);
        doMatch("/part1/part2", "/{param}", false);
        doMatch("/part1/part2", "/{param}/{param1}/{param2}", false);
        doMatch("/part1/part2", "/{param}/part8", false);
        //        doMatch("/part1/part2/part3", "/part1/{param}/{param}", false);
    }

    @Test
    void matchNotSimple() {
        doMatch("/part1/part2", "/part1", false);
        doMatch("/part1/part2", "/part2", false);
        doMatch("/part1/part2", "/", false);
        doMatch("/part1/part2", "/part1/part2-", false);
        doMatch("/part1/part2", "/-part1/part2", false);
        doMatch("/part1", "part1", false);
    }

    @Test
    void matchSimple() {
        doMatch("/part1/part2", "/part1/part2", true);
        doMatch("/part1/part2-", "/part1/part2-", true);
        doMatch("/-part1/part2", "/-part1/part2", true);

    }

    Map<String, String> doMatch(String reqUrl, String routeUrl, boolean isMatch) {
        UrlMatcher matcher = new UrlMatcher(routeUrl);
        Map<String, String> result = matcher.match(reqUrl);

        if (isMatch) {
            assertNotNull(result);
        } else {
            assertNull(result);
        }

        return result;
    }
}