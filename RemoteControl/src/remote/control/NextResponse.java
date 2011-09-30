package remote.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class NextResponse extends ResultReceiver {
    private Receiver mReceiver;

    public NextResponse(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
    		Bundle newData = new Bundle();
        	if(resultCode == HttpClient.STATUS_COMPLETE) {
        		newData = resultData;
        	} else if(resultCode == HttpClient.STATUS_ERROR) {
        		newData = resultData;
        	}
    		mReceiver.onReceiveResult(resultCode, newData);
        }
    }
}
