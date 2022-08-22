1.出测试包可以 debug106.sh、debug240.sh出包，出的分别是106点位和240点位的安装包
2.sdk_demo_publish.sh 这个是专门用于发版本用的，会直接出所有需要的包，包含蒲公英的四个版本
3.debug测试包，在发生崩溃 会在/storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/crashHandle/crash/ 记录crash日志，
出现崩溃，可以直接获取这个日志快速定位崩溃。

4.拍摄页面滤镜下载之后，存放的路径：storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/NvStreamingSdk/Asset/Filter/26073681-6655-4C77-9A49-3C00565C05AA.1.videofx

-- --------------------------------发版本 --------------------------------------------------
sdk demo 应用市场发版本：release 对应的是市场的各个产品维度   release_MS_ST_240、release_MS_ST_106、release_MS_240与release_MS_106 是发蒲公英的四个渠道
应用市场版本：360渠道和腾讯渠道，需要进行加固，否则上传不上应用市场
官网的sdk 使用base_all 和美摄人脸的包，打好了压缩一下就行

 -- --------------------------------发版本 end--------------------------------------------------