package remote.control;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;

public class PrevRequest extends HttpGet {
	
	public PrevRequest(String host) throws UnsupportedEncodingException {
		super(host+"commands/send?q=dec");
	}
	
}
