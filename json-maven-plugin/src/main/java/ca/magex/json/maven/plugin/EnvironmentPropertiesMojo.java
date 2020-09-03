package ca.magex.json.maven.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * The environment properties mojo creates a file with all the system
 * environment variables and attaches it with the project at deploy time to your
 * maven repository.
 * 
 * This can be very useful when debugging which version of java was used,
 * operating system settings, users variables, etc to see differences between
 * build.
 * 
 * @author magex
 */
@Mojo(name = "env-props")
public class EnvironmentPropertiesMojo extends AbstractMojo {

	@Parameter(defaultValue = "target/env.properties", property = "outputFile", required = true)
	protected File outputFile;

	@Component
	protected MavenProject project;

	@Component
	protected MavenProjectHelper projectHelper;

	public void execute() throws MojoExecutionException {
		try {
			// Make the directory for the file if it doesnt already exist
			if (!outputFile.getParentFile().exists())
				outputFile.getParentFile().mkdirs();

			// Create the properties object
			Properties properties = new Properties();
			for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}

			// Write the file to the filesystem
			properties.store(new FileOutputStream(outputFile), "Generated on " + new Date());

			// Attach the artifact to the project
			projectHelper.attachArtifact(project, "props", "env", outputFile);
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file", e);
		}
	}

}