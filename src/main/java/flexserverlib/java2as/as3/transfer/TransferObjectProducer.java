package flexserverlib.java2as.as3.transfer;

import flexserverlib.java2as.as3.As3Type;
import flexserverlib.java2as.as3.DefaultAs3TypeMapper;
import flexserverlib.java2as.core.AbstractProducer;
import flexserverlib.java2as.core.conf.PropertyMapper;
import flexserverlib.java2as.core.conf.TypeMapper;
import flexserverlib.java2as.core.meta.JavaProperty;
import flexserverlib.java2as.core.meta.JavaTransferObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferObjectProducer extends AbstractProducer {
	
	private TransferObjectConfiguration config;
	private List<Class<?>> classes;

	public TransferObjectProducer(TransferObjectConfiguration config, List<Class<?>> classes) {
		this.config = config;
		this.classes = classes;
	}

	@Override
	public void produce() {
		// filter
		List<Class<?>> matchingClasses = findMatchingClasses(config.getMatchers(), classes);

		// build metadata
		List<JavaTransferObject> transferObjects = buildMetadata(matchingClasses);

		// build template configuration
		Configuration fmConfig = new Configuration();
		fmConfig.setClassForTemplateLoading(TransferObjectProducer.class, "");

		try {
			Template template = fmConfig.getTemplate("to-base.ftl");
			Map<String, Object> model = new HashMap<String, Object>();

			// setup default PropertyMapper, if necessary
			List<PropertyMapper<As3Property>> propertyMappers = config.getPropertyMappers();
			if (propertyMappers.size() == 0)
				propertyMappers.add(new DefaultAs3PropertyMapper());

			// do conversion
			TransferObjectMapper mapper = new TransferObjectMapper(propertyMappers);
			List<As3TransferObject> as3TransferObjects = mapper.performMappings(transferObjects);
			
			// generate files
			for (As3TransferObject transferObject : as3TransferObjects) {
				model.put("model", transferObject);
				Writer writer = new PrintWriter(System.out);
				template.process(model, writer);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}
	}

	private List<JavaTransferObject> buildMetadata(List<Class<?>> classes) {
		List<JavaTransferObject> transferObjects = new ArrayList<JavaTransferObject>();
		for (Class<?> clazz : classes)
			transferObjects.add(new JavaTransferObject(clazz));
		return transferObjects;
	}
}