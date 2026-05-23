# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy parent POM first for layer caching
COPY pom.xml .
COPY shared/pom.xml shared/
COPY server/pom.xml server/
COPY client/pom.xml client/

# Download dependencies first (cached layer if POMs unchanged)
RUN mvn dependency:go-offline -pl shared,server --no-transfer-progress

# Copy source
COPY shared/src shared/src
COPY server/src server/src
COPY resources/chat.properties server/src/main/resources/
COPY resources/logback.xml     server/src/main/resources/

# Build — skip tests in CI, run separately
RUN mvn clean package -pl shared,server -DskipTests --no-transfer-progress

# ── Stage 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Security: non-root user
RUN addgroup -S chatapp && adduser -S chatapp -G chatapp

WORKDIR /app

COPY --from=builder /build/server/target/chat-server.jar app.jar

# Config and logs directory
RUN mkdir -p /app/logs && chown -R chatapp:chatapp /app

USER chatapp

# Expose chat socket port (matches chat.properties server.port)
EXPOSE 5000

# Health check — verify the server port is accepting connections
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD nc -z localhost 5000 || exit 1

# JVM tuning for containers
ENTRYPOINT ["java", \
    "-Xms128m", \
    "-Xmx512m", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.util.logging.config.file=/app/logback.xml", \
    "-jar", "app.jar"]