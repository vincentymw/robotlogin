package com.example.robotlogin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Component;

@Component
@MapperScan("com.example.robotlogin.mapper")
public class MybatisPlusConfig {
}
