package com.darizotas.metadatastrip.metadata.extractor;

import java.io.File;
import java.io.IOException;

import com.darizotas.metadatastrip.metadata.MetaDataContainer;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.SparseArray;

public class MetaDataExtractorProxy {
	
	/**
	 * List of registered extractors.
	 */
	private SparseArray<AbstractMetaDataExtractor> mListExtractor;

	/**
	 * Content resolver.
	 */
	private ContentResolver mResolver; 
	
	
	/**
	 * Unique instance of the proxy.
	 */
	private static MetaDataExtractorProxy instance;
	
	/**
	 * Constructor hidden.
	 * @param cr Content Resolver used for getting the mime type.
	 */
	private MetaDataExtractorProxy(ContentResolver cr) {
		mResolver = cr;
		mListExtractor = new SparseArray<AbstractMetaDataExtractor>();
	}
	
	/**
	 * Singletone pattern
	 * @param cr Content resolver for getting the mime type.
	 * @return The proxy instance.
	 */
	public static final MetaDataExtractorProxy getInstance(ContentResolver cr) {
		if (instance == null) {
			instance = new MetaDataExtractorProxy(cr);
		}
		return instance;
	}
	
	/**
	 * Extract the metadata from the given file.
	 * @param file File from which to extract the metadata.
	 * @return Metadata container.
	 * @throws MetadataProcessingException
	 */
	public MetaDataContainer extract(File file) throws IOException, MetadataProcessingException {
		Uri uri = Uri.fromFile(file);
		AbstractMetaDataExtractor extractor = getExtractor(mResolver.getType(uri));
		// Format not supported
		if (extractor == null) {
			String msg = "File format not supported.";
			throw new MetadataProcessingException(msg);
		}
		// Extracts the metadata
		extractor.extract(file);
		// Generates the container.
		return new MetaDataContainer(file, extractor.getMetadata(), extractor.getLocation());
	}

	/**
	 * Returns the associated metadata extractor to the given mime type.
	 * @param mime MIME type
	 * @return The associated metadata extractor. Null if not extractor is registered
	 * 			for the given mime type.
	 */
	private AbstractMetaDataExtractor getExtractor(String mime) {
		AbstractMetaDataExtractor extractor = null;
		// Images.
		if (mime.contains("image")) {
			extractor = mListExtractor.get(0);
			if (extractor == null) {
				extractor = new ImageMetaDataExtractor();
				mListExtractor.put(0, extractor);
			}
		}
		
		return extractor;
	}
}
