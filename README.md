# simple-schedule
一个基于时间轮算法的分布式调度任务工具

# 使用示例

## 1、前置工作

延时调度中心配置任务相关参数，特定场景下非必须，但建议做这一步工作，方便后续维护。  
如回调方式、回调相关信息、重试策略、队列规划、任务优先级等等。

## 2、添加延时任务

### jms方式添加任务（目前配置了activemq）：

[消息格式](simple-scheduling-acceptor/simple-scheduling-acceptor-support/src/main/java/com/qianmi/b2b/scheduling/acceptor/common/req/TaskSubmitRequest.java)  

消息类型：queue或topic即可

消息队列：simple_scheduling_submit

```json
{
  "template": "d2p_order_cancel",
  "payload": "test",
  "delayMs": 1000,
  "indexes": [
    "TP0001"
  ]
}
```

### HTTP方式添加任务

POST: {domain}/api/schedule/task

```json
{
  "template": "d2p_order_cancel",
  "payload": "test",
  "delayMs": 1000,
  "indexes": [
    "TP0001"
  ]
}
```

## 3、取消之前添加的延时任务

index字段可以为内部的任务ID，可以为业务添加任务时传入的indexes字段值【允许批量删除任务】。

### jms方式

消息类型：queue或topic即可

消息队列：simple_scheduling_cancel  

[消息格式](simple-scheduling-acceptor/simple-scheduling-acceptor-support/src/main/java/com/qianmi/b2b/scheduling/acceptor/common/req/TaskCancelRequest.java)  

```json
{
  "template": "d2p_order_cancel",
  "index": "TP0001"
}
```

### HTTP方式

DELETE: {domain}/api/schedule/task

```json
{
  "template": "d2p_order_cancel",
  "index": "TP0001"
}
```

## 4、重置之前定时任务的延时时间
适用场景：比如订单配置原本是延时30分钟后取消未支付的单子，现在配置由30分钟改成了60分钟后，且产品要求比较变态，要求之前30分钟内还没触发取消的单子，也一并调整成相应的60分钟后自动取消。  
缺点说明：由于是采用偏移值重置的任务，如果中间2个系统，有一次任务受理失败或重复，很容易导致结果错误。  

### jms方式

消息类型：queue或topic即可

消息队列：simple_scheduling_reset  
[消息格式](simple-scheduling-acceptor/simple-scheduling-acceptor-support/src/main/java/com/qianmi/b2b/scheduling/acceptor/common/req/TaskResetRequest.java)  

delayMsDiff: 跟之前任务的时间差，可正(加时间)可负(减时间)。负数理论上可能导致部分任务立即触发回调逻辑

```json
{
  "template": "d2p_order_cancel",
  "index": "TP0001",
  "delayMsDiff": 10000
}
```

### HTTP方式

PATCH: {domain}/api/schedule/task

```json
{
  "template": "d2p_order_cancel",
  "index": "TP0001",
  "delayMsDiff": -5000
}
```

## 5、销毁并重新提交任务
使用场景：实际效果=取消任务+提交任务  
适用于业务如果有短时间内对同一个业务数据任务，进行取消，然后立马又提交任务。如果是通过jms方式分别使用上面3、2（先取消再添加）这2个步骤，理论上有可能导致实际效果是先添加再取消【因为队列不一样，消费方有可能先消费了添加的消息】

### jms方式

消息类型：queue或topic即可  
消息队列：simple_scheduling_rebuild  
[消息格式](simple-scheduling-acceptor/simple-scheduling-acceptor-support/src/main/java/com/qianmi/b2b/scheduling/acceptor/common/req/TaskRebuildRequest.java)  
字段uniqueIndex：取消任务使用，唯一索引用于定位之前添加的任务。命名是唯一索引（建议业务上使用唯一定位索引），底层实现是找过匹配的所有任务记录先取消，再添加一条新任务。
其他字段：同添加任务
```json
{
  "template": "d2p_order_cancel",
  "uniqueIndex": "TP0001",
  "payload": "test",
  "delayMs": 1000,
  "indexes": [
    "TP0001"
  ]
}
```

## 6、统一jms队列入口受理任务操作
缺点：所有任务，底层都使用同一个队列，理论并发量没有拆开队列的高。  
优点：不会比如上述第5点提到的消息队列先后乱序问题。  
消息类型：queue或topic即可  
消息队列：simple_scheduling_accept
[消息格式](simple-scheduling-acceptor/simple-scheduling-acceptor-support/src/main/java/com/qianmi/b2b/scheduling/acceptor/common/req/TaskAcceptRequest.java)  
字段requestType：[操作类型](simple-scheduling-acceptor/simple-scheduling-acceptor-support/src/main/java/com/qianmi/b2b/scheduling/acceptor/common/req/TaskAcceptType.java)，不区分大小写  
字段requestPayload：同上述说明的提交任务、取消任务、重置任务、重建任务


比如：添加任务
```json
{
  "requestType": "SUBMIT",
  "requestPayload": {
    "template": "d2p_order_cancel",
    "payload": "test",
    "delayMs": 1000,
    "indexes": [
      "TP0001"
    ]
  }
}
```

# Q&A

## payload业务参数

> (1) payload建议尽量使用必须的字段即可，不建议比如连几百K的字段都传过来  
> 【最近1小时(配置化)的任务，会预热好放在内存队列中】；
>
> (2) 建议业务方使用可扩展的String类型字段，如json，方便未来如果需要加字段方便；

## indexes索引

> (1) indexes索引字段，每个索引列长度不能超过32，indexes列表元素不能超过10个；
>
> (2) indexes索引字段辨识度尽可能高，  
> 比如不要一个值就能把队列里几万甚至几十上百万数据匹配出来了，  
> 后续取消任务时，可能会根据该字段扫描获得对应一个/批任务进行取消。  
> (3) indexes索引如果未设置且jms消息设置了JMSCorrelationID，将自动使用该值；

## 顺序问题

> (1) 任务的添加、取消的操作，业务方自行保证顺序性；
>
> (2) 延时调度中心，收到请求，保证顺序接收处理，并尽量承诺减少可能的积压问题；

## 时间准确

> (1) 看业务数据量及延时长度，理论上可能有积压。  
> 比如：业务一个配置调整，需要重新修改之前相关的延时任务。  
> 底层实现逻辑，删除一大批之前的任务，重新再添加一大批新任务。  
> 假设新添加的任务，有希望1ms后执行的>_<，实际可能很难达到该效果；  
> (2) 为减少不同业务服务器时间差影响，设计上采用delayMs这个绝对值，  
> 结合上面这点，可能会有些许误差，业务需要能容忍这些场景；

## 业务准确

> (1) 回调逻辑目前不承诺保证针对同一个业务ID对象，回调动作的连贯性。  
> 比如针对某一笔订单，期望先定时收货，再定时评价完成。  
> 延时调度中心先回调通知执行收货逻辑，但假设系统当时出问题了，  
> 导致业务逻辑一直失败或未收到ack。  
> 后续可能出现回调通知评价完成订单时，实际业务方还未收货动作。

## 业务幂等

> (1) 别想了，这种回调逻辑（包括http或rpc回调），类似异步的，业务一定要自行保证幂等；
>
> (2) 如果回调逻辑配置了需等待ack回复，注意预估好重试策略时间配置的长短。  
> 比如：业务处理太慢，导致迟迟不发出ack，  
> 重试策略配置太短，可能下一条重试通知又回调给业务方了。  
> 特别是异步回调的场景，其中又特别是queue形式的回调，下一条通知，  
> 因为queue的特性，可能被业务方服务器集群的其他台jvm进程消费掉了。
