package com.darizotas.metadatastrip;

import java.io.File;
import java.io.IOException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single File detail screen. This fragment is either
 * contained in a {@link FileListActivity} in two-pane mode (on tablets) or a
 * {@link FileDetailActivity} on handsets.
 */
public class FileDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private File mFile = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			mFile = new File(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_file_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mFile != null) {
			try {
				Metadata metadata = ImageMetadataReader.readMetadata(mFile);
				String fileDetails = "";
				for (Directory directory : metadata.getDirectories()) {
				    for (Tag tag : directory.getTags()) {
				        fileDetails += tag.toString();
				    }
				}
				((TextView) rootView.findViewById(R.id.file_detail))
					.setText(fileDetails);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			}
		}

		return rootView;
	}
}
