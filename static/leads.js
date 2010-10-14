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
		.append($('<input type="text"></input>')
			.attr('id', fieldName)
			.attr('name', fieldName)
			.val(item[fieldName] || "")
			.addClass("value")
			.focus(function() {
				$(this).addClass("active");
			})
			.blur(function() {
				$(this).removeClass("active");
			})
		);
	container.append(elem);
}

function displayItem(item) {
	var container = $('#workspaceBody');
	container.empty();
	container.append($('<input type="hidden" name="id"></input>').val(item['id'] || ''));
	addField(container, item, 'title', 'Title', 'text');
	for (var i = 0, len = fields.length; i < len; i++) {
		var field = fields[i];
		addField(container, item, field['name'], field['displayName'], field['type'])
	}
	addField(container, item, 'active', 'Active', 'boolean');
	var content = $('<div id="content" class="autogrow"></div>').append(
		$('<textarea></textarea>').attr('name', 'content').text(item['content']));
	container.append(content);
	container.append($('<div id="save"></div>').append($('<button type="submit">Save</button>')).click(save));
	autoGrow(content);
}

function autoGrow(element) {
	var top = element.offset().top;
	var bottom = element.next().offset().top || element.parent().height();
	
	var height = bottom - top;
	element.css('height', height + 'px');
	element.css('height', height + height - element.outerHeight());		
}

function updateAutoGrow() {
	$('.autogrow').each(function() {
		autoGrow($(this));
	})
}

function getFieldValue(result, name, dataType) {
	var fieldElem = $('[name=' + name + ']');
	result[name] = fieldElem.val() || fieldElem.text();
}

function save() {
	var result = {};
	getFieldValue(result, 'id');
	getFieldValue(result, 'title');
	getFieldValue(result, 'active');
	for (var i = 0, len = fields.length; i < len; i++) {
		var field = fields[i];
		getFieldValue(result, field['name'], field['type']);
	}
	getFieldValue(result, 'content');
	$('#save button').text('Saving...')
	$.ajax({
		url: "api/saveItem",
		data: JSON.stringify(result),
		success: function(newId) {
			$('[name=id]').val(newId['id']);
			$('#save button').text('Save');
		}
	})
}

$().ready(function() {
	$('#loading').show();
	
	$(window).resize(updateAutoGrow);
	
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