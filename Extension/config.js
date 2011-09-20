var config = {};
config.host = "http://gdocscontrol.appspot.com/";
config.secureHost = "https://gdocscontrol.appspot.com/";
config.key = "gdocscontrol.appspot.com";
config.secret = "cHh_RdIm-ppkucE1SDRW7vlc";
config.callback = "callback/";
config.identifier = "Chrome";
config.debug = false;

config.save = function(settings) {
  if(typeof settings.host != "undefined")
    localStorage['config.host'] = settings.host;
  if(typeof settings.secureHost != "undefined")
    localStorage['config.secureHost'] = settings.secureHost;
  if(typeof settings.key != "undefined")
    localStorage['config.key'] = settings.key;
  if(typeof settings.secret != "undefined")
    localStorage['config.secret'] = settings.secret;
  if(typeof settings.callback != "undefined")
    localStorage['config.callback'] = settings.callback;
  if(typeof settings.identifier != "undefined")
    localStorage['config.identifier'] = settings.identifier;
  if(typeof settings.debug != "undefined")
    localStorage['config.debug'] = settings.debug;
  config.load();
}

config.load = function() {
  config.host = localStorage['config.host'];
  config.secureHost = config.host.replace("http://", "https://");
  if(localStorage.getItem('config.host') !== null)
    config.secureHost = localStorage['config.secureHost'];
  config.key = "anonymous";
  config.secret = "anonymous";
  config.callback = "callback/";
  if(localStorage.getItem('config.key') !== null && localStorage.getItem('config.secret')) {
    config.key = localStorage['config.key'];
    config.secret = localStorage['config.secret'];
  }
  if(localStorage.getItem('config.callback') !== null)
    config.callback = localStorage['config.callback'];
  config.identifier = localStorage['config.identifier'];
  if(localStorage.getItem('config.debug') !== null)
    config.debug = localStorage['config.debug'];
}

