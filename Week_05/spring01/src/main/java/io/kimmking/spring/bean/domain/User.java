package io.kimmking.spring.bean.domain;

import lombok.AllArgsConstructor;

/**
 * @description
 * @author yangjing
 * @date 2020/11/17 9:16 PM
 * @version v1.0
 */
@AllArgsConstructor
public class User {
	private Integer id;

	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", name='" + name + '\'' +
			'}';
	}
}
