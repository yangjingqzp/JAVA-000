学习笔记

1. 写代码实现 Spring Bean 的装配，方式越多越好(XML、Annotation 都可以)
代码在 spring01 模块中。
- annotation 包中用 @bean 注解方式装配
- definition 包中用 BeanDefinition 定义方式装配
- factoryBean 包中用 FactoryBean 方式装配
- xml 包中使用 Type、Name 方式查找装配

2. 给前面课程提供的 Student/Klass/School 实现自动配置和 Starter
spring01-starter 模块中定义了 starter
spring01-common 模块中定义了需要自动装配的类信息
spring01 中通过导入 spring01-common、spring01-starter 模块，并配置 klass 信息，
在 KlassController 中可以直接注入已经初始化好的 Klass 类实例

3. 研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法:
   1)使用 JDBC 原生接口，实现数据库的增删改查操作。 
   2)使用事务，PrepareStatement 方式，批处理方式，改进上述操作。 
   3)配置 Hikari 连接池，改进上述操作。
代码放在 spring01-jdbc 模块下 JDBCDemo 中。