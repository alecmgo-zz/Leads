// leads.js

// TODO(robbyw): Support 'map' field type.
// TODO(robbyw): Support sort and search for items.

var fields;
var items;
var doneParts = 0;

function initPartDone() {
  if (++doneParts >= 2) {
    $('#loading').hide();
    displayItemList();
  }
}

function createListSection(id, text) {
  return $('<div class="listSection"></div>')
      .attr('id', id)
      .append(text)
      .append('<span/>');
}

function displayItemList() {
  var container = $('#sidebar');
  container.empty();
  container.append($('<button type="submit" id="new">New Item</button>').click(newItem));
  container.append(createListSection('activeHeader', 'Active Items'));
  var active = $('<div id="active"></div>');
  container.append(active);
  container.append(createListSection('inactiveHeader', 'Inactive Items'));
  var inactive = $('<div id="inactive"></div>');
  container.append(inactive);

  for (var i = 0, len = items.length; i < len; i++) {
    var item = items[i];
    var elem = $('<a href="#"></a>')
        .addClass('item')
        .attr('id', item['id'])
        .text(item['title'] || '')
        .click(function() {
      switchToItem(this.id);
    });
    if (item['active']) {
      active.append(elem);
    } else {
      inactive.append(elem);
    }
  }

  inactive.slideUp(0);
  inactive.data('hidden', true);
  $('#inactiveHeader span').text(' (' + inactive.children().length + ')').show();

  $('.listSection').click(function() {
    var items = $('#' + this.id.replace('Header', ''));
    var span = $(this).children('span');
    if (items.data('hidden')) {
      items.data('hidden', false);
      items.slideDown(function() {
        span.hide();
      });
    } else {
      items.data('hidden', true);
      items
          .slideUp(function() {
            items.__hidden = true;
            span.text(' (' + items.children().length + ')').show();
          });
    }
  });
}

function newItem() {
  displayItem({'active': true});
}

function switchToItem(id) {
  $('#workspace-body').empty();
  $('#loading').show();
  $.ajax({
    url: 'api/getItem',
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
  var value = item[fieldName] || '';

  var input;
  if (fieldType == 'boolean') {
    input = $('<span></span>')
        .append(
            $('<input type="radio"/>')
                .attr('id', fieldName + '.yes')
                .attr('name', fieldName)
                .attr('checked', !!value)
                .val('true')
                .addClass('value'))
        .append(
            $('<label/>')
                .attr('for', fieldName + '.yes')
                .text('yes '))
        .append(
            $('<input type="radio"/>')
                .attr('id', fieldName + '.no')
                .attr('name', fieldName)
                .attr('checked', !value)
                .val('false')
                .addClass('value'))
        .append(
            $('<label/>')
                .attr('for', fieldName + '.no')
                .text('no'));
  } else {
    input = $('<input type="text"/>')
        .attr('id', fieldName)
        .attr('name', fieldName)
        .attr('size', fieldType == 'date' ? 15 : 50)
        .val(value)
        .addClass('value')
        .focus(function() {
          $(this).addClass('active');
        })
        .blur(function() {
          $(this).removeClass('active');
        });
    if (fieldType == 'date') {
      input.datepicker();
    }
  }
  var elem = $('<div></div>')
      .addClass('field')
      .append($('<span></span>').text(displayName + ':').addClass('field'))
      .append(input);
  container.append(elem);
}

function displayItem(item) {
  var container = $('#workspaceBody');
  container.empty();
  container.append($('<input type="hidden" name="id"/>').val(item['id'] || ''));
  addField(container, item, 'title', 'Title', 'text');
  for (var i = 0, len = fields.length; i < len; i++) {
    var field = fields[i];
    addField(container, item, field['name'], field['displayName'], field['type'])
  }
  addField(container, item, 'active', 'Active', 'boolean');
  var content = $('<div id="content" class="autogrow"></div>').append(
      $('<textarea></textarea>').attr('name', 'content').text(item['content'] || ''));
  container.append(content);
  container.append($('<div id="save"></div>').append($('<button type="submit">Save</button>')).click(save));
  autoGrow(content);
  $('#title').focus();
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
  if (fieldElem.size() > 1) {
    fieldElem = fieldElem.filter('[checked]');
  }
  var value = fieldElem.val() || fieldElem.text();
  if (dataType == 'text' || value === 0 || value) {
    result[name] = value;
  }
}

function save() {
  var result = {};
  getFieldValue(result, 'id', 'long');
  getFieldValue(result, 'title', 'string');
  getFieldValue(result, 'active', 'boolean');
  for (var i = 0, len = fields.length; i < len; i++) {
    var field = fields[i];
    getFieldValue(result, field['name'], field['type']);
  }
  getFieldValue(result, 'content');
  $('#save button').text('Saving...');
  $.ajax({
    url: 'api/saveItem',
    data: JSON.stringify(result),
    type: 'POST',
    success: function(newId) {
      $('[name=id]').val(newId['id']);
      $('#save button').text('Save');
      refreshSidebar();
    }
  })
}

function refreshSidebar() {
  $.ajax({
      url: 'api/getItemList',
      success: function(data) {
        items = data['items'];
        initPartDone();
      }
    });
}

$().ready(function() {
  $('#loading').show();

  $(window).resize(updateAutoGrow);

  $.ajaxSetup({
    cache: false,
    dataType: 'json',
    error: function(request, err, exc) {
      alert(err + '\n' + (exc && exc.toString()));
    }
  });

  $.ajax({
    url: 'api/getConfig',
    success: function(data) {
      fields = data['fields'];
      initPartDone();
    }
  });

  refreshSidebar();
});
