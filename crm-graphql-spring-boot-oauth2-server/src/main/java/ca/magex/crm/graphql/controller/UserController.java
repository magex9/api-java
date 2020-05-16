package ca.magex.crm.graphql.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@GetMapping("/user")
	public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
//		System.out.println(principal.getName());
//		System.out.println("**************************************");
//		System.out.println("Authorities");
//		principal.getAuthorities().forEach(System.out::println);
//		System.out.println("**************************************");
//		System.out.println("Attributes");
//		principal.getAttributes().forEach((k, v) -> {
//			System.out.println(k + " --> " + v);
//		});
//		System.out.println("**************************************");		
		return Collections.singletonMap("name", principal.getAttribute("login"));
	}
}