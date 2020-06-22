package ca.magex.json.maven.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import ca.magex.json.javadoc.JsondocBuilder;

/**
 * A maven mojo which which converts all the java source files to javadocs in json format.
 * 
 * @author magex
 *
 */
@Mojo(name = "javadoc", requiresProject = true, defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class JsonJavadocMojo extends AbstractMavenMojo {
	
	@Parameter(property = "decorators", required = false)
	protected List<InterfaceDecoratorConfig> decorators;

	@Parameter(property = "adapters", required = false)
	protected List<InterfaceAdapterConfig> adapters;

	public void execute() throws MojoExecutionException {
		File source = new File(basedir, "src/main/java");
		File jsondocs = new File(basedir, "src/main/jsondoc");
		File generated = new File(basedir, "src/main/generated");
		try {
			FileUtils.deleteDirectory(jsondocs);
			FileUtils.mkdir(jsondocs.getAbsolutePath());
			JsondocBuilder.createFiles(new File(basedir, "src/main/java"), new File(basedir, "src/main/jsondoc"));
			
			FileUtils.deleteDirectory(generated);
			FileUtils.mkdir(generated.getAbsolutePath());

			getLog().info("Building decorators: " + decorators);
			decorators.forEach(d -> d.build(source, generated));

			getLog().info("Building adapters: " + adapters);
			adapters.forEach(a -> a.build(source, generated));
			
		} catch (Exception e) {
			getLog().error("Failed to build generated classes", e);
			throw new MojoExecutionException("Failed to execute mojo", e);
		}
	}

}

