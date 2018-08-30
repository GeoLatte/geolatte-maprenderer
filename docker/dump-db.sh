#!/bin/bash

export PGPASSWORD=mosaic
pg_dump -h db -p 5432 -U mosaic mosaic   | gzip > mosaic-postgres/mosaic.sql.gz