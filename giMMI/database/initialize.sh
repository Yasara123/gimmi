#!/bin/sh
HOST=127.0.0.1
PORT=3306
USER=root

echo -- Bootstrapping
cd bootstrap
mysql gimmi -h $HOST --port=$PORT -u $USER < bootstrap.sql
cd ..

echo -- Example Data
cd exampledata 
mysql gimmi -h $HOST --port=$PORT -u $USER < exampledata.sql
cd ..