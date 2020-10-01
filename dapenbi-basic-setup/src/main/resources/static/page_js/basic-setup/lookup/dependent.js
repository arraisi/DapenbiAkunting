jQuery(document).ready(function () {
    var table = $("#tblLookupDependent").DataTable({
        "ajax": {
            contentType : 'application/json',
            url: _baseUrl + '/basicsetup/lookupDependent/getLookupDependentDataTable',
            type: "POST",
            data : function(d) {
                var lookupItemIdCol = generateFilterColumn("lookupItem.lookupItemId", lookupItemId);
                d.columns.push(lookupItemIdCol);
                return JSON.stringify(d);
            }
        },
        serverSide : true,
        columns: [{
            searchable: false,
            sortable: false,
            "defaultContent": getButtonGroup(true, true, false)
        }, {
            title: "Lookup Dependent Code",
            data: "lookupDependentCode"
        }, {
            title: "Lookup Dependent Value",
            data: "lookupDependentValue"
        }, {
            title : "Log Perubahan",
            data : 'lastUpdateDate',
            searchable : false,
            sortable : false,
            "render" : function(data, type, item, meta) {
                var content = updateHistoryFormat(item);
                return content;
            }
        }],
        bFilter: true,
        bLengthChange: false,
        responsive: true,
        order : [[1, 'asc']]
    });

    $.get(_baseUrl + "/basicsetup/lookup/getLookupItem", {
        lookupItemId : lookupItemId
    }, function (response) {

        $.each(response.data, function (index, item) {
            $('#txtLookupItemCodeFinder').val(item.lookupItemCode);
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
            lookupDependentCode: {
                required: true
            },
            lookupDependentValue: {
                required: true
            }
        },
        submitHandler: function (form) {
            $.post(_baseUrl + '/basicsetup/lookupDependent/save', {
                lookupItemId: lookupItemId,
                lookupDependentId: $('#txtLookupDependentId').val(),
                lookupDependentCode: $('#txtLookupDependentCode').val(),
                lookupDependentValue: $('#txtLookupDependentValue').val()
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

    $('#tblLookupDependent tbody').on('click', '.edit-button', function () {
        clearForm();

        var data = table.row($(this).parents('tr')).data();

        $('#txtLookupDependentId').val(data.lookupDependentId);
        $('#txtLookupDependentCode').val(data.lookupDependentCode);
        $('#txtLookupDependentValue').val(data.lookupDependentValue);

        $('#formDialog').modal('show');

    });

    $('#tblLookupDependent tbody').on('click', '.delete-button', function () {
        var data = table.row($(this).parents('tr')).data();

        swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            type: "warning",
            showCancelButton: !0,
            confirmButtonText: "Yes, delete it!"
        }).then(function(e) {
            if(e.value) {
                $.ajax({
                    type: "POST",
                    contentType: "application/json",
                    url: _baseUrl + '/basicsetup/lookupDependent/delete',
                    data: JSON.stringify(data),
                    dataType: 'json',
                    success: function (data) {
                        if (data["error"] === false) {
                            showWarning();
                            $('#confirmDelete').modal('toggle');
                            table.ajax.reload();
                        } else {
                            showError(data["message"]);
                        }
                    },
                    error: function (e) {
                        showError();
                    }
                });
            }
        });
    });
}


function clearForm() {
    $('#txtLookupDependentId').val('');
    $('#txtLookupDependentCode').val('');
    $('#txtLookupDependentName').val('');
}