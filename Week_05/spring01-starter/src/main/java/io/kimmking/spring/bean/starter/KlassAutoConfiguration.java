package io.kimmking.spring.bean.starter;

import io.kimmking.spring.bean.common.domain.Klass;
import io.kimmking.spring.bean.common.domain.Student;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/18 8:06 PM
 */
@Configuration
//@ConditionalOnBean(name = "students")
@ConditionalOnClass(Klass.class)
@EnableConfigurationProperties(KlassProperties.class)
public class KlassAutoConfiguration {
	@Resource
	private KlassProperties klassProperties;

	//@Resource(name = "students")
	//private List<Student> students;

	@Bean
	public Klass generateKlass() {
		Klass klass = new Klass();
		klass.setAge(klassProperties.getAge());
		klass.setStudents(klassProperties.getStudents());
		return klass;
	}
}
