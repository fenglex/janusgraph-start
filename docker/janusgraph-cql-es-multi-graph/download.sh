#!/bin/bash


if [ ! -f "./gremlin/jdk-8u211-linux-x64.tar.gz" ];then
    wget -P ./gremlin/ https://haifeng-ink.oss-cn-beijing.aliyuncs.com/environment/jdk-8u211-linux-x64.tar.gz
fi

if [ ! -f "./gremlin/janusgraph-0.4.0-hadoop2.zip" ];then
    wget -P ./gremlin/ https://haifeng-ink.oss-cn-beijing.aliyuncs.com/environment/janusgraph-0.4.0-hadoop2.zip
fi

