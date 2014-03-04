package com.darizotas.metadatastrip;

import java.io.File;

import com.darizotas.metadatastrip.metadata.GroupContainer;
import com.darizotas.metadatastrip.metadata.MetaDataManager;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
	public Uri getUriGeoLocation() {
		Location location = mFileMetadata.getLocation();
		if (location != null) {
			return Uri.parse("geo:0,0?q=" + location.getLatitude() + "," + location.getLongitude() + 
				"(" + location.getProvider() + ")");
		} else
			return null;
		
	}
}
