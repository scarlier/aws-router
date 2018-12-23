package co.carlier.software.router;

public class ExceptionResponse {

    private int statusCode = 500;
    private String body;

    ExceptionResponse(String body) {
        this.body = body;
    }

    public ExceptionResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public ExceptionResponse(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
