package com.waruler.shoppingnotes;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BaseActivity extends Activity {

	public View mActionBarBack;
	public TextView mActionBarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar();
	}

	private void setActionBar() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setCustomView(R.layout.action_bar_notes_main);
			View customView = actionBar.getCustomView();
			mActionBarBack = customView.findViewById(R.id.imageview_back);
			mActionBarTitle = (TextView) customView
					.findViewById(R.id.textview_action_bar_title);
		}

	}
}
