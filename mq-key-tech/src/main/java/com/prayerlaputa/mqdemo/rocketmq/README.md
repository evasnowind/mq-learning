# RocketMQ 常见功能使用

### transaction  

事务消息示例。

事务消息使用限制：
- 1. 不支持调度、批量
- 2. 检查单个消息的次数默认只有15次，用户可以修改transactionCheckMax来配置。超过这个次数，broker将丢弃这个消息，并打印错误日志。用户可以通过AbstractTransactionCheckListener来改变这个行为。
- 3. 在配置参数transactionTimeout规定的时间内，事务消息将被检查，用户可以在发送消息时通过设置属性CHECK_IMMUNITY_TIME_IN_SECONDS来修改这个时间长度
- 4. 事务消息可以被检查或是消费多次
- 5. 被提交消息重放可能失败。高可用由RocketMQ的高可用机制来保证。想确保事务消息不丢、且保证事务完整性，则推荐采用同步双写机制。
- 6. 没看明白，待定：Producer IDs of transactional messages cannot be shared with producer IDs of other types of messages. Unlike other types of message, transactional messages allow backward queries. MQ Server query clients by their Producer IDs.
上述信息来自：http://rocketmq.apache.org/docs/transaction-example/


使用：TransactionStatus.CommitTransaction
事务的状态：
- TransactionStatus.CommitTransaction
- TransactionStatus.RollbackTransaction
- TransactionStatus.Unknown