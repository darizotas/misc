/*
 * Copyright 2014 Dario B darizotas at gmail dot com
 *
 *    This software is licensed under a new BSD License.
 *    Unported License. http://opensource.org/licenses/BSD-3-Clause
 *
 */
package com.darizotas.metadatastrip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.darizotas.metadatastrip.metadata.GroupContainer;
import com.darizotas.metadatastrip.metadata.MetaDataManager;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleExpandableListAdapter;

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
	 * The metadata associated to the file that this fragment is presenting.
	 */
	private MetaDataManager mFileMetadata = null;
	/**
	 * Path to the extracted metadata.
	 */
	private String mPath = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializes the metadata from the selected file.
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			File file = new File(getArguments().getString(ARG_ITEM_ID));
			mFileMetadata = new MetaDataManager(file);
			mPath = Environment.getExternalStorageDirectory().getPath() + "/" +
					file.getName() + "_metadata.xml";

			setHasOptionsMenu(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail_list,
				container, false);
		
		// Update the file details while creating the view.
		if (mFileMetadata != null) {
			updateFileDetails(rootView);
		}
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   inflater.inflate(R.menu.detail_menu, menu);

	    // Gets the share action provider and initializes it.
	    // http://developer.android.com/training/sharing/shareaction.html
	    MenuItem item = menu.findItem(R.id.share);
	    ShareActionProvider provider = (ShareActionProvider)item.getActionProvider();
	    if (provider != null) {
	        provider.setShareIntent(getShareIntent());
	    }
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
	    MenuItem item = menu.findItem(R.id.location);
	    Boolean enabled = getUriGeoLocation() != null;
		item.setVisible(enabled);
		item.setEnabled(enabled);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.location:
				Uri geoLocation = getUriGeoLocation();
	
				if (geoLocation != null) {
				    Intent intent = new Intent(Intent.ACTION_VIEW);
				    intent.setData(geoLocation);
				    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
				        startActivity(intent);
				    }
				}
			break;
			
			case R.id.about:
			    AboutDialogFragment aboutDialog = new AboutDialogFragment();
			    aboutDialog.show(getFragmentManager(), "fragment_about_dialog");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		File tmp = new File(mPath);
		if (tmp.exists()) {
			tmp.delete();
		}
	}
	
	private Intent getShareIntent() {
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    // http://stackoverflow.com/questions/3272534/what-content-type-value-should-i-send-for-my-xml-sitemap
	    // http://www.grauw.nl/blog/entry/489
	    intent.setType("application/xml");

	    Uri metadataFile = getUriMetadataFile();
		if (metadataFile != null) {
		    //intent.putExtra(Intent.EXTRA_EMAIL, addresses);
		    //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		    intent.putExtra(Intent.EXTRA_STREAM, metadataFile);
		}
		return intent;
	}

	/**
	 * Returns the URI pointing to the file that contains the metadata from the selected file.
	 * @return Uri to the metadata file.
	 */
	private Uri getUriMetadataFile() {
		if (mPath == null)
			return null;
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(mPath));
			out.write(mFileMetadata.getMetadataXml());
			out.close();
			
			return Uri.parse(mPath);
		} catch (IOException e) {
			return null;
		}
	}
	
	/** 
	 * Updates the view with the metadata contained in the selected file that this fragment
	 * is presenting.
	 * @param view View
	 */
	private void updateFileDetails(View view) {
		GroupContainer container = mFileMetadata.getMetadata();
		
		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(getActivity(), 
			// List of groups
			container.getGroups(),
			R.layout.fragment_detail_rowgroup,
			new String[] {GroupContainer.KEY_GROUP_TEXT},
			new int[] { R.id.group_name },
			// List of metadata, per group.
			container.getAllMetadata(),
			R.layout.fragment_detail_rowtag,
			new String[] { GroupContainer.KEY_TAG_NAME, GroupContainer.KEY_TAG_VALUE },
			new int[] { R.id.tag_name, R.id.tag_value });
		
		ExpandableListView details = (ExpandableListView)view.findViewById(R.id.detail_list);
		details.setAdapter(adapter);
	}
	
	/**
	 * Returns the URI containing the geo location of the selected file.
	 * @return Uri "geo:0,0?q=lat,long(label)".
	 */
	private Uri getUriGeoLocation() {
		if (mFileMetadata == null)
			return null;
		
		Location location = mFileMetadata.getLocation();
		if (location != null) {
			return Uri.parse("geo:0,0?q=" + location.getLatitude() + "," + location.getLongitude() + 
				" (" + location.getProvider() + ")");
		} else
			return null;
		
	}
}
