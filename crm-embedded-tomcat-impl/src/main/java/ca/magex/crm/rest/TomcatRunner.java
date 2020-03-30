package ca.magex.crm.rest;

import java.io.File;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;

public class TomcatRunner {

	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("target/embedded-tomcat");
		tomcat.setHostname("0.0.0.0");
		tomcat.setPort(8080);

		StandardContext ctx = (StandardContext) tomcat.addWebapp("/",
				new File("target/embedded-tomcat").getAbsolutePath());

		Tomcat.addServlet(ctx, ApiServlet.class.getName(), new ApiServlet());
		ctx.addServletMapping("/secure/*", ApiServlet.class.getName());
		
		LoginConfig config = new LoginConfig();
		config.setAuthMethod("BASIC");
		ctx.setLoginConfig(config);
		ctx.addSecurityRole("admin");
		SecurityConstraint constraint = new SecurityConstraint();
		constraint.addAuthRole("admin");
		SecurityCollection collection = new SecurityCollection();
		collection.addPattern("/secure/*");
		constraint.addCollection(collection);
		ctx.addConstraint(constraint);

		MemoryRealm realm = new MemoryRealm();
		realm.setPathname(new File("src/main/resources/users.xml").getAbsolutePath());
		tomcat.getEngine().setRealm(realm);

		tomcat.start();
		tomcat.getServer().await();
	}
	
}