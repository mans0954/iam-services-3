#!/bin/bash

if [ -z "$1" ]
  	then
    	echo "Usage: execute.sh [mysql root password] ['new_install' - if this is a new install]"
		return
fi

dos2unix idm/execution_order
dos2unix am/execution_order
dos2unix km/execution_order
dos2unix reports/execution_order

echo "=============== EXECUTING IDM ==============================="
while read IDM_F  ; do
	echo "Executing idm/$IDM_F"
	mysql --user=root --password=$1 < "idm/$IDM_F"
done < idm/execution_order
echo "=============== DONE EXECUTING IDM =========================="
echo ""
echo ""

echo "=============== EXECUTING AM ==============================="
while read AM_F  ; do
	echo "Executing am/$AM_F"
	mysql --user=root --password=$1 < "am/$AM_F"
done < am/execution_order
echo "=============== DONE EXECUTING AM =========================="
echo ""
echo ""

echo "=============== EXECUTING KM ==============================="
while read KM_F  ; do
	echo "Executing km/$KM_F"
	mysql --user=root --password=$1 < "km/$KM_F"
done < km/execution_order
echo "=============== DONE EXECUTING KM =========================="
echo ""
echo ""

echo "=============== EXECUTING REPORTS ==============================="
while read R_F  ; do
	echo "Executing reports/$R_F"
	mysql --user=root --password=$1 < "reports/$R_F"
done < reports/execution_order
echo "=============== DONE EXECUTING REPORTS =========================="
echo ""
echo ""

if [ "$2" == "new_install" ]
		then
			echo "Executing new install script km/dml/reset_passwords.sql"
			mysql --user=root --password=$1 openiam < km/dml/reset_passwords.sql
fi

