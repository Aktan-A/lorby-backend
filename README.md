# lorby-backend

## Project Setup
```bash
git clone git@github.com:Aktan-A/lorby-backend.git
./mvnw clean spring-boot:run
```

## Running Docker-Compose
```bash
docker compose -f docker-compose.dev.yml up --build -d
```

Technologies
- Spring
- Spring Boot
- Spring MVC
- PostgreSQL
- Lombok
- JWT
- Docker
- Java Mail Sender

## Environment Variables
| Key                     | Description                             |
|-------------------------|-----------------------------------------|
| DB_URL                  | Database url.                           |
| DB_USERNAME             | Database user username.                 |
| DB_PASSWORD             | Database user password.                 |
| ACCESS_TOKEN_SECRET_KEY | Secret for access tokens.               |
| BASE_URL                | Base url of the application.            |
| EMAIL_USERNAME          | Email address of the mail account.      |
| EMAIL_PASSWORD          | Email app password of the mail account. |