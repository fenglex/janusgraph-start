这是单图的janusgraph docker配置文件。

执行步骤：  
- 执行download.sh下载需要的文件
- 执行docker-compose up -d 启动单节点的janusgraph

gremlin-server的配置文件在gremlin文件夹中，可自行进行修改配置，  
也可以不使用本docker-compose的es和cassandra，将配置改成自己已有的环境信息