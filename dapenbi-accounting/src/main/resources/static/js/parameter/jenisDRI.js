jQuery(document).ready(function () {
    var table = $("#tblJenisDRI").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/jenis-dri/getJenisDRIDataTables",
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        // language: {
        //     lengthMenu: '<select>'+
        //         '<option value="10">10</option>'+
        //         '<option value="20">20</option>'+
        //         '<option value="30">30</option>'+
        //         '<option value="40">40</option>'+
        //         '<option value="50">50</option>'+
        //         '<option value="-1">All</option>'+
        //         '</select>',
        //     processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        // },
        order: [1, "asc"],
        lengthMenu: [10, 25, 50, 100],
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'No.',
                data: 'kodeDRI',
                orderable: false,
                className: 'text-center',
//                render: function (data, type, row, meta) {
//                    return meta.row + 1;
//                }
            },
            {
                title: 'DRI',
                data: 'kodeDRI',
                className: 'text-center',
            },
            {
                title: 'Nama DRI',
                data: 'namaDRI',
                className: 'text-center',
            },
            {
                title: 'Status',
                data: 'statusAktif',
                className: 'text-center',
                render: function (data) {
                    if(data == "0") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" disabled>';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked disabled>'
                    }
                }
            },
            {
                title: 'Tindakan',
                searchable: false,
                sortable: false,
                className: 'text-center',
                "defaultContent": getButtonGroup(true, true, false)
            }
        ],
        fnRowCallback : function (nRow, aData, iDisplayIndex, iDisplayIndexFull, oSettings) {
            var info = this.api().page.info();
            var page = info.page;
            var length = info.length;
            var index = (page * length + (iDisplayIndex + 1));
            $('td:eq(0)', nRow).html(index);

            return nRow;
        },
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    initEvent(table);
});

function initEvent(table) {
    $("#saveDRIButton").on("click", function () {
        var value = $("#statusModal").val();

        if(value == "update") {
            update(table);
        } else {
            save(table);
        }
    });

    $("#tblJenisDRI tbody").on("click", ".edit-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/jenis-dri/" + data.kodeDRI + "/findById",
            success: function (resp) {
                $("#kodeDRI").val(resp.kodeDRI).prop("disabled", true);
                $("#namaDRI").val(resp.namaDRI);
                $("input[name='statusAktif'][value='"+resp.statusAktif+"']").prop('checked', true);
//                $("input[name='statusAktif']").prop("disabled", true);
                $("#createdDate").val(resp.createdDate);
            }
        });

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblJenisDRI tbody").on("click", ".delete-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#delete-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteButton").off().on("click", function (e) {
            var obj = {
                "kodeDRI": data.kodeDRI
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/parameter/jenis-dri/delete",
                success: function (resp) {
                    $("#delete-form").modal("hide");
                    showWarning();
                    table.ajax.reload();
                }
            });
        });
    });

    $("#tblJenisDRI tbody").on("click", ".btn-check", function () {
        var data = table.row($(this).parents("tr")).data();
        var obj = {
            "kodeDRI": data.kodeDRI,
            "namaDRI": data.namaDRI,
            "statusAktif": "",
            "createdDate": data.createdDate
        };

        if(data.statusAktif == "0") {
            obj.statusAktif = "1";
        } else {
            obj.statusAktif = "0";
        }

        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/parameter/jenis-dri/update",
            success: function (resp) {
            },
            statusCode: {
                201: function () {
                    table.ajax.reload();
                },
            }
        });
    });

    $("#addButton").on("click", function () {
        formReset();
    });

    $("#btnFind").on("click", function (e) {
        e.preventDefault();
        var txtCompanyCodeFinder = $("#txtCompanyCodeFinder");
        table.column(2).search(txtCompanyCodeFinder.val()).draw();
    });

    $('#btnClear').on('click', function () {
        $('#txtCompanyCodeFinder').val('');
    });
}

function save(table) {
    var kodeDRI = $("#kodeDRI").val();
    var namaDRI = $("#namaDRI").val();
    var statusAktif = $('#statusAktif input:radio:checked').val();

    var obj = {
        "kodeDRI": kodeDRI,
        "namaDRI": namaDRI,
        "statusAktif": statusAktif
    };

    if(formValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/parameter/jenis-dri/save",
        success: function (resp) {
            if(resp == "Exist") {
                swal.fire({
                    position: "top-right",
                    type: "error",
                    title: "Kode Jenis DRI sudah digunakan.",
                    showConfirmButton: !1,
                    timer: 1500
                });
            }
        },
        statusCode: {
            201: function () {
                console.log('Saved');
                $("#add-form").modal("hide");
                showSuccess();
                table.ajax.reload();
            },
            400: function () {
                alert('Inputan tidak boleh kosong');
            },
            404: function () {
                alert('Not Found');
            }
        }
    });
}

function update(table) {
    var kodeDRI = $("#kodeDRI").val();
    var namaDRI = $("#namaDRI").val();
    var statusAktif = $('#statusAktif input:radio:checked').val();
    var createdDate = $("#createdDate").val();

    var obj = {
        "kodeDRI": kodeDRI,
        "namaDRI": namaDRI,
        "statusAktif": statusAktif,
//        "createdDate": createdDate
    };

    if(formValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/parameter/jenis-dri/update",
        success: function (resp) {
        },
        statusCode: {
            201: function () {
                $("#add-form").modal("hide");
                $("#statusModal").val("");
                showSuccess();
                table.ajax.reload();
            },
            400: function () {
                alert('Inputan tidak boleh kosong');
            },
            404: function () {
                alert('Not Found');
            }
        }
    });
}

function formReset() {
    $("#kodeDRI").val("").prop("disabled", false);
    $("#namaDRI").val("");
    $("input[name='statusAktif'][value='0']").prop('checked', true);
//    $("input[name='statusAktif']").prop("disabled", true);
    $("#statusModal").val("");
}

function formValidation(obj) {
    var found = Object.keys(obj).filter(function(key) {
            return obj[key] === "";
    });
    if(found.length > 0) {
        return true;
    } else {
        return false;
    }
}