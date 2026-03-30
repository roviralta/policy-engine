# Policy Engine API

A Rule-Based Decision System built with Spring Boot that evaluates feature access policies based on user attributes, subscription plans, regions, and custom rules.

## Overview

This API provides a flexible policy engine for making decisions about feature access, entitlements, and user permissions. It supports dynamic rule configuration, batch evaluations, what-if analysis, and comprehensive audit logging.

### Key Features

- **Dynamic Rule Engine**: Create, update, and manage business rules with conditions
- **Batch Evaluation**: Evaluate multiple features in a single request
- **What-If Analysis**: Simulate decisions with detailed rule evaluation traces
- **Decision Audit**: Full decision history with timestamps and reasoning
- **Extensible Operators**: EQ, NEQ, GT, LT, IN, BETWEEN, CONTAINS, and more
- **JWT Authentication**: Secure API with token-based auth

## Tech Stack

- Java 17
- Spring Boot 4.0
- Spring Security (JWT)
- SpringDoc OpenAPI (Swagger)
- Lombok
- Maven

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+

### Run the Application

```bash
# Clone the repository
git clone <repository-url>
cd policy-engine

# Build
./mvnw clean package

# Run
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Secret key for JWT signing | (dev default) |
| `JWT_EXPIRATION` | Token expiration in ms | 86400000 (24h) |

### API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Policy Evaluation

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/policy/check` | Check single feature access |
| POST | `/api/policy/simulate` | What-if analysis with rule details |
| POST | `/api/policy/batch` | Batch evaluate multiple features |

### Rule Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/rules` | List all rules |
| GET | `/api/rules/active` | List enabled rules |
| GET | `/api/rules/{id}` | Get rule by ID |
| POST | `/api/rules` | Create new rule |
| PUT | `/api/rules/{id}` | Update rule |
| DELETE | `/api/rules/{id}` | Delete rule |
| PATCH | `/api/rules/{id}/toggle` | Toggle rule enabled/disabled |

### Decision Audit

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/decisions` | List all audits (paginated) |
| GET | `/api/decisions/user/{userId}` | Get audits by user |
| GET | `/api/decisions/feature/{featureName}` | Get audits by feature |
| GET | `/api/decisions/range` | Get audits by date range |
| GET | `/api/decisions/stats` | Get decision statistics |

## Example Requests

### Register & Login

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'
```

### Check Policy

```bash
curl -X POST http://localhost:8080/api/policy/check \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "plan": "PRO",
    "region": "EU",
    "featureName": "ADVANCED_EXPORT"
  }'
```

### Simulate Policy (What-If)

```bash
curl -X POST http://localhost:8080/api/policy/simulate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "user": {
      "id": 1,
      "username": "johndoe",
      "plan": "PRO",
      "region": "EU",
      "email": "john@example.com",
      "country": "Germany",
      "verified": true,
      "age": 30,
      "roles": ["USER"]
    },
    "featureName": "ADVANCED_EXPORT"
  }'
```

### Create Rule

```bash
curl -X POST http://localhost:8080/api/rules \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium EU Users Access",
    "description": "Allow PRO plan users in EU",
    "conditions": [
      {"field": "user.plan", "operator": "IN", "value": ["PRO", "ENTERPRISE"]},
      {"field": "user.region", "operator": "EQ", "value": "EU"}
    ],
    "logicalOperator": "AND",
    "action": "ALLOW",
    "priority": 1,
    "enabled": true
  }'
```

## Condition Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `EQ` | Equals | `"field": "user.plan", "operator": "EQ", "value": "PRO"` |
| `NEQ` | Not equals | `"operator": "NEQ", "value": "FREE"` |
| `GT` | Greater than | `"operator": "GT", "value": 17` |
| `GTE` | Greater or equal | `"operator": "GTE", "value": 18` |
| `LT` | Less than | `"operator": "LT", "value": 65` |
| `LTE` | Less or equal | `"operator": "LTE", "value": 65` |
| `IN` | In list | `"operator": "IN", "value": ["PRO", "ENTERPRISE"]` |
| `NOT_IN` | Not in list | `"operator": "NOT_IN", "value": ["CN", "RU"]` |
| `BETWEEN` | Between range | `"operator": "BETWEEN", "value": 18, "secondValue": 65` |
| `CONTAINS` | Contains substring | `"operator": "CONTAINS", "value": "@company.com"` |
| `STARTS_WITH` | Starts with | `"operator": "STARTS_WITH", "value": "admin"` |
| `ENDS_WITH` | Ends with | `"operator": "ENDS_WITH", "value": ".gov"` |

## Field Names

### User Fields
- `user.id` - User ID
- `user.plan` - Subscription plan (FREE, PRO, ENTERPRISE)
- `user.region` - Region code (EU, US, ASIA)
- `user.country` - Country name
- `user.email` - Email address
- `user.verified` - Verification status (true/false)
- `user.age` - User age
- `user.roles` - User roles list

### Feature Fields
- `feature.name` - Feature name
- `feature.requiredPlan` - Required plan to access
- `feature.enabled` - Feature enabled status

### Custom Fields
Any field from the `context` object in the request.

## Available Features

| Feature | Required Plan | Regions |
|---------|---------------|---------|
| `BASIC_DASHBOARD` | FREE | EU, US, ASIA |
| `ADVANCED_EXPORT` | PRO | EU, US |
| `PRIORITY_SUPPORT` | PRO | US |

## Architecture

```
src/main/java/com/rovi/policy_engine/
├── config/           # Security, app configuration
├── controller/       # REST endpoints
├── dto/              # Request/Response DTOs
├── exception/        # Custom exceptions
├── model/            # Domain models
├── policy/           # Policy implementations
├── repository/       # Data access (in-memory)
├── security/         # JWT, auth filters
└── service/          # Business logic
```

## Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## Project Roadmap

### Phase 1: Infrastructure
- [ ] README.md
- [ ] Docker + docker-compose.yml
- [ ] GitHub Actions CI/CD
- [ ] .env.example file

### Phase 2: Database
- [ ] PostgreSQL integration
- [ ] Spring Data JPA repositories
- [ ] Flyway database migrations
- [ ] Database connection pooling (HikariCP)

### Phase 3: Missing Features
- [ ] Feature Management API (CRUD)
- [ ] User Management API (CRUD)
- [ ] Role-based access control (ADMIN, USER)
- [ ] Export audits (CSV/JSON)
- [ ] Token refresh mechanism

### Phase 4: Production Polish
- [ ] Rate limiting (Bucket4j)
- [ ] Redis caching for rules
- [ ] Prometheus/Micrometer metrics
- [ ] API versioning (/api/v1/)
- [ ] Health check endpoints
- [ ] Request validation improvements

### Phase 5: Advanced Features
- [ ] Rule chaining (output feeds next rule)
- [ ] A/B testing support
- [ ] Webhook notifications on decisions
- [ ] Decision explainability API
- [ ] ML-based rule suggestions

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./mvnw test`
5. Submit a pull request

## License

MIT License
