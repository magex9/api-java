package ca.magex.crm.transform;

import java.io.File;
import java.util.List;

import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.json.javadoc.JavadocDelegationBuilder;
import ca.magex.json.javadoc.JavadocInterfaceAdapterBuilder;
import ca.magex.json.javadoc.JavadocSlf4jDecoratorBuilder;

public class DecorationBuilders {
	
//	public static void main(String[] args) throws Exception {
//		decorate(CrmPermissionService.class);
//		decorate(CrmPermissionPolicy.class);
//		decorate(CrmOrganizationService.class);
//		decorate(CrmOrganizationPolicy.class);
//		decorate(CrmLocationService.class);
//		decorate(CrmLocationPolicy.class);
//		decorate(CrmPersonService.class);
//		decorate(CrmPersonPolicy.class);
//		decorate(CrmUserService.class);
//		decorate(CrmUserPolicy.class);
//		interfaceAdapter(
//			List.of(
//				CrmLookupPolicy.class.getName(), 
//				CrmOrganizationPolicy.class.getName(), 
//				CrmLocationPolicy.class.getName(), 
//				CrmPersonPolicy.class.getName(),
//				CrmUserPolicy.class.getName(), 
//				CrmPermissionPolicy.class.getName()
//			), 
//			"CrmPolicies"
//		);
//		interfaceAdapter(
//			List.of(
//				CrmLookupService.class.getName(), 
//				CrmOrganizationService.class.getName(), 
//				CrmLocationService.class.getName(), 
//				CrmPersonService.class.getName(),
//				CrmUserService.class.getName(), 
//				CrmPermissionService.class.getName()
//			), 
//			"CrmServices"
//		);
//	}
//
//	public static void decorate(Class<?> cls) throws Exception {
//		buildDelegate(cls);
//		buildLogger(cls);
//	}
//	
//	public static void buildDelegate(Class<?> cls) throws Exception {
//		File inputDir = new File("../crm-api/src/main/java");
//		String inputPackage = cls.getPackageName(); 
//		String inputClass = cls.getSimpleName();
//		File outputDir = new File("src/main/java");
//		String outputPackage = inputPackage.replaceAll(".api.", ".api.decorators."); 
//		String outputClass = inputClass + "Delegate"; 
//		File inputFile = new File(inputDir, inputPackage.replaceAll("\\.", "/") + "/" + inputClass +".java");
//		File outputFile = new File(outputDir, outputPackage.replaceAll("\\.", "/") + "/" + outputClass + ".java");
//		JavadocDelegationBuilder.build(inputFile, inputPackage, outputFile, outputPackage, outputClass);
//	}
//	
//	public static void buildLogger(Class<?> cls) throws Exception {
//		File inputDir = new File("../crm-api/src/main/java");
//		String inputPackage = cls.getPackageName(); 
//		String inputClass = cls.getSimpleName();
//		File outputDir = new File("src/main/java");
//		String outputPackage = inputPackage.replaceAll(".api.", ".api.slf4j."); 
//		String outputClass = inputClass + "Slf4jDecorator"; 
//		File inputFile = new File(inputDir, inputPackage.replaceAll("\\.", "/") + "/" + inputClass +".java");
//		File outputFile = new File(outputDir, outputPackage.replaceAll("\\.", "/") + "/" + outputClass + ".java");
//		JavadocSlf4jDecoratorBuilder.build(inputFile, inputPackage, outputFile, outputPackage, outputClass);
//	}
//	
//	public static void interfaceAdapter(List<String> interfaces, String adapterClass) throws Exception {
//		File sourceDir = new File("../crm-api/src/main/java");
//		File outputDir = new File("src/main/java");
//		String adapterPackage = "ca.magex.crm.api.adapters";
//		File adapterFile = new File(outputDir, adapterPackage.replaceAll("\\.", "/") + "/" + adapterClass + ".java");
//		JavadocInterfaceAdapterBuilder.build(sourceDir, interfaces, adapterFile, adapterPackage, adapterClass);
//	}
	
}
