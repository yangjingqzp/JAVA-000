package io.kimmking.spring.bean.common.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/17 9:36 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Student implements Serializable  {
	private int id;
	private String name;

	public void init(){
		System.out.println("hello...........");
	}

	public Student create(){
		return new Student(101,"KK101");
	}
}
