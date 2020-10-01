jQuery(document).ready(function () {
    var table = $("#tblLookupItem").DataTable({
        "ajax": {
        	contentType : 'application/json',
            url: _baseUrl + '/basicsetup/lookup/getLookupItemDataTable',
            type: "POST",
            data : function(d) {
            	var lookupIdCol = generateFilterColumn("lookup.lookupId", lookupId);
            	d.columns.push(lookupIdCol);
				return JSON.stringify(d);
			}
        },
        serverSide : true,
        columns: [{
        	searchable: false,
        	sortable: false,
            "defaultContent": getButtonGroup(true, true, true)
        }, {
            data: "lookupItemCode"
        }, {
            data: "lookupItemName"
        }, {
        	data: "lastUpdatedBy",
            sortable: false,
            "render": function (data, type, item, meta) {
                var content = item.lastUpdatedBy;
                return content;
            }
        }, {
        	data: "lastUpdateDate",
            sortable: false,
            "render": function (data, type, item, meta) {
                var content = dateFormat(item.lastUpdateDate);

                return content;
            }
        }],
        bFilter: true,
        bLengthChange: false,
        responsive: true,
        order : [[1, 'asc']]
    });

    $.get(_baseUrl + "/basicsetup/lookup/getLookup", {
        lookupId : lookupId
    }, function (response) {
    	
        $.each(response.data, function (index, item) {
        	$('#txtLookupCodeFinder').val(item.lookupCode);
        });
    });

    initEvent(table);
});

function initEvent(table) {
    $('#btnAdd').on('click', function () {
        clearForm();
        $('#formDialog').modal('show');
    });

    $('#formDialog').find('form').validate({
        rules: {
            companyId: {
                required: true
            },
            lookupItemCode: {
                required: true
            },
            lookupItemName: {
                required: true
            }
        },
        submitHandler: function (form) {
            $.post(_baseUrl + '/basicsetup/lookup/saveLookupItem', {
                lookupId: lookupId,
                lookupItemId: $('#txtLookupItemId').val(),
                lookupItemCode: $('#txtLookupItemCode').val(),
                lookupItemName: $('#txtLookupItemName').val()
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

    $('#tblLookupItem tbody').on('click', '.edit-button', function () {
        clearForm();

        var data = table.row($(this).parents('tr')).data();

        $('#txtLookupItemId').val(data.lookupItemId);
        $('#txtLookupItemCode').val(data.lookupItemCode);
        $('#txtLookupItemName').val(data.lookupItemName);

        $('#formDialog').modal('show');

    });

    $('#tblLookupItem tbody').on('click', '.detail-button', function () {
        var data = table.row($(this).parents('tr')).data();

        window.location.href = _baseUrl + "/basicsetup/lookupDependent/" + data.lookupItemId;
    });

    $('#tblLookupItem tbody').on('click', '.delete-button', function () {
        var data = table.row($(this).parents('tr')).data();

        swal.fire({
			title: "Are you sure?",
			text: "You won't be able to revert this!",
			type: "warning",
			showCancelButton: !0,
			confirmButtonText: "Yes, delete it!"
		}).then(function(e) {
            if(e.value) {
                $.post(_baseUrl + '/basicsetup/lookup/deleteLookupItem', {
                    lookupItemId: data.lookupItemId
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
}
  

function clearForm() {
    $('#txtLookupItemId').val('');
    $('#txtLookupItemCode').val('');
    $('#txtLookupItemName').val('');
}