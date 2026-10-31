# Weather Server

A simple HTTPS-based Java server for storing and managing weather observation data as part of the Programming 3 course assignment. The project is built on top of the course’s template Java project and extends it with user registration, authentication, and collection management features.

## Project Overview

The server exposes a small REST-like API over HTTPS and listens on port `8001`.
It uses a Java KeyStore (JKS) certificate to secure connections and requires the keystore path and password as command-line arguments when starting the server.

Key responsibilities:

- Accepting authenticated weather data records.
- Managing collections of observations.
- Handling user registration and login/authentication.
- Persisting data via a server-side database layer.

## Architecture

The code for the assignment lives under the `src/main/java/com/o3/server` package.

Main components:

- `Server` – Application entry point; configures HTTPS, SSL context, HTTP contexts, logging, and starts the server.
- `ServerHandler` – Handles `/datarecord` requests (creating and querying weather data records).
- `CollectionHandler` – Handles `/collections` requests (managing logical collections of observations).
- `RegistrationHandler` – Handles `/registration` requests (user registration and related flows).
- `UserAuthenticatorDB` – HTTP authenticator backed by the server database for user login.
- `ServerDatabase` – Database access layer (reading/writing users, collections, and observations).
- `User` and classes in `observations/` – Domain model for users and weather observations.

Logging is configured via Logback using the `LogbackConfig` helper.

## Features

The assignment specification refers to “Feature 5”, “Feature 6”, and “Feature 7”, which correspond to the following implemented capabilities:

- **Secure HTTPS server**
	- TLS-based HTTPS on port `8001` using a JKS keystore.
	- SSL parameters configured via `HttpsConfigurator` for each incoming connection.

- **User registration and authentication**
	- Endpoint for registering new users (`/registration`).
	- Database-backed user authenticator (`UserAuthenticatorDB`) attached to protected contexts.

- **Authenticated data and collection management**
	- `/datarecord` endpoint for submitting and retrieving observation data; protected by user authentication.
	- `/collections` endpoint for managing collections of observations; also protected by authentication.
	- Database layer to store users, collections, and observations (`ServerDatabase`).

## Technologies

- Java (standard library, `com.sun.net.httpserver` for HTTP/S).
- Maven for build and dependency management (`pom.xml`).
- Logback / SLF4J for logging.
- Java KeyStore (JKS), TLS, and standard `javax.net.ssl` APIs for HTTPS configuration.

## Prerequisites

To build and run the server you need:

- Java JDK.
- Maven (to build the project and resolve dependencies).
- A JKS keystore file containing a server certificate.
- The keystore password (used both to load the keystore and to initialise key/trust managers).

## Building

Clone the repository:

```bash
git clone [https://github.com/torwanat/weather-server.git](https://github.com/torwanat/weather-server.git)
cd weather-server
```

Build with Maven:

```bash
mvn clean package
```

This produces compiled classes and, depending on the `pom.xml` configuration, a runnable JAR in the `target/` directory.

## Running the Server

The server is started via the `Server` class’s `main` method and expects two command-line arguments: the path to the JKS keystore and its password.

Example (from an IDE):

- Main class: `com.o3.server.Server`
- Program arguments:
	1. `/path/to/keystore.jks`
	2. `yourKeystorePassword`

Example (from the command line, assuming a JAR was built):

```bash
java -jar target/weather-server.jar /path/to/keystore.jks yourKeystorePassword
```

Once started, the server logs messages such as “Starting server” and “Server started” and begins listening on port `8001`.

## API Endpoints

All endpoints are served over HTTPS and, except where noted, require a valid authenticated user via `UserAuthenticatorDB`.

### `POST /registration`

- Registers a new user.
- Request body and exact format depend on the assignment specification and `RegistrationHandler` implementation.
- Returns a status indicating whether registration succeeded or failed.

### `POST /datarecord`

- Submits a new weather observation record (e.g., temperature, humidity, location, timestamp).
- Uses `ServerHandler` to validate and persist the data.
- Requires authentication.

### `GET /datarecord`

- Retrieves existing observation records for the authenticated user or within a given collection.
- Query parameters and response format follow the assignment’s specification.

### `GET /collections` / `POST /collections`

- Lists and creates collections of observations using `CollectionHandler`.
- Requires authentication.
- Allows grouping related observations under named collections.

> Note: The exact JSON/XML formats, status codes, and query parameters are defined in the course assignment and the handler implementations. Consult the handler classes (`ServerHandler`, `CollectionHandler`, `RegistrationHandler`) for precise details.

## Logging and Error Handling

Logging is configured by calling `LogbackConfig.configure()` at startup.
The server logs informational messages for lifecycle events and detailed error messages for common problems, such as:

- Server already started (`IllegalStateException`).
- Invalid path or duplicate HTTP context (`IllegalArgumentException`).
- SSL configuration issues (`NullPointerException`).
- Port binding errors (`BindException`).
- Missing certificate file (`FileNotFoundException`).
- Unexpected runtime errors (generic `Exception`).
