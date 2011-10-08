package remote.control;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SendCommandActivity extends FragmentActivity implements NextResponse.Receiver, CheckTimeResponse.Receiver {
	
	private static final String HOST = "https://gdocscontrol.appspot.com/";

	public static final int BILLING_INTENT_CODE = 0x1079;
	
	public NextResponse mReceiver;
	public CheckTimeResponse timeReceiver;
    
	private OAuthAccount account;
	private Button next_button;
	private Button prev_button;
	private Button first_button;
	
	private boolean contentSet = false;
	
	public final static int EDIT_SETTINGS_REQ_CODE = 0x1234;

	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);    	
		
		mReceiver = new NextResponse(new Handler());
		mReceiver.setReceiver(this);
		timeReceiver = new CheckTimeResponse(new Handler());
		timeReceiver.setReceiver(this);
		
		String requestUrl = null;
		try {
			requestUrl = OAuth.getRequestUrl(HOST);
			Intent intent = new Intent(SendCommandActivity.this, OAuthWebView.class);
			intent.setData(Uri.parse(requestUrl));
			startActivityForResult(intent, OAuth.INTENT_ID);			
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		    	
	}
	
	@Override
	public void onResume() {
		super.onResume();				
		mReceiver.setReceiver(this);		
		timeReceiver.setReceiver(this);						
	}
	
	public void onPause() {
		super.onPause();
		mReceiver.setReceiver(null);
		timeReceiver.setReceiver(null);
		contentSet = false;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void getServerTime() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("remote.control", "remote.control.HttpService"));
		intent.setAction("remote.control.CheckTime");
		intent.putExtra("remote.control.result_receiver", timeReceiver);
		intent.putExtra("remote.control.host", HOST);
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

		next_button.setVisibility(account != null ? View.VISIBLE : View.GONE);
    	prev_button.setVisibility(account != null ? View.VISIBLE : View.GONE);
    	first_button.setVisibility(account != null ? View.VISIBLE : View.GONE);
	}
		
	
	@Override
	protected void onActivityResult(int req_code, int res_code, Intent intent) {
		if (req_code == 4660)
		{
			if (res_code == -1)
			{
				Uri uri = intent.getData();				
				if(uri != null  && uri.getQueryParameter("oauth_token") != null) {					
					String verifier = uri.getQueryParameter("oauth_token");					
					try {
						OAuthConsumer consumer = OAuth.getAccessToken(HOST, verifier);
						account = new OAuthAccount("Default", HOST, consumer.getToken(), consumer.getTokenSecret());						
					} catch (OAuthMessageSignerException e) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						Bundle error_data = new Bundle();
						error_data.putString("stacktrace", sw.toString());
						error_data.putString("verifier", verifier);
						DialogFragment errorFragment = OAuthMessageSignerExceptionDialogFragment.newInstance(error_data);
						errorFragment.show(getSupportFragmentManager(), "dialog");
					} catch (OAuthNotAuthorizedException e) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						Bundle error_data = new Bundle();
						error_data.putString("stacktrace", sw.toString());
						error_data.putString("verifier", verifier);
						DialogFragment errorFragment = OAuthNotAuthorizedExceptionDialogFragment.newInstance(error_data);
						errorFragment.show(getSupportFragmentManager(), "dialog");
					} catch (OAuthExpectationFailedException e) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						Bundle error_data = new Bundle();
						error_data.putString("stacktrace", sw.toString());
						error_data.putString("verifier", verifier);
						DialogFragment errorFragment = OAuthExpectationFailedExceptionDialogFragment.newInstance(error_data);
						errorFragment.show(getSupportFragmentManager(), "dialog");
					} catch (OAuthCommunicationException e) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						Bundle error_data = new Bundle();
						error_data.putString("stacktrace", sw.toString());
						error_data.putString("verifier", verifier);
						error_data.putString("response_body", e.getResponseBody());
						DialogFragment errorFragment = OAuthCommunicationExceptionDialogFragment.newInstance(error_data);
						errorFragment.show(getSupportFragmentManager(), "dialog");
					}
				} 
				else {
					account = null;
				}
		    }			
			else
			{
				account = null;
			}
		}
		render();
	}
	
	private void render() {
		if(!contentSet) {
			setContentView(R.layout.main);
			contentSet = true;
			
	    	next_button = (Button) findViewById(R.id.next);
	    	prev_button = (Button) findViewById(R.id.prev);
	    	first_button = (Button) findViewById(R.id.first);	    	
	    	
	    	next_button.setVisibility(account != null ? View.VISIBLE : View.GONE);
	    	prev_button.setVisibility(account != null ? View.VISIBLE : View.GONE);
	    	first_button.setVisibility(account != null ? View.VISIBLE : View.GONE);
	    	
	    	next_button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		next_button.setVisibility(View.GONE);
	        		prev_button.setVisibility(View.GONE);
	        		first_button.setVisibility(View.GONE);
        			sendNext();
    			}
	    	});
	    	
	    	prev_button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		next_button.setVisibility(View.GONE);
	        		prev_button.setVisibility(View.GONE);
	        		first_button.setVisibility(View.GONE);
        			sendPrev();
    			}
	    	});
	    	
	    	first_button.setOnClickListener(new View.OnClickListener() {
	        	public void onClick(View v) {
	        		next_button.setVisibility(View.GONE);
	        		prev_button.setVisibility(View.GONE);
	        		first_button.setVisibility(View.GONE);
        			sendFirst();
    			}
	    	});
		}
	}
	
	private void sendNext() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("remote.control", "remote.control.HttpService"));
		intent.setAction("remote.control.Next");
		intent.putExtra("remote.control.result_receiver", mReceiver);
		intent.putExtra("remote.control.host", account.getHost());
		intent.putExtra("remote.control.oauth_token", account.getToken());
		intent.putExtra("remote.control.oauth_secret", account.getKey());
		intent.putExtra("remote.control.sender", "Android");
		startService(intent);

	}
	
	private void sendPrev() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("remote.control", "remote.control.HttpService"));
		intent.setAction("remote.control.Prev");
		intent.putExtra("remote.control.result_receiver", mReceiver);
		intent.putExtra("remote.control.host", account.getHost());
		intent.putExtra("remote.control.oauth_token", account.getToken());
		intent.putExtra("remote.control.oauth_secret", account.getKey());
		intent.putExtra("remote.control.sender", "Android");
		startService(intent);
	}
	
	private void sendFirst() {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("remote.control", "remote.control.HttpService"));
		intent.setAction("remote.control.First");
		intent.putExtra("remote.control.result_receiver", mReceiver);
		intent.putExtra("remote.control.host", account.getHost());
		intent.putExtra("remote.control.oauth_token", account.getToken());
		intent.putExtra("remote.control.oauth_secret", account.getKey());
		intent.putExtra("remote.control.sender", "Android");
		startService(intent);
	}
}
