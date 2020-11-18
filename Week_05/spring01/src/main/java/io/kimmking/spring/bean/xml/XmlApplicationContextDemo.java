package io.kimmking.spring.bean.xml;

import io.kimmking.spring.bean.common.domain.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/17 9:38 PM
 */
public class XmlApplicationContextDemo {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		// 通过 类型获取 xml 中定义的 Bean, 如果有多个 Bean，使用 primary 设置主类
		Student student = context.getBean(Student.class);
		System.out.println(student);

		// 通过名称获取 xml 中定义的 Bean
		Student student2 = (Student)context.getBean("student2");
		System.out.println(student2);
	}
}
