#!/bin/sh

# add basic data
mysql gimmi -h 127.0.0.1 --port=3306 -u root < testData.sql
