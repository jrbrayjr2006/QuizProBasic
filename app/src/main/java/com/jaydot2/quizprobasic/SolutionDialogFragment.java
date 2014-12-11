/**
 * 
 */
package com.jaydot2.quizprobasic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * @author james_r_bray
 *
 */
public class SolutionDialogFragment extends DialogFragment {
	
	private String mSolution;

	/**
	 * 
	 */
	public SolutionDialogFragment() {}

    public static SolutionDialogFragment newInstance(String _solution) {
        SolutionDialogFragment sdf = new SolutionDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(MainActivity.TEST_SOLUTION, _solution);
        sdf.setArguments(arguments);
        return sdf;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.mSolution = getArguments().getString(MainActivity.TEST_SOLUTION);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getActivity().getResources().getString(R.string.label_solution));
		builder.setMessage(getSolution());
		builder.setPositiveButton(getActivity().getResources().getString(R.string.close_label), null);
		return builder.create();
	}
	
	public String getSolution() {
		if(this.mSolution == null) {
			this.mSolution = "";
		}
		return this.mSolution;
	}

}
