# Java Multi-User Chat Application

A production-ready, multi-user Java chat application built with **Java 21**, **JavaFX**, and **Maven**. This project follows a clean, layered architecture and is designed for scalability and resilience.

## 🚀 Features

- **Multi-User Real-time Messaging**: Concurrent handling of multiple clients using a fixed thread pool.
- **JavaFX GUI**: Modern, minimalist interface with a clean three-column layout (Rooms, Chat Feed, User Roster).
- **Resilient Networking**:
    - **Heartbeat Mechanism**: Automatic detection and cleanup of "zombie" or unresponsive connections.
    - **Exponential Backoff Reconnection**: Client-side logic to automatically reconnect after network interruptions (1s to 32s backoff).
- **Layered Architecture**: Strict separation of concerns between Domain, Application, Presentation, and Infrastructure layers.
- **Self-Contained Client**: Configured for `jlink` to produce native, JDK-less runtimes.
- **Dockerized Server**: Production-ready server deployment with health checks and non-root security.

## 📂 Project Structure

- `shared/`: Core domain models (`Message`, `User`, `Room`), enums, and utility classes. No UI or network dependencies.
- `server/`: Headless TCP server logic, connection management, and business services.
- `client/`: JavaFX GUI, service layer for UI state, and network connection manager.
- `resources/`: Shared configuration (`chat.properties`), logging (`logback.xml`), and assets.

## 🛠️ Requirements

- **Java**: JDK 21+
- **Build Tool**: Maven 3.9+
- **Containerization**: Docker & Docker Compose (for Server)

## 🏃 Running the Application

### 1. Start the Server (Docker)
The server is containerized for easy deployment.
```bash
cd chat-app
docker compose up -d --build
```
*Port: 5000 (TCP)*

### 2. Run the Client (Maven)
Install the shared library first, then launch the GUI.
```bash
cd chat-app
mvn install -DskipTests
mvn javafx:run -pl client
```

## 📦 Packaging for Production

### Server
The server is already configured to build a fat JAR within the Docker container.
```bash
docker build -t chat-server ./server
```

### Client (jlink)
To create a self-contained runtime image (no JDK required on end-user machine):
```bash
# This generates the image in client/target/chat-client-runtime/
mvn javafx:jlink -pl client
```

## ⚙️ Configuration
Application settings can be tuned in `resources/chat.properties`:
- `server.port`: TCP port for connections.
- `server.pool.size`: Thread pool size for handling clients.
- `heartbeat.interval.seconds`: Frequency of client pings.
- `reconnect.max.attempts`: Number of retries before showing an error.

## 🧪 Testing
Run unit and integration tests across all modules:
```bash
mvn test
```
- `MessageRouterTest`: Validates broadcast logic.
- `ServerIntegrationTest`: Spins up an ephemeral server to test real socket communication.
