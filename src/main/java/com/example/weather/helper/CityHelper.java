package com.example.weather.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.web.client.RestTemplate;

@Component
public class CityHelper {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RestTemplate restTemplate;
	private static final String urlPrefix = "http://www.weather.com.cn/data/city3jdata/provshi/";
	
	public Optional<String> getCode(String provinceCode,String cityName) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", "text/html");
	        HttpEntity entity = new HttpEntity(headers);
	        Map<String, String> params = new HashMap<String, String>();
			ParameterizedTypeReference<Map<String,String>> responseType = new ParameterizedTypeReference<Map<String,String>>() {};
	        ResponseEntity<Map<String,String>> resp = restTemplate.exchange(urlPrefix+provinceCode+".html", HttpMethod.GET, entity, responseType,params);
//			Map<String,String> responseMap = (Map<String, String>) restTemplate.getForEntity(url, ProvinceEntity.class);
	        Map<String,String> responseMap = resp.getBody();
	        Optional<Entry<String, String>> result = responseMap.entrySet().stream().filter(entry -> entry.getValue().equals(cityName)).findFirst();
			if(result.isPresent()) {
				//TODO:正常情况下不应该在日志中存在表达式
				logger.info("获取城市编码,{},{},{}",provinceCode,cityName,result.get().getKey());
				return Optional.of(result.get().getKey());
			}
		}catch(Exception ex) {
			logger.error("获取城市编码异常，{},{}",provinceCode,cityName,ex);
		}
		
		return Optional.ofNullable(null);
		
	}
}
