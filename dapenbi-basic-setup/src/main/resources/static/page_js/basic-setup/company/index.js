jQuery(document).ready(function() {

	var table = $("#tblCompany").DataTable({
		ajax : {
			contentType : 'application/json',
			url : _baseUrl + '/basicsetup/company/getCompanyDataTable',
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
			title: "Company Code",
			data : 'companyCode'
		}, {
			title: "Company Name",
			data : 'companyName'
		}],
		bFilter : true,
		bLengthChange : false,
		responsive : true
	});

	initEvent(table);
});

function initEvent(table) {
	$("#btnFind").on("click", function(e) {
		e.preventDefault();
		var txtCompanyCodeFinder = $("#txtCompanyCodeFinder");
		table.column(1).search(txtCompanyCodeFinder.val()).draw();
	});

	$('#btnAdd').on('click', function() {
		clearForm();
		$('#formDialog').modal('show');
	});

	$('#btnClear').on('click', function() {
		$('#txtCompanyCodeFinder').val('');
		
	});

	$('#tblCompany tbody').on('click', '.edit-button', function() {
		clearForm();
		var data = table.row($(this).parents('tr')).data();

		$('#txtCompanyId').val(data.companyId);
		$('#txtCompanyCode').val(data.companyCode);
		$('#txtCompanyName').val(data.companyName);

		$('#formDialog').modal('show');

	});

	$('#tblCompany tbody').on('click', '.delete-button', function() {
		var data = table.row($(this).parents('tr')).data();

		swal.fire({
			title: "Are you sure?",
			text: "You won't be able to revert this!",
			type: "warning",
			showCancelButton: !0,
			confirmButtonText: "Yes, delete it!"
		}).then(function(e) {
			$.post(_baseUrl + '/basicsetup/company/deleteCompany', {
	            companyId: data.companyId
	        }, function (data) {
	            if (data["error"] === false) {
	                showWarning();
	                $('#confirmDelete').modal('toggle');
	                table.ajax.reload();
	            } else {
	                showError(data["message"]);
	            }
	        });
		});
	});
	
	$('#formDialog').find('form').validate({
        rules: {
            companyCode: {
                required: true
            },
            companyName: {
                required: true
            }
        },
        submitHandler: function (form) {
        	showLoading("#formDialog .modal-content");
            $.post(_baseUrl + '/basicsetup/company/saveCompany', {
                companyId: $('#txtCompanyId').val(),
                companyCode: $('#txtCompanyCode').val(),
                companyName: $('#txtCompanyCode').val()
            }, function (data) {
                if (data["error"] === false) {
                    table.ajax.reload();
                    showSuccess();
                    $('#formDialog').modal('toggle');
                } else {
                    showError(data["message"]);
                }
            });
            
            hideLoading("#formDialog .modal-content");

            return false;
        }
    });
}

function clearForm() {
	$('#txtCompanyId').val('');
	$('#txtCompanyCode').val('');
	$('#txtCompanyName').val('');
}