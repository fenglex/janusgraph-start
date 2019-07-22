############################################################
# Dockerfile with jdk8(jdk-8u201) and janusgraph(gremlin-server)
# Based on centos ∂
# For detail  --> https://haifeng.ink
############################################################
FROM centos
MAINTAINER haifeng
WORKDIR /root/
ENV janusgraph_version 0.4.0

COPY jdk-8u211-linux-x64.tar.gz .
COPY janusgraph-${janusgraph_version}-hadoop2.zip .
COPY gremlin-server-WsAndHttp.yaml .
COPY janusgraph-cql-es.properties cql-es.properties

run set -ex \
&& yum install -y unzip \
&& unzip janusgraph-${janusgraph_version}-hadoop2.zip \
&& rm -rf janusgraph-${janusgraph_version}-hadoop2.zip \
&& mv janusgraph-${janusgraph_version}-hadoop2 janusgraph \
&& tar -zxvf jdk* \
&& rm -rf *.gz \
&& mkdir /usr/java/ \
&& mv jdk* /usr/java/jdk8 \
&& mv gremlin-server-WsAndHttp.yaml  janusgraph/conf/gremlin-server/ \
&& mv cql-es.properties janusgraph/conf/gremlin-server/janusgraph-cql-es-WsAndHttp.properties

ENV JAVA_HOME /usr/java/jdk8
ENV PATH $PATH:$JAVA_HOME/bin
EXPOSE 8182

CMD sleep 30 &&  /root/janusgraph/bin/gremlin-server.sh /root/janusgraph/conf/gremlin-server/gremlin-server-WsAndHttp.yaml>start.log
