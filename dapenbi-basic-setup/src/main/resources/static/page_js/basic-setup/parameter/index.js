jQuery(document).ready(function () {
    var table = $("#tblParameter").DataTable({
        ajax: {
            contentType: 'application/json',
        	url: _baseUrl + '/basicsetup/parameter/getParameterDataTable',
            type: 'POST',
            data: function (d) {
            	return JSON.stringify(d);
            }
        },
        serverSide: true,
        columns: [{
        	searchable: false,
        	sortable: false,
            "defaultContent": getButtonGroup(true, true, false)
        }, {
        	title: "Parameter Code",
            data: "parameterCode"
        }, {
        	title: "Parameter Value",
            data: "parameterValue"
        }, {
        	title: "Description",
            data: "description"
        }, {
        	title: "Flag Active",
        	data: "flagActive",
            sortable: false,
            "render": function (data, type, item, meta) {
                var content = getActiveInactive(item.flagActive);
                return content;
            }
        }],
        bFilter: true,
        bLengthChange: false,
        responsive: true,
        order: [[1, 'asc']]
    });

    initEvent(table);
});

function initEvent(table) {
    $('#btnFind').on('click', function () {
        table.ajax.reload();
    });

    $('#btnAdd').on('click', function () {
        clearForm();
        $('#formDialog').modal('show');
    });

    $('#formDialog').find('form').validate({
        rules: {
            parameterCode: {
                required: true
            },
            parameterValue: {
                required: true
            },
            flagActive: {
                required: true
            }
        },
        submitHandler: function (form) {
            $.post(_baseUrl + '/basicsetup/parameter/saveParameter', {
                parameterId: $('#txtParameterId').val(),
                parameterCode: $('#txtParameterCode').val(),
                parameterValue: $('#txtParameterValue').val(),
                description: $('#txtDescription').val(),
                flagActive: $("input[name='flagActive']:checked").val()
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

    $('#tblParameter tbody').on('click', '.edit-button', function () {
        clearForm();

        var data = table.row($(this).parents('tr')).data();

        $('#txtParameterId').val(data.parameterId);
        $('#txtParameterCode').val(data.parameterCode);
        $('#txtParameterValue').val(data.parameterValue);
        $('#txtDescription').val(data.description);

        if (data["flagActive"] === true) {
            $('#radFlagActiveTrue').prop('checked', true);
        } else {
            $('#radFlagActiveFalse').prop('checked', true);
        }

        $('#formDialog').modal('show');

    });

    $('#tblParameter tbody').on('click', '.delete-button', function () {
        var data = table.row($(this).parents('tr')).data();

        swal.fire({
			title: "Are you sure?",
			text: "You won't be able to revert this!",
			type: "warning",
			showCancelButton: !0,
			confirmButtonText: "Yes, delete it!"
		}).then(function(e) {
            if(e.value) {
                $.post(_baseUrl + '/basicsetup/parameter/deleteParameter', {
                    parameterId: data.parameterId
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

    $('#btnClear').on('click', function () {
        $('#txtParameterCodeFinder').val('');
        $('#txtParameterValueFinder').val('');
        $('#txtDescriptionFinder').val('');
        $('input[name="radflagActiveFinder"]').prop('checked', false);
        $('input[name="radIsUsedBySystemFinder"]').prop('checked', false);
    });
}

function clearForm() {
    $('#txtParameterId').val('');
    $('#txtParameterCode').val('');
    $('#txtParameterValue').val('');
    $('#txtDescription').val('');
    $('#radflagActiveTrue').prop('checked', false);
    $('#radflagActiveFalse').prop('checked', false);
}