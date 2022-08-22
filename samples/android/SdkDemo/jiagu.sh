#!/bin/bash
echo "$1"
echo "$2"
echo "$3"
echo "$4"
echo "$5"
echo "$6"
echo "$7"
echo "$8"
echo "$9"

cd $1
echo $`pwd`
java -jar jiagu.jar -login $2 $3
java -jar jiagu.jar -importsign $4 $5 $6 $7
java -jar jiagu.jar -jiagu $8 $9 -autosign
