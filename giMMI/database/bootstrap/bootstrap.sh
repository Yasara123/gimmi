#!/bin/sh
mysql gimmi -h 127.0.0.1 --port=3306 -u root < bootstrap.sql
