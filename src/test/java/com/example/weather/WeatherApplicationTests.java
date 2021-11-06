package com.example.weather;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.weather.helper.CityHelper;
import com.example.weather.helper.CountyHelper;
import com.example.weather.helper.ProvinceHelper;
import com.example.weather.helper.WeatherHelper;

@SpringBootTest
class WeatherApplicationTests {
	@Autowired
	ProvinceHelper provinceHelper;
	@Autowired
	CityHelper cityHelper;
	@Autowired
	CountyHelper countyHelper;
	@Autowired
	WeatherHelper weatherHelper;
	@Test
	void provinceCodeTest() {
		Optional<String> code = provinceHelper.getCode("江苏");
		Assert.isTrue("10119".equals(code.get()), "获取成功");
	}
	@Test
	void cityCodeTest() {
		Optional<String> code = cityHelper.getCode("10119","苏州");
		Assert.isTrue("04".equals(code.get()), "获取成功");
	}
	@Test
	void countyCodeTest() {
		Optional<String> code = countyHelper.getCode("10119","04","吴江");
		Assert.isTrue("07".equals(code.get()), "获取成功");
	}
	@Test
	void weather1Test() {
		String province = "10119";
		String city = "04";
		String county = "07";
		Optional<Integer> temperature = weatherHelper.getTemperature(province,city,county);
		Assert.isTrue(temperature.isPresent(), "获取成功");
	}
}
