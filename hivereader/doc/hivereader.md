# DataX HiveReader 插件文档

## 介绍

通过Shell执行自定义Hive查询SQL，写入临时表(ORCFILE)，再将临时表数据给到DataX，最后删除。

## 要求

创建必须的临时目录：

```shell
hdfs dfs -mkdir -p /tmp/datax-hivereader
```