package io.kimmking.spring.bean.factory;

import io.kimmking.spring.bean.domain.User;
import org.springframework.beans.factory.FactoryBean;

/**
 * @description
 * @author yangjing
 * @date 2020/7/19 11:27 AM
 * @version v1.0
 */
public class UserFactoryBean implements FactoryBean<User> {

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public User getObject() throws Exception {
		return new User(1, "zhangsan");
	}

	@Override
	public Class<?> getObjectType() {
		return User.class;
	}
}
