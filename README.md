# feed
Personal information aggregation and notification tool. Pulls articles/events/data from various sources and compiles it to a easy to read feed.

# Deployment
This section explains how to deploy this project to your server. This guide is not yet complete.

## Database migration
[Source](https://www.baeldung.com/database-migrations-with-flyway)
```
mvn clean flyway:migrate -Dflyway.configFile=flyway.local.properties
```
