package co.carlier.software.router;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private int statusCode = 200;
    private Map<String, String> headers = new HashMap<>();
    private Object body;

    public Response() {
    }

    public Response(Object body) {
        this.body = body;
    }

    public Response(int statusCode) {
        this.statusCode = statusCode;
    }

    public Response(Object body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Response setBody(Object body) {
        this.body = body;
        return this;
    }

    public Response setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Response addHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }
}
