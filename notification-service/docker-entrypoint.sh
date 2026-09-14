#!/bin/sh
set -e

if [ -n "$RABBITMQ_PASSWORD_FILE" ] && [ -f "$RABBITMQ_PASSWORD_FILE" ]; then
    export RABBITMQ_PASSWORD="$(cat "$RABBITMQ_PASSWORD_FILE")"
fi

exec uvicorn app.main:app --host 0.0.0.0 --port 8083
