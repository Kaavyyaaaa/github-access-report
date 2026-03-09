package com.githubreport.service;

import com.githubreport.model.AccessReport;
import com.githubreport.model.RepoCollaborator;
import com.githubreport.model.GitHubRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccessReportService {

    private static final Logger log = LoggerFactory.getLogger(AccessReportService.class);
    private static final int CONCURRENCY = 10;

    private final GitHubClient gitHubClient;

    public AccessReportService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public AccessReport generateReport(String org) {

        log.info("Generating access report for org: {}", org);

        List<GitHubRepo> repos = gitHubClient.getAllReposForOrg(org);
        System.out.println("Repos fetched: " + repos.size());

        Map<String, List<AccessReport.RepoAccess>> accessMap = new ConcurrentHashMap<>();

        Flux.fromIterable(repos)
                .flatMap(repo -> fetchCollaboratorsForRepo(org, repo), CONCURRENCY)
                .doOnNext(entry -> {

                    entry.forEach((username, access) ->
                            accessMap
                                    .computeIfAbsent(username, k -> Collections.synchronizedList(new ArrayList<>()))
                                    .add(access)
                    );

                })
                .then()
                .block();

        accessMap.forEach((user, accessList) ->
                accessList.sort((a, b) -> a.getRepoName().compareTo(b.getRepoName()))
        );

        log.info("Report generated: {} repos, {} users", repos.size(), accessMap.size());

        AccessReport report = new AccessReport();
        report.setOrganization(org);
        report.setGeneratedTime(Instant.now());
        report.setTotalRepositories(repos.size());
        report.setTotalUsers(accessMap.size());
        report.setUserAccess(accessMap);

        return report;
    }

    private Mono<Map<String, AccessReport.RepoAccess>> fetchCollaboratorsForRepo(String org, GitHubRepo repo) {

        return Mono.fromCallable(() ->
                gitHubClient.getAllCollaboratorsForRepo(org, repo.getName())
        )
        .subscribeOn(Schedulers.boundedElastic())
        .map(collaborators -> {

            Map<String, AccessReport.RepoAccess> result = new HashMap<>();

            for (RepoCollaborator collaborator : collaborators) {

                AccessReport.RepoAccess access = new AccessReport.RepoAccess();
                access.setRepoName(repo.getName());
                access.setFullName(repo.getFullName());
                access.setUrl(repo.getUrl());
                access.setPrivate(repo.isPrivate());
                access.setRole(determineRole(collaborator));

                result.put(collaborator.getLogin(), access);
            }

            return result;
        })
        .onErrorReturn(Map.of());
    }

    private String determineRole(RepoCollaborator collaborator) {

        if (collaborator.getRoleName() != null) {
            return collaborator.getRoleName();
        }

        RepoCollaborator.Permissions perms = collaborator.getPermissions();

        if (perms == null) return "read";

        if (perms.isAdmin()) return "admin";
        if (perms.isMaintain()) return "maintain";
        if (perms.isPush()) return "write";
        if (perms.isTriage()) return "triage";

        return "read";
    }
}