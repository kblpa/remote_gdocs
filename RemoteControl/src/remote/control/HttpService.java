package remote.control;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpRequestBase;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;


public class HttpService extends IntentService {
	
	public HttpService() {
		super("HttpService");
	}
	
	private HttpClient client;

	@Override
	protected void onHandleIntent(Intent intent) {
		if(client == null) {
			String oauth_token = intent.getStringExtra("remote.control.oauth_token");
			String oauth_secret = intent.getStringExtra("remote.control.oauth_secret");
			client = new HttpClient(oauth_token, oauth_secret);
		}
		String requestType = intent.getAction();
		String host = intent.getStringExtra("remote.control.host");
		final ResultReceiver result = intent.getParcelableExtra("remote.control.result_receiver");
		HttpRequestBase request = null;
		Bundle b = new Bundle();
		try {
			if(requestType.equals("remote.control.Next")) {				
				request = new NextRequest(host);
			} else if (requestType.equals("remote.control.Prev")) {
				request = new PrevRequest(host);
			} else if (requestType.equals("remote.control.First")) {
				request = new FirstRequest(host);
			} else if (requestType.equals("remote.control.CheckTime")) {
				request = new CheckTimeRequest(host);
			}
			String response = client.exec(request);
			b.putString("raw_result", response);
			result.send(HttpClient.STATUS_COMPLETE, b);
		} catch (UnsupportedEncodingException e) {
			b.putInt("response_code", 600);
			b.putString("type", "unsupported_encoding_exception_error");
			result.send(HttpClient.STATUS_ERROR, b);
		} catch (IllegalStateException e) {
			b.putInt("response_code", 600);
			b.putString("type", "illegal_state_exception_error");
			b.putString("request_type", requestType);
			if(request != null)
				b.putString("host", request.getURI().getHost());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			b.putString("stacktrace", sw.toString());
			result.send(HttpClient.STATUS_ERROR, b);
		} catch (IllegalArgumentException e) {
			b.putInt("response_code", 600);
			b.putString("type", "illegal_argument_exception_error");
			b.putString("request_type", requestType);
			if(request != null)
				b.putString("host", request.getURI().getHost());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			b.putString("stacktrace", sw.toString());
			result.send(HttpClient.STATUS_ERROR, b);
		}
	}
}