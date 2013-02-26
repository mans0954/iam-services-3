#!/bin/bash

rm all_scripts.sql -f

while read F  ; do
	echo "" >> all_scripts.sql
	echo "/* new file */" >> all_scripts.sql
	cat $F >> all_scripts.sql
done < execution_order

#mysql -u root -p openiam < all_scripts.sql

