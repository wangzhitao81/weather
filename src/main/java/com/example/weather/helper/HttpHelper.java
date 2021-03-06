package com.example.weather.helper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	        Map<String, String> params = new HashMap<String, String>();
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
				//TODO:???????????????????????????????????????????????????
				logger.info("????????????,{},{}",name,result.get().getKey());
				return Optional.of(result.get().getKey());
			}
		}catch(Exception ex) {
			logger.error("?????????????????????{}",name,ex);
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
	        Map<String, String> params = new HashMap<String, String>();
//			ParameterizedTypeReference<Map<String,WeatherInfo>> responseType = new ParameterizedTypeReference<Map<String,WeatherInfo>>() {};
			String fullCode = new StringBuilder().append(province).append(city).append(county).toString();
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
				logger.warn("{}?????????????????????",url);
				return Optional.ofNullable(null);
			}
			ObjectMapper mapper = new ObjectMapper();
	        Map<String,WeatherInfo> responseMap =mapper.readValue(respBody, new TypeReference<Map<String,WeatherInfo>>(){});
	        WeatherInfo jsonBody = responseMap.get("weatherinfo");
	        if(ObjectUtils.isEmpty(jsonBody)) {
	        	logger.warn("??????????????????weatherinfo??????{}",fullCode);
	        	return Optional.ofNullable(null);
	        }else {
	        	logger.info("??????{}???????????????,{}",fullCode,jsonBody.getTemp());
				return Optional.ofNullable(new Double(jsonBody.getTemp()).intValue());
	        }
		}catch(Exception ex) {
			logger.error("???????????????????????????{}{}{}",province,city,county,ex);
		}
		
		return Optional.ofNullable(null);
		
	}
}
