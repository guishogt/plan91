# Deploy to Railway

Deploy the Plan 91 application to Railway production.

## IMPORTANT: Only run this skill when the user explicitly asks to deploy!

## Steps

1. **Create database backup first** (MANDATORY):
```bash
MYSQL_HOST=metro.proxy.rlwy.net \
MYSQL_PORT=51299 \
MYSQL_USER=root \
MYSQL_PASSWORD=NsIoVKtJQxgMGGFrLpTnAtnrKjcfuqJX \
MYSQL_DATABASE=railway \
./scripts/backup-db.sh
```

2. **Verify backup was created**:
```bash
ls -la ./backups/*.sql.gz | tail -1
```

3. **Commit any pending changes** (if any):
```bash
git add -A && git status --short
```
If there are changes, commit them with a descriptive message.

4. **Deploy to Railway**:
```bash
railway up --detach
```

5. **Monitor deployment**:
- Check deployment status
- Verify logs show successful startup
- Report the deployment URL to the user

## Post-deployment
- Inform user of backup file location
- Report deployment status (SUCCESS/FAILED)
- If failed, check logs and report the error
