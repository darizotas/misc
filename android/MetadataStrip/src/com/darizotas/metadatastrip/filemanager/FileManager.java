package com.darizotas.metadatastrip.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
	
	public static List<FileData> getDirContents(String startPath) {
    	File path = new File(startPath);
    	File[] pathContents = path.listFiles();
    	
    	List<FileData> files = new ArrayList<FileData>();
    	for (int i = 0; i < pathContents.length; i++) {
    		File file = pathContents[i];

    		if (file.canRead()) {
    			FileData data = new FileData(file.getName(), file.getPath());
    			files.add(data);
    		}	
    	}
    	
    	return files;
	}
}
