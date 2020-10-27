package com.leo.labs.jenkins.qps.controller;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.RateLimiter;

@RestController
public class HelloController {
	public static final String Succ = "succ";
	public static final String Fail = "fail";

	@GetMapping("hello")
	public String hello() {
		return Succ;
	}
	public static void main(String[] args) {
		try {

			String name="";
			Assert.hasText(name, "test not exist");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	RateLimiter rateLimiter = RateLimiter.create(1);

	@GetMapping("helloLimited")
	public String helloLimited() {
		if (rateLimiter.tryAcquire()) {
			return Succ;
		} else {
			return Fail;
		}
	}
}
