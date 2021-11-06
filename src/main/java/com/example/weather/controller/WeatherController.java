package com.example.weather.controller;

import java.util.Optional;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather.entity.RestResult;
import com.example.weather.enums.ResultStatus;
import com.example.weather.helper.HttpHelper;

@RestController
@RequestMapping("weather")
public class WeatherController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final int MAX_TPS = 100; 
	/** * Semaphore主限流，全局就行 */ 
	private static final Semaphore WEATHER_SEMAPHORE = new Semaphore(MAX_TPS);
	@Autowired
	HttpHelper httpHelper;
	@GetMapping("/temperature")
	public RestResult<Integer> temperature(@RequestParam("province") String province,@RequestParam("city") String city,@RequestParam("county") String county){
		Optional<String> provinceCode = httpHelper.getProvinceCode(province);
		if(!provinceCode.isPresent()){
			return RestResult.failure("不正确的参数,错误的省份名称");
		}
		Optional<String> cityCode = httpHelper.getCityCode(provinceCode.get(),city);
		if(!cityCode.isPresent()){
			return RestResult.failure("不正确的参数,错误的城市名称");
		}
		Optional<String> countyCode = httpHelper.getCountyCode(provinceCode.get(),cityCode.get(),county);
		if(!countyCode.isPresent()){
			return RestResult.failure("不正确的参数,错误的区县名称");
		}
		if (!WEATHER_SEMAPHORE.tryAcquire()) {
			logger.warn("请求频率超过限制：" + MAX_TPS);
			return RestResult.failure("请求频率超过限制：" + MAX_TPS); 
		}
		try {
			Optional<Integer> temperature = httpHelper.getTemperature(provinceCode.get(),cityCode.get(),countyCode.get());
			if(!temperature.isPresent()) {
				return RestResult.failure("未能获取到温度信息,请稍后重试");
			}else {
				return RestResult.success(ResultStatus.OK,temperature.get());
			}
		} catch (Exception e) {
			// 错误处理 
			logger.error("业务处理失败",e);
			return RestResult.failure("业务处理失败"); 
		} finally { 
			// 04. 一定要释放，否则导致接口假死无法处理请求 
			WEATHER_SEMAPHORE.release(); 
		}
	}
	/** 
	 * 最大信号量，例如此处3，生成环境可以做成可配置项，通过注入方式进行注入 
	 *
	*/ 
	private static final int MAX_SEMAPHORE = 10; 
	/** * Semaphore主限流，全局就行 */ 
	private static final Semaphore SEMAPHORE = new Semaphore(MAX_SEMAPHORE); 
	@RequestMapping("/limit") public String limit() { 
		// 01.使用非阻塞tryAcquire，如果获取不到就快速返回失败 
		if (!SEMAPHORE.tryAcquire()) {
			logger.warn("请求频率超过限制：" + MAX_SEMAPHORE);
			return "请求频率超过限制：" + MAX_SEMAPHORE; 
		} 
		// 02. 如果能进入到这里，说明一定获取到了许可证 
		// TODO: 可能的参数校验，注意如果参数校验不通过，一定要调用release方法 /*if (valid(xxx)) { SEMAPHORE.release(); }*/ 
		try { 
			// 03. 模拟业务处理，假如需要1s 
			Thread.sleep(1000); 
			logger.info("业务处理成功");
			return "业务处理成功"; 
		} catch (InterruptedException e) { 
			// 错误处理 
			logger.error("业务处理成功",e);
			return "业务处理失败"; 
		} finally { 
			// 04. 一定要释放，否则导致接口假死无法处理请求 
			SEMAPHORE.release(); 
		} 
	}
}
