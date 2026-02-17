#!/bin/bash
# Plan 91 Database Restore Script
# Usage: ./scripts/restore-db.sh <backup_file.sql.gz>
#
# WARNING: This will OVERWRITE all data in the database!
#
# Requires environment variables:
#   MYSQL_HOST - Database host (use public Railway URL)
#   MYSQL_PORT - Database port
#   MYSQL_USER - Database user
#   MYSQL_PASSWORD - Database password
#   MYSQL_DATABASE - Database name

set -e

# Check for backup file argument
if [ -z "$1" ]; then
    echo "Usage: $0 <backup_file.sql.gz>"
    echo ""
    echo "Available backups:"
    ls -lh ./backups/plan91_backup_*.sql.gz 2>/dev/null || echo "  No backups found in ./backups/"
    exit 1
fi

BACKUP_FILE="$1"

# Check if backup file exists
if [ ! -f "$BACKUP_FILE" ]; then
    echo "Error: Backup file not found: $BACKUP_FILE"
    exit 1
fi

# Default values
MYSQL_HOST="${MYSQL_HOST:-}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
MYSQL_DATABASE="${MYSQL_DATABASE:-railway}"

# Check required variables
if [ -z "$MYSQL_HOST" ]; then
    echo "Error: MYSQL_HOST is required"
    exit 1
fi

if [ -z "$MYSQL_PASSWORD" ]; then
    echo "Error: MYSQL_PASSWORD is required"
    exit 1
fi

echo "=========================================="
echo "WARNING: This will OVERWRITE all data!"
echo "=========================================="
echo "Host: $MYSQL_HOST:$MYSQL_PORT"
echo "Database: $MYSQL_DATABASE"
echo "Backup: $BACKUP_FILE"
echo ""
read -p "Are you sure you want to continue? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo "Restore cancelled."
    exit 0
fi

echo ""
echo "Restoring database..."

# Decompress and restore
gunzip -c "$BACKUP_FILE" | mysql \
    --host="$MYSQL_HOST" \
    --port="$MYSQL_PORT" \
    --user="$MYSQL_USER" \
    --password="$MYSQL_PASSWORD" \
    "$MYSQL_DATABASE"

echo "Restore completed successfully!"
echo ""
echo "Note: You may need to restart the application for changes to take effect."
