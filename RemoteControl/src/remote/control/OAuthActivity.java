package remote.control;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class OAuthActivity extends FragmentActivity implements CheckTimeResponse.Receiver  {
	private EditText host_input;
	private EditText account_input;
	public CheckTimeResponse mReceiver;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
    	setContentView(R.layout.add_account);
    	host_input = (EditText) findViewById(R.id.host_entry);
    	account_input = (EditText) findViewById(R.id.account_entry);
    	
    	final Button add_button = (Button) findViewById(R.id.add_account);
    	
    	add_button.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		String requestUrl = "";
				try {
					requestUrl = OAuth.getRequestUrl(host_input.getText().toString());
		    		Intent intent = new Intent(OAuthActivity.this, OAuthWebView.class);
		    		intent.setData(Uri.parse(requestUrl));
		    		startActivityForResult(intent, OAuth.INTENT_ID);
				} catch (OAuthMessageSignerException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
				} catch (OAuthNotAuthorizedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
				} catch (OAuthExpectationFailedException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
				} catch (OAuthCommunicationException e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					Bundle error_data = new Bundle();
					error_data.putString("stacktrace", sw.toString());
					error_data.putString("host", host_input.getText().toString());
					error_data.putString("account", account_input.getText().toString());
					error_data.putString("request_url", requestUrl);
					error_data.putString("response_body", e.getResponseBody());
					getServerTime();
					mReceiver.setPassThrough(error_data);
	        	    //getServerTime() is asynchronous, so we pass the error information to it so it can display the error if the time is right
					//DialogFragment errorFragment = OAuthCommunicationExceptionDialogFragment.newInstance(error_data);
	        	    //errorFragment.show(getSupportFragmentManager(), "dialog");
				}
	    	}
	    });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new CheckTimeResponse(new Handler());
		mReceiver.setReceiver(this);
	}
	

    @Override
	protected void onActivityResult(int req_code, int res_code, Intent intent) {
    	super.onActivityResult(req_code, res_code, intent);
    	if(intent != null) {
	        
    	} else {
    	}
	}
    
    public void getServerTime() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("remote.control", "remote.control.HttpService"));
		intent.setAction("remote.control.CheckTime");
		intent.putExtra("remote.control.result_receiver", mReceiver);
		intent.putExtra("remote.control.host", host_input.getText().toString());
		startService(intent);
    }
    
    public Bundle getDeviceTime() {
    	Bundle deviceTime = new Bundle();
    	deviceTime.putLong("currentTime", System.currentTimeMillis());
    	deviceTime.putString("timezone", Time.getCurrentTimezone());
    	deviceTime.putString("friendlyTime", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis())));
    	return deviceTime;
    }
    
    public boolean correctTime(Long serverTime) {
    	Bundle device = getDeviceTime();
    	long diff = device.getLong("currentTime") - serverTime;
    	long acceptableDiff = 1800000; // thirty minutes in milliseconds
    	Log.d("OAuthActivity", device.getLong("currentTime") + "");
    	return (diff < acceptableDiff && diff > 0);
    }

    @Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		
	}
}
