# ⚠️ Deprecated Docker Compose Files

These files are no longer used. They have been replaced by the new architecture.

## Old Files

- `docker-compose.yml` - Old root compose (replaced)
- `docker-compose.dev.yml` - Old dev setup (replaced)
- `docker-compose.prod.yml` - Old prod setup (replaced)
- `docker-compose.uat.yml` - Old UAT setup (replaced)

## New Architecture

The project now uses:

```
docker-compose.infra.yml          # Base infrastructure
docker-compose.infra.dev.yml      # Dev overrides
docker-compose.infra.prod.yml     # Prod overrides

docker-compose.apps.yml           # Base applications
docker-compose.apps.dev.yml       # Dev overrides
docker-compose.apps.prod.yml      # Prod overrides
```

## Usage

```bash
# Dev
make infra-up

# Production
make infra-up ENV=prod
```

## Can I Delete These Files?

**Yes**, after confirming the new setup works for your team.

Keep them for a few weeks as reference, then delete this entire `deprecated/` folder.
