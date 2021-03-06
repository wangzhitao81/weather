package com.example.weather;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.weather.helper.HttpHelper;

@SpringBootTest
class HttpHelperTests {
	@Autowired
	HttpHelper httpHelper;
	@Test
	void provinceCodeTest() {
		Optional<String> code = httpHelper.getProvinceCode("江苏");
		Assert.isTrue("10119".equals(code.get()), "获取成功");
	}
	@Test
	void provinceCode2Test() {
		Optional<String> code = httpHelper.getProvinceCode("江苏2");
		Assert.isTrue(!code.isPresent(),"不存在江苏2的省份编码");
	}
	@Test
	void cityCodeTest() {
		Optional<String> code = httpHelper.getCityCode("10119","苏州");
		Assert.isTrue("04".equals(code.get()), "获取成功");
	}
	@Test
	void cityCode2Test() {
		Optional<String> code = httpHelper.getCityCode("10119","苏州2");
		Assert.isTrue(!code.isPresent(), "不存在苏州2的编码");
	}
	@Test
	void countyCodeTest() {
		Optional<String> code = httpHelper.getCountyCode("10119","04","吴江");
		Assert.isTrue("07".equals(code.get()), "获取成功");
	}
	@Test
	void countyCode2Test() {
		Optional<String> code = httpHelper.getCountyCode("10119","04","吴江2");
		Assert.isTrue(!code.isPresent(), "不存在吴江2的编码");
	}
	@Test
	void weather1Test() {
		String province = "10119";
		String city = "04";
		String county = "07";
		Optional<Integer> temperature = httpHelper.getTemperature(province,city,county);
		Assert.isTrue(temperature.isPresent(), "获取成功");
	}
	@Test
	void weather2Test() {
		String province = "90119";
		String city = "04";
		String county = "07";
		Optional<Integer> temperature = httpHelper.getTemperature(province,city,county);
		Assert.isTrue(!temperature.isPresent(), "不存在的地址信息");
	}
}
