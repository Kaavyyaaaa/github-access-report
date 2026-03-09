package com.githubreport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessReport {

    private String organization;
    private Instant generatedTime;
    private int totalRepositories;
    private int totalUsers;
    private Map<String, List<RepoAccess>> userAccess;

    public AccessReport() {
    }

    public AccessReport(String organization, Instant generatedTime, int totalRepositories, int totalUsers,
                        Map<String, List<RepoAccess>> userAccess) {
        this.organization = organization;
        this.generatedTime = generatedTime;
        this.totalRepositories = totalRepositories;
        this.totalUsers = totalUsers;
        this.userAccess = userAccess;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Instant getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Instant generatedTime) {
        this.generatedTime = generatedTime;
    }

    public int getTotalRepositories() {
        return totalRepositories;
    }

    public void setTotalRepositories(int totalRepositories) {
        this.totalRepositories = totalRepositories;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Map<String, List<RepoAccess>> getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(Map<String, List<RepoAccess>> userAccess) {
        this.userAccess = userAccess;
    }

    public static class RepoAccess {

        private String repoName;
        private String fullName;
        private String url;
        private boolean isPrivate;
        private String role;

        public RepoAccess() {
        }

        public RepoAccess(String repoName, String fullName, String url, boolean isPrivate, String role) {
            this.repoName = repoName;
            this.fullName = fullName;
            this.url = url;
            this.isPrivate = isPrivate;
            this.role = role;
        }

        public String getRepoName() {
            return repoName;
        }

        public void setRepoName(String repoName) {
            this.repoName = repoName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isPrivate() {
            return isPrivate;
        }

        public void setPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}