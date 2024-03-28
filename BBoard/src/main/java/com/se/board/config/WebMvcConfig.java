package com.se.board.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.se.board.interceptor.LoggerInterceptor;
import com.se.board.interceptor.LoginCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor())
		        .excludePathPatterns("/css/**", "/images/**", "/js/**");

		registry.addInterceptor(new LoginCheckInterceptor())
				.addPathPatterns("/**/*.do")
				.excludePathPatterns("/log*", "/api/book/**", "/api/esboard/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*");
	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/templates/", "classpath:/static/")
				//.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
				.setCacheControl(CacheControl.noCache());
	}
}
