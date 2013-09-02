package com.darizotas.metadatastrip.filemanager;

public class FileData {
    private String name;
    private String path;
    public FileData(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    public String getName() {
        return name;
    }
    public String getPath() {
        return path;
    }
    
    public String toString() {
        return name;
    }
}