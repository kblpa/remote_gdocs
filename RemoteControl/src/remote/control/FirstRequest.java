package remote.control;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;

public class FirstRequest extends HttpGet {

	public FirstRequest(String host) throws UnsupportedEncodingException {
		super(host+"commands/send?q=res");
	}
	
}
