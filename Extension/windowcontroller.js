var windowController = {};

windowController.clickPrev = function() {
  chrome.windows.getAll({ populate: true }, function(windowList) {
    for (var i = 0; i < windowList.length; i++) {
      for (var j = 0; j < windowList[i].tabs.length; j++) {
		chrome.tabs.executeScript(windowList[i].tabs[j].id, {code:"var e = document.createEvent('Events'); e.initEvent('getprev', true, false);document.dispatchEvent(e);"});
      }
    }
  });
};

windowController.clickNext = function() {
	chrome.windows.getAll({ populate: true }, function(windowList) {
    for (var i = 0; i < windowList.length; i++) {
      for (var j = 0; j < windowList[i].tabs.length; j++) {
        chrome.tabs.executeScript(windowList[i].tabs[j].id, {code:"var e = document.createEvent('Events'); e.initEvent('getnext', true, false);document.dispatchEvent(e);"});
      }
    }
  });
};

windowController.clickFirst = function(slide) {
	chrome.windows.getAll({ populate: true }, function(windowList) {
    for (var i = 0; i < windowList.length; i++) {
      for (var j = 0; j < windowList[i].tabs.length; j++) {
        chrome.tabs.executeScript(windowList[i].tabs[j].id, {code:"var e = document.createEvent('Events'); e.initEvent('getfirst', true, false);document.dispatchEvent(e);"});
      }
    }
  });
};

windowController.clickSlideNumber = function(slide) {
	var slideNumber = parseInt(message.slide);
	if (!isNaN(slideNumber)) {
		return;
	}
	chrome.windows.getAll({ populate: true }, function(windowList) {
    for (var i = 0; i < windowList.length; i++) {
      for (var j = 0; j < windowList[i].tabs.length; j++) {
		chrome.tabs.executeScript(windowList[i].tabs[j].id, {code:"var e = document.createEvent('UIEvents'); e.initUIEvent('getnumber', true, false, null, " + slideNumber + ");document.dispatchEvent(e);" });
      }
    }
  });
};

windowController.browserAction = function() {
  console.dir(channel);
  if(channel.connection_status == 'connected') {
    channel.socket.close();
  } else if(channel.connection_status == 'disconnected') {
    windowController.connecting();
    socketController.connect();
  }
}

windowController.openLink = function(link) {
  newTab = {};
  if(link.url.indexOf('://') === -1)
    newTab.url = 'http://' + link.url;
  else
    newTab.url = link.url;
  chrome.tabs.create(newTab, function(link) {
    //TODO: handle link creation
  });
}

windowController.serverDown = function() {  
  channel.connection_status = "error";
  chrome.browserAction.setIcon({'path': 'icon_disabled.png'});  
  chrome.browserAction.setTitle({'title': 'Server error.'});
}

windowController.disconnected = function() {
  //TODO: update UI to reflect the channel being disconnected
  channel.connection_status = "disconnected";
  chrome.browserAction.setIcon({'path': 'icon_disabled.png'});  
  chrome.browserAction.setTitle({'title': 'Disconnected. Click to connect.'});
}

windowController.connecting = function() {
  channel.connection_status = "connecting";
  chrome.browserAction.setIcon({'path': 'icon_disabled.png'});  
  chrome.browserAction.setTitle({'title': 'Connecting to the server...'});
  //TODO: update the UI to reflect the extension connecting to the server
}

windowController.connected = function() {
  //TODO: update the UI to reflect the extension being connected to the server
  channel.connection_status = "connected";  
  chrome.browserAction.onClicked.addListener(function(tab) {
    channel.socket.close();
	console.dir('Socket closed');
  });
  chrome.browserAction.setTitle({'title': 'Connected. Click to disconnect.'});
  chrome.browserAction.setIcon({'path': 'icon.png'});  
}



