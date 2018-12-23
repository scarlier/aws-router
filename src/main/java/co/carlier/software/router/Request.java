package co.carlier.software.router;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Request {

    private String sourceIp;
    private String body;
    private Map<String, String> urlParameters = new HashMap<>();

    public Optional<String> getSourceIp() {
        return Optional.ofNullable(sourceIp);
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getUrlParameters() {
        return urlParameters;
    }

    public void setUrlParameters(Map<String, String> urlParameters) {
        this.urlParameters = urlParameters;
    }


    //auth
    private Auth auth;

    public Optional<Auth> getAuth() {
        return Optional.ofNullable(auth);
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {
        private UUID tenantId;
        private UUID userId;
        private String role;
        private String email;
        private String accessToken;

        public Auth(UUID tenantId) {
            this.tenantId = tenantId;
        }

        public Auth(UUID tenantId, UUID userId, String role, String email, String accessToken) {
            this.tenantId = tenantId;
            this.userId = userId;
            this.role = role;
            this.email = email;
            this.accessToken = accessToken;
        }

        public UUID getTenantId() {
            return tenantId;
        }

        public UUID getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }

        public String getEmail() {
            return email;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }
}
