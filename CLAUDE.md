# Claude Code Instructions for Plan 91

## CRITICAL: Do NOT Deploy Without Permission

**NEVER deploy to production unless the user explicitly asks you to deploy.**

When ready to deploy, tell the user and wait for their confirmation.
Use the `/deploy` skill when they give permission.

---

## CRITICAL: Pre-Deployment Backup

**ALWAYS create a database backup before deploying ANY changes to production.**

Before running `railway up` or pushing to trigger a deployment, run:

```bash
MYSQL_HOST=metro.proxy.rlwy.net \
MYSQL_PORT=51299 \
MYSQL_USER=root \
MYSQL_PASSWORD=NsIoVKtJQxgMGGFrLpTnAtnrKjcfuqJX \
MYSQL_DATABASE=railway \
./scripts/backup-db.sh
```

This is mandatory after the data loss incident from V10 migration on 2026-02-17.

## Project Overview

Plan 91 is a habit tracking application with:
- 91-day commitment cycles
- One-strike forgiveness rule
- Spring Boot 3.2.2 backend
- MySQL database on Railway
- Thymeleaf + HTMX + Tailwind CSS frontend

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.2, Spring Security 6
- **Database**: MySQL 9.4 on Railway
- **Migrations**: Flyway
- **Frontend**: Thymeleaf, HTMX, Tailwind CSS
- **Deployment**: Railway (Dockerfile)

## Important Files

- `/scripts/backup-db.sh` - Database backup script
- `/scripts/restore-db.sh` - Database restore script
- `/src/main/resources/db/migration/` - Flyway migrations

## Deployment Checklist

1. [ ] Run database backup (see command above)
2. [ ] Verify backup file was created in `./backups/`
3. [ ] Commit changes
4. [ ] Deploy with `railway up --detach` or `git push`
5. [ ] Monitor logs: check Railway dashboard or use `railway logs`

## Database Connection (Production)

- **Internal** (from Railway): `mysql-pa0x.railway.internal:3306`
- **Public** (for backups): `metro.proxy.rlwy.net:51299`
- **Database**: `railway`
- **User**: `root`

## Common Issues

### Flyway Migration Failures
If a migration fails, the app has `FlywayRepairConfig` that runs `repair()` before `migrate()`.
This auto-fixes the schema history but **does not recover lost data**.

### JavaScript Not Loading
Check for missing `</script>` closing tags in Thymeleaf templates.
Bump version query params (e.g., `v=23`) to bust cache.
