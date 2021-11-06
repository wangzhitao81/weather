package com.example.weather.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather.entity.RestResult;
import com.example.weather.enums.ResultStatus;
import com.example.weather.helper.CityHelper;
import com.example.weather.helper.CountyHelper;
import com.example.weather.helper.ProvinceHelper;
import com.example.weather.helper.WeatherHelper;

@RestController
@RequestMapping("weather")
public class WeatherController {
	@Autowired
	ProvinceHelper provinceHelper;
	@Autowired
	CityHelper cityHelper;
	@Autowired
	CountyHelper countyHelper;
	@Autowired
	WeatherHelper weatherHelper;
	@GetMapping("/temperature")
	public RestResult<Integer> temperature(@RequestParam("province") String province,@RequestParam("city") String city,@RequestParam("county") String county){
		Optional<String> provinceCode = provinceHelper.getCode(province);
		if(!provinceCode.isPresent()){
			return RestResult.failure("不正确的参数,错误的省份名称");
		}
		Optional<String> cityCode = cityHelper.getCode(provinceCode.get(),city);
		if(!cityCode.isPresent()){
			return RestResult.failure("不正确的参数,错误的城市名称");
		}
		Optional<String> countyCode = countyHelper.getCode(provinceCode.get(),cityCode.get(),county);
		if(!countyCode.isPresent()){
			return RestResult.failure("不正确的参数,错误的区县名称");
		}
		Optional<Integer> temperature = weatherHelper.getTemperature(provinceCode.get(),cityCode.get(),countyCode.get());
		if(!temperature.isPresent()) {
			return RestResult.failure("未能获取到温度信息,请稍后重试");
		}else {
			return RestResult.success(ResultStatus.OK,temperature.get());
		}
	}
	
}
