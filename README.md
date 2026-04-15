# Event Sourcing Workshop - Java

## Prerequisites

- Java 25 (Use Google to find out how to install a version of Java 25 on your operating system)
- [Gradle](https://docs.gradle.org/current/userguide/installation.html)
- [Docker](https://docs.docker.com/get-docker/)

## Running the application

1. Run `docker compose up` to start the KurrentDB instance and the frontend application.
2. Run `./gradlew classes --continuous` to watch for changes and rebuild the application automatically.
3. Open another terminal and run `./gradlew bootRun` to start the application.

## Running frontend outside of Docker

If you want to run the frontend locally outside of Docker, you can clone the repository: https://github.com/TechedLemur/es-workshop-frontend and follow the instructions in the README file.

## Tasks

The workshop tasks are located in the [docs](docs) directory and split in multiple parts. Start with [part 1](docs/part-1.md) and follow the instructions in the files.
