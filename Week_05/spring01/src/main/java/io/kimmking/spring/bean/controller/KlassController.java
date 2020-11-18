package io.kimmking.spring.bean.controller;

import io.kimmking.spring.bean.common.domain.Klass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangjing
 * @version v1.0
 * @description
 * @date 2020/11/18 9:11 PM
 */
@RestController
public class KlassController {
	@Autowired
	private Klass klass;

	@GetMapping("/test")
	public String test() {
		System.out.println(klass);
		return "";
	}
}
