package com.example.dynamodbdemo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/*
 * Current configuration exclude from application initialization all security components / all web components
 */

@Configuration
@ComponentScan(
    basePackages = {"com.example.dynamodbdemo"},
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)})
public class ApplicationTestConfig {}
