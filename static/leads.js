// leads.js

var fields;
var items;
var doneParts = 0;

function initPartDone() {
	if (++doneParts == 2) {
		$('#loading').hide();
		displayItemList();
	}
}

function displayItemList() {
	var container = $('#sidebar');
	container.empty();
	for (var i = 0, len = items.length; i < len; i++) {
		var elem = $('<a href="#"></a>')
			.addClass('item')
			.attr('id', items[i]['id'])
			.text(items[i]['title'])
			.click(function() {
				switchToItem(this.id);
			});
		container.append(elem);
	}
}

function switchToItem(id) {
	$('#workspace-body').empty();
	$('#loading').show();
	$.ajax({
		url: "api/getItem",
		data: {
			id: id
		},
		success: function(item) {
			$('#loading').hide();
			displayItem(item);
		}
	});
}

function addField(container, item, fieldName, displayName, fieldType) {
	var elem = $('<div></div>')
		.addClass('field')
		.append($('<span></span>').text(displayName + ':').addClass("field"))
		.append($('<span></span>').attr('id', fieldName).text(item[fieldName] || "").addClass("value"));
	container.append(elem);	
}

function displayItem(item) {
	var container = $('#workspaceBody');
	container.empty();
	addField(container, item, 'title', 'Title', 'text');
	for (var i = 0, len = fields.length; i < len; i++) {
		var field = fields[i];
		addField(container, item, field['name'], field['displayName'], field['type'])
	}
	addField(container, item, 'active', 'Active', 'boolean');
	container.append($('<div id="content"></div>').text(item['content']));
}

$().ready(function() {
	$('#loading').show();
	
	$.ajaxSetup({
		cache: false,
		dataType: 'json',
		error: function(request, err, exc) {
			alert(err + '\n' + (exc && exc.toString()));
		},
	});
	
	$.ajax({
		url: "api/getConfig",
		success: function(data) {
			fields = data['fields'];
			initPartDone();
		}		
	});
	
	$.ajax({
		url: "api/getItemList",
		success: function(data) {
			items = data['items'];
			initPartDone();
		}		
	});
})