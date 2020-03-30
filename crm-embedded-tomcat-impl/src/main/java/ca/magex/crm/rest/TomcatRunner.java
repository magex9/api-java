package ca.magex.crm.rest;

import java.io.File;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

public class TomcatRunner {

	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("target/embedded-tomcat");
		tomcat.setHostname("localhost");
		tomcat.setPort(8080);

		StandardContext ctx = (StandardContext) tomcat.addWebapp("/",
				new File("src/main/webapp").getAbsolutePath());

		Tomcat.addServlet(ctx, ApiServlet.class.getName(), new ApiServlet());
		ctx.addServletMapping("/*", ApiServlet.class.getName());

		tomcat.start();
		tomcat.getServer().await();
	}
}