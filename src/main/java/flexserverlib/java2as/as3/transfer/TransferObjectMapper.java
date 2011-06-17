package flexserverlib.java2as.as3.transfer;

import flexserverlib.java2as.as3.As3Type;
import flexserverlib.java2as.core.conf.PropertyMapper;
import flexserverlib.java2as.core.conf.TypeMapper;
import flexserverlib.java2as.core.meta.DependencyKind;
import flexserverlib.java2as.core.meta.JavaProperty;
import flexserverlib.java2as.core.meta.JavaTransferObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cliff.meyers
 */
public class TransferObjectMapper {

	private PropertyMapper<As3Property> propertyMapper;
	private TypeMapper<As3Type> typeMapper;

	private Map<JavaTransferObject, As3TransferObject> transferObjectMap;
	private List<JavaTransferObject> javaTransferObjects;
	private List<As3TransferObject> as3TransferObjects;

	//
	// Constructors
	//

	public TransferObjectMapper(PropertyMapper<As3Property> propertyMapper, TypeMapper<As3Type> typeMapper) {
		this.propertyMapper = propertyMapper;
		this.typeMapper = typeMapper;
		this.transferObjectMap = new HashMap<JavaTransferObject, As3TransferObject>();
	}

	//
	// Public Methods
	//

	public List<As3TransferObject> performMappings(List<JavaTransferObject> javaTransferObjects) {

		this.javaTransferObjects = javaTransferObjects;
		this.as3TransferObjects = new ArrayList<As3TransferObject>();

		List<As3TransferObject> as3TransferObjects = new ArrayList<As3TransferObject>();

		for (JavaTransferObject javaTransferObject : javaTransferObjects) {

			As3TransferObject as3TransferObject = performMap(javaTransferObject);
			as3TransferObjects.add(as3TransferObject);
			transferObjectMap.put(javaTransferObject, as3TransferObject);

		}

		return as3TransferObjects;

	}

	//
	// Protected Methods
	//

	protected As3TransferObject performMap(JavaTransferObject javaTransferObject) {

		As3TransferObject as3TransferObject = new As3TransferObject(javaTransferObject);

		// map each property
		for (JavaProperty javaProperty : javaTransferObject.getProperties()) {

			As3Property as3Property = propertyMapper.mapProperty(javaProperty);
			as3TransferObject.addProperty(as3Property);

			As3Dependency superclassType = new As3Dependency(DependencyKind.SUPERCLASS, typeMapper.mapType(javaTransferObject.getSuperclass()));


			//for (Class<?> interfaceClass : javaTransferObject.getInterfaces())


		}

		return as3TransferObject;

	}

}