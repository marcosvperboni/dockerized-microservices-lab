# Dockerized Microservices Lab

A hands-on, production-style microservices playground built to demonstrate professional containerization: independent services, a relational database, a cache, and a message broker, all wired together with health checks, named volumes, an isolated network, secrets, and multi-stage builds — runnable with either **Podman** or **Docker**.

## Architecture

```
                    ┌─────────────────────┐
                    │   order-service      │  Java 25 / Spring Boot 4
        POST/GET/   │   (port 8081)        │
        PUT/DELETE  │                       │
      ─────────────▶│  REST API ─▶ Postgres │
                    │  GET /{id} ─▶ Redis   │  (cache)
                    │  create ─▶ RabbitMQ   │──── publishes "order.created" ────┐
                    └─────────────────────┘                                    │
                                                                                 ▼
                    ┌─────────────────────┐                    ┌─────────────────────┐
                    │ inventory-service     │                    │ notification-service │
                    │ (port 8082)           │◀── consumes ───────│ (port 8083)           │
                    │ Java 25 / Spring Boot 4│                   │ Python 3.13 / FastAPI │
                    │ REST API ─▶ Postgres  │                    │ consumes ─▶ Redis list │
                    └─────────────────────┘                    └─────────────────────┘
```

- **order-service** exposes full CRUD for orders, caches single-order reads in Redis, and publishes an `order.created` event to RabbitMQ whenever an order is placed.
- **inventory-service** exposes full CRUD for inventory items and consumes `order.created` events asynchronously to reserve stock for the ordered product.
- **notification-service** consumes the same `order.created` events, turns them into notification records stored in Redis, and exposes a read API over them.

Each Java service follows a lightweight DDD-inspired layering (`domain` / `application` / `infrastructure` / `api`) with the domain model free of framework annotations beyond JPA mapping, application services orchestrating use cases, and infrastructure adapters (repositories, messaging) kept behind interfaces. The Python service mirrors the same layering (`domain`, `application`, `infrastructure`, `api`).

## Tech stack

| Concern | Technology |
|---|---|
| Language / runtime | Java 25 (order-service, inventory-service), Python 3.13 (notification-service) |
| Framework | Spring Boot 4.0.8, FastAPI |
| Database | PostgreSQL 16 (one schema per service, Flyway migrations) |
| Cache | Redis 7 |
| Messaging | RabbitMQ 3.13 (topic exchange, durable queues) |
| Containers | Podman / Docker, multi-stage builds, non-root runtime users, health checks |
| Orchestration | Podman Compose / Docker Compose, secrets, named volumes, dedicated bridge network |
| CI | GitHub Actions (build + unit tests for all three services, then a compose build) |
| Testing | JUnit 5 + Mockito + MockMvc (Java), Pytest + FastAPI TestClient (Python) |

## Project layout

```
dockerized-microservices-lab/
├── order-service/          Spring Boot 4 service (Maven, Java 25)
├── inventory-service/      Spring Boot 4 service (Maven, Java 25)
├── notification-service/   FastAPI service (Python 3.13)
├── scripts/                start.sh / stop.sh, postgres-init scripts
├── secrets/                *.example templates (real secrets are git-ignored)
├── .github/workflows/      CI pipeline
└── docker-compose.yml      Full stack definition
```

## Running the stack

Works identically with **Podman** (`podman compose`, requires `podman machine start` on Windows/macOS) or **Docker** (`docker compose`). The helper scripts auto-detect whichever engine is on your `PATH`.

```bash
# generates local secret files (git-ignored) and starts everything, building images as needed
./scripts/start.sh

# tears everything down, including volumes
./scripts/stop.sh
```

Equivalent manual commands:

```bash
# Podman
podman machine start          # first time only
podman compose up --build -d
podman compose down --volumes

# Docker
docker compose up --build -d
docker compose down --volumes
```

### Ports

Non-default host ports are used throughout to avoid clashing with other local services. Override any of them via a `.env` file (see `.env.example`).

| Service | Host port | Notes |
|---|---|---|
| order-service | 18100 | REST API |
| inventory-service | 18101 | REST API |
| notification-service | 18102 | REST API |
| PostgreSQL | 15495 | one instance, two databases: `orders_db`, `inventory_db` |
| Redis | 16395 | cache (order-service) + notification store (notification-service) |
| RabbitMQ AMQP | 15695 | broker protocol |
| RabbitMQ management UI | 15696 | http://localhost:15696 |

## API reference

### order-service — `http://localhost:18100`

| Method | Path | Description |
|---|---|---|
| POST | `/api/orders` | Create an order, publishes `order.created` |
| GET | `/api/orders` | List all orders |
| GET | `/api/orders/{id}` | Get one order (Redis-cached) |
| PUT | `/api/orders/{id}` | Update an order |
| DELETE | `/api/orders/{id}` | Delete an order |

```bash
curl -X POST http://localhost:18100/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Alice","productName":"Keyboard","quantity":2}'
```

### inventory-service — `http://localhost:18101`

| Method | Path | Description |
|---|---|---|
| POST | `/api/inventory` | Create an inventory item |
| GET | `/api/inventory` | List all inventory items |
| GET | `/api/inventory/{id}` | Get one inventory item |
| PUT | `/api/inventory/{id}` | Update an inventory item |
| DELETE | `/api/inventory/{id}` | Delete an inventory item |

### notification-service — `http://localhost:18102`

| Method | Path | Description |
|---|---|---|
| GET | `/api/notifications` | List all notifications generated from `order.created` events |
| GET | `/api/notifications/{id}` | Get one notification |
| GET | `/health` | Liveness check |

Health endpoints for the Java services follow Spring Boot Actuator conventions: `GET /actuator/health`.

## Testing

```bash
# order-service / inventory-service
cd order-service && ./mvnw test
cd inventory-service && ./mvnw test

# notification-service
cd notification-service
python -m venv .venv && ./.venv/Scripts/pip install -r requirements-dev.txt   # Windows
pytest -q
```

All three services ship unit tests (service-layer logic with mocked repositories) and slice tests (MockMvc for the Java controllers, FastAPI `TestClient` for the Python API), covering the happy path, validation errors, and not-found cases for every CRUD endpoint.

## Secrets

Database and broker passwords are never baked into images or committed to git. `docker-compose.yml` declares them as Compose **secrets** backed by files under `secrets/` (git-ignored; only `*.example` templates are tracked). Each container reads its secret from `/run/secrets/...` through a small `docker-entrypoint.sh` that exports it as an environment variable before starting the JVM/uvicorn process — the same pattern used by the official Postgres and RabbitMQ images.

## CI/CD

`.github/workflows/ci.yml` runs on every push and pull request against `master`:

1. Build + test `order-service` (`mvnw verify`).
2. Build + test `inventory-service` (`mvnw verify`).
3. Install + test `notification-service` (`pytest`).
4. Build all three container images via `docker compose build` to catch Dockerfile regressions.
