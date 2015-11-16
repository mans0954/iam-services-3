package org.openiam.rest;

import java.util.Map;
import java.util.Properties;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/environment")
public class EnvironmentRestController {

	@RequestMapping("/variables")
	public @ResponseBody Map<String, String> variables() {
		return System.getenv();
	}
	
	@RequestMapping("/properties")
	public @ResponseBody Properties properties() {
		return System.getProperties();
	}
}
