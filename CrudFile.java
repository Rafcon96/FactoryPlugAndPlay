package il.co.ilrd.factorypnp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CrudFile implements CRUD<Integer,String>{

	private String BackUpFile;
	
	public CrudFile(String backupFilePath)  {
		BackUpFile = backupFilePath;
	}

	@Override
	public Integer create(String data) { //new line
        try {
            List<String> list = writeToList();
            list.add(data);
            Files.write(getBackFile().toPath(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}

	@Override
	public String read(Integer id) { //read line
		String line = null;
		try {
			FileInputStream fs = new FileInputStream(getBackFile().getPath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			for(int i = 0; i < id; ++i) {
				  br.readLine();
			}

			line = br.readLine();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return line;    
	}
	
	
	@Override
	public void update(Integer id, String data) { //update line
        try {
            List<String> list = writeToList();
			list.set(id.intValue(), data);
            Files.write(getBackFile().toPath(), list);
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

	@Override
	public void delete(Integer id) { //remove line
			List<String> list = writeToList();
			list.remove(id.intValue());
            try {
				Files.write(getBackFile().toPath(), list);
			} catch (IOException e) {
				e.printStackTrace();
			}                                         
	}
	
	
	/********* help function************/
	private File getBackFile() {
		return new File(BackUpFile);
	}	
		private List<String> writeToList(){
	        List<String> list = new ArrayList<String>();

	        try (BufferedReader br = new BufferedReader(new FileReader(getBackFile()))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                list.add(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return list;
	    }
}