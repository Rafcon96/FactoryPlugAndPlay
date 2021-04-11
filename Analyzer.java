package il.co.ilrd.factorypnp;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;


import il.co.ilrd.observer.Callback;

public class Analyzer {
	private CRUD<Integer,String> crudBackup;
	private Callback<String> callback;
	private File orgFile;

	public Analyzer(String orgFilePath, String backupFilePath) {
		orgFile = new File(orgFilePath);
		crudBackup = new CrudFile(backupFilePath);			
		callback = new Callback<String>(this::update, this::stopUpdate);
	}

	public void register(FolderMonitor folderMonitor) {
		folderMonitor.register(callback);
	}

	public void unRegister() {
		callback.unRegister();
	}
	private Void update(String str)  {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(orgFile));
			String currentLine;
			int id = 0;
			while((currentLine = reader.readLine()) != null) {
				if(null == crudBackup.read(id)) {
					crudBackup.create(currentLine);
				}
				else {
					crudBackup.update(id, currentLine);
				}
				++id;
			}
			while (crudBackup.read(id) != null) {
				crudBackup.delete(id);
				++id;
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private Void stopUpdate(Void data) {
		return null;
	}
}