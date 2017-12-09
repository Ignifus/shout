# Shout

## Overview
Social network upvote Twitter with embedded Glassfish 5, JEE 8, MySQL or PGSQL database, User Authentication, Bean Validation and WebSocket feed.

## Running
* Create shout database.
* Customize database connection in `src/main/webapp/WEB-INF/glassfish-resources.xml`.
* Run `mvn clean package embedded-glassfish:run`.