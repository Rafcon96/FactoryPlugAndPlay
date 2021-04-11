package il.co.ilrd.factorypnp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import il.co.ilrd.observer.Callback;

public class FactoryPlugAndPlay<K, D, T> {
	private Map<K, Function<D,? extends T>> factoryMethodsMap;
	private FolderMonitor watchedFolder; 
	private AnalyzeJarFiles analyzer;

	private FactoryPlugAndPlay() {
		factoryMethodsMap = new ConcurrentHashMap<K, Function<D,? extends T>>();
	}

	public void setJarDirectory(String watchedPathName) {
		if (null == watchedFolder) {
			analyzer = new AnalyzeJarFiles();
			watchedFolder = new FolderMonitor(watchedPathName);			
		} else {
			watchedFolder.setPath(watchedPathName);
		}		
		analyzer.register(watchedFolder);
		watchedFolder.startWatchService();
		loadJarFiles(watchedPathName);
	}

	private void loadJarFiles(String watchedPathName) {
		File directory = new File(watchedPathName);
		for (File file : directory.listFiles()) {
			analyzer.update(file.getPath());
		}
	}
	
	public void add(K key, Function <D,? extends T> factoryMethod) {
		factoryMethodsMap.put(key, factoryMethod);
	}
	
	public T create(K key, D param) {
		System.out.println(factoryMethodsMap.size());
		return factoryMethodsMap.get(key).apply(param);
	}
	
	public static <K, D, T> FactoryPlugAndPlay<K, D, T> getFactory() {
		@SuppressWarnings("unchecked")		
		FactoryPlugAndPlay<K, D, T> factory = (FactoryPlugAndPlay<K, D, T>)FactoryHolder.instance;
		return factory;
	}
	
	private static class FactoryHolder{
		private static final FactoryPlugAndPlay<Object, Object, Object> instance = new FactoryPlugAndPlay<>();
	}
	
	private class AnalyzeJarFiles {
		private Callback<String> callback;
		private final String INTERFACE_NAME = "il.co.ilrd.factorypnp.FactoryLoader";
		
		private AnalyzeJarFiles() {
			callback = new Callback<String>(this::update, this::stopUpdate);
		}
		
		private Void update(String jarPath) {
			if (jarPath.endsWith(".jar")) {
				try {
					DynamicJarLoaderILRD jarLoader = new DynamicJarLoaderILRD(INTERFACE_NAME, jarPath);
					for (Class<?> classes : jarLoader.loadClasses()) {
						System.out.println(classes.getName());
						FactoryLoader factoryLoader = (FactoryLoader)classes.getConstructor().newInstance();
						factoryLoader.load();
					}
				}	catch (ClassNotFoundException | IOException |InstantiationException | 
						IllegalAccessException | IllegalArgumentException | InvocationTargetException |
						NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		
		private Void stopUpdate(Void data) {
			System.out.println("stopUpdate");
			analyzer.unRegister();
			return null;
		}
		
		public void register(FolderMonitor folderMonitor) {
			folderMonitor.register(callback);
		}
		
		public void unRegister() {
			callback.unRegister();
		}
	}
}
