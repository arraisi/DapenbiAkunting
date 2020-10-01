var tableArusKasRincian;
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
                width: '20%',
            },
            {
                title: 'Keterangan',
                data: 'keterangan',
                className: '',
                width: '60%',
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
        bLengthChange: false,
        responsive: true,
        bPaginate: true,
        bInfo: true
    });

    arusKasRincianDatatables("00");

    $('#tblArusKas tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            arusKasRincianDatatables("00");
        } else {
            tableArusKas.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var data = tableArusKas.row(".selected").data();
            $("#kodeArusKasRead").val(data.kodeArusKas);
            arusKasRincianDatatables(data.kodeArusKas);
        }
    });

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
                    return formatMoney(data, "", 2);
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
                render: function (data) {
                    return "<a href='javascript:void(0);' class='validasi-button'>Koreksi</a>";
                }
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
    $("#saveArusKasRincianButton").on("click", function () {
        var value = $("#statusModalArusKasRincian").val();

        if (value == "update") {
            updateArusKasRincian(tableArusKas);
        } else {
            saveArusKasRincian(tableArusKas);
        }
    });

    $("#tblArusKasRincian tbody").on("click", ".validasi-button", function () {
        var data = tableArusKasRincian.row($(this).parents("tr")).data();
        console.log(data);
        var urlRekening = _baseUrl + "/api/akunting/parameter/rekening/findById/" + data.idRekening;
        getJson(urlRekening, function (data) {
            console.log(data);
            $("#idRekening").val(data);
        });
        $("#kodeArusKasRead").val(data.kodeArusKas);
        $("#kodeArusKasRincian").val(data.kodeRincian);
//        $("#idRekening").val(data.idRekening);
        $("#keteranganArusKasRincian").val(data.keterangan);
        $("#saldoAwalTahun").val(formatMoney(data.saldoAwalTahun, ""));
        $("#flagRumus").val(data.flagRumus).selectpicker("refresh");
        $("#flagGroup").val(data.flagGroup).selectpicker("refresh");
        $("input[name='statusAktifArusKasRincian'][value='" + data.statusAktif + "']").prop('checked', true);

        $("#add-form-arus-kas-rincian").modal({
            backdrop: 'static',
            keyboard: false
        });
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

function getJson(url, callback) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        success: function (resp) {
            var value = resp.kodeRekening + " - " + resp.namaRekening;
            callback(value);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
                var message = resp.responseJSON.message;
                if (message == "No value present")
                    callback(message);
            }
        }
    });
}