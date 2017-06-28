
function executeRental(){

  var rootURLrental = rootURL1 + "/rentals";
  /*var rootURLuserrentals = rootURLrental + "/user/" + userEmail;
  console.log("user url: "+rootURLuserrentals);*/
  var rootURLbooks = rootURL1 +"/books";
  console.log(rootURL1);
  console.log(rootURLrental);

$(document).ready(function() {
  showUserRentals();
})

$(document).on("click", ".rentalsTableToggle", function(){
  showUserRentals();
})

function showUserRentals() {
  $('#rentalsTableRows').empty();
  $.ajax({
			url: rootURLrental+'/user/'+userCustomerId
	}).then(function(data) {
    var book;
		$.each( data, function( key, val ) {
      $.ajax({
        type: 'GET',
        url: rootURLbooks+"/"+val.bookuri,
        success: function(data, textStatus, jqXHR){
          book = data;
          var auth = "No authors found.";
    			if(book.hasOwnProperty('authors')){
    				auth = book.authors;
    				if(book.authors.length === 0)	auth = "No authors found";
    			}
          $('#rentalsTableRows').append(
    				'<tr> <td class="pleaseHideID">'+val.id+'</td> <td>'+book.title+' by '+auth+'</td> <td class="dateTd">'
            +val.start+'</td> <td class="dateTd">'+val.end+'</td>'+
    				'<td>'+'<p><a class="btn btn-default returnBook" data-toggle="popover" data-placement="left" >'
            +'<span class="glyphicon glyphicon-remove">'+
    				'</span></a></p>'+
    				'<p><a class="btn btn-default updateRental" data-toggle="popover" data-placement="left">'+
    				'<span class="glyphicon glyphicon-pencil"></span></a></p></td>'+
    				' </tr>'
    			);
          $('.pleaseHideID').hide();
          $('[class="btn btn-default returnBook"]').popover({
    				html: true,
    				trigger: 'click',
    				content: function () {
    					return '<div class="popoverContent"><p>Return this book?</p>'+
    					'<p></p><p><div>'+
    					'<button type="button" class="btn btn-default returnCheck">'+
    					'<span class="glyphicon glyphicon-ok"></button>'+
    					'&nbsp; <button type="button" class="btn btn-default returnNot">'+
    					'Cancel</button></div></p></div>';
    				}
    			});
          $('[class="btn btn-default updateRental"]').popover({
    				html: true,
    				trigger: 'click',
    				content: function () {
    					return '<div class="popoverContent">'+
    					'<div class="col-sm-offset-2"><strong>Update Rental:</strong></div>'+
    					'<form class="form-horizontal">'+
    						'<div class="form-group">'+
    							'<label class="control-label" for="startUpdate">Start Date:</label>'+
    							'<input type="text" class="form-control rentalInput" id="startUpdate" placeholder="YYYY-MM-DD">'+
    						'</div>'+
    							'<div class="form-group">'+
    								'<label class="control-label" for="endUpdate">End Date:</label>'+
    								'<input type="text" class="form-control rentalInput" id="endUpdate" placeholder="YYYY-MM-DD">'+
    							'</div>'+
    							'<div class="form-group">'+
    								'<button type="button" class="btn btn-default" id="updateRentalbtn">Update</button>'+
    								'&nbsp; <button type="button" class="btn btn-default updateNot">'+
    								'Cancel</button>'
    							'</div>'+
    						'</form>'+
    						'</div>';
    				}
    			});
        },
        error: function(jqXHR, textStatus, errorThrown){
          alert('get book by id error: '+ textStatus);
        }
      })//-----------------------------------------------------------------------

    });
  });
}

//is still used
$(document).on("click", ".returnNot", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TD')) {
		parent = parent.parentElement;
	}
	$(parent.firstChild.firstChild).trigger('click');
})

//still used
$(document).on("click", ".returnCheck", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TR')) {
		parent = parent.parentElement;
	}
	var id = $(parent).find("td:nth-child(1)"); //gets first td of row
	var id = id[0].innerHTML;	//id of rental
	returnBookFromList(id, elem, parent);
})

//still used
function returnBookFromList(valueID, element, parent){
	console.log('returnBook');
	$.ajax({
		type: 'DELETE',
		url: rootURLrental + '/' + valueID,
		success: function(data, textStatus, jqXHR){
			//close popover
			var parent1 = element.parentElement;
			while(!(parent1.tagName=='TD')) {
				parent1 = parent1.parentElement;
			}
			$(parent1).trigger('click');
			$(parent).fadeOut(400);
			$.bootstrapGrowl("The borrowed book has been returned ("+valueID+").", {type:'success', delay: 2000});
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('returnBook error: ' + textStatus);
		}
	});
}

//still used
$(document).on("click", ".updateNot", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TD')) {
		parent = parent.parentElement;
	}
	$(parent.children[1].children[0]).trigger('click');
})

//still used
$(document).on("click", "#updateRentalbtn", function (e) {
	var elem, evt = e ? e:event;
	if (evt.srcElement)  elem = evt.srcElement;
	else if (evt.target) elem = evt.target;

	var parent = elem.parentElement;
	while(!(parent.tagName=='TR')) {
		parent = parent.parentElement;
	}
	var id = $(parent).find("td:nth-child(1)"); //gets first td of row
	var id = id[0].innerHTML;	//id of rental
	updateRental(id, elem);
})

//still used
function updateRental(valueID, element) {
  console.log('updateRental');
  $.ajax({
    type: 'PUT',
    contentType: 'application/json',
    url: rootURLrental +'/'+valueID,
    dataType: 'json',
    data: formToJSONRentalUpdate(valueID),
    success: function(data, textStatus, jqXHR){
			//close popover
			var parent1 = element.parentElement;
			while(!(parent1.tagName=='TD')) {
				parent1 = parent1.parentElement;
			}
			$(parent1).trigger('click');
      showUserRentals(); //load table
			$.bootstrapGrowl("The rental "+valueID+" has been updated.", {type:'success', delay: 2000});

    },
    error: function(jqXHR, textStatus, errorThrown){
      alert('updateCustomer error: '+ textStatus);
    }
  })
}
//still used
function formToJSONRentalUpdate(valueID) {
	return JSON.stringify({
		"id": valueID,
		"bookid": "1", //will not be changed in java-server method
		"customerid": userCustomerId,
		"start": $('#startUpdate').val(),
    "end": $('#endUpdate').val(),
		});
}


//still needed, but should change to something better
$(document).on("click", ".addRentalToggle", function(e) {
	$('#selBid').empty();

	$('.bookTable tr').each(function (i, row) {
		if(i!=0){
			var $row = $(row);
			var $tempBId = $row[0].children[0].innerHTML;
      var $tempTitle = $row[0].children[1].innerHTML;
      var $tempAuthor = $row[0].children[2].innerHTML;
			$('#selBid').append(
				'<option value='+$tempBId+'>'+$tempTitle+' by '+$tempAuthor+'</option>'
			);
		}
	});
})

//still needed
$(document).ready(function() {
	$('#addNewrental').click(function() {
		addRental();
	});
})

//still needed
function addRental() {
	console.log('addRental');
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: rootURLrental,
		//dataType: "json",
		data: formToJSONRental(),
		success: function(data, textStatus, jqXHR){
			showUserRentals();
			$('.rentalInput').val('');
			$.bootstrapGrowl("The rental has been registered.", {type:'success', delay: 2000});
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('addRental error: ' + textStatus);
		}
	});
}


// Helper function to serialize all the form fields into a JSON string
function formToJSONRental() {
	return JSON.stringify({
		"id": "1",
		"bookid": $('#selBid').find("option:selected").val(),
		"customerid": userCustomerId,
		"start": $('#start').val(),
    "end": $('#end').val(),
		});
}

//still used
$(document).ready(function(){
	$('#deleteAllRentalsbtn').popover({
		html: true,
		trigger: 'click',
		container: 'body',
		content: function () {
			return '<div class="popoverContent">'+
			'<p>Are you sure you want to return all your borrowed books?</p>'+
			'<p></p><p><div class="col-sm-offset-3">'+
			'<button type="button" class="btn btn-default removeAllRents">'+
			'<span class="glyphicon glyphicon-ok"></button>'+
			'&nbsp; <button type="button" class="btn btn-default removeNot">'+
			'<span class="glyphicon glyphicon-remove"></button></div></p></div>';
		}
	});
})

$(document).on("click", ".removeNot", function (e) {
	var butt = document.getElementById('deleteAllRentalsbtn');
	$(butt).trigger('click');
})

$(document).on("click", ".removeAllRents", function (e) {
	deleteAllRents()
})
//still used
function deleteAllRents(){
	console.log('deleteAll');
	$.ajax({
		type: 'DELETE',
		url: rootURLrental+'/user/'+userCustomerId,
		success: function(data, textStatur, jqXHR){
			//location.reload();
			showUserRentals();
			$.bootstrapGrowl("All borrowed books have been returned.", {type:'success', delay: 2000});
			var butt = document.getElementById('deleteAllRentalsbtn');
			$(butt).trigger('click');
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('delete all rentals error: '+ textStatus);
		}
	})
}



$(document).ready(function() {
	$('#updateRental').click(function() {
		updateRental();
	});
})




}

//outside of executeRental()
//adds rentals from conversation
//customerid still in the input (matches conversation.js)
function addRentalConv(bookid, custid, startDate, endDate) {
  console.log('addRental from chat');
  $.ajax({
    type: 'POST',
    contentType: 'application/json',
    url: rootURL1 + "/rentals",
    //dataType: "json",
    data: formToJSONRentalConv(bookid, startDate, endDate),
    success: function(data, textStatus, jqXHR){
      executeRental();
      $('.rentalInput').val('');
      $.bootstrapGrowl("The rental has been registered.", {type:'success', delay: 2000});
    },
    error: function(jqXHR, textStatus, errorThrown){
      alert('addRental error: ' + textStatus);
    }
  });
}


//Helper function for addRentalConv
function formToJSONRentalConv(bookid, startDate, endDate) {
  return JSON.stringify({
		"bookid": bookid,
		"customerid": userCustomerId,
		"start": startDate,
    "end": endDate,
		});
}
