#!/bin/bash
# Plan 91 Database Backup Script
# Usage: ./scripts/backup-db.sh
#
# Requires environment variables:
#   MYSQL_HOST - Database host (use public Railway URL)
#   MYSQL_PORT - Database port (usually 3306 or Railway's public port)
#   MYSQL_USER - Database user
#   MYSQL_PASSWORD - Database password
#   MYSQL_DATABASE - Database name (usually 'railway')

set -e

# Default values (override with environment variables)
MYSQL_HOST="${MYSQL_HOST:-}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
MYSQL_DATABASE="${MYSQL_DATABASE:-railway}"

# Backup directory
BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/plan91_backup_${TIMESTAMP}.sql"

# Check required variables
if [ -z "$MYSQL_HOST" ]; then
    echo "Error: MYSQL_HOST is required"
    echo "Get the public URL from Railway dashboard > MySQL service > Connect tab"
    exit 1
fi

if [ -z "$MYSQL_PASSWORD" ]; then
    echo "Error: MYSQL_PASSWORD is required"
    exit 1
fi

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "Starting backup of Plan 91 database..."
echo "Host: $MYSQL_HOST:$MYSQL_PORT"
echo "Database: $MYSQL_DATABASE"
echo "Output: $BACKUP_FILE"

# Run mysqldump
mysqldump \
    --host="$MYSQL_HOST" \
    --port="$MYSQL_PORT" \
    --user="$MYSQL_USER" \
    --password="$MYSQL_PASSWORD" \
    --single-transaction \
    --routines \
    --triggers \
    "$MYSQL_DATABASE" > "$BACKUP_FILE"

# Compress the backup
gzip "$BACKUP_FILE"
BACKUP_FILE="${BACKUP_FILE}.gz"

echo "Backup completed successfully!"
echo "File: $BACKUP_FILE"
echo "Size: $(du -h "$BACKUP_FILE" | cut -f1)"

# Keep only last 30 backups
echo "Cleaning up old backups (keeping last 30)..."
ls -t "$BACKUP_DIR"/plan91_backup_*.sql.gz 2>/dev/null | tail -n +31 | xargs -r rm

echo "Done!"
