#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE authservice;
    CREATE DATABASE productservice;
    CREATE DATABASE orderservice;
    CREATE DATABASE paymentservice;
    CREATE DATABASE shippingservice;
    CREATE DATABASE inventoryservice;
    CREATE DATABASE favouriteservice;
    CREATE DATABASE ratingservice;
    CREATE DATABASE media;
    CREATE DATABASE taxservice;
    CREATE DATABASE promotionservice;
EOSQL
