# GitHub Data Aggregator

## Overview

A recently signed customer wants to integrate a subset of GitHub’s data into their application. We have discussed their
needs and they want an endpoint they can provide a username that will then return the data in JSON format as specified
below (that also serves as an example):

```json
{
  "user_name": "octocat",
  "display_name": "The Octocat",
  "avatar": "https://avatars.githubusercontent.com/u/583231?v=4",
  "geo_location": "San Francisco",
  "email": null,
  "url": "https://api.github.com/users/octocat",
  "created_at": "Tue, 25 Jan 2011 18:44:36 GMT",
  "repos": [
    {
      "name": "boysenberry-repo-1",
      "url": "https://api.github.com/repos/octocat/boysenberry-repo-1"
    }
  ]
}
```

The response above is produced by calling the username against GitHub's user and repos APIs, then merging the results.

## Data sources

- Getting started: https://docs.github.com/en/rest/guides/getting-started-with-the-rest-api
- GitHub API
    - Retrieve User: https://api.github.com/users/octocat
    - Retrieve User Repos: https://api.github.com/users/octocat/repos

## Tech stack

- Java 21
- Spring Boot
- Maven

## Scripts

### Install Maven

```bash
./mvnw clean install
```

### Run Application

```bash
./mvnw spring-boot:run
```

### Run Tests

```bash
./mvnw test
```