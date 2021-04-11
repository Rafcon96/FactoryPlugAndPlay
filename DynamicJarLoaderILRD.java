package il.co.ilrd.factorypnp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicJarLoaderILRD {
	private JarFile jarFile;
	private Class<?> interfaceObj;
	
	public DynamicJarLoaderILRD(String interfaceName, String jarPath) throws IOException, ClassNotFoundException {
		jarFile = new JarFile(jarPath);
		interfaceObj = Class.forName(interfaceName);
	}
	
	public Class<?>[] loadClasses() throws ClassNotFoundException{
		List<Class<?>> classes = new LinkedList<Class<?>>();
		Enumeration<JarEntry> jarEntry = jarFile.entries();
		
		while (jarEntry.hasMoreElements()) {
			JarEntry je = jarEntry.nextElement();
			if (je.getName().endsWith(".class")) {
				String classPath =  je.getName().replace("/", ".").replace(".class", "");
				Class<?> curClass = Class.forName(classPath);
				if (interfaceObj.isAssignableFrom(curClass) && !curClass.isInterface()) {
					classes.add(curClass);
				}
			}
		}
		
		return classes.toArray(new Class[classes.size()]);
	}
}
