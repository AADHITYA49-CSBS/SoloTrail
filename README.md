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

## Product flow

1. A user registers and logs in.
2. The user enters their starting city, Chennai travel date, transport mode, and budget.
3. SoloTrail shows hotel, restaurant, and guide choices from the local catalog.
4. The user selects any services and completes a simulated single checkout.
5. A booking reference and rupee total are saved in MySQL with `DEMO_PAID` status.
6. The `Ask Solo` page provides travel guidance through an optional AI provider or the
   built-in fallback assistant. The assistant receives the current Chennai catalog and
   the logged-in user's saved plans and demo bookings as context.

The checkout is intentionally simulated: it does not charge money or issue a real ticket.

## Optional AI assistant

Leave the provider keys blank to use built-in travel guidance. The backend checks providers
in this order: Groq, OpenAI, then a generic OpenAI-compatible endpoint.

```text
GROQ_API_KEY=
GROQ_MODEL=openai/gpt-oss-20b
OPENAI_API_KEY=
OPENAI_MODEL=gpt-4o-mini
AI_API_KEY=
AI_BASE_URL=https://api.openai.com/v1
AI_MODEL=gpt-4o-mini
```

Keep all API keys in `.env`; never commit them.
