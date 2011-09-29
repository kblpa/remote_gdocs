var auth = {};

auth.initiate = function() {
  background = chrome.extension.getBackgroundPage();
  background.oauth.clearTokens();
  oauth_config = {
    'request_url': config.secureHost + "_ah/OAuthGetRequestToken",
    'authorize_url': config.secureHost + "_ah/OAuthAuthorizeToken",
    'access_url': config.secureHost + "_ah/OAuthGetAccessToken",
    'consumer_key' : config.key,
    'consumer_secret' : config.secret,
    'scope': "",
    'callback_page' : config.host + config.callback,
  };
  background.chromeExOAuthConfig = oauth_config;
  background.oauth = background.ChromeExOAuth.fromConfig(oauth_config);
  background.oauth.authorize(background.socketController.getTokenRequest);
}

auth.request = function(path, callback, opts) {
  chrome.extension.getBackgroundPage().oauth.sendSignedRequest(path, callback, opts);
}

