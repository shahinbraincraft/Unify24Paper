#!/bin/bash
echo "-----------------------开始加固-----------------------------"
currentPath=$PWD

cd $currentPath/app/build/outputs/apk/_360/release/
for i in `find . -name "*.apk"`
do
	apkPath360=$currentPath/app/build/outputs/apk/_360/release/${i##*/}
done


cd $currentPath/app/build/outputs/apk/tencent/release/
for i in `find . -name "*.apk"`
do
#	echo ${i##*/}
	apkPathTengXun=$currentPath/app/build/outputs/apk/tencent/release/${i##*/}
done

echo $apkPath360
echo $apkPathTengXun

jiagupath="/home/ms/tools/360jiagubao_linux_64/jiagu/"
signPath="/home/ms/tools/keystore/meishe.jks"
username="569307092@qq.com"
password="a52952700"
storePassword="app001"
keyAlias="key0"
keyPassword="app001"
destPath360="$currentPath/app/build/outputs/apk/_360/release/"

apkPathTengXun=$apkPathTengXun
destPathTengXun="$currentPath/app/build/outputs/apk/tencent/release/"

sh $currentPath/jiagu.sh $jiagupath $username $password $signPath $storePassword $keyAlias $keyPassword $apkPath360 $destPath360
sh $currentPath/jiagu.sh $jiagupath $username $password $signPath $storePassword $keyAlias $keyPassword $apkPathTengXun $destPathTengXun

echo "-----------------------加固完成-----------------------------"