package io.kimmking.spring.bean.starter;

import io.kimmking.spring.bean.common.domain.Student;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/18 8:51 PM
 */
@Configuration
@ConditionalOnClass(Student.class)
@ConfigurationProperties(prefix = "klass.students")
public class StudentAutoConfiguration {

}
