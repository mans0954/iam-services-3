package org.openiam.rest;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/environment")
public class EnvironmentRestController {

	@RequestMapping("/container")
	public @ResponseBody String container() {
		String retVal = StringUtils.trimToNull(System.getenv("container"));
		return (retVal == null) ? "" : retVal;
	}
}
