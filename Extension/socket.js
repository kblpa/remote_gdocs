var sockets = {};
var channel = {};
channel.connection_status = "disconnected";
console.dir(channel);

sockets.getTokenRequest = function() {
  windows.connecting();
  config.load();
  var pastFrame = chrome.extension.getBackgroundPage().document.getElementById('wcs-iframe');
  if(pastFrame)
    pastFrame.parentNode.removeChild(pastFrame);
  auth.request(config.host + 'channels/get/' + config.identifier, sockets.getTokenResult, sockets.getTokenResult);
  console.log("Sent token request");
  //TODO: Check to make sure the quota isn't depleted before requesting a new token
}

sockets.getTokenResult = function(resp, xhr) {
  channel = {};
  console.dir(resp);
  try {
    result = JSON.parse(resp);
  } catch(err) {
    result = {};
    result.code = 600;
    result.err = err;
  }
  if(Number(result.code) === 200 || Number(result.code) === 304) {
    //handle a successful retrieval of a token
    //200 means a token was created
    //304 means a token was already cached
    if(config.debug) {
      console.log("Channel token result:");
      console.log(result);
    }
    channel.token = result.token;
    sockets.connect();
  } else if(Number(result.code) === 503) {
    windows.overQuota();
  } else {
    channel.error = "Error " + result.code +": ";
    channel.error += JSON.stringify(result.err);
  }
}

sockets.connect = function() {
  channel.channel = new goog.appengine.Channel(channel.token);
  channel.socket = channel.channel.open();
  channel.socket.onopen = sockets.onOpen;
  channel.socket.onmessage = sockets.onMessage;
  channel.socket.onerror = sockets.onError;
  channel.socket.onclose = sockets.onDisconnect;
}

sockets.onOpen = function() {
  console.dir('Socket opened');
  windows.connected();
  window.setTimeout(
    function() {
      auth.request(config.host + 'channels/connected/' + config.identifier, function(resp, xhr) { }, {'method' : 'POST'});
    }, 100);
}

sockets.onMessage = function(evt) {
  console.dir('Message recieved');
  if(config.debug)
    console.log(evt);
  var message = JSON.parse(evt.data);
  if(config.debug)
    console.log(message);
  if(message.command) {
    if (message.command == "inc") {
		windows.clickNext();
	}
	if (message.command == "dec") {
		windows.clickPrev();
	}
	if (message.command == "res") {
	}
  }
  //TODO: Handle other messages
}

sockets.onError = function(error) {
  console.log(error);
  if(error.code != 401) {
    channel.error = "Error " + error.code +": ";
    channel.error += error.description;
    windows.serverDown();
    console.log(error);
  } else {
    sockets.getTokenRequest();
  }
}

sockets.onDisconnect = function() {
  windows.disconnected();
}

