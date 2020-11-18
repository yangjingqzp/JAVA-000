package io.kimmking.spring.bean.definition;

import io.kimmking.spring.bean.domain.User;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/17 9:20 PM
 */
@ComponentScan(value = "io.kimmking.spring.bean")
public class BeanDefinitionCreateDemo {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(BeanDefinitionCreateDemo.class);

		// 通过手动组装 BeanDefinition 方式获取 Bean
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
		builder.addPropertyValue("id", 2)
			.addPropertyValue("name", "xiangzi");
		BeanDefinitionReaderUtils
			.registerWithGeneratedName(builder.getBeanDefinition(), applicationContext);

		applicationContext.refresh();

		System.out.println("User 类型的所有 Beans"+ applicationContext.getBeansOfType(User.class));
	}
}
