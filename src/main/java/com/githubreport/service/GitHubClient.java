package com.githubreport.service;

import com.githubreport.model.GitHubRepo;
import com.githubreport.model.RepoCollaborator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);
    private static final int PAGE_SIZE = 100;

    private final WebClient client;

    public GitHubClient(@Value("${github.token:}") String token) {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs((ClientCodecConfigurer configurer) ->
                        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
                .build();

        WebClient.Builder builder = WebClient.builder()
                .baseUrl("https://api.github.com")
                .exchangeStrategies(strategies)
                .defaultHeader("Accept", "application/vnd.github+json");

        if (token != null && !token.isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + token);
        }

        this.client = builder.build();
    }

    @Cacheable(value = "orgRepos", key = "#org")
    public List<GitHubRepo> getAllReposForOrg(String org) {

        List<GitHubRepo> repos = new ArrayList<>();
        int page = 1;

        while (true) {

            List<GitHubRepo> result = fetchReposPage(org, page).block();

            log.info("Page {} repos: {}", page, result == null ? "null" : result.size());

            if (result == null || result.isEmpty()) {
                break;
            }

            repos.addAll(result);

            if (result.size() < PAGE_SIZE) {
                break;
            }

            page++;
        }

        log.info("Repos fetched: {}", repos.size());

        return repos;
    }

    @Cacheable(value = "repoCollaborators", key = "#org + '/' + #repoName")
    public List<RepoCollaborator> getAllCollaboratorsForRepo(String org, String repoName) {

        List<RepoCollaborator> collaborators = new ArrayList<>();
        int page = 1;

        while (true) {

            List<RepoCollaborator> result = fetchCollaboratorsPage(org, repoName, page).block();

            if (result == null || result.isEmpty()) {
                break;
            }

            collaborators.addAll(result);

            if (result.size() < PAGE_SIZE) {
                break;
            }

            page++;
        }

        return collaborators;
    }

    private Mono<List<GitHubRepo>> fetchReposPage(String org, int page) {

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orgs/{org}/repos")
                        .queryParam("per_page", PAGE_SIZE)
                        .queryParam("page", page)
                        .queryParam("type", "all")
                        .build(org))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GitHubRepo>>() {})
                .doOnNext(repos ->
                        log.info("Fetched {} repos from page {}", repos.size(), page))
                .doOnError(error ->
                        log.error("Error fetching repos for org {} page {}", org, page, error))
                .onErrorReturn(new ArrayList<>());
    }

    private Mono<List<RepoCollaborator>> fetchCollaboratorsPage(String org, String repoName, int page) {

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{org}/{repo}/collaborators")
                        .queryParam("per_page", PAGE_SIZE)
                        .queryParam("page", page)
                        .queryParam("affiliation", "all")
                        .build(org, repoName))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RepoCollaborator>>() {})
                .doOnError(error ->
                        log.error("Error fetching collaborators for {}/{} page {}", org, repoName, page, error))
                .onErrorReturn(new ArrayList<>());
    }
}