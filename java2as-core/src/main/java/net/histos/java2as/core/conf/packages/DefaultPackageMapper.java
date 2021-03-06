package net.histos.java2as.core.conf.packages;

import net.histos.java2as.core.conf.PackageMapper;

/**
 * Default implementation of PackageMapper that maps all package names to the original value.
 *
 * @author cliff.meyers
 */
public class DefaultPackageMapper implements PackageMapper {

	/**
	 * @inheritDoc
	 */
	public boolean canMap(String packageName) {
		return true;
	}

	/**
	 * @inheritDoc
	 */
	public String performMap(String packageName) {
		return packageName;
	}

}
