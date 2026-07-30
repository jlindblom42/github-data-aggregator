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

## Decision Log

Architecture is fairly cut-and-dry Spring MVC with one nuance, I separated out "core" from "githubapi" to better
separate the dto's primarily. Maybe overkill right now, but has maintenance benefits long-term. Alternative would have
been to separate from within the component packages themselves (e.g. `dto/githubapi` and `dto/core`.

I introduced Maven "wrapper" artifacts (via `mvn wrapper:wrapper`) to allow for streamlined install/run on boxes that
don't necessarily have maven already installed. Facilitates the unit testing github action as well.

I added caching to `GitHubApiClient` methods instead of the `UserService` to account for future usage scenarios.  
Again, might be overkill, so I've also added a `// TODO` to consider moving the cache to the `UserService`
if those scenarios don't/won't surface. The cost there is negligible since it's just data mapping/massaging, so the risk
is low to keep it more granular.

I encapsulated global JSON conventions in JacksonConfig (snake casing, ignore unknown). Maybe casing will change later,
but from all the materials I was seeing it seemed safe to establish.  "Ignore unknown" I applied out of habit, I do not
understand why it is not the default TBH, I have never encountered a situation where I want runtime exceptions to occur
due to a new field added to a third-party API that I have no control over.

I used Java `Record` objects for the dto's to take advantage of automatically generated boilerplate code (toString,
equals, hashCode, accessor methods) as opposed to leaning on Lombok's `@Data` and related annotations.

I've delegated stack trace logging to the `GlobalExceptionHandler` so as not to create noise from duplicated stack
traces in the logs.

I've standardized on a JSON error message response for uncaught exceptions via `GlobalExceptionHandler`, but needs
validation/confirmation from the customer as to what they're expecting for unhappy path scenarios.

I've deliberately omitted retryable considerations out of mindfulness for rate limiting. May want to consider retrying
for 502, 503, 504. GitHub hasn't had a great track record for outages lately so this may rise in priority if it becomes
a frequent enough issue.
