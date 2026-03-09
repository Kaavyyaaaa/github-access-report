# GitHub Access Report API

This project was developed as part of a **Backend Engineering Assignment for Cloudeagle**.

The goal of the assignment is to build a service that connects to the GitHub API and generates a report showing **which users have access to which repositories within a GitHub organization**.

The application authenticates with GitHub, retrieves repositories and collaborators, and exposes a REST API endpoint that returns an aggregated access report.

---

# Problem Statement

Organizations often need visibility into **who has access to which repositories** in GitHub.

This service connects to the GitHub API and generates an access report for a given organization by:

* Fetching all repositories in the organization
* Identifying users who have access to each repository
* Aggregating the information into a structured report

The report is exposed through a REST API endpoint in JSON format.

---

# Features

* GitHub API integration using **Spring WebClient**
* Secure authentication using **GitHub Personal Access Token**
* Retrieves repositories from a GitHub organization
* Fetches collaborators for each repository
* Aggregates data into a **user → repository access mapping**
* Exposes REST API endpoint for generating reports
* Pagination support for large organizations
* Clean layered architecture (Controller → Service → Client)

---

# Tech Stack

* **Java 17**
* **Spring Boot**
* **Spring WebFlux (WebClient)**
* **Maven**
* **GitHub REST API**

---

# Project Structure

```
src/main/java/com/githubreport

controller
    AccessReportController.java

service
    AccessReportService.java
    GitHubClient.java

model
    GitHubRepo.java
    RepoCollaborator.java

GitHubAccessReportApplication.java
```

---

# How to Run the Project

### 1. Clone the repository

```
git clone https://github.com/YOUR_USERNAME/github-access-report.git
```

### 2. Navigate to the project directory

```
cd github-access-report
```

### 3. Configure GitHub Authentication

Generate a **GitHub Personal Access Token**.

Add the token in:

```
src/main/resources/application.properties
```

```
github.token=YOUR_GITHUB_TOKEN
```

This token is used to authenticate requests to the GitHub API.

---

### 4. Run the application

```
mvn spring-boot:run
```

The server will start at:

```
http://localhost:8080
```

---

# API Endpoint

### Generate Access Report

```
GET /api/access-report?org={organization}
```

Example request:

```
http://localhost:8080/api/access-report?org=spring-projects
```

---

# Example Response

```json
{
  "organization": "spring-projects",
  "generatedTime": "2026-03-09T10:30:00Z",
  "totalRepositories": 5,
  "totalUsers": 3,
  "userAccess": {
    "user1": ["repo1", "repo2"],
    "user2": ["repo3"]
  }
}
```

---

# Scalability Considerations

The implementation is designed to support organizations with:

* **100+ repositories**
* **1000+ users with repository access**

Pagination is used when fetching repositories and collaborators from the GitHub API to support large organizations.

---

# Design Decisions

* **Spring WebClient** was used instead of RestTemplate for efficient non-blocking HTTP requests.
* **Pagination** was implemented to handle organizations with many repositories.
* **Service layer abstraction** separates GitHub API communication from business logic.
* **Caching** is used to avoid repeated API calls where possible.

---

# Assumptions

* Collaborators returned by the GitHub API represent users who have repository access.
* Repository access is determined based on GitHub's collaborator data.
* The application assumes the provided GitHub token has sufficient permissions to read organization repositories.

---

# Possible Improvements

* Handle GitHub API rate limiting
* Add retry logic for API failures
* Improve parallel API calls for better scalability
* Add integration tests
* Containerize the application using Docker

---


## Project Structure

```
src/
├── main/java/com/githubreport/
│   ├── GitHubAccessReportApplication.java   # Entry point
│   ├── config/
│   │   └── CacheConfig.java                 # Caffeine cache setup
│   ├── controller/
│   │   └── AccessReportController.java      # REST endpoints
│   ├── model/
│   │   ├── AccessReport.java                # Response DTO
│   │   ├── GitHubCollaborator.java          # GitHub API model
│   │   └── GitHubRepo.java                  # GitHub API model
│   └── service/
│       ├── GitHubApiClient.java             # Low-level GitHub API calls
│       └── AccessReportService.java         # Business logic + aggregation
└── test/
    └── AccessReportServiceTest.java         # Unit tests
```
