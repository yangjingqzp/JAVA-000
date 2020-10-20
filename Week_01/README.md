学习笔记
## JVM 核心技术 - 基础知识
### Java语言
面向对象、静态类型、编译执行、有JVM/GC和运行时、跨平台的高级语言
运行时，上下文环境，如 虚拟机。

### 字节码技术
研究代码细节时，使用字节码找到根本的原因
java bytecode 由单字节（byte）的指令组成，理论上最多支持 256 个操作码（opcode）。

根据指令的性质，主要分为4大类：
- 操作栈的指令， 所有的计算都放在栈中。
变量的定义和值在本地变量表，计算需要load到栈中，计算完后store回本地变量表。
栈是用来执行的，本地变量是用来存储中间结果的，直接与栈发生交互。
JVM 是一台基于栈的计算器，栈内包括操作数、本地变量表，以及当前方法的运行时常量池的引用
- 程序流程控制指令
- 对象操作指令
对字节码来说，最小的操作单位是 int, 4个字节， 32 位
- 算术运算以及类型转换指令
JVM 是一台基于栈的计算器。
每个线程都有一个独立属于自己的线程栈（JVM Stack），用于存储栈帧（Frame）。
每一次方法调用，JVM 都会自动创建一个栈帧。
栈帧由操作数栈、局部变量数组以及一个 Class 引用组成。
### JVM 类加载器 *
1. 加载（Loading） 找到 Class 文件
    2. 验证（Verification） 验证格式 CAFABABE、依赖
    3. 准备（Preparation） 静态字段、方法表
    4. 解析（Resolution）符号解析为引用
5. 初始化（Initialization） 构造器、静态变量赋值、静态代码块
6. 使用（Using）
7. 卸载（Unloading）

#### 类的加载时机
- 当虚拟机启动的时候，初始化用户指定的主类，就是启动执行的 main 方法所在的类
- 当遇到用以新建目标类实例的 new 指令时，如果没被加载，需要加载
- 当调用静态方法时
- 访问静态字段时
- 子类的初始化时会触发父类的初始化
- 如果一个接口定义了 default 方法，那个依赖或实现类的初始化，会触发接口的初始化
- 使用发射API进行调用时
- JDK7 以后的，初始化该 MethodHandle 指向的方法所在的类

类可能被加载，不会被初始化
- 通过子类引用父类的静态字段，只会触发父类的初始化，不会触发子类的初始化
- 定义对象数组，不会触发该类的初始化，直到 new 一个对象
- 常量在编译期间会存入调用类的常量池中，本质上并没有直接引用定义常量的类
- 通过类名获取 Class 对象，不会触发类的初始化 Hello.class 不会让 Hello 类初始化
- 通过 Class.forName 加载指定类时，如果指定参数 initialize 为 false 时，也不会触发初始化
- ClassLoader 默认的 loadClass 方法，也不会触发初始化动作。需要 new 类时才会初始化

三类加载器, 存在的原因？
1. 启动类加载器 BootstrapClassLoader 加载jvm核心类， rt.jar
2. 扩展类加载器 ExtClassLoader 设置扩展类目录
3. 应用类加载器 AppClassLoader
4. 自定义加载器 CustomClassLoader

特点
- 双亲委托。应用类加载器在使用类时，会先查看父加载器中是否已经有了，如果没有再去父加载器中查找，
    有时就用，没有就自己加载
- 负责依赖。负责加载跟他相关的依赖的类
- 缓存加载。一个类被加载后，只会加载一次，加载后会在内部被缓存

添加引用类的几种方式
- 放到 JDK 的 lib/ext 下，或者 -Djava.ext.dirs
- java -cp/classpath 或者 class 文件放到当前路径
- 自定义 ClassLoader 加载
- 拿到当前执行类的 ClassLoader， 反射调用 addUrl 方法添加 jar 或者路径； JDK 9.0 之后 直接用 Class.forName() 实现。

### JVM 内存模型 *
JVM 是基于对栈的，
对象是由生命周期的，栈在方法执行完后就不要了；对象占有的内存不确定，栈的深度在开始时就知道
如果是原生数据类型的局部变量，那么它的内容就全部保留在线程栈上；如果是对象引用，则栈中的局部变量槽位中保存着对象
的引用地址，而实际的对象内容保存在堆中。
对象的成员变量与对象本身一起存储在堆上，不管成员变量的类型是原生数值，还是对象引用。
类的静态变量则和类定义一样都保存在堆中。
- 每个线程都只能访问自己的线程栈

Java进程 ： 栈+堆+非堆+JVM自身内存
线程执行过程中，一般会有多个方法组成调用栈（Stack Trace）,比如 A 调用 B，B 调用C... 每执行到一个方法，就会创建对应的 栈帧（Frame）
栈帧是一个逻辑上的概念，具体大小在一个方法编写完成后就确定了。
栈帧 包括 返回值、局部变量表、操作数栈、Class/Method 指针
一个虚拟机或者容器，Xmx 不超过总内存的 60%

JVM 堆内存
老年代（Old generation）：大对象、老对象
年轻代划分为3个内存池：新生代(Eden space、TLAB)、存活区(Survivor space, S0, S1)
Non-Heap 本质上还是 Heap，只是一般不归 GC 管理，里面划分3个内存池。
    Metaspace 元数据区（常量池、方法区）。持久代（永久代 Permanent generation）Java8 换了个名字叫 Metaspace
    CCS, Compresessed Class Space，存放 class 信息的 和 Metaspace 有交叉
    Code Cache，存放 JIT 编译器编译后的本地机器码

### CPU 与内存行为
- CPU 乱序执行
- volatile 关键字
- 原子性操作
- 内存屏障

### GC 的背景与一般原理
本质上是内存资源的有限性，需要共享使用、手工申请、手动释放

1. 引用计数
问题，循环引用，大家的计数永远不为0，导致：内存泄露->内存溢出
解决方法：引用跟踪，从根对象出发，找到引用对象，没有被标记的对象就是需要清理的
标记清除算法（Mark and Sweep）
- Marking 标记：遍历所有的可达对象，并在本地内存（native）中分门别类记下
- Sweeping 清除：不可达对象所占内存后面可以继续使用
内存不连续，需要做压缩整理。清理阶段需要 STW，让全世界停止下来

分代假设：大部分新生对象很快无用，存活较长时间的对象，可能存活更长时间
内存池划分：不同类型对象不同区域，不同处理策略
对象分配在新生代的 Eden 区，标记阶段 Eden 区存活的对象就会复制到存活区；
为什么是复制？不是移动？
对象存活到一定周期会提升到老年代。
由如下参数控制提升阈值
-XX: +MaxTenuringThreshold=15

可以作为 GC Roots 的对象
- 当前正在执行的方法里的局部变量和输入参数
- 活动线程 Active threads
- 所有类的静态字段（static field）
- JNI 引用

串行 GC (Serial GC) / ParNewGC
-XX: +UseSerialGC
-XX: +UseParNewGC 并行

并行 GC (Parallel GC)
-XX: +UseParallelGC
-XX: +UseParallelOldGC
-XX: ParallelGCThreads=N 来指定并行的线程数，默认是 CPU 核心数

CMS GC (Mostly Concurrent Mark And Sweep Garbage Collector)
并发GC
-XX: +UseConcMarkSweepGC
缺点：GC 复制

CMS GC - 六个阶段
a. Initial Work 初始标记
b. Concurrent Mark 并发标记
c. Concurrent Preclean 并发预清理

G1 GC Garbage-First 垃圾优先，哪一块的垃圾最多就优先处理它
设计目标：将 STW 停顿的时间和分布，变成可预期且可配置的。