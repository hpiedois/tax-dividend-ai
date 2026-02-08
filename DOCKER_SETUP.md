# Docker Setup - Tax Dividend AI

## 🏗️ Architecture

Le projet utilise une **séparation Infra/Apps** pour maximum de flexibilité :

```
docker-compose.infra.yml   → Infrastructure (toujours up en dev)
docker-compose.apps.yml    → Applications (optionnel, pour déploiement)
```

### Infrastructure (docker-compose.infra.yml)
- **PostgreSQL** - Base de données principale
- **MinIO** - Stockage S3-compatible (PDFs, exports)
- **Keycloak** - Authentification OAuth2/OIDC
- **Redis** - Cache distribué
- **MailHog** - Test d'emails (dev)
- **Observability** - Grafana, Prometheus, Loki, Tempo

### Applications (docker-compose.apps.yml)
- **Backend** - API Spring Boot 4 + Java 25
- **BFF Gateway** - Gateway OAuth2 + routing
- **Frontend** - React + Vite (Nginx en prod)

---

## 🚀 Quick Start

### Dev Local (recommandé)

1. **Démarrer l'infrastructure**
   ```bash
   make infra-up
   ```

2. **Démarrer le backend dans VS Code** (F5)
   - Flyway applique automatiquement les migrations
   - Profile `dev` activé par défaut

3. **Seed les données de test**
   ```bash
   make seed-dev
   ```

4. **Démarrer le frontend**
   ```bash
   cd frontend
   npm run dev
   ```

✅ **Apps en local (IDE) + Infra en Docker**

---

### Full-Stack Docker (staging/prod)

```bash
make up   # Démarre infra + apps en containers
```

🌐 Accès: http://localhost:5173

---

## 📋 Commandes Principales

### Infrastructure
```bash
make infra-up       # Start PostgreSQL, MinIO, Keycloak, etc.
make infra-down     # Stop infrastructure
make infra-logs     # Logs en temps réel
```

### Applications
```bash
make apps-up        # Start Backend, BFF, Frontend (containers)
make apps-down      # Stop applications
make apps-logs      # Logs en temps réel
```

### All-in-one
```bash
make up             # Start tout (infra + apps)
make down           # Stop tout
make restart        # Restart tout
make logs           # Logs complets
```

### Database
```bash
make reset-db       # Reset PostgreSQL (⚠️ destroy data!)
make seed-dev       # Seed test data
```

### Monitoring
```bash
make status         # Running containers
make ps             # All containers (including stopped)
```

### Cleanup
```bash
make clean          # Remove ALL containers & volumes (⚠️ full reset)
```

---

## 🔧 Configuration

### Variables d'environnement

Copier `.env.example` → `.env` et personnaliser :

```bash
cp .env.example .env
nano .env
```

### Profils Spring

**Backend** :
- `dev` - Développement local (default)
- `docker` - Déploiement containerisé
- `uat` - UAT environment
- `prod` - Production

**BFF** :
- `dev` - Développement local
- `docker` - Déploiement containerisé

---

## 🌐 Ports

### Infrastructure
- **PostgreSQL** - `5432`
- **MinIO API** - `9000`
- **MinIO Console** - `9001`
- **Keycloak** - `8180` (to avoid conflict with BFF on 8080)
- **Redis** - `6379`
- **MailHog SMTP** - `1025`
- **MailHog UI** - `8025`
- **Grafana** - `3000`
- **Prometheus** - `9090`
- **Tempo** - `3200`, `4317` (OTLP), `9411` (Zipkin)
- **Loki** - `3100`

### Applications (si lancées en Docker)
- **Backend** - `8081`
- **BFF Gateway** - `8080`
- **Frontend** - `5173`

---

## 🗄️ Volumes

Tous les volumes sont nommés et persistent entre les restarts :

- `tax-dividend-postgres-data` - PostgreSQL
- `tax-dividend-minio-data` - MinIO
- `tax-dividend-redis-data` - Redis
- `tax-dividend-keycloak-db-data` - Keycloak DB
- `tax-dividend-grafana-data` - Grafana dashboards
- `tax-dividend-prometheus-data` - Prometheus métriques
- `tax-dividend-loki-data` - Loki logs
- `tax-dividend-tempo-data` - Tempo traces

---

## 🔐 Credentials (Dev)

### PostgreSQL
- **Host**: `localhost:5432`
- **DB**: `taxdividend_dev`
- **User**: `taxdividend_user`
- **Password**: `dev_password_123`

### MinIO
- **Console**: http://localhost:9001
- **User**: `minioadmin`
- **Password**: `minioadmin123`

### Keycloak
- **URL**: http://localhost:8180
- **Admin**: `admin`
- **Password**: `admin`

### Grafana
- **URL**: http://localhost:3000
- **Auth**: Anonymous (admin role)

### Test User (seeded)
- **Email**: `jdoe@dummy.com`
- **Password**: `password` (Keycloak)
- **UUID**: `4e012f70-3846-4941-b95f-5b98c70a235d`

---

## 🔄 Migration depuis ancien setup

Si vous avez l'ancien `infrastructure/docker-compose.dev.yml` qui tourne :

```bash
# 1. Stop ancien setup
cd infrastructure
docker-compose -f docker-compose.dev.yml down

# 2. Cleanup anciens containers/volumes
docker stop tax-dividend-postgres-dev tax-dividend-keycloak tax-dividend-minio-dev || true
docker rm tax-dividend-postgres-dev tax-dividend-keycloak tax-dividend-minio-dev || true
docker volume rm infrastructure_postgres_dev_data || true

# 3. Start nouveau setup
cd ..
make infra-up

# 4. Reset database et seed
make reset-db
# (puis restart backend dans VS Code)
make seed-dev
```

---

## 🧪 Testing

### Test d'authentification

1. **Login Keycloak** : http://localhost:8080
2. **Test User** : `jdoe@dummy.com` / `password`
3. **Frontend** : http://localhost:5173

### Test de l'API

```bash
# Health check
curl http://localhost:8081/actuator/health

# Get stats (avec JWT token)
curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/api/dividends/stats?taxYear=2023
```

---

## 📊 Observability

### Grafana Dashboards
- **URL** : http://localhost:3000
- **Datasources** : Prometheus, Loki, Tempo (auto-configured)

### Prometheus Metrics
- **URL** : http://localhost:9090
- **Targets** : Backend actuator endpoints

### Loki Logs
- **URL** : http://localhost:3100
- **Query** : Via Grafana

### Tempo Traces
- **URL** : http://localhost:3200
- **OTLP** : Backend → Tempo (auto-configured)

---

## 🐛 Troubleshooting

### Port already in use
```bash
# Trouver le process
lsof -i :5432  # (ou autre port)

# Stopper services existants
make down
```

### Container won't start
```bash
# Logs détaillés
make infra-logs

# Ou logs spécifiques
docker logs tax-dividend-postgres
```

### Volume permissions
```bash
# Reset volumes
make clean
make infra-up
```

### Database connection refused
```bash
# Vérifier que l'infra tourne
make status

# Vérifier PostgreSQL health
docker exec tax-dividend-postgres pg_isready -U postgres
```

---

## 📚 Documentation

- **OpenAPI** : http://localhost:8081/swagger-ui.html (backend)
- **Keycloak Admin** : http://localhost:8080/admin
- **MinIO Console** : http://localhost:9001

---

## 🎯 Best Practices

### Dev Workflow
1. ✅ Keep infra running (`make infra-up` une seule fois)
2. ✅ Start/stop backend dans l'IDE (fast restart)
3. ✅ Frontend HMR (`npm run dev`)
4. ❌ Don't `make down` entre chaque session de dev

### Before Commit
1. ✅ Test avec `make up` (full-stack)
2. ✅ Vérifier logs : `make logs`
3. ✅ Cleanup : `make down`

### Database Changes
1. ✅ Create new Flyway migration
2. ✅ Test avec `make reset-db`
3. ✅ Update seed script si nécessaire

---

Made with ❤️ by Tax Dividend AI Team
