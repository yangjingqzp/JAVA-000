使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例

-XX:+PrintGCDetails 开启打印 GC 日志详情信息
-XX:+PrintGCDateStamps 开启打印 GC 日志的时间戳
-Xloggc:gc.demo.log 设置把 GC 日志保存到文件及指定文件路径

+XX:+UserSerialGC 指定串行 GC
-XX:+UseParallelGC 指定并行 GC
-XX:+UseConcMarkSweepGC 指定 CMS GC
-XX:+UseG1GC 指定 G1 GC

通过运行下面 GC 分析程序，查看 GC 日志
```
/*
演示GC日志生成与解读
*/
public class GCLogAnalysis {
    private static Random random = new Random();

    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数; 可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象; 进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内,持续循环
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束!共生成对象次数:" + counter.longValue());
    }

    // 生成对象
    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
```

## 串行 GC
java -XX:+UseSerialGC -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
java -XX:+UseSerialGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -jar gateway-server-0.0.1-SNAPSHOT.jar

```
2020-10-28T08:29:41.312-0800: [GC (Allocation Failure) 2020-10-28T08:29:41.312-0800: [DefNew: 69952K->8704K(78656K), 0.0204296 secs] 69952K->24993K(253440K), 0.0205123 secs] [Times: user=0.01 sys=0.01, real=0.02 secs]
...
2020-10-28T08:29:41.658-0800: [GC (Allocation Failure) 2020-10-28T08:29:41.658-0800: [DefNew: 69952K->69952K(78656K), 0.0000316 secs]2020-10-28T08:29:41.658-0800: [Tenured: 159339K->174747K(174784K), 0.0349656 secs] 229291K->177782K(253440K), [Metaspace: 2704K->2704K(1056768K)], 0.0350657 secs] [Times: user=0.03 sys=0.01, real=0.04 secs]
2020-10-28T08:29:41.722-0800: [Full GC (Allocation Failure) 2020-10-28T08:29:41.722-0800: [Tenured: 174747K->174739K(174784K), 0.0420249 secs] 253331K->190743K(253440K), [Metaspace: 2704K->2704K(1056768K)], 0.0420818 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]
...
2020-10-28T08:29:42.201-0800: [Full GC (Allocation Failure) 2020-10-28T08:29:42.202-0800: [Tenured: 174780K->174168K(174784K), 0.0464244 secs] 253369K->238981K(253440K), [Metaspace: 2704K->2704K(1056768K)], 0.0465554 secs] [Times: user=0.04 sys=0.00, real=0.04 secs]
执行结束!共生成对象次数:4120
Heap
 def new generation   total 78656K, used 66609K [0x00000007b0000000, 0x00000007b5550000, 0x00000007b5550000)
  eden space 69952K,  95% used [0x00000007b0000000, 0x00000007b410c4c8, 0x00000007b4450000)
  from space 8704K,   0% used [0x00000007b4cd0000, 0x00000007b4cd0000, 0x00000007b5550000)
  to   space 8704K,   0% used [0x00000007b4450000, 0x00000007b4450000, 0x00000007b4cd0000)
 tenured generation   total 174784K, used 174168K [0x00000007b5550000, 0x00000007c0000000, 0x00000007c0000000)
   the space 174784K,  99% used [0x00000007b5550000, 0x00000007bff66298, 0x00000007bff66400, 0x00000007c0000000)
 Metaspace       used 2711K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 295K, capacity 386K, committed 512K, reserved 1048576K
```
第一行中
GC (Allocation Failure) 的 `GC` 表示发生了年轻代 GC 还是 Full GC，括号里面的内容是 GC 的原因（Allocation Failure 内存分配失败）。
DefNew: 69952K->8704K(78656K), 0.0204296 secs 中的 DefNew 表示 Serial 串行回收（Tenured 表示进行老年代回收），后面表示新生代现在分配有 78656K 内存，这次回收从 69952K降低到了 8704K，回收了 61248K（78%）的内存，用了 0.0204296 secs。
69952K->24993K(253440K), 0.0205123 secs，表示现在堆中已分配总内存为 253440K，这里总内存从 69952K 回收到 24993K，回收了 44959K，因为刚开始生成对象，所以总的使用的内存最开始都是在新生代，堆中总使用内存和年轻代中总使用内存是一致的（69952K）。年轻代回收-堆回收的内容=从年轻代晋升到老年代的内存，62148K-44959K=17189K。
第三行中，看到堆使用内存为 253369K，马上达到最大堆内存，对象分配内存失败所以触发了 Full GC。
观察结论：
1. 堆内存分配的越大，GC 触发的次数越少，GC 单次使用的时间越多，生成的对象越多。
2. 年轻代 GC 时，只回收年轻代内存；老年代 GC 时，只触发回收老年代内存。
3. 分配内存比例：新生代中 eden:s1:s0 = 8:1:1， 新生代：老年代 = 1:2。总体如下图


## 并行 GC
java -XX:+UseParallelGC -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
java -XX:+UseParallelGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -jar gateway-server-0.0.1-SNAPSHOT.jar

```
2020-10-28T21:32:18.974-0800: [GC (Allocation Failure) [PSYoungGen: 33217K->5109K(38400K)] 33217K->12284K(125952K), 0.0272169 secs] [Times: user=0.01 sys=0.01, real=0.03 secs]
...
2020-10-28T21:32:19.157-0800: [Full GC (Ergonomics) [PSYoungGen: 10026K->0K(29184K)] [ParOldGen: 74884K->79359K(87552K)] 84910K->79359K(116736K), [Metaspace: 2705K->2705K(1056768K)], 0.0369049 secs] [Times: user=0.03 sys=0.01, real=0.04 secs]

```
第一行中的 PsYoungGen 表示了使用 Parallel Scavenge 策略对年轻代进行 GC
第二行中的 ParOldGen 表示的是 Serial Mark-Sweep-Compact 策略对老年代进行 GC
疑问：eden space + from space = PSYoungGen total，并且 from space = to space ？
老年代 GC 时，会同时触发年轻代的 GC。


## CMS GC
java -XX:+UseConcMarkSweepGC -Xms256m -Xmx256m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -jar gateway-server-0.0.1-SNAPSHOT.jar
```
2020-10-28T22:06:14.533-0800: [GC (Allocation Failure) 2020-10-28T22:06:14.533-0800: [ParNew: 139776K->17468K(157248K), 0.0329980 secs] 139776K->51955K(506816K), 0.0330543 secs] [Times: user=0.03 sys=0.05, real=0.03 secs]
...
2020-10-28T22:06:15.037-0800: [CMS-concurrent-abortable-preclean: 0.004/0.167 secs] [Times: user=0.27 sys=0.06, real=0.16 secs]
 (concurrent mode failure): 307795K->249888K(349568K), 0.0682195 secs] 464866K->249888K(506816K), [Metaspace: 2705K->2705K(1056768K)], 0.0683265 secs] [Times: user=0.07 sys=0.00, real=0.07 secs]
 ...
2020-10-28T22:06:15.335-0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 312284K(349568K)] 330228K(506816K), 0.0001893 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T22:06:15.335-0800: [CMS-concurrent-mark-start]
2020-10-28T22:06:15.338-0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T22:06:15.338-0800: [CMS-concurrent-preclean-start]
2020-10-28T22:06:15.339-0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T22:06:15.339-0800: [CMS-concurrent-abortable-preclean-start]
2020-10-28T22:06:15.339-0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
2020-10-28T22:06:15.339-0800: [GC (CMS Final Remark) [YG occupancy: 43241 K (157248 K)]2020-10-28T22:06:15.339-0800: [Rescan (parallel) , 0.0005254 secs]2020-10-28T22:06:15.340-0800: [weak refs processing, 0.0000138 secs]2020-10-28T22:06:15.340-0800: [class unloading, 0.0002610 secs]2020-10-28T22:06:15.340-0800: [scrub symbol table, 0.0009785 secs]2020-10-28T22:06:15.341-0800: [scrub string table, 0.0004252 secs][1 CMS-remark: 312284K(349568K)] 355525K(506816K), 0.0025300 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
2020-10-28T22:06:15.342-0800: [CMS-concurrent-sweep-start]
2020-10-28T22:06:15.344-0800: [CMS-concurrent-sweep: 0.002/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
2020-10-28T22:06:15.345-0800: [CMS-concurrent-reset-start]
2020-10-28T22:06:15.350-0800: [CMS-concurrent-reset: 0.005/0.005 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
```
CMS 默认使用 ParNewGC 策略对年轻代进行回收，
老年代 GC 步骤：
1. Initial Mark （初始标记）这步标记出 Root 直接引用的对象
2. Concurrent Mark （并发标记）：包括 concurrent-mark-start -> consurrent-mark
3. Concurrent Preclean（并发预处理）：包括 concurrent-preclean-start -> concurrent-preclean -> concurrent-abortable-preclean-start -> concurrent-abortable-preclean
4. Final Remark（最终标记）：包括 Final Remark -> Rescan (parallel) -> scrub symbol table -> scrub string table -> CMS-remark
5. Concurrent Sweep（并发清除）：包括 concurrent-sweep-start -> concurrent-sweep
6. Concurrent Reset （并发重置）：包括 concurrent-reset-start -> concurrent-reset

concurrent mode failure : 并发模式失败，CMS的目标就是在回收老年代对象的时候不要停止全部应用线程，在并发周期执行期间，用户的线程依然在运行，如果这时候如果应用线程向老年代请求分配的空间超过预留的空间（担保失败），就回触发concurrent mode failure，然后CMS的并发周期就会被一次Full GC代替——停止全部应用进行垃圾收集，并进行空间压缩。如果我们设置了UseCMSInitiatingOccupancyOnly和CMSInitiatingOccupancyFraction参数，其中CMSInitiatingOccupancyFraction的值是70，那预留空间就是老年代的30%。

## G1 GC
java -XX:+UseG1GC -Xms512m -Xmx512m  -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
```
2020-10-28T23:10:15.642-0800: [GC pause (G1 Evacuation Pause) (young) 28M->8253K(512M), 0.0055980 secs]
...
2020-10-28T23:10:16.562-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 357M->356M(512M), 0.0013596 secs]
2020-10-28T23:10:16.564-0800: [GC concurrent-root-region-scan-start]
2020-10-28T23:10:16.564-0800: [GC concurrent-root-region-scan-end, 0.0002389 secs]
2020-10-28T23:10:16.564-0800: [GC concurrent-mark-start]
2020-10-28T23:10:16.566-0800: [GC concurrent-mark-end, 0.0022431 secs]
2020-10-28T23:10:16.566-0800: [GC remark, 0.0027492 secs]
2020-10-28T23:10:16.569-0800: [GC cleanup 371M->371M(512M), 0.0004568 secs]
2020-10-28T23:10:16.576-0800: [GC pause (G1 Evacuation Pause) (young) 402M->368M(512M), 0.0070507 secs]
```
GC pause (G1 Evacuation Pause) (young) ：纯年轻代模式转移暂停，新生代minor GC
全局并发标记周期：
- Initial Mark (初始化标记)：并发标记的第一阶段，是伴随着一次 YGC 一次发生的（GC pause (G1 Humongous Allocation)）
- 开始并发ROOT区域扫描：GC concurrent-root-region-scan-start
- 结束并发ROOT区域扫描，并统计这个阶段的耗时：concurrent-root-region-scan-end
- 并发标记：concurrent-mark-start -> concurrent-mark-end
- 再次标记，即SATB buffer处理，并统计这个阶段耗时：GC remark
- 清理阶段会根据所有Region标记信息，计算出每个Region存活对象信息，并且把Region根据GC回收效率排序：GC cleanup -> GC pause (G1 Evacuation Pause) (mixed)
Evacuation Pause (Mixed) （转移暂停：混合模式）
