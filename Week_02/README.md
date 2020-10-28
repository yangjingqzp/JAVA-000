学习笔记
1. GC 日志解读与分析 **
javac -encoding UTF-8 xxxx.java
java -XX:+PrintGCDetails xxxx.java
java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:PrintGCDateStamps xxxx
启动串行GC：-XX:+UseSerialGC
启动并行GC：-XX:+UseParallelGC
启动CMS GC：-XX:+UseConcMarkSweepGC
启动G1 GC：-XX:+UseG1GC

默认配的堆内存是物理内存的 1/4

2. JVM 线程堆栈数据分析
JVM 内部线程主要分为以下几种
- VM 线程：单例的 VMThread 对象，负责执行 VM 操作，
- 定时任务线程：单例的 WatcherThread 对象，模拟在 VM 中执行定时操作的计时器中断
- GC 线程：垃圾收集器中，用于支持并行和并发垃圾回收的线程
- 编译器线程：将字节码编译为本地机器代码
- 信号分发线程：等待进程指示的信号，并将其分配给 Java 级别的信号处理方法

安全点
a. 方法代码中被植入的安全点检测入口；
b. 线程处于安全点状态：线程暂停执行，这个时候线程栈不再发生改变
c. JVM 的安全点状态：所有线程都处于安全点状态

JVM 支持多种方式来进行线程转储
a. JDK 工具，包括：jstack, jcmd, jconsole, jvisualvm, java mission Control
b. Shell 命令或者系统控制台 比如 kill -3, Ctrl+Break
c. JMX 技术

死锁
fastthread
https://fastthread.io/

3. 内存分析与相关工具
一个 Java 对象占用多少内存？
可以使用 Instrumentation.getObjectSize() 方法来估算一个对象占有的内存空间。

对象头和对象引用
在64位JVM中，对象头占据的空间是 12 byte（=96bit=64+32），但是以 8 字节对齐，所以一个空类实例至少占用 16 字节
在32位JVM中，对象头占8个字节，以4的倍数对齐（32=4\*8）
在32位JVM中，以及内存小于 Xmx32G 的64 位JVM上，一个引用占的内存默认是 4 字节。
64 位机器需要多消耗内存。

包装类型
Integer：占用 16 个字节（8+4=12+补齐），因为 int 部分占有 4 个字节，所以 Integer 比原生 int 多消耗 300% 的内存
Long: 占用 16 个字节（8+8=16）。

多维数组
在二维数组 int[dim1][dim2] 中，每个嵌套的数组 int[dim2] 都是一个单独的 Object，会额外占用 16 字节空间，当数组维度更大时，这种开销特别明显。

int[128][2] 实例占用 3600（16+128\*4+128\*(16+4\*2)） 字节
int[256] 实例占用 1040（16+256\*4） 字节

String
String 对象的空间随着内部字符数组的增长而增长。当然，String 类的对象有 24 个字节的额外开销。
对于 10 字符以内的非空 String ，增加的开销比起有效负荷（每个字符2字节+4个字节的length）多占有很多内存。

对齐是绕不过去的问题

内存分析与相关工具
OutOfMemoryError: Java heap space
- 创建新的对象时，堆内存中的空间不足以存放新创建的对象
- 超出预期的访问量/数据量
- 内存泄露（Memory leak）

OutOfMemoryError: PermGen space/OutOfMemoryError: Metaspace
PermGen space 的主要原因是 加载到内存中的 class 数量太多或体积太大，超过了 PermGen 区大小
解决方法：增大 PermGen/Metaspace
-XX:MaxPermSize=512m
-XX:MaxMetaspaceSize=512m

OutOfMemoryError: Unable to create new native thread
Java.lang.OutOfMemoryError: Unable to create new native thread 错误是程序创建的线程数量已达到上限值的
解决思路：
- 调整系统参数 ulimit -a，echo 120000 >/proc/sys/kernel/threads-max
- 降低 xss 等参数
- 调整代码，改变线程创建和使用方式

内存 dump 分析工具
jhat


4. JVM 问题分析调优经验

a. 高分配速率（High Allocation Rate）
表示单位时间内分配的内存量。通常使用 MB/sec 作为单位。上一次垃圾收集之后，与下一次 GC 开始之前的年轻代使用量，两者的差值除以时间就是分配率。
分配速率过高就会严重影响程序性能，在JVM中可能会导致巨大的GC开销。
new 出来的对象存放在 Eden，增加 Eden 区，会影响 Minor GC 的次数和时间，进而影响吞吐量。
在某些情况下，增加年轻代的大小，即可降低分配速率过高所造成的影响，增加年轻代并不会降低分配速率，但是会减少GC的频率。如果每次GC后存活的对象数量一致，minor GC 的暂停时间就不会明显增加。
b. 过早提升（Premature Promotion）
用于衡量单位时间内从年轻代提升到老年代的数据量。过早提升可能是对象存活时间不够长的时候就被提升到了老年代，major GC 不是为频繁回收而设计的，单 major GC 现在也要清理这些生命短暂的对象，会导致 GC 暂停时间过长，严重影响系统的吞吐量。

一般来说过早提升的症状表现为：
- 段时间内频繁的执行 full GC
- 每次 full GC 后老年代的使用率都很低，在 10-20% 或以下
- 提升速率接近于分配速率
通过指定 GC 参数达到：
-Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1

解决方法：目标是让临时数据能够在年轻代存放得下
- 增加年轻代的大小 -XmX64m -XX:NewSize=32
- 减少每次批处理的数量


5. GC 疑难情况问题分析
-Xmx4g -Xms4g -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -Xloggc:gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps

arthas

- 查询业务日志，可以发现：请求压力大、波峰、遭遇降级、熔断
- 查看系统资源和监控信息：
- 查看性能指标，包括实时监控、历史数据。
- 排查系统日志
- APM
- 排查应用系统：配置文件、内存问题、GC 问题
- 排查线程
- 排查代码
- 单元测试
- 排除资源竞争
- 疑难问题排查分析手段