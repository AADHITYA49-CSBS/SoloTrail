# SoloTrail
AI-powered solo travel companion platform for Indian travelers 

## Project structure

- `frontend/` - Angular standalone frontend
- `backend/` - Spring Boot REST API
- `docker-compose.yml` - MySQL, backend, and frontend stack

Start the complete local stack from the repository root:

```bash
docker compose up -d --build
```

The frontend is available at `http://localhost:8081` when `APP_PORT=8081` is configured.
