package remote.control;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;

public class NextRequest extends HttpGet {

	
	public NextRequest(String host) throws UnsupportedEncodingException {
		super(host+"commands/send?q=inc");
	}
}
