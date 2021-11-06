package com.example.weather.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.example.weather.entity.WeatherInfo;

@Component
public class WeatherHelper {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RestTemplate restTemplate;
	private static final String urlPrefix = "http://www.weather.com.cn/data/sk/";
	public Optional<Integer> getTemperature(String province, String city, String county) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", "application/json");
	        HttpEntity entity = new HttpEntity(headers);
	        Map<String, String> params = new HashMap<String, String>();
			ParameterizedTypeReference<Map<String,WeatherInfo>> responseType = new ParameterizedTypeReference<Map<String,WeatherInfo>>() {};
			String fullCode = new StringBuilder().append(province).append(city).append(county).toString();
			
	        ResponseEntity<Map<String,WeatherInfo>> resp = restTemplate.exchange(urlPrefix+fullCode+".html", HttpMethod.GET, entity, responseType,params);
	        Map<String,WeatherInfo> responseBody = resp.getBody();
	        WeatherInfo jsonBody = responseBody.get("weatherinfo");
	        if(ObjectUtils.isEmpty(jsonBody)) {
	        	logger.warn("接口没有返回weatherinfo域，{}",fullCode);
	        	return Optional.ofNullable(null);
	        }else {
	        	logger.info("获取{}的天气信息,{}",fullCode,jsonBody.getTemp());
				return Optional.ofNullable(new Double(jsonBody.getTemp()).intValue());
	        }
		}catch(Exception ex) {
			logger.error("获取天气信息异常，{}{}{}",province,city,county,ex);
		}
		
		return Optional.ofNullable(null);
		
	}
	
}
