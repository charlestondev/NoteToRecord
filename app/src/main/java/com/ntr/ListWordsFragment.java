package com.ntr;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by charleston on 19/02/14.
 */
public class ListWordsFragment extends ListFragment {
//	public ListAdapter listAdapter;
	public ArrayAdapter<Word> adapter;
	public ListView listView;
	public ActionMode actionMode;
	private DBAdapter dbAdapter;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dbAdapter = new DBAdapter(getActivity());
		((SlidingPaneActivity)getActivity()).list = this;
		getActivity().findViewById(R.id.leftpane).setBackgroundColor(Color.rgb(200,200,200));

		listView = getListView();
		View header = getActivity().getLayoutInflater().inflate(R.layout.list_header, null);
		header.setOnClickListener(null);
		listView.addHeaderView(header);

		List<Word> words = dbAdapter.getAllWords();
		adapter = new ArrayAdapter<Word>(getActivity(),android.R.layout.simple_list_item_activated_1, words);
		setListAdapter(adapter);

		//transferToRightPane(adapter.getItem(0).getWord(),adapter.getItem(0).getDefinition());
		//Todo implement ordering
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
												  long id, boolean checked) {
				// Here you can do something when items are selected/de-selected,
				// such as update the title in the CAB
				int items = getListView().getCheckedItemCount();
				mode.setTitle(items+" "+getResources().getString(R.string.selected));
				mode.invalidate();
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
					//Todo limpar painel da direita ao deletar
					case R.id.action_delete:
						deleteSelectedItems();
						mode.finish(); // Action picked, so close the CAB
						return true;
					case R.id.action_edit:
						Intent edit = new Intent(getActivity(),AddWordActivity.class);
						edit.putExtra("edit", true);
						Word word = getCheckedItems().get(0);
						Log.i("position -----", adapter.getPosition(word)+"");
						edit.putExtra("id",word.getId());
						edit.putExtra("word",word.getWord());
						edit.putExtra("definition",word.getDefinition());
						edit.putExtra("checked", word.getChecked());
						edit.putExtra("review", word.getReview());
						edit.putExtra("position", adapter.getPosition(word));
						startActivityForResult(edit, 100);
						if(ListWordsFragment.this.actionMode != null)
							ListWordsFragment.this.actionMode.finish();
						return true;
					default:
						return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);
				ListWordsFragment.this.actionMode = mode;
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are deselected/unchecked.
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				int items = getListView().getCheckedItemCount();
				if(items>1)
					menu.findItem(R.id.action_edit).setVisible(false);
				else
					menu.findItem(R.id.action_edit).setVisible(true);
				return false;
			}
		});
	}
	public void addWord(String search)
	{
		//Intent add = new Intent(getActivity(), AddWordActivity.class);
		//add.putExtra("edit", false);
		//add.putExtra("search", search);
		//startActivityForResult(add, 100);
        Log.i("addword","teste");
        FragmentTransaction fragmentTransaction= getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rightpane, new AddWordFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==100)
		{
			if(resultCode==100) {
				Bundle extras = data.getExtras();
				int position = extras.getInt("position");
				Word word = new Word(extras.getLong("id"), extras.getString("word"), extras.getString("definition"), extras.getInt("checked"), extras.getString("review"));

				if (position != -1) {
					adapter.remove(adapter.getItem(position));
					adapter.insert(word, position);
					/*List<Word> words = dbAdapter.getAllWords();
					adapter = new ArrayAdapter<Word>(getActivity(),android.R.layout.simple_list_item_activated_1, words);
					setListAdapter(adapter);*/
				} else
					adapter.add(word);
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		transferToRightPane(adapter.getItem(position-1).getWord(),adapter.getItem(position-1).getDefinition());
		((SlidingPaneActivity)getActivity()).pane.closePane();
	}
	public void transferToRightPane(String word,String definition){
		((TextView)((SlidingPaneActivity)getActivity()).details.getView().findViewById(R.id.key_word)).setText(word);
		((TextView)((SlidingPaneActivity)getActivity()).details.getView().findViewById(R.id.key_definition)).setText(definition);
	}
	public void deleteSelectedItems(){
		ArrayList<Word> words = getCheckedItems();
		for (int i = 0; i < words.size(); i++) {
			dbAdapter.removeWord(words.get(i));
			adapter.remove(words.get(i));
		}
	}
	public ArrayList<Word> getCheckedItems(){
		final SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
		if (checkedItems == null) {
			// That means our list is not able to handle selection
			// (choiceMode is CHOICE_MODE_NONE for example)
			return null;
		}
		// For each element in the status array
		final int checkedItemsCount = checkedItems.size();
		ArrayList<Word> words = new ArrayList<Word>();
		for (int i = 0; i < checkedItemsCount; ++i) {
			// This tells us the item position we are looking at
			final int position = checkedItems.keyAt(i);

			// And this tells us the item status at the above position
			final boolean isChecked = checkedItems.valueAt(i);

			// And we can get our data from the adapter like that
			final Word currentItem = adapter.getItem(position-1);
			if(isChecked)
				words.add(currentItem);
		}
		return words;
	}
	public void filterAdapter(String word){
		//Todo redefine the fields key_word and key_definition into the data base
		//Todo test performance with many data
		List<Word> words = dbAdapter.getAllWords("key_word LIKE ?", new String[]{"%"+word+"%"});
		adapter = new ArrayAdapter<Word>(getActivity(),android.R.layout.simple_list_item_activated_1, words);
		setListAdapter(adapter);
		//adapter.notifyDataSetChanged();
	}
}
