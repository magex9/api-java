package ca.magex.crm.feign.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient
public interface CrmGraphQLClient {

	@RequestMapping("/graphql")
	public String Orgs();
}
