package ca.magex.crm.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;

/**
 * An abstract maven mojo that can be used by other projects which need access
 * to many of the common methods for getting dependency trees, settings,
 * information about the project etc.
 * 
 * Many of the plugins in this suite of tools extension this abstract version with
 * all the information already inside.
 * 
 * @author magex
 *
 */
public abstract class AbstractMavenMojo extends AbstractMojo {

	@Component
	protected MavenProject mavenProject;

	@Component
	protected DependencyGraphBuilder dependencyGraphBuilder;

	@Component
	protected MavenProjectHelper mavenProjectHelper;

	@Component
	protected MavenProjectBuilder mavenProjectBuilder;

	@Component
	protected BuildPluginManager pluginManager;

	@Component
	protected RepositorySystem repositorySystem;

	@Component
	protected Settings settings;

	@Parameter(property = "mavenSession", defaultValue = "${session}", readonly = true)
	protected MavenSession mavenSession;

	@Parameter(property = "repoteRepositories", defaultValue = "${project.remoteArtifactRepositories}")
	protected List<?> remoteRepositories;

	@Parameter(property = "localRepository", defaultValue = "${localRepository}")
	protected ArtifactRepository localRepository;

	@Parameter(defaultValue = "${project.build.directory}", property = "workingDirectory", required = true)
	protected File workingDirectory;

	@Parameter(defaultValue = "${project.basedir}", property = "basedir", required = true)
	protected File basedir;

	@Parameter(defaultValue = "${session}", property = "session", required = true)
	protected MavenSession session;

	@Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
	protected List<String> classpath;

	@Parameter(property = "maven.test.skip", required = false, readonly = true, defaultValue = "false")
	protected boolean skipTests;

	public MavenProject buildMavenProject(File file) {
		try {
			return new MavenProject(new MavenXpp3Reader().read(new FileReader(file)));
		} catch (Exception e) {
			throw new RuntimeException("Unable to build maven project from file: " + file.getAbsolutePath());
		}
	}

	public Map<MavenProject, List<MavenProject>> buildProjectMap() throws MojoExecutionException {
		return appendProjects(mavenProject, new HashMap<MavenProject, List<MavenProject>>());
	}

	private Map<MavenProject, List<MavenProject>> appendProjects(MavenProject mavenProject,
			Map<MavenProject, List<MavenProject>> projectMap) throws MojoExecutionException {
		if (!projectMap.containsKey(mavenProject)) {
			for (MavenProject child : projectMap.get(mavenProject)) {
				appendProjects(child, projectMap);
			}
			projectMap.put(mavenProject, getDependencyProjects(mavenProject));
		}
		return projectMap;
	}

	protected List<MavenProject> getDependencyProjects(MavenProject mavenProject) throws MojoExecutionException {
		try {
			List<MavenProject> projectList = new ArrayList<MavenProject>();
			for (Object obj : mavenProject.getDependencies()) {
				Dependency dep = (Dependency) obj;
				Artifact artifact = repositorySystem.createDependencyArtifact(dep);
				projectList.add(mavenProjectBuilder.buildFromRepository(artifact, remoteRepositories, localRepository));
			}

			for (Object obj : mavenProject.getModules()) {
				String module = (String) obj;
				Artifact artifact = repositorySystem.createArtifact(mavenProject.getGroupId(), module,
						mavenProject.getVersion(), "", mavenProject.getPackaging());
				projectList.add(mavenProjectBuilder.buildFromRepository(artifact, remoteRepositories, localRepository));
			}
			return projectList;
		} catch (Exception e) {
			throw new RuntimeException("Problem getting dependency projects", e);
		}
	}

	public void loadClasspath() {
		try {
			Set<URL> urls = new HashSet<URL>();
			for (String path : classpath) {
				urls.add(new File(path).toURI().toURL());
			}
			ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]),
					Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load the project's classpath", e);
		}
	}

	public InputStream getResourceAsStream(String name) throws FileNotFoundException {
		File file = new File("src/main/resources/" + name);
		if (file.exists())
			return new FileInputStream(file);

		File moduleFile = new File(mavenProject.getArtifactId() + "/src/main/resources/" + name);
		if (moduleFile.exists())
			return new FileInputStream(moduleFile);

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		if (is == null)
			throw new FileNotFoundException("Unable to locate resource: " + name);
		return is;
	}

	public String getResourceAsString(String name) {
		try {
			return IOUtils.toString(getResourceAsStream(name), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("Unable to read content: " + name, e);
		}
	}

	public void attachFile(String classifier, String packaging, File file) {
		mavenProjectHelper.attachArtifact(mavenProject, packaging, classifier, file);
	}

}
