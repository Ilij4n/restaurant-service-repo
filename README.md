# Restaurant Reservation System

A restaurant reservation system built as Java 17 / Spring Boot microservices (Maven multi-module).

## Architecture

| Module | Port | Role |
|---|---|---|
| `microservices/api-gateway/eureka-server` | 8761 | Eureka service registry |
| `microservices/api-gateway/api-gateway` | 8080 | Spring Cloud Gateway: routes `/users/**`, `/reservations/**`, `/notifications/**` to the services through Eureka (`lb://`), with CORS configuration |
| `microservices/user-service` | 8081 | Client and manager registration, login, email verification, password reset, profile, ban, filtered user search |
| `microservices/reservation-service` | 8082 | Restaurants, time slots, reservations, loyalty perks, filtering with JPA Specifications |
| `microservices/notification-service` | 8083 | Consumes notification messages from RabbitMQ and sends email through `JavaMailSender`; keeps templates and notification history |
| `security-lib/security` | - | Shared library: `@CheckSecurity` annotation plus an AOP aspect that validates the Bearer JWT and the user's role |

Notable details:

- **Messaging:** user-service and reservation-service publish notification requests to a RabbitMQ topic exchange (`notification.exchange`, JSON message converter). notification-service listens on a durable queue bound to it and sends the email.
- **Reminders:** a `@Scheduled` job in reservation-service runs every 30 minutes and sends a reminder for reservations starting within the next 24 hours (a `notificationSent` flag prevents duplicates); it also marks finished reservations as completed.
- **Security:** JWT authentication; endpoint access is controlled with `@CheckSecurity` from the shared security library.
- **Persistence:** every service uses an in-memory H2 database (`create-drop`), seeded on startup, so data is lost on restart.

## Running it

Requirements: JDK 17, Maven (wrappers are included), and a RabbitMQ broker on `localhost:5672` (default `guest`/`guest`), for example:

```
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Email credentials are read from environment variables (SMTP defaults to `smtp.gmail.com:587`, so an app password is required for Gmail). Without them the services still start, but emails are not delivered:

```
export MAIL_USERNAME=your-smtp-user
export MAIL_PASSWORD=your-smtp-app-password
```

Notification recipients in the user and reservation services are currently hardcoded placeholders (`example@example.com`) so that the project never emails real people; change them to the real user or manager email to receive messages.

Start the modules (each in its own terminal), Eureka server first:

```
mvn -pl microservices/api-gateway/eureka-server spring-boot:run
mvn -pl microservices/user-service spring-boot:run
mvn -pl microservices/reservation-service spring-boot:run
mvn -pl microservices/notification-service spring-boot:run
mvn -pl microservices/api-gateway/api-gateway spring-boot:run
```

The API is then available through the gateway on `http://localhost:8080`. The JWT secret in each service's `application.properties` is a development placeholder.

## Credits

Built with Nevena, who implemented the API gateway and its security/CORS configuration.
