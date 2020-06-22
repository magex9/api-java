package ca.magex.crm.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * A maven mojo which attaches all of the files in the src/main/config folder as
 * attachments inside the maven repository which can be used referenced during
 * deploy processes by the projects classifiers.
 * 
 * @author magex
 *
 */
@Mojo(name = "attachments", requiresProject = true, defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class AttachmentsMojo extends AbstractMavenMojo {

	public void execute() throws MojoExecutionException {
		try {
			for (File file : fileFilesToAttach(new File(basedir + "/src/main/config"))) {
				attachFile(file);
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Error running mojo", e);
		}
	}

	private List<File> fileFilesToAttach(File rootDir) {
		List<File> files = new ArrayList<File>();
		if (rootDir.getName().startsWith("."))
			return files;
		if (rootDir.exists() && rootDir.isDirectory()) {
			for (File file : rootDir.listFiles()) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					files.addAll(fileFilesToAttach(file));
				}
			}
		}
		return files;
	}

	private void attachFile(File file) {
		String filename = file.getName();
		int index = filename.lastIndexOf('.');
		if (index == -1) {
			getLog().info("No file extension found");
			return;
		}
		String classifier = filename.substring(0, index);
		String packaging = filename.substring(index + 1);
		getLog().info("Attaching file " + file.getAbsolutePath() + " as " + classifier + "." + packaging);
		mavenProjectHelper.attachArtifact(mavenProject, packaging, classifier, file);
	}

}
