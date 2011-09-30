package remote.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class NoAccountSelectedDialogFragment extends DialogFragment {
	
    public static NoAccountSelectedDialogFragment newInstance() {
        NoAccountSelectedDialogFragment frag = new NoAccountSelectedDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.default_error_title;
        int body = R.string.no_account_selected_error;
        int yesButton = R.string.no_account_selected_error_account_button;

        return new AlertDialog.Builder(getActivity())
    		.setCancelable(false)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton(yesButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    	    			 
        			}
                }
            )
            .create();
    }
}