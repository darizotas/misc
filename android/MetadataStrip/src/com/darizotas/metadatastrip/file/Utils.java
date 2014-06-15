/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class with miscellaneous helpful methods.
 * @author dario
 *
 */
public class Utils {
	/**
	 * Generates the hash of the given file.
	 * @param algo Hash algorithm
	 * @param fd File
	 * @return Hash value in hex.
	 * @see http://www.mkyong.com/java/how-to-generate-a-file-checksum-value-in-java/
	 */
	public static String getSignature(File fd, String algo) {
		try {
			
			MessageDigest digest = MessageDigest.getInstance(algo);
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(fd));
		    byte[] dataBytes = new byte[1024];
		    int nread = 0; 
		    while ((nread = in.read(dataBytes)) != -1) {
		    	digest.update(dataBytes, 0, nread);
		    }
		    in.close();
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
		} catch (IOException e) {
			return "";
		}
	}

}
