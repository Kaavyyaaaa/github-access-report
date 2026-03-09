package com.githubreport.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepo {

    private Long id;
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("private")
    private boolean privateRepo;

    @JsonProperty("html_url")
    private String url;

    @JsonProperty("description")
    private String repoDescription;

    public GitHubRepo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public boolean isPrivate() { return privateRepo; }
    public void setPrivate(boolean privateRepo) { this.privateRepo = privateRepo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getRepoDescription() { return repoDescription; }
    public void setRepoDescription(String repoDescription) { this.repoDescription = repoDescription; }
}