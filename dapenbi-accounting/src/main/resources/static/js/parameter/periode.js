jQuery(document).ready(function () {

    var table = $("#tblPeriode").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/parameter/periode/getPeriodeDataTables',
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
                data: 'kodePeriode',
                className: 'text-center',
                orderable: false
            },
            {
                title: 'Periode',
                data: 'kodePeriode',
                className: 'text-center',
            },
            {
                title: 'Nama Bulan',
                data: 'namaPeriode'
            },
            {
                title: 'Triwulan',
                data: 'triwulan'
            },
            {
                title: 'Quarter',
                data: 'quarter'
            },
//            {
//                title: 'Status',
//                data: 'statusAktif',
//                className: 'text-center',
//                render: function (data) {
//                    if(data == "0") {
//                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle">';
//                    } else {
//                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked>'
//                    }
//                }
//            },
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
    $("#savePeriodeButton").on("click", function () {
        var value = $("#statusModal").val();

        if(value == "update") {
            update(table);
        } else {
            save(table);
            console.log("Saved");
        }
//        $("#statusModal").val("");
    });

    $("#tblPeriode tbody").on("click", ".edit-button", function () {
        var data = table.row($(this).parents("tr")).data();
        console.log(data);
        var obj = _baseUrl + '/api/akunting/parameter/periode/' + parseInt(data.kodePeriode) + '/findById';

        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + '/api/akunting/parameter/periode/' + data.kodePeriode + '/findById',
            success: function (resp) {
                $("#periode").val(resp.kodePeriode).prop("disabled", true);
                $("#namaBulan").val(resp.namaPeriode);
                $("#triwulan").val(resp.triwulan).selectpicker("refresh");
                $("#quarter").val(resp.quarter).selectpicker("refresh");
                $("input[name='statusAktif'][value='"+resp.statusAktif+"']").prop('checked', true);
//                $("input[name='statusAktif']").prop("disabled", true);
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
        console.log(obj);
    });

    $("#tblPeriode tbody").on("click", ".delete-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#delete-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteButton").off().on("click", function (e) {
//            console.log('Deleting...', data.kodePeriode);
//
            var obj = {
                kodePeriode: data.kodePeriode
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/api/akunting/parameter/periode/delete",
                success: function (resp) {
                    $("#delete-form").modal("hide");
                    showWarning();
                    table.ajax.reload();
                }
            });

//            var obj = {
//                "kodePeriode": data.kodePeriode,
//                "namaPeriode": data.namaPeriode,
//                "triwulan": data.triwulan,
//                "quarter": data.quarter,
//                "statusAktif": "0",
//                "createdDate": data.createdDate
//            };
//
//            $.ajax({
//                type: "POST",
//                contentType: "application/json",
//                data: JSON.stringify(obj),
//                url: _baseUrl + "/api/akunting/parameter/periode/update",
//                success: function (response) {
//                },
//                statusCode: {
//                    201: function () {
//                        $("#delete-form").modal("hide");
//                        showWarning();
//                        table.ajax.reload();
//                    },
//                    400: function () {
//                        alert('Inputan tidak boleh kosong');
//                    },
//                    404: function () {
//                        alert('Not Found');
//                    }
//                }
//            });
        });
    });

    $("#tblPeriode tbody").on("click", ".btn-check", function () {
        var data = table.row($(this).parents("tr")).data();
        var obj = {
            "kodePeriode": data.kodePeriode,
            "namaPeriode": data.namaPeriode,
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
            url: _baseUrl + "/api/akunting/parameter/periode/update",
            success: function (response) {
            },
            statusCode: {
                201: function () {
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

    $("#tblPeriode tbody").on("click", ".detail-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#detailOk").prop("hidden", false);
        $("#savePeriodeButton").prop("hidden", true);
        $("#batalButton").prop("hidden", true);

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + '/api/akunting/parameter/periode/' + data.kodePeriode + '/findById',
            success: function (resp) {
                console.log(resp);
                $("#periode").val(resp.kodePeriode).prop("disabled", true);
                $("#namaBulan").val(resp.namaPeriode).prop("disabled", true);
                $("input[name='statusAktif'][value='"+resp.statusAktif+"']").prop('checked', true);
                $("input[name='statusAktif']").prop("disabled", true);
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

    $("#detailOk").on("click", function () {
        setTimeout(function () {
            $("#detailOk").prop("hidden", true);
            $("#savePeriodeButton").prop("hidden", false);
            $("#batalButton").prop("hidden", false);
            formReset();
        }, 1000);
        $("#add-form").modal("hide");

    });
}

function save(table) {
    console.log('Saving...');
    var periode = document.getElementById("periode").value;
    var namaBulan = document.getElementById("namaBulan").value;
    var triwulan = document.getElementById("triwulan").value;
    var quarter = document.getElementById("quarter").value;
    var statusAktif = $('#statusAktif input:radio:checked').val();
    console.log(periode, statusAktif);

    var obj = {
        "kodePeriode": periode,
        "namaPeriode": namaBulan,
        "triwulan": triwulan,
        "quarter": quarter,
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
        url: _baseUrl + "/api/akunting/parameter/periode/save",
        success: function (resp) {
            if(resp == "Exist") {
                swal.fire({
                    position: "top-right",
                    type: "error",
                    title: "Kode Periode sudah digunakan.",
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
    var periode = document.getElementById("periode").value;
    var namaBulan = document.getElementById("namaBulan").value;
    var triwulan = document.getElementById("triwulan").value;
    var quarter = document.getElementById("quarter").value;
    var statusAktif = $('#statusAktif input:radio:checked').val();
    var createdDate = $("#createdDate").val();

    var obj = {
        "kodePeriode": periode,
        "namaPeriode": namaBulan,
        "triwulan": triwulan,
        "quarter": quarter,
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
        url: _baseUrl + "/api/akunting/parameter/periode/update",
        success: function (response) {
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
    $("#periode").val("").prop("disabled", false);
    $("#namaBulan").val("").prop("disabled", false);
    $("#triwulan").val("").selectpicker("refresh");
    $("#quarter").val("").selectpicker("refresh");
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
