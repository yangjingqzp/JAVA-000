package io.kimmking.spring.bean.starter;

import io.kimmking.spring.bean.common.domain.Student;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/18 8:33 PM
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "klass")
public class KlassProperties {
	private Integer age;

	private List<Student> students;
}
