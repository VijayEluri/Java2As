package net.histos.java2as.maven;

import net.histos.java2as.as3.transfer.TransferObjectConfiguration;
import net.histos.java2as.as3.transfer.TransferObjectProducer;
import net.histos.java2as.core.conf.PackageMapper;
import net.histos.java2as.core.conf.PropertyMapper;
import net.histos.java2as.core.conf.packages.PackageMapperRule;
import net.histos.java2as.core.conf.packages.RuleBasedPackageMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates transfer objects.
 *
 * @goal generate-dtos
 * @phase process-classes
 */
public class TransferObjectMojo extends GeneratorMojo<TransferObjectConfiguration> {

	private Logger _log = LoggerFactory.getLogger(getClass());

	//
	// Fields
	//

	// internal infrastructure

	/**
	 * The producer that creates the files.
	 */
	protected TransferObjectProducer producer;

	// parameters supplied by plugin end-users

	/**
	 * List of PropertyMapper class names to be used by java2as.
	 *
	 * @parameter
	 */
	private String[] propertyMappers = new String[]{};

	/**
	 * Custom PackageMapper class name to be used by java2as.
	 *
	 * @parameter
	 */
	private String packageMapper;

	/**
	 * List of PackageMapperRule class names to be used by java2as.
	 *
	 * @parameter
	 */
	private PackageMapperRule[] packageMapperRules = new PackageMapperRule[]{};

	/**
	 * Location to write custom classes.
	 *
	 * @parameter default-value="${project.build.directory}/java2as"
	 * @required
	 */
	private File customClassDir;

	/**
	 * Location to write base classes.
	 *
	 * @parameter default-value="${project.build.directory}/java2as"
	 * @required
	 */
	private File baseClassDir;

	/**
	 * Freemarker template to use for generation of custom classes.
	 *
	 * @parameter
	 */
	private File customClassTemplate;

	/**
	 * Freemarker template to use for generation of base classes.
	 *
	 * @parameter
	 */
	private File baseClassTemplate;

	/**
	 * Enable generation of the transfer object "manifest" file.
	 *
	 * @parameter
	 */
	private boolean generateManifest;

	/**
	 * Include [ArrayElementType] metadata for Array and ArrayCollection types.
	 *
	 * @parameter
	 */
	private boolean includeArrayElementType;

	/**
	 * Provide a base class which all Transfer Objects will extend.
	 *
	 * @parameter
	 */
	private String transferObjectBaseClass;

	//
	// Public Methods
	//

	public void execute() throws MojoExecutionException, MojoFailureException {

		super.execute();

		config = new TransferObjectConfiguration();
		config.setBaseClassDir(baseClassDir);
		config.setCustomClassDir(customClassDir);
		config.setBaseClassTemplate(baseClassTemplate);
		config.setCustomClassTemplate(customClassTemplate);
		config.setGenerateManifest(generateManifest);
		config.setIncludeArrayElementType(includeArrayElementType);
		config.setTransferObjectBaseClass(transferObjectBaseClass);
		loadConfiguratonClasses();

		_log.info("Configuration classes loaded successfully!");
		config.logConfiguration();

		List<Class<?>> candidateClasses = loadCandidateClasses();

		if (candidateClasses.size() == 0) {
			_log.warn("No candidate classes were found; produce will be skipped.");
			_log.warn("This is probably due to a configuration error in compiledClassesLocations");
			return;
		}

		_log.info("Candidate classes were found for generation: " + candidateClasses.size() + " total");

		producer = new TransferObjectProducer(config, candidateClasses);
		producer.produce();
	}

	//
	// Protected Methods
	//

	protected void loadConfiguratonClasses() throws MojoExecutionException {

		super.loadConfigurationClasses();

		try {

			ClassLoader loader = getClassLoader();

			if (propertyMappers.length > 0) {
				config.removeAllPropertyMappers();
				for (String propertyMapper : propertyMappers) {
					Class<PropertyMapper> propertyMapperClass = (Class<PropertyMapper>) loader.loadClass(propertyMapper);
					config.addPropertyMapper(propertyMapperClass.newInstance());
				}
			}

			if (packageMapper != null) {
				Class<PackageMapper> packageMapperClass = (Class<PackageMapper>) loader.loadClass(packageMapper);
				config.setPackageMapper(packageMapperClass.newInstance());
			}

			if (packageMapperRules.length > 0) {
				if (config.getPackageMapper() instanceof RuleBasedPackageMapper) {
					RuleBasedPackageMapper ruleBasedPackageMapper = (RuleBasedPackageMapper) config.getPackageMapper();
					for (PackageMapperRule packageMapperRule : packageMapperRules)
						ruleBasedPackageMapper.addMapperRule(packageMapperRule);
				} else {
					_log.warn("PackageMapper is not an instance of RuleBasedPackageMapper; packageMapperRules have been ignored");
				}
			}

		} catch (Exception e) {
			_log.error(e.getMessage());
			throw new MojoExecutionException(e.getMessage());
		}

	}


}
