/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 * This file makes of third party libraries:
 *    1. Metadata-extractor
 *        http://drewnoakes.com/code/exif/
 *        http://code.google.com/p/metadata-extractor/ 
 */
package com.darizotas.metadatastrip.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.util.Base64;
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
	 * Path to the file.
	 */
	private String mFileName;
	/**
	 * Hash value.
	 */
	private String mHash;
	
	/**
	 * Reads the metadata from the given file.
	 * @param fd File.
	 * @see <a href="http://drewnoakes.com/code/exif">drewnoakes.com</a>
	 */
	public MetaDataManager(File fd) {
		mContainer = new GroupContainer();
		mLocation = null;
		
		try {
			mFileName = fd.getName();
			mHash = MetaDataManager.getSignature(fd, "SHA1");

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
	 * Returns the file name containing the metadata.
	 * @return File name.
	 */
	public String getFileName() {
		return mFileName;
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
		// Pattern to match no printable chars.
		Pattern p = Pattern.compile("\\p{Cntrl}");
		
		try {
			xml.setOutput(writer);
			// Start Document
		    xml.startDocument("UTF-8", true); 
		    xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			
		    xml.startTag("", "metadatastrip");
		    addSignature(xml);
		    // For each group.
		    int groupIndex = 0;
		    for (HashMap<String, String> group : mContainer.getGroups()) {
		    	String groupTag = group.get(GroupContainer.KEY_GROUP_TEXT);
		    	xml.startTag("", groupTag);
		    	
		    	// Loop through all the tags.
		    	for (HashMap<String, String> tag : mContainer.getMetadata(groupIndex++)) {
		    		String tagName = tag.get(GroupContainer.KEY_TAG_NAME);
		    		xml.startTag("", tagName);
		    		
		    		String tagValue = tag.get(GroupContainer.KEY_TAG_VALUE);
		    		// Checks whether there are no printable chars.
		    		Matcher m = p.matcher(tagValue);
		    		if (!m.find())
		    			xml.text(tagValue);
		    		else
		    			xml.text(Base64.encodeToString(tagValue.getBytes(), Base64.DEFAULT));
		    		
		    		xml.endTag("", tagName);
		    	}
		    	
		    	xml.endTag("", groupTag);
		    }
		    
		    xml.endTag("", "metadatastrip");
		    xml.endDocument();
			
			return writer.toString();
		} catch (IllegalArgumentException e) {
			return "";
		} catch (IllegalStateException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
	}
	
	/**
	 * Adds the signature section: file name, hash, date/time.
	 * @param xml Xml serializer.
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 */
	private void addSignature(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
		xml.startTag("", "signature");
		
		xml.startTag("", "filename");
		xml.text(mFileName);
		xml.endTag("", "filename");
		
		xml.startTag("", "hash");
		xml.text(mHash);
		xml.endTag("", "hash");
		
		xml.startTag("", "timestamp");
		xml.text(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
		xml.endTag("", "timestamp");
		
		xml.endTag("", "signature");
	}
	
	/**
	 * Generates the hash of the given file.
	 * @param algo Hash algorithm
	 * @param fd File
	 * @return Hash value in hex.
	 */
	private static String getSignature(File fd, String algo) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algo);
			DigestInputStream in = new DigestInputStream(new FileInputStream(fd), digest);
			byte[] bytes = digest.digest();
		    //convert the byte to hex format
		    StringBuffer sb = new StringBuffer("");
		    for (int i = 0; i < bytes.length; i++) {
		    	sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		    
		    return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		} catch (FileNotFoundException e) {
			return "";
		}
	}
}
