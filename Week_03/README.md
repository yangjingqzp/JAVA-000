学习笔记

1. 再谈谈什么是高性能

高并发用户 （Concurrent Users）
    从业务的角度衡量
    带来系统复杂度\*10、建设和维护成本大、故障和Bug破坏性大

    应对策略：
    1. 容量。最高并发：TPS。
        每秒 1000 个订单，一天 86400000，8.64 千万。淘宝 一天 3000w
        每天 1000 TPS
        国内最大的并发 50 w/s
    2. 爆炸半径
    3.
高吞吐量 （Throughout）qps， 每秒钟查询数，tps 每秒钟处理数
    从技术指标衡量
低延迟（Latency）
    从技术指标衡量

延迟（LAT） VS 响应时间（RT）
响应时间是 请求得到数据时间-请求开始发出时间
延迟是 出服务器时间-请求到达服务器时间

2. Netty 如何实现高性能
网络应用开发框架
- 异步
- 事件驱动
- 基于 NIO
- 网络的处理编程抽象统一

事件处理机制
Reactor 模型
Netty NIO 模型
Netty 运行原理（高性能、灵活性）

关键对象
Bootstrap: 启动线程，开启 socket
EventLoopGroup:
EventLoop:
SocketChannel: 连接
ChannelInitialize: 初始化
ChannelPipeline: 处理器链
ChannelHandler: 处理器

Event & Handler
入站事件
- 通道激活和停用
- 读操作事件
- 异常事件
- 用户事件
出站事件
- 打开链接
- 关闭链接
- 写入数据
- 刷新数据

3. Netty 网络程序优化
粘包与拆包
都是人的问题，没有明确数据包的长度。

Nagle 与 TCP_NODELAY
网络拥堵与 Nagle 算法优化
TCP_NODELAY

MTU: Maxitum Transmission Unit
最大传输单元 1500 Byte
MSS: Maxitum Segment Size
最大分段大小 1460 Byte，40 Byte 的 TCP和IP 头

send 将数据发送给操作系统的内核；接受数据是一样的，recive 放到内核缓冲区

优化条件：
- 缓冲区满
- 达到超时

连接优化

三次握手
最后一次 ACK 服务器 没收到时？超时报错
SYN & ACK
四次挥手
TCP 必需经过时间 2MSL 后才真正释放掉
wins 1 分钟
linux 2 分钟

netstat -anot

连接优化
- 降低等待周期
- 端口、地址 复用，打开复用参数

Netty 优化
1. 不要阻塞 EventLoop
2. 系统参数优化
fd 文件描述符 是否可用
ulimit -a
/proc/sys/net/ipv4/tcp_fin_timeout, TcpTimedWaitDelay
3. 缓冲区优化
SO_REVBUF/SO_SNDBUG/SO_BACKLOG/REUSEXXX
4. 心跳周期优化
心跳机制与断线重连
5. 内存与 ByteBuffer 优化
DirectBuffer 与 HeapBuffer
6. 其他优化
- ioRatio 50:50
- WaterMark
- TrafficShaping

4. 典型应用：API 网关
网关的结构和功能？
- 请求接入：作为所有API接入
- 业务聚合：作为所有后端业务服务的聚合点
- 中介策略：实现安全、验证、路由、过滤、流控等策略
- 统一管理：对所有API服务和策略进行统一管理

网关的分类：
- 流量网关 Nginx
    关注稳定与安全
    防止 SQL 注入
    防止 Web 攻击
    证书/加解密处理
- 业务网关 Zuul \  Spring Cloud Gateway
    提供更好的服务
    路由与负载均衡、灰度策略
    权限验证与用户登录策略

典型应用：API 网关
Zuul 的内部原理可以简单看做是很多不同功能的 filter 的集合，最主要的是 pre, routing, post 这三种过滤器，分别
作用调用业务服务API 之前的请求处理、直接响应、调用业务服务API 之后的响应处理。
Zuul 2.x 是基于 Netty 内核重构的版本。
Inbound Filter
Outbound Filter
Spring Cloud Gateway
\*Handler -> filter1 -> service -> filter2
底层基于 Netty

5. 自己动手实现 API 网关