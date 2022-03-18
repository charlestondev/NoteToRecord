package com.ntr;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ViewWordFragment extends Fragment {
	public DBAdapter dbAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		dbAdapter = new DBAdapter(getActivity());
		View v = inflater.inflate(R.layout.view_word_fragment, container, false);
		((SlidingPaneActivity)getActivity()).details = this;
		return v;
	}
}
