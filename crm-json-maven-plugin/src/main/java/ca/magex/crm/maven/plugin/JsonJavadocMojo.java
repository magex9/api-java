package ca.magex.crm.maven.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import ca.magex.json.javadoc.JavadocBuilder;

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
		try {
			FileUtils.mkdir(new File(basedir, "src/main/generated").getAbsolutePath());
			JavadocBuilder.processDirectory(new File(basedir, "src/main/java"), new File(basedir, "src/main/generated/" + mavenProject.getArtifactId() + ".json"));
			
			getLog().info("Building decorators: " + decorators);
			decorators.forEach(d -> d.build(basedir));

			getLog().info("Building adapters: " + adapters);
			adapters.forEach(a -> a.build(basedir));
			
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to execute mojo", e);
		}
	}

}

