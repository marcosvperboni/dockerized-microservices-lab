#!/bin/sh
set -e

if [ -n "$DB_PASSWORD_FILE" ] && [ -f "$DB_PASSWORD_FILE" ]; then
    export DB_PASSWORD="$(cat "$DB_PASSWORD_FILE")"
fi

if [ -n "$RABBITMQ_PASSWORD_FILE" ] && [ -f "$RABBITMQ_PASSWORD_FILE" ]; then
    export RABBITMQ_PASSWORD="$(cat "$RABBITMQ_PASSWORD_FILE")"
fi

exec java -jar app.jar
