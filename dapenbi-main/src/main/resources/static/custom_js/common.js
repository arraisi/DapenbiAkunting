jQuery.validator.addMethod("notEqualZero", function(value, element, param) {
	  return this.optional(element) || Number(value.replace(',','')) != param;
	}, "Value must greater than 0");

function escapeRegExp(string) {
    return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}

function toRp(a, b, c, d, e) {
    e = function(f) {
        return f.split('').reverse().join('')
    };
    b = e(parseInt(a, 10).toString());
    for (c = 0, d = ''; c < b.length; c++) {
        d += b[c];
        if ((c + 1) % 3 === 0 && c !== (b.length - 1)) {
            d += '.';
        }
    }
    return 'Rp.\t' + e(d);
}

function showSuccess(text = "Your work has been saved!", timer = 1500) {
	swal.fire({
		position: "top-right",
		type: "success",
		title: text,
		showConfirmButton: !1,
		timer: 1500
	});
}

function showError(text = "An error has occured!", timer = 1500) {
    swal.fire({
		position: "top-right",
		type: "error",
		title: text,
		showConfirmButton: !1,
		timer: 1500
	});
}

function showWarning(text = "Data has been deleted!", timer = 1500) {
	swal.fire({
		position: "top-right",
		type: "warning",
		title: text,
		showConfirmButton: !1,
		timer: timer
	});
}

function appendValidationMessage(control, validationType) {
    var isValid = true
    if (validationType['required']) {
        validationMessage = '<span style="color:red;">This field is required</span>';
        isValid = false;
    }
    var span = control.parent().find('span').remove();
    control.parent().append(validationMessage);
    return isValid;
}

function getSlug(text) {
    return text.toLowerCase().replace(/ /g, '-').replace(/[^\w-]+/g, '');
}

function trimText(input, length, ellipses = true, strip_html = true) {
    if (strip_html) {
        input.replace(/(<([^>]+)>)/ig, "");
    }
    if (input.length <= length) {
        return input;
    }
    var text = input.substr(0, length);
    var lastSpace = text.indexOf(" ");
    var trimmedText = input.substr(0, lastSpace);
    if (ellipses) {
        trimmedText += "...";
    }
    return trimmedText;
}

function dateFormat(input) {
	
	if(input !== '' && input !== undefined && input !== null){
	    var date = new Date(input);
	    var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	    var day = date.getDate();
	    var monthIndex = date.getMonth();
	    var year = date.getFullYear();
	    return monthNames[monthIndex] + ' ' + day + ' ' + year;
	}
	
	return '';
}

function toDate(dateStr) {
    var parts = dateStr.split("-")
    return new Date(parts[0], parts[1] - 1, parts[2])
}

function toCurrencyFormat(input) {
    var	number_string = input.toString(),
	split	= number_string.split(','),
	temp 	= split[0].length % 3,
	rupiah 	= split[0].substr(0, temp),
	thousand 	= split[0].substr(temp).match(/\d{1,3}/gi);
		
    if (thousand) {
        separator = temp ? '.' : '';
        rupiah += separator + thousand.join('.');
    }
    rupiah = split[1] != undefined ? rupiah + ',' + split[1] : rupiah;

    return 'Rp. ' + rupiah;
}

function getNumbers(inputString){
    var regex=/\d+\.\d+|\.\d+|\d+/g, 
        results = [],
        n;

    while(n = regex.exec(inputString)) {
        results.push(parseFloat(n[0]));
    }

    return results;
}

function toJsDate(value) {
    // var pattern = /Date\(([^)]+)\)/;
    // var results = pattern.exec(value);
    var dt = new Date(parseFloat(value));
    return (dt.getMonth() + 1) + "/" + dt.getDate() + "/" + dt.getFullYear();
}

function toJsInputDate(value) {
	
	if(value !== '' && value !== undefined && value !== null){
	    var pattern = /Date\(([^)]+)\)/;
	    var results = pattern.exec(value);
	    var dt = new Date(value);
	
	    var day = ("0" + dt.getDate()).slice(-2);
	    var month = ("0" + (dt.getMonth() + 1)).slice(-2);
	
	    return dt.getFullYear() + "-" + month + "-" + day;
	}
	
	return '';
}

function validateRequired(control) {
    if(control.val() === "") {
        control.after(getValidationFeedback("This field is required!"));
        return false;
    } else {
        return true;
    }
}

function setSelectPicker(control) {
    control.addClass("kt-bootstrap-select m_selectpicker");
    control.attr("data-live-search", "true");
    control.selectpicker();
}

function refreshSelectPicker(control){
	control.selectpicker('refresh');
}

function setValueSelectPicker(control, value, isLiveSearch = false, text = "") {
    control.val(value);
    
    if(isLiveSearch){
    	control.parent().find('.filter-option-inner-inner').empty().html(text);
    	var toAppend = '<optgroup label="Currently Selected">' + 
    						'<option value="' + value + '" title="' + text + '" selected="selected">' + text + '</option>' + 
    				  '</optgroup>';
    	control.append(toAppend);
    }
    
    control.selectpicker('refresh');
}

function setTextSelectPicker(control, text = ""){
	control.parent().find('.filter-option-inner-inner').empty().html(text);
	var toAppend = '<optgroup label="Currently Selected">' + 
						'<option value="' + text + '" title="' + text + '" selected="selected">' + text + '</option>' + 
				  '</optgroup>';
	control.append(toAppend);
	
	control.selectpicker('refresh');
}

function setDisableSelectPicker(control, value){
	control.prop("disabled", value);
	control.selectpicker('refresh');
}

function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
};

function goToNextPage(url) {
    setTimeout(
        function () {
            window.location.href = base_url + url;
        }, 2000);
}

function generateFilterColumn(data, searchValue, name = "", regex = false, searchable = true, orderable = true){
	var obj = {};
	var searchObj = {};
	searchObj.value = searchValue;
	searchObj.regex = regex
	
	obj.data = data;
	obj.name = name;
	obj.searchable = searchable;
	obj.orderable = orderable;
	obj.search = searchObj;
	
	return obj;
}

function showLoading(element){
	KTApp.block(element, {
		overlayColor: "#000000",
		type: "v2",
		state: "success",
		message: "Please wait..."
	});
}

function hideLoading(element){
	KTApp.unblock(element);
}

function setValueDecimalDigit(value, decimalDigit = 2){
	if(value === null)
		value = 0;
	
	var tmp = value.toString().replace(/,/g, '');
    var val = Number(tmp).toLocaleString('en-CA', { minimumFractionDigits: decimalDigit });

    var result = '';
	if (tmp == '') {
		result = '';
	} else {
		result = val;
	}
	
	return result;
}

function NumericInput(inp, locale, decimalDigit = 2) {
	var numericKeys = '0123456789.';

	// restricts input to numeric keys 0-9
	inp.addEventListener('keypress', function(e) {
		var event = e || window.event;
		var target = event.target;

		if (event.charCode == 0) {
			return;
		}

		if (-1 == numericKeys.indexOf(event.key)) {
			// Could notify the user that 0-9 is only acceptable input.
			event.preventDefault();
			return;
		}
	});
	
	inp.addEventListener('blur', function(e) {
		var event = e || window.event;
		var target = event.target;
		
		var tmp = target.value.replace(/,/g, '');
	    var val = Number(tmp).toLocaleString(locale, { minimumFractionDigits: decimalDigit });

		if (tmp == '') {
			target.value = '';
		} else {
			target.value = val;
		}

	});

	// strip the thousands separator when the user puts the input in focus.
	inp.addEventListener('focus', function(e) {
		var event = e || window.event;
		var target = event.target;
		var val = target.value.replace(/[,]/g, '');

		target.value = val;
	});
}

function dateDiffInDays(startDate, toDate) {
	return Math.floor((Date.UTC(toDate.getFullYear(), toDate.getMonth(), toDate.getDate()) - Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()) ) /(1000 * 60 * 60 * 24));
}

function buildComboboxFromLookup(lookupCode, control){
	$.get(_baseUrl + '/basicsetup/lookup/getLookupItem', {
		lookupCode : lookupCode
	}, function (response) {
		var appendItem = "";

		$.each(response.data, function(index, item) {
			appendItem += '<option value="' + item.lookupItemCode + '">';
			appendItem += item.lookupItemName;
			appendItem += '</option>';
		});

		$(control).find('option').remove().end().append(appendItem);
		setSelectPicker(control);
	});
}