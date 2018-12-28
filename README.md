简单实现SpringCloud分布式事务一致性的问题，初次提交，很多不完善，后续将会持续更新

部署需求:
redis
springcloud
mysql

主要原理:借鉴github上开源的项目lcn的思想，加上自己的一些想法，做了一些改变

本项目共有6个微服务，分别是serverA，serverB，serverC，serverD，serverE，tx-manager

A,B,C,D,E 服务每次service方法中事务部分结束后，并不会真正将方法结束，线程这个时候会等待tx-manager的通知请求返回的结果，并通过该请求产生的线程与当前挂起的服务的线程之间进行通信，并告知被挂起的线程是去提交事务还是回滚事务

具体流程图如下所示：

![Image text](https://raw.githubusercontent.com/ylx977/img_folder/master/distributionTx.png)


模拟起见，在本地数据库创建表accounta, accountb, accountc, accountd, accounte
CREATE TABLE `......` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `money` decimal(10,2) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

线程之间通信采用java.util.concurrent.Exchanger这个类的exchange方法进行信息交互
