var tableArusKasRincian;
var _tipeAktivitas = [];
jQuery(document).ready(function () {
    var tableArusKas = $("#tblArusKas").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/arus-kas/getArusKasDataTables",
            type: "POST",
            data: function (d) {
                console.log(d);
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        lengthMenu: [[10, 25, 50, 100, -1], ['10', '25', '50', '100', 'All']],
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'No.',
                data: 'kodeArusKas',
                orderable: false,
                className: 'text-center',
                width: '10%',
//               render: function (data, type, row, meta) {
//                   return meta.row + 1;
//               }
            },
            {
                title: 'Arus Kas',
                data: 'kodeArusKas',
                className: 'text-center',
                width: '10%',
            },
            {
                title: 'Keterangan',
                data: 'keterangan',
                className: '',
                width: '60%',
            },
            {
                title: 'Status',
                width: '10%',
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
                width: '10%',
                searchable: false,
                sortable: false,
                className: 'text-center except',
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
        responsive: true,
        bPaginate: true,
        bInfo: true
    });

    arusKasRincianDatatables("00");

    initEvent(tableArusKas);
    getRekeningDatatables();
});

function arusKasRincianDatatables(idArusKas) {
    // console.log(idArusKas);
    if (tableArusKasRincian != undefined) {
        tableArusKasRincian.clear();
        tableArusKasRincian.destroy();
    }
    tableArusKasRincian = $("#tblArusKasRincian").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/arus-kas-rincian/" + idArusKas + "/getByArusKasIdDatatables",
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [0, "asc"],
        lengthMenu: [10, 25, 50, 100],
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'Rincian',
                data: 'kodeRincian',
                className: 'text-center',
                width: '6%'
            },
            {
                title: 'Rekening',
                data: 'idRekening',
                className: 'text-center',
                width: '15%'
            },
            {
                title: 'Saldo Awal Tahun',
                data: 'saldoAwalTahun',
                className: 'text-right',
                orderable: false,
                width: '20%',
                render: function (data) {
                    return formatMoney(data, "Rp. ", 2);
                }
            },
            {
                title: 'Keterangan',
                data: 'keterangan',
                className: '',
                orderable: false,
                width: '20%'
            },
            {
                title: 'Flag Rekening',
                data: 'flagRekening',
                className: 'text-center',
                orderable: false,
                width: '8%'
            },
            {
                title: 'Flag Rumus',
                data: 'flagRumus',
                className: 'text-center',
                orderable: false,
                width: '8%'
            },
            {
                title: 'Flag Kelompok',
                data: 'flagGroup',
                className: 'text-center',
                orderable: false,
                width: '8%'
            },
            {
                title: 'Status',
                data: 'statusAktif',
                className: 'text-center',
                width: '7%',
                render: function (data) {
                    if (data == "0") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" disabled>';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked disabled>'
                    }
                },
                orderable: false
            },
            {
                title: 'Tindakan',
                searchable: false,
                sortable: false,
                className: 'text-center',
                width: '8%',
                "defaultContent": getButtonGroup(true, true, false)
            }
        ],
        bFilter: true,
        bLengthChange: false,
        responsive: true,
        bPaginate: false,
        bInfo: false
    });
//    tableArusKasRincian.ajax.reload();
}

function initEvent(tableArusKas) {
    $('#tblArusKas tbody').on('click', 'td', function () {
        if ($(this)[0].cellIndex == 4)
            return;

        var row = $(this).closest("tr");

        if (row.hasClass('selected')) {
            row.removeClass('selected');
            arusKasRincianDatatables("00");
        } else {
            tableArusKas.$('tr.selected').removeClass('selected');
            row.addClass('selected');
            var data = tableArusKas.row(".selected").data();
            $("#kodeArusKasRead").val(data.kodeArusKas);
            arusKasRincianDatatables(data.kodeArusKas);
        }
    });

    $("#saveArusKasButton").on("click", function () {
        var value = $("#statusModalArusKas").val();

        if (value == "update") {
            updateArusKas(tableArusKas);
        } else {
            saveArusKas(tableArusKas);
        }
    });

    $("#saveArusKasRincianButton").on("click", function () {
        var value = $("#statusModalArusKasRincian").val();

        if (value == "update") {
            updateArusKasRincian(tableArusKas);
        } else {
            saveArusKasRincian(tableArusKas);
        }
    });

    $("#tblArusKas tbody").on("click", ".edit-button", function () {
        var data = tableArusKas.row($(this).parents("tr")).data();

        $("#statusModalArusKas").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/arus-kas/" + data.kodeArusKas + "/findById",
            success: function (resp) {
                // console.log(resp, "edit");
                $("#kodeArusKas").val(resp.kodeArusKas).prop("disabled", true);
                $("#keteranganArusKas").val(resp.keterangan);
                $("#txtJenisAktivitas").val(resp.arusKasAktivitas).selectpicker("refresh");
                $("input[name='statusAktifArusKas'][value='" + resp.statusAktif + "']").prop('checked', true);
//                $("input[name='statusAktifArusKas']").prop("disabled", true);
                $("#createdDate").val(resp.createdDate);
            }
        });

        $("#add-form-arus-kas").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblArusKasRincian tbody").on("click", ".edit-button", function () {
        var data = tableArusKasRincian.row($(this).parents("tr")).data();

        $("#statusModalArusKasRincian").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/arus-kas-rincian/" + data.kodeArusKas + "/" + data.kodeRincian + "/findByKodeArusKasAndKodeRincian",
            success: function (resp) {
                $("#kodeArusKasRead").val(resp.kodeArusKas);
                $("#kodeArusKasRincian").val(resp.kodeRincian).prop("disabled", true);
//                $("#idRekening").val("").selectpicker("refresh");
//                $("#idRekening option").each(function(){
//                    if(this.value == resp.idRekening) {
//                        $("#idRekening").val(resp.idRekening).selectpicker("refresh");
//                        return false;
//                    } else {
//                        return true;
//                    }
//                });

                $("#trueIdRekening").val(resp.idRekening);
                $("#idRekening").val(resp.detailRekening);
                $("#keteranganArusKasRincian").val(resp.keterangan);
                $("#saldoAwalTahun").val(formatMoney(resp.saldoAwalTahun, ""));
                $("#flagRumus").val(resp.flagRumus).selectpicker("refresh");
                $("#flagGroup").val(resp.flagGroup).selectpicker("refresh");
                $("input[name='statusAktifArusKasRincian'][value='" + resp.statusAktif + "']").prop('checked', true);
//                $("input[name='statusAktifArusKasRincian']").prop("disabled", true);
                $("#createdDateArusKasRincian").val(resp.createdDate);
            }
        });

        $("#add-form-arus-kas-rincian").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblArusKas tbody").on("click", ".delete-button", function () {
        var data = tableArusKas.row($(this).parents("tr")).data();

        $("#delete-form-arus-kas").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteArusKasButton").off().on("click", function (e) {
            var obj = {
                "kodeArusKas": data.kodeArusKas
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/parameter/arus-kas/delete",
                success: function (resp) {
                    $("#delete-form-arus-kas").modal("hide");
                    showWarning();
                    tableArusKas.ajax.reload();
                }
            });
        });
    });

    $("#tblArusKasRincian tbody").on("click", ".delete-button", function () {
        var dataArusKas = tableArusKas.row(".selected").data();
        var data = tableArusKasRincian.row($(this).parents("tr")).data();

        $("#delete-form-arus-kas-rincian").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteArusKasRincianButton").off().on("click", function (e) {
            var obj = {
                "kodeRincian": data.kodeRincian,
                "kodeArusKas": data.kodeArusKas
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/parameter/arus-kas-rincian/delete",
                success: function (resp) {
                    $("#delete-form-arus-kas-rincian").modal("hide");
                    showWarning();
                    arusKasRincianDatatables(dataArusKas.kodeArusKas);
//                    tableArusKas.ajax.reload();
                }
            });
        });
    });

    $("#tblArusKas tbody").on("click", ".btn-check", function () {
        var data = tableArusKas.row($(this).parents("tr")).data();
        var obj = {
            "kodeArusKas": data.kodeArusKas,
            "keterangan": data.keterangan,
            "statusAktif": "",
            "createdDate": data.createdDate
        };

        if (data.statusAktif == "0") {
            obj.statusAktif = "1";
        } else {
            obj.statusAktif = "0";
        }

        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/parameter/arus-kas/update",
            success: function (resp) {
            },
            statusCode: {
                201: function () {
                    tableArusKas.ajax.reload();
                },
            }
        });
    });

    $("#tblArusKasRincian tbody").on("click", ".btn-check", function () {
        var data = tableArusKasRincian.row($(this).parents("tr")).data();
        var obj = {
            "kodeArusKas": data.kodeArusKas,
            "kodeRincian": data.kodeRincian,
            "idRekening": data.idRekening,
            "keterangan": data.keterangan,
            "saldoAwalTahun": data.saldoAwalTahun,
            "flagRumus": data.flagRumus,
            "flagGroup": data.flagGroup,
            "statusAktif": "",
            "createdDate": data.createdDate
        };

        if (data.statusAktif == "0") {
            obj.statusAktif = "1";
        } else {
            obj.statusAktif = "0";
        }

        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/parameter/arus-kas-rincian/update",
            success: function (resp) {
            },
            statusCode: {
                201: function () {
                    arusKasRincianDatatables(data.kodeArusKas);
                },
            }
        });
    });

    $("#addButtonArusKas").on("click", function () {
        formArusKasReset();
    });

    $("#addButtonArusKasRincian").on("click", function () {
        var selected = tableArusKas.rows(".selected").count();
        var data = tableArusKas.row(".selected").data();
        if (selected == 0) {
            showPreventArusKas();
        } else {
            formArusKasRincianReset();
            getIdArusKasRincian(data.kodeArusKas);
            $("#add-form-arus-kas-rincian").modal({
                backdrop: 'static',
                keyboard: false
            });
        }
    });

}

function saveArusKas(tableArusKas) {
    var kodeArusKas = $("#kodeArusKas").val();
    var keterangan = $("#keteranganArusKas").val();
    var statusAktif = $('#statusAktifArusKas input:radio:checked').val();
    var jenisAktivitas = $('#txtJenisAktivitas').val();
    // console.log(statusAktif, 'status aktif');
    // console.log(jenisAktivitas, 'jenisAktivitas');

    // $("input[name='statusAktifArusKas'][value='" + resp.statusAktif + "']").prop('checked', true);

    if (kodeArusKas == "" || keterangan == "" || jenisAktivitas === null) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    var obj = {
        "kodeArusKas": kodeArusKas,
        "keterangan": keterangan,
        "statusAktif": statusAktif,
        "arusKasAktivitas": jenisAktivitas,
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/parameter/arus-kas/save",
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
            201: function (resp) {
                $("#add-form-arus-kas").modal("hide");
                showSuccess();
                tableArusKas.ajax.reload();
            }
        }
    });
}

function updateArusKas(tableArusKas) {
    var kodeArusKas = $("#kodeArusKas").val();
    var keterangan = $("#keteranganArusKas").val();
    var statusAktif = $('#statusAktifArusKas input:radio:checked').val();
    var jenisAktivitas = $('#txtJenisAktivitas').val();
    var createdDate = $("#createdDate").val();

    if (kodeArusKas == "" || keterangan == "" || jenisAktivitas === null) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    var obj = {
        "kodeArusKas": kodeArusKas,
        "keterangan": keterangan,
        "statusAktif": statusAktif,
        "arusKasAktivitas": jenisAktivitas,
//        "createdDate": createdDate
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/parameter/arus-kas/update",
        success: function (resp) {
        },
        statusCode: {
            201: function () {
                $("#add-form-arus-kas").modal("hide");
                $("#statusModalArusKas").val("");
                showSuccess();
                tableArusKas.ajax.reload();
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

function formArusKasReset() {
    $("#kodeArusKas").val("").prop("disabled", false);
    $("#keteranganArusKas").val("");
    $("#txtJenisAktivitas").val("").selectpicker('refresh');
    $("input[name='statusAktifArusKas'][value='0']").prop('checked', true);
//    $("input[name='statusAktifArusKas']").prop("disabled", true);
    $("#statusModalArusKas").val("");
}

function saveArusKasRincian(tableArusKas) {
    var data = tableArusKas.row(".selected").data();
    var selected = tableArusKas.rows(".selected").count();
    var kodeRincian = $("#kodeArusKasRincian").val();
    var idRekening = $("#trueIdRekening").val();
    var keterangan = $("#keteranganArusKasRincian").val();
    var saldoAwalTahun = getNumberRegEx($("#saldoAwalTahun").val());
    var flagRumus = $("#flagRumus").val();
    var flagGroup = $("#flagGroup").val();
    var statusAktif = $('#statusAktifArusKasRincian input:radio:checked').val();

    var obj = {
        "kodeRincian": kodeRincian,
        "kodeArusKas": data.kodeArusKas,
        "idRekening": idRekening,
        "keterangan": keterangan,
        "saldoAwalTahun": saldoAwalTahun,
        "flagRumus": flagRumus,
        "flagGroup": flagGroup,
        "statusAktif": statusAktif
    }

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
        url: _baseUrl + "/akunting/parameter/arus-kas-rincian/save",
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
            201: function (resp) {
                $("#add-form-arus-kas-rincian").modal("hide");
                showSuccess();
                arusKasRincianDatatables(data.kodeArusKas);
//                tableArusKasRincian.ajax.reload();
            }
        }
    });

//    console.log(selected);
}

function updateArusKasRincian(tableArusKas) {
    var data = tableArusKas.row(".selected").data();
    var selected = tableArusKas.rows(".selected").count();
    var kodeRincian = $("#kodeArusKasRincian").val();
    var idRekening = $("#trueIdRekening").val();
    var keterangan = $("#keteranganArusKasRincian").val();
    var saldoAwalTahun = getNumberRegEx($("#saldoAwalTahun").val());
    var flagRumus = $("#flagRumus").val();
    var flagGroup = $("#flagGroup").val();
    var statusAktif = $('#statusAktifArusKasRincian input:radio:checked').val();
    var createdDate = $("#createdDateArusKasRincian").val();

    var obj = {
        "kodeRincian": kodeRincian,
        "kodeArusKas": data.kodeArusKas,
        "idRekening": idRekening,
        "keterangan": keterangan,
        "saldoAwalTahun": saldoAwalTahun,
        "flagRumus": flagRumus,
        "flagGroup": flagGroup,
        "statusAktif": statusAktif,
//        "createdDate": createdDate
    }

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
        url: _baseUrl + "/akunting/parameter/arus-kas-rincian/update",
        success: function (resp) {
        },
        statusCode: {
            201: function () {
                $("#add-form-arus-kas-rincian").modal("hide");
                $("#statusModalArusKasRincian").val("");
                showSuccess();
                arusKasRincianDatatables(data.kodeArusKas);
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

function formArusKasRincianReset() {
    $("#kodeArusKasRincian").val("").prop("disabled", false);
    $("#idRekening").val("").selectpicker("refresh");
    $("#keteranganArusKasRincian").val("");
    $("#saldoAwalTahun").val("");
    $("#flagRumus").val("").selectpicker("refresh");
    $("#flagGroup").val("").selectpicker("refresh");
    $("input[name='statusAktifArusKasRincian'][value='0']").prop('checked', true);
//    $("input[name='statusAktifArusKasRincian']").prop("disabled", true);
    $("#statusModalArusKasRincian").val("");
}

function getIdArusKasRincian(kode) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/arus-kas-rincian/" + kode + "/get-id-arus-kas-rincian",
        success: function (resp) {
            // console.log(resp);
        }
    });
}

function showPreventArusKas() {
    swal.fire({
        position: "top-right",
        type: "error",
        title: "Pilih Arus Kas terlebih dahulu.",
        showConfirmButton: !1,
        timer: 1500
    });
}

function formValidation(obj) {
    var found = Object.keys(obj).filter(function (key) {
        if (key != "saldoAwalTahun") {
            return obj[key] === "";
        }
    });
    if (found.length > 0) {
        return true;
    } else {
        return false;
    }
}

function onlyNumberKey(evt) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57))
        return false;
    return true;
}

function formatMoney(amount, currency, decimalCount = 2, decimal = ",", thousands = ".") {
    try {
        decimalCount = Math.abs(decimalCount);
        decimalCount = isNaN(decimalCount) ? 2 : decimalCount;

        const negativeSign = amount < 0 ? "-" : "";

        let i = parseInt(amount = Math.abs(Number(amount) || 0).toFixed(decimalCount)).toString();
        let j = (i.length > 3) ? i.length % 3 : 0;

        if (currency == "") {
            return negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
        } else {
            return currency + negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
        }
    } catch (e) {
        console.log(e)
    }
};

function getRekeningDatatables() {
    var tableRekening = $("#tblRekening").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/parameter/arus-kas-rincian/getRekeningDataTables",
            type: "POST",
            data: function (d) {
//                console.log(d);
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
        order: [0, "asc"],
        lengthMenu: [10, 25, 50, 100],
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
//            {
//               title: 'No.',
//               data: 'idRekening',
//               orderable: false,
//               className: 'text-center',
//            },
            {
                title: 'Kode Rekening',
                data: 'kodeRekening',
                className: 'text-center',
//               orderable: false
            },
            {
                title: 'Nama Rekening',
                data: 'namaRekening',
                className: '',
                orderable: false
            },
            {
                title: 'Level',
                data: 'levelRekening',
                className: '',
                orderable: false
            },
//            {
//               title: 'Status',
//               data: 'statusAktif',
//               className: 'text-center',
//               render: function (data) {
//                   if(data == "0") {
//                       return '<input class="btn-check" type="checkbox" style="vertical-align: middle" disabled>';
//                   } else {
//                       return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked disabled>'
//                   }
//               }
//            },
        ],
//        fnRowCallback : function (nRow, aData, iDisplayIndex, iDisplayIndexFull, oSettings) {
//            var info = this.api().page.info();
//            var page = info.page;
//            var length = info.length;
//            var index = (page * length + (iDisplayIndex + 1));
//            $('td:eq(0)', nRow).html(index);
//
//            return nRow;
//        },
        bFilter: true,
        bLengthChange: true,
        responsive: true,
        bPaginate: true,
        bInfo: true
    });

    $('#tblRekening tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            // if ($(this).find("td:eq(2)").text() != 6) {
            //     return;
            // }

            tableRekening.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var data = tableRekening.row(".selected").data();
//            dataRekening = data;
//            console.log(data);
            $("#trueIdRekening").val(data.idRekening);
            $("#idRekening").val(data.kodeRekening + " - " + data.namaRekening);
        }
    });

    $("#batalRekening").on("click", function () {
        tableRekening.$('tr.selected').removeClass('selected');
//        dataRekening = "";
//        $("#idRekening").val("");
    });

    $("#pilihRekening").on("click", function () {
        $("#rekening-form").modal("hide");
    });
}

function getNumberRegEx(value) {
    var res = value.replace(/\./g, "").replace(/\,/g, ".");
    return res;
}

function currencyWhileTyping(value) {
    value.val(function (index, value) {
        if (value != "") {
        //return '$' + value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            var decimalCount;
            value.match(/\,/g) === null ? decimalCount = 0 : decimalCount = value.match(/\,/g);

            if (decimalCount.length > 1) {
                value = value.slice(0, -1);
            }

            var components = value.toString().split(",");
            if (components.length === 1)
                components[0] = value;
            components[0] = components[0].replace(/\D/g, '').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
            if (components.length === 2) {
                components[1] = components[1].replace(/\D/g, '').replace(/^\d{3}$/, '');
            }

            if (components.join(',') != '')
                return components.join(',');
            else
                return '';
        }
    });
}


// EXPORT PDF
function exportPdf() {
    // console.log(_realisasiDatatable.rows().data());
    var periode = moment(new Date).format("YYYY-MM-DD");

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({}),
        url: _baseUrl + `/akunting/parameter/arus-kas/export-pdf?periode=${periode}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "RincianArusKas.pdf";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        complete: function (resp) {
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}