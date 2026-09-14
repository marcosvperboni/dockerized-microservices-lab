#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE orders_db;
    CREATE DATABASE inventory_db;

    CREATE USER orders_user WITH PASSWORD '$POSTGRES_PASSWORD';
    CREATE USER inventory_user WITH PASSWORD '$POSTGRES_PASSWORD';

    GRANT ALL PRIVILEGES ON DATABASE orders_db TO orders_user;
    GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "orders_db" \
    -c "GRANT ALL ON SCHEMA public TO orders_user;"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "inventory_db" \
    -c "GRANT ALL ON SCHEMA public TO inventory_user;"
