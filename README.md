# Shout

## Overview
Social network upvote Twitter with embedded Glassfish 5, JEE 8, MySQL or PGSQL database, User Authentication, Bean Validation and WebSocket feed.

## Running
* Clone this repository.
* Run create_database.sql file in the database as admin.
* Customize database connection in `src/main/webapp/WEB-INF/glassfish-resources.xml` if neccessary.
* Run `mvn clean package embedded-glassfish:run` in the root folder.
