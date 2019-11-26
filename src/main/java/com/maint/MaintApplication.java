package com.maint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.maint.system.mapper")
public class MaintApplication {
	public static void main(String[] args) {
		SpringApplication.run(MaintApplication.class, args);
	}
}