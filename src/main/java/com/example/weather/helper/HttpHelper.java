package com.example.weather.helper;

import java.nio.charset.StandardCharsets;
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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.example.weather.entity.WeatherInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HttpHelper {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RetryTemplate retryTemplate;
	private static final String provinceUrl = "http://www.weather.com.cn/data/city3jdata/china.html";
	private static final String cityPrefix = "http://www.weather.com.cn/data/city3jdata/provshi/";
	private static final String countyPrefix = "http://www.weather.com.cn/data/city3jdata/station/";
	private static final String weatherPrefix = "http://www.weather.com.cn/data/sk/";
	
	public Optional<String> getCode(String name,String url) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", "text/html");
	        HttpEntity entity = new HttpEntity(headers);
	        Map<String, String> params = new HashMap<String, String>();
			ParameterizedTypeReference<Map<String,String>> responseType = new ParameterizedTypeReference<Map<String,String>>() {};
			ResponseEntity<Map<String,String>> resp=null;
			restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			String respBody = retryTemplate.execute(new RetryCallback<String, Exception>() {
				@Override
				public String doWithRetry(RetryContext arg0) throws Exception {
					ResponseEntity<String> respEntity = restTemplate.getForEntity(url,String.class,params);
					return respEntity.getBody();
				}
	        });
	        
	        logger.info(respBody);
//			Map<String,String> responseMap = (Map<String, String>) restTemplate.getForEntity(url, ProvinceEntity.class);
	        ObjectMapper mapper = new ObjectMapper();
	        Map<String,String> responseMap =mapper.readValue(respBody, new TypeReference<Map<String, String>>(){});
	        Optional<Entry<String, String>> result = responseMap.entrySet().stream().filter(entry -> entry.getValue().equals(name)).findFirst();
			if(result.isPresent()) {
				//TODO:正常情况下不应该在日志中存在表达式
				logger.info("获取编码,{},{}",name,result.get().getKey());
				return Optional.of(result.get().getKey());
			}
		}catch(Exception ex) {
			logger.error("获取编码异常，{}",name,ex);
		}
		
		return Optional.ofNullable(null);
		
	}
	public Optional<String> getProvinceCode(String provinceName){
		return getCode(provinceName,provinceUrl);
	}
	public Optional<String> getCityCode(String provinceCode,String cityName){
		return getCode(cityName,cityPrefix+provinceCode+".html");
	}
	public Optional<String> getCountyCode(String provinceCode,String cityCode,String countyName){
		return getCode(countyName,countyPrefix+provinceCode+cityCode+".html");
	}
	public Optional<Integer> getTemperature(String province, String city, String county) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", "application/json");
	        HttpEntity entity = new HttpEntity(headers);
	        Map<String, String> params = new HashMap<String, String>();
//			ParameterizedTypeReference<Map<String,WeatherInfo>> responseType = new ParameterizedTypeReference<Map<String,WeatherInfo>>() {};
			String fullCode = new StringBuilder().append(province).append(city).append(county).toString();
			ResponseEntity<Map<String,String>> resp=null;
			restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			String url = weatherPrefix+fullCode+".html";
			String respBody = retryTemplate.execute(new RetryCallback<String, Exception>() {
				@Override
				public String doWithRetry(RetryContext arg0) throws Exception {
					ResponseEntity<String> respEntity = restTemplate.getForEntity(url, String.class,params);
					return respEntity.getBody();
				}
	        });
			if(respBody.indexOf("!DOCTYPE HTML")>0) {
				logger.warn("{}，网页无法访问",url);
				return Optional.ofNullable(null);
			}
			ObjectMapper mapper = new ObjectMapper();
	        Map<String,WeatherInfo> responseMap =mapper.readValue(respBody, new TypeReference<Map<String,WeatherInfo>>(){});
	        WeatherInfo jsonBody = responseMap.get("weatherinfo");
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
//	private Optional<String> getProvinceEntity(String provinceName) {
//		try {
//			HttpHeaders headers = new HttpHeaders();
//	        headers.set("Accept", "application/json");
//	        HttpEntity entity = new HttpEntity(headers);
//	        Map<String, String> params = new HashMap<String, String>();
//			ParameterizedTypeReference<List<ProvinceEntiy>> responseType = new ParameterizedTypeReference<List<ProvinceEntiy>>() {};
//	        ResponseEntity<List<ProvinceEntiy>> resp = restTemplate.exchange(url, HttpMethod.GET, entity, responseType,params);
//	//		Map<String,String> responseMap = (Map<String, String>) restTemplate.getForEntity(url, ProvinceEntity.class);
//	        List<ProvinceEntiy> list = resp.getBody();
//	        Optional<ProvinceEntiy> result = list.stream().filter(entry -> entry.getName().equals(provinceName)).findFirst();
//			if(result.isPresent()) {
//				return Optional.of(result.get().getName());
//			}
//		}catch(Exception ex) {
//			logger.error("获取省份编码异常,{}",provinceName,ex);
//		}
//	
//		return Optional.ofNullable(null);
//	
//	}
}
