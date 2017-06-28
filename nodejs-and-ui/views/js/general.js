function startLibrary(){

var myurl = window.location.host;
var protocol = window.location.protocol;
myurlapi = protocol + '//' + myurl + "/apiuri";
console.log('The rootURLapi is found at: '+myurlapi);
rootURLspeechNode = protocol + '//' + myurl + "/gettoken";
console.log('url tts token: '+rootURLspeechNode);
rootURLstartConv = protocol + '//' + myurl + "/startConv";
rootURLconChat = protocol + '//' + myurl + "/say";
console.log('url conv start: '+rootURLstartConv);

if(!(myurlapi== null || rootURLspeechNode==null || rootURLstartConv==null || rootURLconChat==null )){
  $.ajax({
    type: 'GET',
    url: myurlapi,
    dataType: 'json', // data type of response
    success: function(data){
      rootURL1 = data.uri;
      console.log('The rootURL1 is: '+rootURL1);
      execute();
      executeRental();
      user();
      executeConversation();
    }
  });
}


}
