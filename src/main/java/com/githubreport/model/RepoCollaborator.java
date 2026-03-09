package com.githubreport.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepoCollaborator {

    private Long id;
    private String login;

    @JsonProperty("avatar_url")
    private String avatar;

    @JsonProperty("html_url")
    private String url;

    private String type;

    @JsonProperty("role_name")
    private String roleName;

    @JsonProperty("permissions")
    private Permissions permissions;

    public RepoCollaborator() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public static class Permissions {

        private boolean pull;
        private boolean push;
        private boolean admin;
        private boolean maintain;
        private boolean triage;

        public Permissions() {
        }

        public boolean isPull() {
            return pull;
        }

        public void setPull(boolean pull) {
            this.pull = pull;
        }

        public boolean isPush() {
            return push;
        }

        public void setPush(boolean push) {
            this.push = push;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

        public boolean isMaintain() {
            return maintain;
        }

        public void setMaintain(boolean maintain) {
            this.maintain = maintain;
        }

        public boolean isTriage() {
            return triage;
        }

        public void setTriage(boolean triage) {
            this.triage = triage;
        }
    }
}