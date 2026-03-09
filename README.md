# GitHub Access Report Service

A Spring Boot REST API that generates a report showing **which users have access to which repositories** in a GitHub organization.

---

## Features

- ✅ Authenticates with GitHub using a Personal Access Token
- ✅ Fetches all repositories in an organization (with pagination)
- ✅ Fetches all collaborators per repo (with pagination)
- ✅ Processes repos **in parallel** (10 concurrent requests) for efficiency at scale
- ✅ **Caches** results for 10 minutes to avoid redundant API calls
- ✅ Supports orgs with **100+ repos** and **1000+ users**
- ✅ Returns structured JSON reports

---

## Requirements

- Java 17+
- Maven 3.8+
- A GitHub Personal Access Token (see below)

---

## How to Configure Authentication

### Step 1: Generate a GitHub Personal Access Token

1. Go to [GitHub Settings → Developer Settings → Personal Access Tokens → Fine-grained tokens](https://github.com/settings/tokens?type=beta)
2. Click **"Generate new token"**
3. Select your target organization under "Resource owner"
4. Grant the following permissions:
   - **Repository permissions**: `Metadata` → Read-only
   - **Organization permissions**: `Members` → Read-only
5. Copy the token

### Step 2: Set the Token as an Environment Variable

```bash
export GITHUB_TOKEN=github_pat_your_token_here
```

> ⚠️ **Never** hardcode your token in `application.properties` or commit it to Git.

---

## How to Run the Project

### Option 1: Maven (Development)

```bash
# Clone the repo
git clone https://github.com/your-username/github-access-report.git
cd github-access-report

# Set your token
export GITHUB_TOKEN=your_token_here

# Run
./mvnw spring-boot:run
```

The server starts at **http://localhost:8080**

### Option 2: Build and Run JAR

```bash
./mvnw clean package -DskipTests
java -jar target/github-access-report-1.0.0.jar
```

---

## How to Call the API Endpoints

### 1. Full Access Report

Returns all users and their repository access across the organization.

```
GET /api/access-report?org={orgName}
```

**Example:**
```bash
curl "http://localhost:8080/api/access-report?org=google"
```

**Response:**
```json
{
  "organization": "google",
  "generatedAt": "2024-01-15T10:30:00Z",
  "totalRepositories": 120,
  "totalUsers": 450,
  "userAccessMap": {
    "john_doe": [
      {
        "repoName": "repo-alpha",
        "repoFullName": "google/repo-alpha",
        "repoUrl": "https://github.com/google/repo-alpha",
        "private": false,
        "role": "admin"
      }
    ],
    "jane_smith": [
      {
        "repoName": "repo-beta",
        "repoFullName": "google/repo-beta",
        "repoUrl": "https://github.com/google/repo-beta",
        "private": true,
        "role": "write"
      }
    ]
  }
}
```

---

### 2. Single User Access

Returns the list of repos a specific user has access to.

```
GET /api/access-report/user?org={orgName}&username={username}
```

**Example:**
```bash
curl "http://localhost:8080/api/access-report/user?org=google&username=john_doe"
```

**Response:**
```json
{
  "organization": "google",
  "username": "john_doe",
  "totalRepos": 3,
  "repositories": [
    { "repoName": "repo-alpha", "role": "admin", "private": false }
  ]
}
```

---

### 3. Health Check

```bash
curl "http://localhost:8080/api/health"
# {"status":"UP"}
```

---

## Running Tests

```bash
./mvnw test
```

---

## Design Decisions

### Why WebClient + Flux for parallel requests?
GitHub API rate limits are 5,000 requests/hour for authenticated users. With 100 repos, we'd need 100+ API calls just for collaborators. Using reactive parallel streams (10 concurrent requests via `flatMap(..., concurrency=10)`) means the report generates ~10x faster while staying within rate limits.

### Why Caffeine Cache?
If the same org is queried multiple times, we avoid redundant API calls. Cache expires after 10 minutes, balancing freshness vs. performance.

### Why `affiliation=all` for collaborators?
GitHub repos can have direct collaborators, team members, and org members. Using `affiliation=all` ensures we capture everyone.

### Pagination
GitHub returns max 100 items per page. The service automatically loops through all pages until the results are exhausted.

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
