package com.example.weather.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.weather.helper.CityHelper;
import com.example.weather.helper.CountyHelper;
import com.example.weather.helper.ProvinceHelper;
import com.example.weather.helper.WeatherHelper;

@Component
public class WeatherService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	ProvinceHelper provinceHelper;
	@Autowired
	CityHelper cityHelper;
	@Autowired
	CountyHelper countyHelper;
	@Autowired
	WeatherHelper weatherHelper;
	public Optional<Integer> getTemperature(String province, String city, String county){
		StringBuilder countyFullCode = new StringBuilder();
		try {
			Optional<String> provinceCode = provinceHelper.getCode(province);
			if(provinceCode.isPresent()) {
				countyFullCode.append(provinceCode.get());
			}else {
				
			}
		}catch(Exception ex) {
			logger.error("获取气温，获取省份编码异常,{}");
		}
		return Optional.ofNullable(Integer.MIN_VALUE);
	}
}
