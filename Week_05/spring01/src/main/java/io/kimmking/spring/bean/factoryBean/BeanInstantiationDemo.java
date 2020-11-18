package io.kimmking.spring.bean.factoryBean;

import io.kimmking.spring.bean.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/17 9:57 PM
 */
public class BeanInstantiationDemo {

	public static void main(String[] args) {
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("bean-instantiation-context.xml");

		// 通过 FactoryBean 方式获取 Bean
		User userByFactoryBean = beanFactory.getBean("user-by-factory-bean", User.class);
		System.out.println(userByFactoryBean);
	}
}
