package ca.magex.crm.transform;

import java.io.File;

import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.json.javadoc.LoggerDelegationBuilder;

public class DelegateBuilders {
	
	public static void main(String[] args) throws Exception {
		buildLogger(CrmPermissionService.class);
		buildLogger(CrmPermissionPolicy.class);
		buildLogger(CrmOrganizationService.class);
		buildLogger(CrmOrganizationPolicy.class);
		buildLogger(CrmLocationService.class);
		buildLogger(CrmLocationPolicy.class);
		buildLogger(CrmPersonService.class);
		buildLogger(CrmPersonPolicy.class);
		buildLogger(CrmUserService.class);
		buildLogger(CrmUserPolicy.class);
	}
	
	public static void buildLogger(Class<?> cls) throws Exception {
		File inputDir = new File("../crm-api/src/main/java");
		String inputPackage = cls.getPackageName(); 
		String inputClass = cls.getSimpleName();
		File outputDir = new File("src/main/java");
		String outputPackage = inputPackage.replaceAll(".api.", ".slf4j."); 
		String outputClass = inputClass + "Slf4jDecorator"; 
		File inputFile = new File(inputDir, inputPackage.replaceAll("\\.", "/") + "/" + inputClass +".java");
		File outputFile = new File(outputDir, outputPackage.replaceAll("\\.", "/") + "/" + outputClass + ".java");
		LoggerDelegationBuilder.build(inputFile, inputPackage, outputFile, outputPackage, outputClass);
	}
	
}
