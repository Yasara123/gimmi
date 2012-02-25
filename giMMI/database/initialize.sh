#!/bin/sh
HOST=127.0.0.1
PORT=3306
USER=root

echo "=======================[WARNING]======================="
echo "This will ERASE ALL DATA in the database named 'gimmi'!"
echo "=======================[WARNING]======================="
echo "Do you really want to proceed? (y/N)"
read answer
if test "$answer" != "Y" -a "$answer" != "y";
	then exit 0;
fi

echo -- Creating database
cd bootstrap
mysql gimmi -h $HOST --port=$PORT -u $USER < createdb.sql
cd ..

echo -- Bootstrapping content
cd bootstrap
mysql gimmi -h $HOST --port=$PORT -u $USER < bootstrap.sql
cd ..

echo "Add example data (for testing)? (y/N)"
read answer
if test "$answer" != "Y" -a "$answer" != "y";
	then exit 0;
fi
echo -- Adding example data
cd exampledata 
mysql gimmi -h $HOST --port=$PORT -u $USER < exampledata.sql
cd ..