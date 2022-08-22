#!/bin/bash

echo "$1"

git checkout -b $1
git push origin $1
git branch --set-upstream-to=origin/$1
git pull