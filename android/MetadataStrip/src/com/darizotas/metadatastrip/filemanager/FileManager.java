package com.darizotas.metadatastrip.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.darizotas.metadatastrip.R;

public class FileManager {
	/** Icon hash key. */
	public static final String KEY_ICON = "icon";
	/** Filename hash key. */
	public static final String KEY_FILENAME = "filename";
	/** Path hash key. */
	public static final String KEY_PATH = "path";
	
//	public static List<FileData> getDirContents(String startPath) {
	public static List<HashMap<String, String>> getDirContents(String startPath) {
    	File path = new File(startPath);
    	File[] pathContents = path.listFiles();
    	
//    	List<FileData> files = new ArrayList<FileData>();
//    	http://stackoverflow.com/questions/18628831/listview-with-arraylist-simple-adapter-in-android
//    	http://www.vogella.com/tutorials/AndroidListView/article.html
//    	http://developer.android.com/reference/android/widget/SimpleAdapter.html
//    	http://prakashgavade.blogspot.nl/2013/05/android-listview-with-imageview-using.html
    	List<HashMap<String, String>> files = new ArrayList<HashMap<String, String>>();
    	for (int i = 0; i < pathContents.length; i++) {
    		File file = pathContents[i];

    		if (file.canRead()) {
//    			FileData data = new FileData(file.getName(), file.getPath());
    			HashMap<String, String> data = new HashMap<String, String>();
    			
    			data.put(FileManager.KEY_FILENAME, file.getName());
    			data.put(FileManager.KEY_PATH, file.getPath());
    			if (file.isDirectory()) {
        			data.put(FileManager.KEY_ICON, Integer.toString(R.drawable.ic_action_collection));
    			} else {
        			data.put(FileManager.KEY_ICON, Integer.toString(R.drawable.ic_action_help));
    			}
    			files.add(data);
    		}	
    	}
    	
    	return files;
	}
}
