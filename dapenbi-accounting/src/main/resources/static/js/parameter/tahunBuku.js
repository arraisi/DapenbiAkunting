jQuery(document).ready(function () {
    var table = $("#tblTahunBuku").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/api/akunting/parameter/tahun-buku/getTahunBukuDataTables",
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
                data: 'kodeTahunBuku',
                orderable: false,
                className: 'text-center',
//                render: function (data, type, row, meta) {
//                    return meta.row + 1;
//                }
            },
            {
                title: 'Tahun Buku',
                data: 'kodeTahunBuku',
                className: 'text-center',
            },
            {
                title: 'Keterangan',
                data: 'namaTahunBuku',
                className: 'text-center',
            },
            {
                title: 'Status',
                data: 'statusAktif',
                className: 'text-center',
                render: function (data) {
                    if (data == "0") {
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
        fnRowCallback: function (nRow, aData, iDisplayIndex, iDisplayIndexFull, oSettings) {
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
    $("#saveTahunBukuButton").on("click", function () {
        var value = $("#statusModal").val();

        if (value == "update") {
            console.log("Updating...");
            update(table);
        } else {
            save(table);
        }
//        $("#statusModal").val("");
    });

    $("#addButton").on("click", function () {
        formReset();
    });

    $("#tblTahunBuku tbody").on("click", ".edit-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/api/akunting/parameter/tahun-buku/" + data.kodeTahunBuku + "/findById",
            success: function (resp) {
                $("#kodeTahunBuku").val(resp.kodeTahunBuku).prop("disabled", true);
                $("#tahun").val(resp.tahun);
                $("#namaTahunBuku").val(resp.namaTahunBuku);
                $("input[name='statusAktif'][value='" + resp.statusAktif + "']").prop('checked', true);
//                $("input[name='statusAktif']").prop("disabled", false);
                $("#createdDate").val(resp.createdDate);
            },
            statusCode: {
                204: function (value) {
                    console.log(value);
                }
            }
        });

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblTahunBuku tbody").on("click", ".delete-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#delete-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteButton").off().on("click", function (e) {
            var obj = {
                "kodeTahunBuku": data.kodeTahunBuku
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/api/akunting/parameter/tahun-buku/delete",
                success: function (resp) {
                    $("#delete-form").modal("hide");
                    showWarning();
                    table.ajax.reload();
                }
            });
        });
    });

//    $("#tblTahunBuku tbody").on("click", ".btn-check", function () {
//        var data = table.row($(this).parents("tr")).data();
//        var obj = {
//            "kodeTahunBuku": data.kodeTahunBuku,
//            "namaTahunBuku": data.namaTahunBuku,
//            "tahun": data.tahun,
//            "statusAktif": "1",
//            "createdDate": data.createdDate
//        };
//
//        $.ajax({
//            type: "POST",
//            url: _baseUrl + "/api/akunting/parameter/tahun-buku/" + data.kodeTahunBuku + "/reset-status-aktif",
//            success: function () {
//                $.ajax({
//                    type: "POST",
//                    contentType: "application/json",
//                    data: JSON.stringify(obj),
//                    url: _baseUrl + "/api/akunting/parameter/tahun-buku/update",
//                    success: function () {
//                    },
//                    statusCode: {
//                        201: function () {
//                            table.ajax.reload();
//                        },
//                    }
//                });
//            }
//        });
//    });

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
    var kodeTahunBuku = $("#kodeTahunBuku").val();
    var namaTahunBuku = $("#namaTahunBuku").val();
    var tahun = $("#tahun").val();
    var statusAktif = $('#statusAktif input:radio:checked').val();

    var obj = {
        "kodeTahunBuku": kodeTahunBuku,
        "namaTahunBuku": namaTahunBuku,
        "tahun": tahun,
        "statusAktif": statusAktif
    };

    if (formValidation(obj) == true) {
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
        url: _baseUrl + "/api/akunting/parameter/tahun-buku/save",
        success: function (resp) {
            if (resp == "Exist") {
                swal.fire({
                    position: "top-right",
                    type: "error",
                    title: "Kode Arus Kas sudah digunakan.",
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
    var kodeTahunBuku = $("#kodeTahunBuku").val();
    var namaTahunBuku = $("#namaTahunBuku").val();
    var tahun = $("#tahun").val();
    var statusAktif = $('#statusAktif input:radio:checked').val();
    var createdDate = $("#createdDate").val();

    var obj = {
        "kodeTahunBuku": kodeTahunBuku,
        "namaTahunBuku": namaTahunBuku,
        "tahun": tahun,
        "statusAktif": statusAktif,
//        "createdDate": createdDate
    };

    if (formValidation(obj) == true) {
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
        url: _baseUrl + "/api/akunting/parameter/tahun-buku/update",
        success: function () {
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
    $("#kodeTahunBuku").val("").prop("disabled", false);
    $("#tahun").val("").prop("disabled", false);
    $("#namaTahunBuku").val("").prop("disabled", false);
    $("input[name='statusAktif'][value='0']").prop('checked', true);
//    $("input[name='statusAktif']").prop("disabled", true);
    $("#statusModal").val("");
}

function formValidation(obj) {
    var found = Object.keys(obj).filter(function (key) {
        return obj[key] === "";
    });
    if (found.length > 0) {
        return true;
    } else {
        return false;
    }
}

function onlyNumberKey(evt, value) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57))
        return false;
    if (value[0].value.length > 3)
        return false;
    return true;
}