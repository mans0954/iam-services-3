#!/bin/bash

if [ -z "$1" ]
  then
    echo "Usage: execute.sh [mysql root password]"
	return
fi

dos2unix execution_order

while read F  ; do
	echo "Executing $F"
	mysql --user=root --password=$1 < $F
done < execution_order

