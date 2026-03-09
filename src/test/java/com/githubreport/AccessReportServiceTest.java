package com.githubreport;

import com.githubreport.model.AccessReport;
import com.githubreport.model.RepoCollaborator;
import com.githubreport.model.GitHubRepo;
import com.githubreport.service.AccessReportService;
import com.githubreport.service.GitHubClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessReportServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private AccessReportService accessReportService;

    private GitHubRepo repo1;
    private GitHubRepo repo2;
    private RepoCollaborator collaborator1;
    private RepoCollaborator collaborator2;

    @BeforeEach
    void setUp() {
        repo1 = new GitHubRepo();
        repo1.setId(1L);
        repo1.setName("repo-alpha");
        repo1.setFullName("test-org/repo-alpha");
        repo1.setUrl("https://github.com/test-org/repo-alpha");
        repo1.setPrivate(false);

        repo2 = new GitHubRepo();
        repo2.setId(2L);
        repo2.setName("repo-beta");
        repo2.setFullName("test-org/repo-beta");
        repo2.setUrl("https://github.com/test-org/repo-beta");
        repo2.setPrivate(true);

        collaborator1 = new RepoCollaborator();
        collaborator1.setId(101L);
        collaborator1.setLogin("alice");
        collaborator1.setRoleName("admin");

        collaborator2 = new RepoCollaborator();
        collaborator2.setId(102L);
        collaborator2.setLogin("bob");
        collaborator2.setRoleName("write");
    }

    @Test
    void generateReport_shouldReturnCorrectUserAccessMap() {
        when(gitHubClient.getAllReposForOrg("test-org"))
                .thenReturn(List.of(repo1, repo2));

        when(gitHubClient.getAllCollaboratorsForRepo("test-org", "repo-alpha"))
                .thenReturn(List.of(collaborator1, collaborator2));

        when(gitHubClient.getAllCollaboratorsForRepo("test-org", "repo-beta"))
                .thenReturn(List.of(collaborator1));

        AccessReport report = accessReportService.generateReport("test-org");

        assertThat(report).isNotNull();
        assertThat(report.getOrganization()).isEqualTo("test-org");
        assertThat(report.getTotalRepositories()).isEqualTo(2);
        assertThat(report.getTotalUsers()).isEqualTo(2);

        assertThat(report.getUserAccess().get("alice")).hasSize(2);

        assertThat(report.getUserAccess().get("bob")).hasSize(1);
        assertThat(report.getUserAccess().get("bob").get(0).getRepoName())
                .isEqualTo("repo-alpha");
    }

    @Test
    void generateReport_shouldHandleEmptyOrg() {
        when(gitHubClient.getAllReposForOrg("empty-org"))
                .thenReturn(List.of());

        AccessReport report = accessReportService.generateReport("empty-org");

        assertThat(report.getTotalRepositories()).isEqualTo(0);
        assertThat(report.getTotalUsers()).isEqualTo(0);
        assertThat(report.getUserAccess()).isEmpty();
    }

    @Test
    void generateReport_shouldHandleRepoWithNoCollaborators() {
        when(gitHubClient.getAllReposForOrg("test-org"))
                .thenReturn(List.of(repo1));

        when(gitHubClient.getAllCollaboratorsForRepo("test-org", "repo-alpha"))
                .thenReturn(List.of());

        AccessReport report = accessReportService.generateReport("test-org");

        assertThat(report.getTotalRepositories()).isEqualTo(1);
        assertThat(report.getTotalUsers()).isEqualTo(0);
    }
}