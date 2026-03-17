# kjrepo-java-infra

## 一 介绍
- kjrepo-java-infra

## 二 软件架构

### 0. pom
```
<dependency>
    <groupId>kjrepo-infra</groupId>
    <artifactId>kjrepo-infra-pom</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <type>pom</type>
</dependency>
```

### 1. common:通用组件
- com.kjrepo.infra.common.logger.LoggerUtils
    ```
    org.slf4j.Logger logger = LoggerUtils.logger(clazz);
    logger.info("");
    ```
- com.kjrepo.infra.common.hook.HookHelper
    ```
    HookHelper.addHook(module,runnable);
    ```
- com.kjrepo.infra.common.term.TermHelper,拦截TERM信号
    ```
    TermHelper.addTerm(module,runnable);
    ```

### 2. buffer:缓存组件
- disruptor
- com.kjrepo.infra.buffer.trigger.BufferTrigger<E>
```
this.bufferTrigger = BufferTrigger.<PerfContext, Map<PerfLogTag, PerfLogMetrics>>simple() //
    .disableEnqueueLock()
	.setInterval(1, TimeUnit.MINUTES) //
	.setConsumer(this::handle) //
	.setContainer(Maps::newConcurrentMap, (container, builder) -> {
		container.merge(builder.getPerfLog(), new PerfLogMetrics(builder.getCount(), builder.getMicro()),
				(value1, value2) -> {
					value1.accept(value2.getTotalCount(), value2.getTotalMicro());
					return value1;
				});
		return true;
	}).build();
```

### 3. cluster:集群组件
- com.kjrepo.infra.cluster.Cluster<R>
```
Cluster<MailSmtp> smtp = ClusterFactory.gcluster(SmtpClusterInfo.class, MailSmtpInfo.class,
            "network/smtp", info -> new MailSmtp(info.getInfo()), MailSmtp::close);
smtp.getResource().send(......);
```
- com.kjrepo.infra.cluster.Master<R>
- com.kjrepo.infra.cluster.MasterCluster<R>

### 4. code:代码自动化组件
### 5. crawler:爬虫
### 6. crypto:密码组件
- cipher
    - com.kjrepo.infra.crypto.cipher.Decrypt
    - com.kjrepo.infra.crypto.cipher.Encrypt
- com.kjrepo.infra.crypto.digest.Digest
- com.kjrepo.infra.crypto.mac.Mac
- signature
    - com.kjrepo.infra.crypto.signature.SignatureSign
    - com.kjrepo.infra.crypto.signature.SignatureVertify

### 7. distrib:分布式组件
- com.kjrepo.infra.distrib.cache.DLoadingCache<K, V>
- com.kjrepo.infra.distrib.lock.DLock

### 8. executor:执行器组件
- com.kjrepo.infra.executor.executor.Executor<T>
- com.kjrepo.infra.executor.pool.PoolExecutor<T, I>

### 9. monitor:监控组件
- 系统监控：内存、线程等

### 10. network
- com.kjrepo.infra.network.browser.Browser
- com.kjrepo.infra.network.capture.ChromeCapture
- ftp
    - com.kjrepo.infra.network.ftp.KftpClient
    - com.kjrepo.infra.network.ftp.KftphttpClient
    - com.kjrepo.infra.network.ftp.KftpsClient
- http
    - com.kjrepo.infra.network.http.KhttpClient
    - com.kjrepo.infra.network.http.KhttpAsyncClient
- com.kjrepo.infra.network.jsch.sftp.JschSftp
- mail
    - com.kjrepo.infra.network.mail.receiver.MailReceiver
    - com.kjrepo.infra.network.mail.sender.MailSender
    - com.kjrepo.infra.network.mail.sender.MailSmtp
- okhttp
    - com.kjrepo.infra.network.okhttp.OkhttpAsync
    - com.kjrepo.infra.network.okhttp.OkhttpSync
- com.kjrepo.infra.network.retrofit.RetrofitUtils

### 11. perf:性能统计组件
- com.kjrepo.infra.perf.utils.PerfUtils

### 12. register:配置组件
- com.kjrepo.infra.register.context.IRegisterContext
- com.kjrepo.infra.register.group.context.IGroupRegisterContext
- com.kjrepo.infra.register.kconf.Kconf<T>
- com.kjrepo.infra.register.resource.IResource<I, R>

### 13. reporter

### 14. runner
- com.kjrepo.infra.runner.binlog.BinlogRunner
- com.kjrepo.infra.runner.mq.kafka.KafkaRunner<K, V>
- com.kjrepo.infra.runner.mq.rocket.RocketRunner
- com.kjrepo.infra.runner.rpc.grpc.GrpcRunner
- com.kjrepo.infra.runner.rpc.grpc.GrpcRunnerBindable
- com.kjrepo.infra.runner.sch.ksch.KschRunner
- com.kjrepo.infra.runner.sch.quatz.QuatzRunner
- com.kjrepo.infra.runner.simple.SimpleRunner
- com.kjrepo.infra.runner.server.RunnerServerMain

### 15. script
- com.kjrepo.infra.script.utils.ScriptUtils

### 16. server
- com.kjrepo.infra.server.jetty.JettyServer
- com.kjrepo.infra.server.startup.Startable
- com.kjrepo.infra.server.startup.StartableMain

### 17. storge
- db
    - com.kjrepo.infra.storage.db.jdbc.Kjdbc<T>
    - com.kjrepo.infra.storage.db.jdbc.KjdbcRepository
    - com.kjrepo.infra.storage.db.jdbc.KjdbcRepositoryResource<I>
        - com.kjrepo.infra.storage.db.jdbc.dbcp2.Dbcp2RepositoryResource
        - com.kjrepo.infra.storage.db.jdbc.druid.DruidRepositoryResource
        - com.kjrepo.infra.storage.db.jdbc.hikari.HikariRepositoryResource
    - com.kjrepo.infra.storage.db.jdbc.cluster.MasterRepositoryResource<I, C>
        - com.kjrepo.infra.storage.db.jdbc.dbcp2.Dbcp2MasterRepositoryResource
        - com.kjrepo.infra.storage.db.jdbc.druid.DruidMasterRepositoryResource
        - com.kjrepo.infra.storage.db.jdbc.hikari.HikariMasterRepositoryResource
    - com.kjrepo.infra.storage.db.jdbc.cluster.MasterClusterRepositoryResource<I, C>
        - com.kjrepo.infra.storage.db.jdbc.dbcp2.Dbcp2MasterClusterRepositoryResource
        - com.kjrepo.infra.storage.db.jdbc.druid.DruidMasterClusterRepositoryResource
        - com.kjrepo.infra.storage.db.jdbc.hikari.HikariMasterClusterRepositoryResource
    - 注解
        - com.kjrepo.infra.storage.db.model.KdbTable
        - com.kjrepo.infra.storage.db.model.KdbColumn
        - com.kjrepo.infra.storage.db.model.KdbIndex
        - com.kjrepo.infra.storage.db.model.KdbInsertTime
        - com.kjrepo.infra.storage.db.model.KdbUpdateTime
    - sql
- es
    - com.kjrepo.infra.storage.es.ElasticsearchRepository
- hbase
    - com.kjrepo.infra.storage.hbase.HbaseRepository.HbaseRepository(Configuration)
- hdfs
    - com.kjrepo.infra.storage.hdfs.HdfsRepository
- jedis
    - com.kjrepo.infra.storage.jedis.JedisRepository
    - com.kjrepo.infra.storage.jedis.JedisShardingRepository
- lucene
    - com.kjrepo.infra.storage.lucene.Lucene
- mongo
    - com.kjrepo.infra.storage.mongo.MongoRepository
- memcache
    - com.kjrepo.infra.storage.spy.SpyRepository

### 18. text
- json
    - com.kjrepo.infra.text.json.ConfigUtils
    - com.kjrepo.infra.text.json.JsonUtils
- pinyin
    - com.kjrepo.infra.text.pinyin.PinyinUtils
- tpl
    - com.kjrepo.infra.text.tpl.beetl.Beetl
    - com.kjrepo.infra.text.tpl.enjoy.Enjoy
    - com.kjrepo.infra.text.tpl.freemarker.Freemarker
- xml
    - 

### 19. thread
- com.kjrepo.infra.thread.pool.KrExecutors
- com.kjrepo.infra.thread.utils.ThreadHelper

### 20. trace
- 跟踪

## 三 Maven依赖
```
<dependency>
	<groupId>kjrepo-infra</groupId>
	<artifactId>kjrepo-infra-pom</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<type>pom</type>
</dependency>
```
