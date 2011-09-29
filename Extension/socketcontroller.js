var socketController = {};
var channel = {};
channel.connection_status = "disconnected";
console.dir(channel);

socketController.getTokenRequest = function() {
  windowController.connecting();
  config.load();
  var pastFrame = chrome.extension.getBackgroundPage().document.getElementById('wcs-iframe');
  if(pastFrame)
    pastFrame.parentNode.removeChild(pastFrame);
  auth.request(config.host + 'channels/get/' + config.identifier, socketController.getTokenResult, socketController.getTokenResult);
  console.log("Sent token request");
}

socketController.getTokenResult = function(resp, xhr) {
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
    socketController.connect();
  } else {
    channel.error = "Error " + result.code +": ";
    channel.error += JSON.stringify(result.err);
  }
}

socketController.connect = function() {
  channel.channel = new goog.appengine.Channel(channel.token);
  channel.socket = channel.channel.open();
  channel.socket.onopen = socketController.onOpen;
  channel.socket.onmessage = socketController.onMessage;
  channel.socket.onerror = socketController.onError;
  channel.socket.onclose = socketController.onDisconnect;
}

socketController.onOpen = function() {
  console.dir('Socket opened');
  windowController.connected();
  window.setTimeout(
    function() {
      auth.request(config.host + 'channels/connected/' + config.identifier, function(resp, xhr) { }, {'method' : 'POST'});
    }, 100);
}

socketController.onMessage = function(evt) {
  console.dir('Message recieved');
  if(config.debug)
    console.log(evt);
  var message = JSON.parse(evt.data);
  if(config.debug)
    console.log(message);
  if(message.command) {
    if (message.command == "inc") {
		windowController.clickNext();
	}
	if (message.command == "dec") {
		windowController.clickPrev();
	}
	if (message.command == "res") {
		windowController.clickFirst();
	}
	if (message.command == "set") {			    
		windowController.clickSlideNumber(message.slide);		
	}
  }
  //TODO: Handle other messages
}

socketController.onError = function(error) {
  console.log(error);
  if(error.code != 401) {
    channel.error = "Error " + error.code +": ";
    channel.error += error.description;
    windowController.serverDown();
    console.log(error);
  } else {
    socketController.getTokenRequest();
  }
}

socketController.onDisconnect = function() {
  windowController.disconnected();
}

