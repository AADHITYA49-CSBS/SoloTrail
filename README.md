# SoloTrail
AI-powered solo travel companion platform for Indian travelers 

## Project structure

- `frontend/` - Angular standalone frontend
- `backend/` - Spring Boot REST API
- `docker-compose.yml` - MySQL, backend, and frontend stack

Start the complete local stack from the repository root with one command:

```powershell
.\run-app.ps1
```

The script creates `.env` on the first run. It uses Docker when available; otherwise it
uses the local MySQL80 service and starts the backend and frontend directly.

The local frontend is available at `http://localhost:4200` and the backend at
`http://localhost:8080`. When Docker is available, the frontend uses `APP_PORT` from
`.env` instead.
