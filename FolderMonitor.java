package il.co.ilrd.factorypnp;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Semaphore;

import il.co.ilrd.observer.Callback;
import il.co.ilrd.observer.Dispatcher;	

public class FolderMonitor {
	private Dispatcher<String> dispatcher;
	private boolean stillWatching;
	private String watchedFolderPath;
	private Semaphore terminateWatchSem = new Semaphore(0);

	public FolderMonitor(String watchedFolderPath){
		this.watchedFolderPath = watchedFolderPath;
		dispatcher = new Dispatcher<>();
	}

	public void register(Callback<String> c) {
		dispatcher.register(c);
	}

	public void startWatchService() {
		stillWatching = true;
		new Thread(){
			@Override
			public void run() {
				try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
					Path watchedDirectory = Paths.get(watchedFolderPath);
					WatchKey watchKey = watchedDirectory.register(watchService, 
							StandardWatchEventKinds.ENTRY_MODIFY,
							StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE);

					while(stillWatching) {
						for (WatchEvent<?> event : watchKey.pollEvents()) {
							Path directoryChanged = watchedDirectory.resolve((Path)event.context());
							System.out.println("................" + directoryChanged.toString());
							dispatcher.sendNotifications(directoryChanged.toString());
						}
					}
					terminateWatchSem.release();
				} catch (IOException e) {}
			}
		}.start();
	}
	public void stopWatchService() {
		stillWatching = false;
		try {
			terminateWatchSem.acquire();
		} catch (InterruptedException e) {}
		dispatcher.stopNotifications();
	}
	public void setPath(String newWatchedPath) {
		watchedFolderPath = newWatchedPath;

	}
}