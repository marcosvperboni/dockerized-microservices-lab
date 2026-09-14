#!/bin/bash
# Builds and starts the whole lab. Works with either Podman or Docker.
set -euo pipefail
cd "$(dirname "$0")/.."

if command -v podman >/dev/null 2>&1; then
    ENGINE="podman"
elif command -v docker >/dev/null 2>&1; then
    ENGINE="docker"
else
    echo "Neither podman nor docker was found on PATH." >&2
    exit 1
fi

mkdir -p secrets
[ -f secrets/postgres_password.txt ] || openssl rand -hex 16 > secrets/postgres_password.txt 2>/dev/null || echo "orders_pass_$(date +%s)" > secrets/postgres_password.txt
[ -f secrets/rabbitmq_password.txt ] || openssl rand -hex 16 > secrets/rabbitmq_password.txt 2>/dev/null || echo "rabbit_pass_$(date +%s)" > secrets/rabbitmq_password.txt

# RabbitMQ's own image rejects the deprecated RABBITMQ_DEFAULT_PASS_FILE
# convention, so the broker container needs the password as a plain env var.
# It is still sourced from the (git-ignored) secrets file, never hardcoded.
export RABBITMQ_PASSWORD
RABBITMQ_PASSWORD="$(cat secrets/rabbitmq_password.txt)"

echo "Using engine: $ENGINE compose"
"$ENGINE" compose up --build -d

echo
echo "Services starting. Check health with: $ENGINE compose ps"
