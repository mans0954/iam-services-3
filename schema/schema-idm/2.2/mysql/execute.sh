#!/bin/bash

rm all_scripts.sql -f

while read F  ; do
	cat $F >> all_scripts.sql
done < execution_order

#mysql -u root -p < all_scripts.sql

