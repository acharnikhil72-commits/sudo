# JWT Authentication

The application uses Spring Boot 3, Spring Security 6, JPA, MySQL, and JJWT 0.12.6. Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` in the environment before running in a deployed environment. `JWT_SECRET` must contain at least 32 UTF-8 bytes; the default in `application.properties` is only for local development. `JWT_EXPIRATION_MS` defaults to one hour.

Registration creates a `ROLE_USER` account and stores only a BCrypt password hash. Login returns an HS256-signed access token. The profile endpoint reloads the user and authorities from the database for each authenticated request.

```sh
curl -i -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","email":"alice@example.com","password":"correct-horse-battery"}'

curl -i -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"correct-horse-battery"}'

curl -i http://localhost:8080/api/user/profile \
  -H 'Authorization: Bearer <token-from-login>'
```

Invalid or missing credentials receive HTTP 401, insufficient permissions receive HTTP 403, and duplicate registrations receive HTTP 409. Invalid request bodies receive HTTP 400.