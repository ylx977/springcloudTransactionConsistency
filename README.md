简单实现SpringCloud分布式事务一致性的问题

部署需求:
redis
springcloud
mysql

主要原理:借鉴github上开源的项目lcn的思想，加上自己的一些想法，做了一些改变

本项目共有6个微服务，分别是serverA，serverB，serverC，serverD，serverE，tx-manager

A,B,C,D,E 服务每次service方法中事务部分结束后，并不会真正将方法结束，线程这个时候会等待tx-manager的返回的结果,并告知当前线程是否提交事务还是回滚事务

具体流程图如下所示：

![Image text](https://raw.githubusercontent.com/ylx977/img_folder/master/distributionTx2.png)


模拟起见，在本地数据库创建表
accounta, accountb, accountc, accountd, accounte

CREATE TABLE `......` (

  `id` int(11) NOT NULL AUTO_INCREMENT,
  
  `name` varchar(255) DEFAULT NULL,
  
  `money` decimal(10,2) DEFAULT NULL,
  
  `ctime` bigint(20) DEFAULT NULL,
  
  `utime` bigint(20) DEFAULT NULL,
  
  PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

