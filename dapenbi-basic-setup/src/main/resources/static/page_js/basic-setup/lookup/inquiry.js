jQuery(document).ready(function() {

	var table = $("#tblLookup").DataTable({
		ajax : {
			contentType : 'application/json',
			url : _baseUrl + '/basicsetup/lookup/getLookupDataTable',
			type : "POST",
			data : function(d) {
				return JSON.stringify(d);
			}
		},
		serverSide : true,
		columns : [ {
			searchable : false,
			sortable : false,
			"defaultContent": getButtonGroup(true, true, true)

		}, {
			title: "Lookup Code",
			data : 'lookupCode'
		}, {
			title: "Description",
			data : 'description'
		}, {
			title: "Flag Active",
			data : 'flagActive',
			"render" : function(data, type, item, meta) {
				var content = getActiveInactive(item.flagActive);
				return content;
			}
		}, {
			title: "Flag System",
			data : 'flagSystem',
			"render" : function(data, type, item, meta) {
				var content = getActiveInactive(item.flagSystem);
				return content;
			}
		}, {
			title: "Last Updated By",
			data : 'lastUpdatedBy'
		}, {
			title: "Last Update Date",
			data : 'lastUpdateDate',
			"render" : function(data, type, item, meta) {
				var content = dateFormat(item.lastUpdateDate);
				return content;
			}
		} ],
		bFilter : true,
		bLengthChange : false,
		responsive : true,
		order: [[1, 'asc']]
	});

	initEvent(table);
});

function initEvent(table) {
	$("#btnFind").on("click", function(e) {
		e.preventDefault();
		var txtLookupCodeFinder = $("#txtLookupCodeFinder");
		table.column(1).search(txtLookupCodeFinder.val()).draw();
	});

	$('#btnAdd').on('click', function() {
		clearForm();
		$('#formDialog').modal('show');
	});

	$('#btnClear').on('click', function() {
		$('#txtLookupCodeFinder').val('');
		$('input[name="radFlagActiveFinder"]').prop('checked', false);
		$('input[name="radFlagSystemFinder"]').prop('checked', false);
	});

	$('#tblLookup tbody').on('click', '.edit-button', function() {
		clearForm();
		var data = table.row($(this).parents('tr')).data();

		$('#txtLookupId').val(data.lookupId);
		$('#txtLookupCode').val(data.lookupCode);

		if (data.flagActive === true) {
			$('#radFlagActiveTrue').prop('checked', true);
		} else {
			$('#radFlagActiveFalse').prop('checked', true);
		}

		if (data.flagSystem === true) {
			$('#radFlagSystemTrue').prop('checked', true);
		} else {
			$('#radFlagSystemFalse').prop('checked', true);
		}

		$('#formDialog').modal('show');

	});

	$('#tblLookup tbody').on('click', '.delete-button', function() {
		var data = table.row($(this).parents('tr')).data();

		swal.fire({
			title: "Are you sure?",
			text: "You won't be able to revert this!",
			type: "warning",
			showCancelButton: !0,
			confirmButtonText: "Yes, delete it!"
		}).then(function(e) {
			if(e.value) {
				$.post(_baseUrl + '/basicsetup/lookup/deleteLookup', {
					lookupId: data.lookupId
				}, function (data) {
					if (data["error"] === false) {
						showWarning();
						$('#confirmDelete').modal('toggle');
						table.ajax.reload();
					} else {
						showError(data["message"]);
					}
				});
			}
		});
	});
	
	$('#tblLookup tbody').on('click', '.detail-button', function () {
        var data = table.row($(this).parents('tr')).data();
        
        window.location.href = _baseUrl + "/basicsetup/lookup/item/" + data.lookupId;
    });
	
	$('#formDialog').find('form').validate({
        rules: {
            lookupCode: {
                required: true
            },
            radFlagSystem: {
                required: true
            },
            radFlagActive: {
                required: true
            }
        },
        submitHandler: function (form) {
            $.post(_baseUrl + '/basicsetup/lookup/saveLookup', {
                lookupId: $('#txtLookupId').val(),
                lookupCode: $('#txtLookupCode').val(),
                flagActive: $("input[name='flagActive']:checked").val(),
                flagSystem: $("input[name='flagSystem']:checked").val()
            }, function (data) {
                if (data["error"] === false) {
                    table.ajax.reload();
                    showSuccess();
                    $('#formDialog').modal('toggle');
                } else {
                    showError(data["message"]);
                }
            });

            return false;
        }
    });
}

function clearForm() {
	$('#txtLookupId').val('');
	$('#txtLookupCode').val('');
	$('#radFlagActiveTrue').prop('checked', false);
    $('#radFlagActiveFalse').prop('checked', false);
    $('#radFlagSystemTrue').prop('checked', false);
    $('#radFlagSystemFalse').prop('checked', false);
}