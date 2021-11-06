package com.example.weather.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
	@Bean
    public RetryTemplate simpleRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
//        Map<Class<? extends Throwable>, Boolean> maps = new HashMap<Class<? extends Throwable>, Boolean>();
//        maps.put(TimeoutException.class, true);
//        maps.put(UnknownHostException.class, true);
//        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(10, maps);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(10);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(30000);
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setMaxInterval(15000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML,MediaType.TEXT_PLAIN));
		restTemplate.getMessageConverters().add(converter);
		return restTemplate;
	}
}
