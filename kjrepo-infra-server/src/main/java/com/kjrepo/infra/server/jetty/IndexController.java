package com.kjrepo.infra.server.jetty;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class IndexController {

	@RequestMapping("/")
	public Object index(HttpServletResponse response) {
		return "ok";
	}

}