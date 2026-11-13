# Render deployment

## Backend

1. Create a hosted MySQL database with a provider that allows external connections.
2. In Render, create a **Blueprint** from this repository. Render will use `render.yaml`
   to create the `solotrail-backend` web service. Render does not currently provide a
   native Java runtime, so the service uses the included Dockerfile to build with Java 21.
3. Set these backend environment variables in Render:
   - `DB_URL`: JDBC URL, for example
     `jdbc:mysql://host:3306/database?useSSL=true&serverTimezone=UTC`
   - `DB_USERNAME`: hosted database username
   - `DB_PASSWORD`: hosted database password
   - `JWT_SECRET`: a long random secret
   - `CORS_ALLOWED_ORIGINS`: the deployed frontend URL, for example
     `https://solotrail-frontend.onrender.com`
4. After deployment, verify the backend service URL and logs.

The backend listens on Render's `PORT` variable and falls back to port `8080` locally.

## Frontend

1. Update `frontend/src/environments/environment.prod.ts` so `apiUrl` is the deployed
   backend URL followed by `/api`, for example:

   ```ts
   apiUrl: 'https://solotrail-backend.onrender.com/api'
   ```

2. Create a Render **Static Site** with:
   - Root directory: `frontend`
   - Build command: `npm ci && npm run build`
   - Publish directory: `frontend/dist/solotrail-frontend/browser`
3. Add a rewrite from `/*` to `/index.html` so Angular routes work on refresh.

Do not commit database credentials or JWT secrets. The local `proxy.conf.json` remains
for development only; production uses the deployed backend URL from the Angular
production environment file.
