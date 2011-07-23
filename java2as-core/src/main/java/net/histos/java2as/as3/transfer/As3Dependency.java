package net.histos.java2as.as3.transfer;

import net.histos.java2as.as3.As3SimpleType;
import net.histos.java2as.as3.As3Type;
import net.histos.java2as.core.meta.DependencyKind;
import org.apache.commons.lang.ArrayUtils;

/**
 * Represents a dependency that the host ActionScript class has to a collaborator.
 *
 * @author cliff.meyers
 */
public class As3Dependency {

	//
	// Statics
	//

	private static final As3Type[] BUILTIN_TYPES = new As3Type[]{
			As3SimpleType.Array,
			As3SimpleType.Boolean,
			As3SimpleType.Date,
			As3SimpleType.Integer,
			As3SimpleType.Number,
			As3SimpleType.Object,
			As3SimpleType.String,
			As3SimpleType.Void
	};

	//
	// Fields
	//

	private As3Type dependencyType;
	private DependencyKind dependencyKind;

	//
	// Constructors
	//

	/**
	 * Constructs an ActionScript dependency based on its kind and underlying type.
	 *
	 * @param dependencyKind Kind of dependency.
	 * @param dependencyType Backing ActionScript type.
	 */
	public As3Dependency(DependencyKind dependencyKind, As3Type dependencyType) {
		this.dependencyType = dependencyType;
		this.dependencyKind = dependencyKind;
	}

	//
	// Public Methods
	//

	public boolean requiresImport() {
		return !ArrayUtils.contains(BUILTIN_TYPES, dependencyType);
	}

	public String getQualifiedName() {
		return dependencyType.getQualifiedName();
	}

	public String getSimpleName() {
		return getQualifiedName().substring(getQualifiedName().lastIndexOf(".") + 1);
	}

	@Override
	public String toString() {
		return "As3Dependency{" +
				dependencyKind + ":" +
				dependencyType +
				'}';
	}


	//
	// Getters and Setters
	//

	public As3Type getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(As3Type dependencyType) {
		this.dependencyType = dependencyType;
	}

	public DependencyKind getDependencyKind() {
		return dependencyKind;
	}

	public void setDependencyKind(DependencyKind dependencyKind) {
		this.dependencyKind = dependencyKind;
	}

}