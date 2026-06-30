# IMDG Server

The authoritative Java backend for the IMDG Chatbot system. This Spring Boot application owns structured maritime data and enforces planning rules for dangerous-goods stowage, vessel structure, container management, and vessel stability calculations.

## Domains

| Domain               | Description                                                                 |
|----------------------|-----------------------------------------------------------------------------|
| **IMDG**             | Dangerous-goods records, compatibility groups, segregation rules, codes, and hazard definitions. |
| **Vessel**           | Vessel profiles and structural data — bays, rows, cells.                    |
| **Container**        | Container records and synthetic container generation for testing.           |
| **Stowage Plan**     | Creates plans, snapshots vessel data into plans, exposes plan views.        |
| **Stowage Slot**     | Validates and manages container placement within a stowage plan.            |
| **Vessel Stability** | Computes CG, KG, and overall stability based on stowage-plan state.         |
| **Data Operations**  | Imports seed data and generates synthetic container data.                   |

## API Endpoints

All endpoints are prefixed with `/api/v1`.

| Controller                    | Base Path              | Description                              |
|-------------------------------|------------------------|------------------------------------------|
| `ContainerControllerV1`       | `/api/v1/containers`   | Container CRUD and queries               |
| `DataControllerV1`            | `/api/v1/data`         | Data import and container generation     |
| `IMDGControllerV1`            | `/api/v1/imdg`         | IMDG reference data queries              |
| `StabilityControllerV1`       | `/api/v1/stability`    | Vessel stability calculations            |
| `StowagePlanControllerV1`     | `/api/v1/stowage-plans`| Stowage plan CRUD and management         |
| `StowagePlanSlotControllerV1` | `/api/v1/stowage-slots`| Slot placement and validation            |
| `VesselControllerV1`          | `/api/v1/vessels`      | Vessel structure and profile queries     |

## How Tests Are Done

The test suite under `src/test/java` is integration-test oriented.

### Testing style

- Tests boot the full Spring application with `@SpringBootTest` using a random HTTP port.
- Each test class starts a real MongoDB instance through Testcontainers.
- The tests call HTTP APIs through `RestTestClient`, not by invoking controller methods directly.
- Assertions validate both HTTP responses and the persisted MongoDB state.

### Test data setup

- Test fixtures live under `src/test/resources/test-data`.
- The fixture set includes containers, vessel profiles, vessel-structure bays, stowage plans, and stowage-plan slots.
- Each integration test class copies the fixture directory into the MongoDB container and runs `mongoimport` inside the container.
- Some test classes reset mutable collections such as stowage-plan slots in `@BeforeEach` to preserve test isolation.

## Monitoring

Spring Boot Actuator is enabled with the following endpoints:

- `/actuator/health` — Liveness and readiness probes
- `/actuator/metrics` — Application metrics

## Project Structure

```
app/server/
├── build.gradle                  # Gradle build configuration
├── settings.gradle               # Gradle project settings
├── Dockerfile                    # Multi-stage Docker build
├── gradlew / gradlew.bat         # Gradle wrapper scripts
├── ARCHITECTURE_DIAGRAM.md       # Architecture and planning flow docs
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java          # Application entry point
│   │   │   ├── config/                       # Configuration classes
│   │   │   ├── controller/                   # REST controllers
│   │   │   │   ├── advice/                   # Exception handlers
│   │   │   │   └── api/                      # API v1 controllers
│   │   │   ├── entity/                       # MongoDB document entities
│   │   │   │   ├── container/
│   │   │   │   ├── imdg/
│   │   │   │   ├── stowage/
│   │   │   │   ├── vessel/
│   │   │   │   └── voyage/
│   │   │   ├── event/                        # Application events and listeners
│   │   │   ├── repository/                   # Spring Data MongoDB repositories
│   │   │   │   ├── bay/
│   │   │   │   ├── container/
│   │   │   │   ├── dg/
│   │   │   │   ├── imdg/
│   │   │   │   ├── stowage/
│   │   │   │   └── vessel/
│   │   │   └── service/                      # Business logic services
│   │   │       ├── domain/                   # Domain-specific services
│   │   │       ├── exception/                # Service-level exceptions
│   │   │       └── param/                    # Service parameter objects
│   │   └── resources/
│   │       └── application.yaml              # Application configuration
│   └── test/                                 # Test sources
└── gradle/wrapper/                           # Gradle wrapper files