package org.example.springboot.config;

import org.example.springboot.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public LoginFilter loginFilter() {
        return new LoginFilter();
    }

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterRegistration(LoginFilter filter) {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>(filter);
        bean.addUrlPatterns("/contacts/*");
        bean.setOrder(1);
        return bean;
    }
}
