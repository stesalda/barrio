/*
 * Copyright (C) 2006 Jens Dietrich</a>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package nz.ac.massey.cs.barrio.extensionFinder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Utility to find extensions that implement a given interface or subclass a given class.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich" target="_top">Jens Dietrich </a>
 * @version 1.1
 * @since 1.1
 */
public class ExtensionFinderUtil<T> {
	 public List<T> findExtensionObjects(String extensionPointId,String implementationClassAttributeName ){
			List<T> list = new ArrayList<T>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(extensionPointId);
			if (point == null) 
				System.err.println("[xfinder]: Cannot find extension point " + extensionPointId);
			else 
				System.out.println("[xfinder]: Extension point found " + extensionPointId);
			IExtension[] extensions = point.getExtensions();
			if (extensions == null) 
				System.err.println("[xfinder]: Extensions for " + extensionPointId + " are null");
			System.out.println("[xfinder]: number extensions = " + extensions.length);
			for (int i = 0; i < extensions.length; i++) {
				IExtension x = extensions[i];
				System.out.println("[xfinder]: Extension found " + x.getUniqueIdentifier());
				IConfigurationElement[] attributes = x.getConfigurationElements();
				String pluginId = x.getNamespace();
				for (int j=0;j<attributes.length;j++) {
					IConfigurationElement p = attributes[j];
					String clazz = p.getAttribute(implementationClassAttributeName);
					//System.out.println(clazz);
					try {
						Class c = Platform.getBundle(pluginId).loadClass(clazz);
						T inst = (T)c.newInstance();
						list.add(inst);
					}
					catch (Exception ex) {
						System.err.println("[xfinder]: " +ex + " Error loading extension for extension point: " + extensionPointId);
						ex.printStackTrace();
					}
				}
			}
			
			return list;
	 }

}
