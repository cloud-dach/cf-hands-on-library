var express = require('express');
//new
var session = require('express-session');
var log4js = require('log4js');
var passport = require('passport');
var WebAppStrategy = require('bluemix-appid').WebAppStrategy;

var cors = require('cors');
var app = express();
//new
//const logger = log4js.getLogger("testApp");

var request = require('request');
var watson = require('watson-developer-cloud');
var bodyParser = require('body-parser');
app.use(bodyParser.json()); // for parsing application/json
app.use(cors());
//new
app.use(passport.initialize());

// Below URLs will be used for App ID OAuth flows
const LANDING_PAGE_URL = "/web-app-sample.html";
const LOGIN_URL = "/ibm/bluemix/appid/login";
const CALLBACK_URL = "/ibm/bluemix/appid/callback";


var vcapServices;
if(!process.env.VCAP_SERVICES){
      //vcapServices = require('.env');
      var path = require('path');
      vcapServices = require( path.resolve( __dirname, "./env.json" ) );
      //console.log(vcapServices);
}
else {
  vcapServices = JSON.parse(process.env.VCAP_SERVICES);
}


// Setup express application to use express-session middleware
// Must be configured with proper session storage for production
// environments. See https://github.com/expressjs/session for
// additional documentation
app.use(session({
	secret: '123456',
	resave: true,
	saveUninitialized: true
}));

app.set('view engine', 'ejs');

// Use static resources from /public directory
app.use(express.static(__dirname + '/views'));

// Configure express application to use passportjs
app.use(passport.initialize());
app.use(passport.session());


//---------------------------------------------------------------------------//
passport.use(new WebAppStrategy());


// Configure passportjs with user serialization/deserialization. This is required
// for authenticated session persistence across HTTP requests. See passportjs docs
// for additional information http://passportjs.org/docs
passport.serializeUser(function(user, cb) {
	cb(null, user);
});

passport.deserializeUser(function(obj, cb) {
	cb(null, obj);
});

// Explicit login endpoint. Will always redirect browser to login widget due to {forceLogin: true}. If forceLogin is set to false the redirect to login widget will not occur if user is already authenticated
app.get(LOGIN_URL, passport.authenticate(WebAppStrategy.STRATEGY_NAME, {
	//successRedirect: LANDING_PAGE_URL,
	forceLogin: true
}));

// Callback to finish the authorization process. Will retrieve access and identity tokens/
// from App ID service and redirect to either (in below order)
// 1. the original URL of the request that triggered authentication, as persisted in HTTP session under WebAppStrategy.ORIGINAL_URL key.
// 2. successRedirect as specified in passport.authenticate(name, {successRedirect: "...."}) invocation
// 3. application root ("/")
app.get(CALLBACK_URL, passport.authenticate(WebAppStrategy.STRATEGY_NAME));


//logout
app.get("/logout", function(req, res){
  WebAppStrategy.logout(req);
  res.sendFile(__dirname + '/views/logout.html');
});

//Generate the main html page
app.get('/',function(req,res){
	res.sendFile(__dirname + '/views/index.html');
});

// Protected area. If current user is not authenticated - redirect to the login widget will be returned.
// In case user is authenticated - a page with current user information will be returned.
app.get("/protected", passport.authenticate(WebAppStrategy.STRATEGY_NAME), function(req, res){
	//return the protected page with user info
	res.render('protected',{name: req.user.name || "guest", picture: req.user.picture || "/images/anonymous.svg", email: req.user.email || "unkown-email"});
});




app.listen(process.env.PORT || 3000);






var libraryURI = (process.env.LIBRARY_URI || 'http://localhost:9080/library-server-java/api');
console.log("The Library URI is: " + libraryURI);


//java server
app.get('/apiuri', function(req, res) {
    res.json({ uri: libraryURI });
});

//authenticate conversation service
var workspace_id_copy = '4e3c0199-9871-4346-8b30-c2bfba973f8c';
var conversation = watson.conversation({
  username: vcapServices.conversation[0].credentials.username,
  password: vcapServices.conversation[0].credentials.password,
  path: { workspace_id: workspace_id_copy },
  version: 'v1',
  version_date: '2017-02-03'
});

//authorization token text to speech
app.get('/gettoken', function(req, res) {
   // read from VCAP services
   var tts_username = vcapServices.text_to_speech[0].credentials.username;
   var tts_password = vcapServices.text_to_speech[0].credentials.password;

   var buffer = Buffer.from(tts_username + ':' + tts_password);
   var authstring = 'Basic ' + buffer.toString('base64');
   //console.log("authstring: "+authstring);
   auth_url = "https://stream.watsonplatform.net/authorization/api/v1/token";
   tts_url = "https://stream.watsonplatform.net/text-to-speech/api";
   var options = {
                  url: auth_url + '?url=' + tts_url,
                  headers: {
                      'Authorization': authstring
                  }
   };
   request(options,function(err,response,body){
      //console.log(body);
      if(!err){
        res.send(body);
      }
      else {
        res.status(500).send('Something broke!');
      }
   });
});

//conversation
//conversation start
app.get('/startConv', function(req, res) {
  conversation.message({}, function(err, data){
    if(err) {
      console.error(err);
      return;
    }
    //console.log("get try: "+JSON.stringify(data.output));
    res.json(data);
  });

});


app.put('/say', function(req, res) {
  conversation.message({
      input: { text: req.body.user_input},
      // Send back the context to maintain state.
      context : req.body.context,
  }, function(err, data){
    if(err) {
      console.error(err);
      return;
    }
    //get context from conversation.js, no need to save context here
    //change answer text if action and book collection is empty


    if (data.output.hasOwnProperty('action')) {
      if (data.output.action == 'search_title') {
        var title = data.output.action_param;
        //get books request
        request(libraryURI+'/books/title/'+title, function (error, response, body) {
          if (error) {
            console.error(error);
          }
          if (!error && response.statusCode == 200) {
            data.output.books_by_title = JSON.parse(body);
            //console.log('data before sending: '+JSON.stringify(data.output));
            res.json(data);
            return;
          }
        })
      } else if (data.output.action == 'search_author' && data.outpunt.hasOwnProperty('action_param')) {
        var author = data.output.action_param;
        //get books request
        request(libraryURI+'/books/author/'+author, function (error, response, body) {
          if (error) {
            console.error(error);
          }
          if (!error && response.statusCode == 200) {
            data.output.books_by_author = JSON.parse(body);
            //console.log('data before sending: '+JSON.stringify(data.output));
            res.json(data);
            return;
          }
        })

      } else if (data.output.action == 'select_books' && data.output.hasOwnProperty('action_param')) {
        var tag = data.output.action_param;
        request(libraryURI+'/books/tag-search/'+tag, function (error, response, body) {
          if (error) {
            console.error(error);
          }
          if (!error && response.statusCode == 200) {
            data.output.selected_books = JSON.parse(body);
            //console.log('data before sending: '+JSON.stringify(data.output));
            res.json(data);
            return;
          }
        })
      } else {
        res.json(data);
      }
    } else {
      res.json(data);
    }

  });
});
