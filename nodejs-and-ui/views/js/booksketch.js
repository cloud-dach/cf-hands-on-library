
function execute(){

var rootURLbooks = rootURL1 + "/books";
console.log(rootURLbooks);
var rootURLspeech = rootURL1 + '/text_to_speech';
console.log(rootURLspeech);
var token;


/*$.ajax({
	url: rootURLspeech,
	type: 'GET',
	dataType: 'json',
	success: function(data){
		token = data.token;
		showBooks();
	},
	error: function(jqXHR, textStatus, errorThrown){
		alert('audio error: '+ textStatus);
	}
});*/

//get token
$(document).ready(function() {
	$.ajax({
		url: rootURLspeechNode,
		type: 'GET',
		success: function(data){
			token = data;
			//console.log("token: "+token);
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('audio error Nodejs: '+ textStatus);
		}
	});
});

/*$(document).ready(function() {
	$('#addNewBook').click(function() {
		addBook();
	});
})*/

/*$(document).ready(function() {
	$('#updateBook').click(function() {
		updateBook();
	});
})*/

//load table content
$(document).ready(function(){
	showBooks();
})

//table content
function showBooks(){
	$('#bookTableRows').empty();
	$.ajax({
			url: rootURLbooks
	}).then(function(data) {
		$.each( data, function( key, val ) {
		//do something with data
			var auth = "No authors found.";
			if(val.hasOwnProperty('authors')){
				auth = val.authors;
				if(val.authors.length === 0)	auth = "No authors found";
			}
			$('#bookTableRows').append(
				'<tr> <td class="pleaseHide">'+val.id+'</td> <td>'+val.title+'</td> <td>'+auth+'</td> <td>'+val.isbn+'</td>'+
				'<td> <a class="btn btn-default cover" data-toggle="popover" data-placement="left">'+
				'<span class="glyphicon glyphicon-book"></span> </a>'+
				'&nbsp;<a class="btn btn-default tags" data-toggle="popover" data-placement="left">'+
				'<span class="glyphicon glyphicon-tags"></span></a>'+
				/*'&nbsp;<a class="btn btn-default removeBook" data-toggle="popover" data-placement="left" >'+
				'<span class="glyphicon glyphicon-remove">'+
				'</span></a>'+*/
				/*'&nbsp;<a class="btn btn-default updateBook" data-toggle="popover" data-placement="left">'+
				'<span class="glyphicon glyphicon-pencil"></span></a>'+*/
				'&nbsp;<a class="btn btn-default listenBook" data-placement="left">'+
				'<span class="glyphicon glyphicon-volume-up"></span></a>'+
				'<div class="pleaseHide">'+val.about_the_book+'</div>'+
				'</td>'+
				'</tr>'
			);
			$('.pleaseHide').hide();
			$('[class="btn btn-default tags"]').popover({
				html: true,
				trigger: 'hover click',
				content: function () {
					return showTags(val);
				}
			});
			$('[class="btn btn-default cover"]').popover({
				html: true,
				trigger: 'hover click',
				content: function() {
					return showCover(val);
				}
			});
			/*$('[class="btn btn-default removeBook"]').popover({
				html: true,
				trigger: 'click',
				content: function () {
					return '<div class="popoverContent"><p>Delete this book?</p>'+
					'<p></p><p><div>'+
					'<button type="button" class="btn btn-default deleteCheckBook">'+
					'<span class="glyphicon glyphicon-ok"></button>'+
					'&nbsp; <button type="button" class="btn btn-default deleteNotBook">'+
					'Cancel</button></div></p></div>';
				}
			});*/
			//<div class="deleteDescription">Delete</div>
			/*$(".btn.btn-default.removeBook").mouseover(function() {
					//$(".deleteDescription").show();
			}).mouseout(function() {
					$(".deleteDescription").hide();
			});*/
			/*$('[class="btn btn-default updateBook"]').popover({
				html: true,
				trigger: 'click',
				content: function () {
					return '<div class="popoverContent">'+
					'<div class="col-sm-offset-2"><strong>Update Book:</strong></div>'+
					'<form class="form-horizontal">'+
						'<div class="form-group">'+
							'<label class="control-label" for="titleUpdate">Title:</label>'+
							'<input type="text" class="form-control rentalInput" id="titleUpdate" placeholder="Enter Title">'+
						'</div>'+
						'<div class="form-group">'+
							'<label class="control-label" for="authorsUpdate">Customer ID:</label>'+
							'<input type="text" class="form-control rentalInput" id="authorsUpdate" placeholder="Enter Authors">'+
						'</div>'+
						'<div class="form-group">'+
							'<label class="control-label" for="isbnUpdate">Start Date:</label>'+
							'<input type="text" class="form-control rentalInput" id="isbnUpdate" placeholder="Enter ISBN">'+
						'</div>'+
						'<div class="form-group">'+
							'<button type="button" class="btn btn-default" id="updateBookbtn">update</button>'+
						'</div>'+
					'</form>'+
					'</div>';
				}
			});*/
		});
	});
}

$(document).on("click", ".listenBook", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TR')) {
		parent = parent.parentElement;
	}
	var title = parent.children[1].innerHTML;
	var author = parent.children[2].innerHTML;
	var isbn = parent.children[3].innerHTML;
	var about = parent.children[4].lastChild.innerHTML;
	//console.log(about);
	//console.log(title+' '+author+' '+isbn);
	if(about=="undefined" || about=="") {
		speechBook(title, author, isbn);
	} else {
		speechAboutBook(title, author, isbn, about);
	}
})

/*$(document).on("click", ".deleteNotBook", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TD')) {
		parent = parent.parentElement;
	}
	var butt = $(parent).find("a:nth-child(3)")[0];
	$(butt).trigger('click');
})*/

/*$(document).on("click", ".deleteCheckBook", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TR')) {
		parent = parent.parentElement;
	}
	var id = $(parent).find("td:nth-child(1)"); //gets first td of row
	var id = id[0].innerHTML;	//id of rental
	deleteBook(id, elem, parent);
})*/

function showTags(data) {
	if(data.hasOwnProperty('tags') && !jQuery.isEmptyObject(data.tags)){
		var tags = data.tags;
		var text = "";
		var i;
		for (i = 0; i < tags.length; i++) {
			text += tags[i] + "<br>";
		}
		return text;
	} else {
		return "No tags found."
	}
}

function showCover(data) {
	if((!data.hasOwnProperty('picture') || data.picture == "No Cover found") ) {
		return "No cover found.";
	} else {
		return '<img src="'+data.picture + '" style="width:240px" />';
	}
}

/*$(document).on("click", "#updateBookbtn", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TR')) {
		parent = parent.parentElement;
	}
	var id = $(parent).find("td:nth-child(1)"); //gets first td of row
	var id = id[0].innerHTML;	//id of rental
	updateBook(id, elem);
})*/

/*function updateBook(valueID, element){
	console.log('updateBook');
	$.ajax({
		type:'PUT',
		contentType: 'application/json',
		url: rootURLbooks +'/' + valueID,
		dataType: "json",
		data: formToJSONBookUpdate(valueID),
		success: function(data, textStatus, jqXHR){
				//close popover
				var parent1 = element.parentElement;
				while(!(parent1.tagName=='TD')) {
					parent1 = parent1.parentElement;
				}
				$(parent1).trigger('click');
				showBooks();
				$.bootstrapGrowl("The book "+valueID+" has been updated.", {type:'success', delay: 2000});
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('update Book error: '+ textStatus);
		}
	})
}*/

function speechBook(title, author, isbn){
		WatsonSpeech.TextToSpeech.synthesize({
			//text: 'The book '+title+' by '+author+' has ISBN '+isbn,
			text: '<speak version="1.0">The book '+title+' by '+author+' has ISBN '+'<say-as interpret-as="digits">'+isbn+'</say-as></speak>',
			voice: 'en-US_AllisonVoice',
			token: token
		});
}

function speechAboutBook(title, author, isbn, about) {
	WatsonSpeech.TextToSpeech.synthesize({
		text: '<speak version="1.0"><emphasis>About the book </emphasis> '+title+' by '+author+': '+about+'</speak>',
		voice: 'en-US_AllisonVoice',
		token: token
	});
}


}
