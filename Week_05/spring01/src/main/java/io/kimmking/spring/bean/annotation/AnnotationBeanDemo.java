package io.kimmking.spring.bean.annotation;

import io.kimmking.spring.bean.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/17 10:08 PM
 */
public class AnnotationBeanDemo {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(AnnotationBeanDemo.class);
		applicationContext.refresh();

		// 通过注解方式获取 Bean
		User user = applicationContext.getBean(User.class);
		System.out.println(user);

		applicationContext.close();
	}

	@Bean
	public User createBean() {
		return new User(11, "geektimeUser");
	}
}
