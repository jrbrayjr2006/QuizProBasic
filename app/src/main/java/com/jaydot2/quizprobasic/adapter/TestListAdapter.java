/**
 * 
 */
package com.jaydot2.quizprobasic.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaydot2.quizprobasic.R;
import com.jaydot2.quizprobasic.model.TestObject;

/**
 * @author james_r_bray
 *
 */
public class TestListAdapter extends ArrayAdapter<TestObject> {
	
	private final LayoutInflater mInflater;

	public TestListAdapter(Context context, int resource) {
		super(context, resource);
		mInflater = LayoutInflater.from(context);
	}
	
	public TestListAdapter(Context context, List<TestObject> objects) {
		super(context, -1, objects);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TestObject test = getItem(position);
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, parent, false);
		}
		
		TextView tv = (TextView)convertView.findViewById(R.id.textViewTestName);
		tv.setText(test.getTestTitle());
		
		TextView tvScore = (TextView)convertView.findViewById(R.id.textViewTestScore);
		tvScore.setText(test.getScoreText());
		
		return convertView;
	}

}
