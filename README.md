# Simple_Stream_System
简易流计算系统设计

### 1. 项目部署

#### 1.1. 集群环境

    主机：node1、node2、node3
    软件：jdk 1.8.0_51、Zookeeper 3.6.1、Kafka 2.12-2.8.1

#### 1.2. 启动步骤

    1. 在三台主机上分别安装jdk、Zookeeper和Kafka
    2. 在三台主机上运行Zookeeper，启动成功后运行Kafka
    3. 将项目所需jar包编译打包，提交到集群中，包含master.jar、worker.jar、client.jar、wordcount.jar和mock.jar
    4. 在node1上启动master：java -jar master.jar
    5. 分别在node2和node3上启动worker：java -jar worker.jar
    6. 在node1上启动client：java -jar client.jar start node1 wordcount.jar wordcount
    7. 使用mock模拟Kafka Source向系统发送数据：java -jar mock.jar

#### 1.3. 运行结果

    窗口大小为20s，统计当前窗口内每个单词的数量
    worker1：
        one: 2022-08-18T09:23:14.064 "apple",9
        one: 2022-08-18T09:23:14.064 "apricot",13
        one: 2022-08-18T09:23:14.064 "arbutus",5
        one: 2022-08-18T09:23:14.064 "bennet",12
        one: 2022-08-18T09:23:14.064 "berry",26
        one: 2022-08-18T09:23:34.064 "apple",11
        one: 2022-08-18T09:23:34.064 "apricot",3
        one: 2022-08-18T09:23:34.064 "arbutus",21
        one: 2022-08-18T09:23:34.064 "bennet",14
        one: 2022-08-18T09:23:34.064 "berry",16
    worker2：
        two: 2022-08-18T09:23:14.670 "avocado",6
        two: 2022-08-18T09:23:14.670 "betelnut",17
        two: 2022-08-18T09:23:14.670 "banana",16
        two: 2022-08-18T09:23:14.670 "bergamot",3
        two: 2022-08-18T09:23:14.670 "almond",7
        two: 2022-08-18T09:23:34.671 "banana",8
        two: 2022-08-18T09:23:34.671 "betelnut",13
        two: 2022-08-18T09:23:34.671 "bergamot",1
        two: 2022-08-18T09:23:34.671 "almond",13


 
