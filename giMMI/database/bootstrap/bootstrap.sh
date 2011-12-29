#!/bin/sh
# insert some static data into the DB
mysqlimport --host=127.0.0.1 --port=3306 --user=root --columns=country_code,name_en --fields-terminated-by=";" --fields-enclosed-by='"' --lines-terminated-by="\n" --delete -p gimmi --local --ignore-lines=2 --verbose=TRUE country.csv