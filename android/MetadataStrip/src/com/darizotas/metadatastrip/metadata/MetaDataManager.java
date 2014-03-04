package com.darizotas.metadatastrip.metadata;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.util.Xml;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;

/**
 * Reads the metadata associated to a file.
 * @author darizotas
 *
 */
public class MetaDataManager {
	/**
	 * Contains all extracted metadata from the file.
	 */
	private GroupContainer mContainer;
	/**
	 * Contains the geo location of the file.
	 */
	private Location mLocation;
	
	/**
	 * Reads the metadata from the given file.
	 * It makes use of external libraries such as {@link http://http://drewnoakes.com/code/exif}
	 * @param fd File.
	 */
	public MetaDataManager(File fd) {
		mContainer = new GroupContainer();
		mLocation = null;
		
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(fd);
			for (Directory directory : metadata.getDirectories()) {
				// Fills in the metadata structure.
				int group = mContainer.addGroup(directory.getName());
			    for (Tag tag : directory.getTags()) {
			    	mContainer.addMetadataInGroup(group, tag.getTagName(), tag.getDescription());
			    }
			    // Gets the GPS location.
			    if (directory.getClass() == GpsDirectory.class) {
					mLocation = new Location(fd.getName());

					GeoLocation location = ((GpsDirectory) directory).getGeoLocation();
				    mLocation.setLatitude(location.getLatitude());
				    mLocation.setLongitude(location.getLongitude());
				    // Let's retrieve the time stamp as well.
				    if (directory.containsTag(GpsDirectory.TAG_GPS_TIME_STAMP)) {
					    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss z", Locale.getDefault());
					    try {
						    Date tStamp = df.parse(
						    	directory.getDescription(GpsDirectory.TAG_GPS_TIME_STAMP));
					    	mLocation.setTime(tStamp.getTime());
					    } catch (ParseException e) {}
				    }
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the metadata read.
	 * @return Metadata.
	 */
	public GroupContainer getMetadata() {
		return mContainer;
	}
	
	/**
	 * Returns the GPS location.
	 * @return GPS location.
	 */
	public Location getLocation() {
		return mLocation;
	}
	
	/**
	 * Returns the metadata read in XML format so that it is easily sharable.
	 * @return Metadata read in XML format.
	 */
	public String getMetadataXml() {
		XmlSerializer xml = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			xml.setOutput(writer);
			// Start Document
		    xml.startDocument("UTF-8", true); 
		    xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			
		    xml.startTag("", "metadata");
		    // For each group.
		    int groupIndex = 0;
		    for (HashMap<String, String> group : mContainer.getGroups()) {
		    	String groupTag = group.get(GroupContainer.KEY_GROUP_TEXT);
		    	xml.startTag("", groupTag);
		    	
		    	// Loop through all the tags.
		    	for (HashMap<String, String> tag : mContainer.getMetadata(groupIndex++)) {
		    		String tagName = tag.get(GroupContainer.KEY_TAG_NAME);
		    		xml.startTag("", tagName);
		    		xml.text(tag.get(GroupContainer.KEY_TAG_VALUE));
		    		xml.endTag("", tagName);
		    	}
		    	
		    	xml.endTag("", groupTag);
		    }
		    
		    xml.endTag("", "metadata");
		    xml.endDocument();
			
			return writer.toString();
		} catch (IOException e) {
			return "";
		}
	}
}
