var tableSaldoWarkatPembukuan;
var tableSaldoPreApproval;
var tableSaldoFinalApproval;
var tableSaldoMasterPembukuan;
jQuery(document).ready(function () {
    $("#tglTransaksi").val(moment(new Date()).format("DD/MM/YYYY"));

    tableSaldoWarkatPembukuan = $("#tblSaldoWarkatPembukuan").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/informasi-saldo/getSaldoCurrentDatatables",
            type: "POST",
            data: function (d) {
                var saldoCurrent = {};
                saldoCurrent.statusData = "";
                d.saldoCurrent = saldoCurrent;
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
//        lengthMenu: [ 10, 25, 50, 100 ],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>' +
                '<option value="10">10</option>' +
                '<option value="20">20</option>' +
                '<option value="30">30</option>' +
                '<option value="40">40</option>' +
                '<option value="50">50</option>' +
                '<option value="-1">All</option>' +
                '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        },
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: 'kodeRekening',
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                title: 'Rekening',
                data: 'kodeRekening',
                className: 'text-center',
            },
            {
                title: 'Nama Rekening',
                data: 'namaRekening',
                className: '',
                orderable: false,
            },
            {
                title: 'Saldo Awal',
                data: 'saldoAwal',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Debit',
                data: 'saldoDebet',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Kredit',
                data: 'saldoKredit',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Akhir',
                data: 'saldoAkhir',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Tambah',
                data: 'serapTambah',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Kurang',
                data: 'serapKurang',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Anggaran',
                data: 'saldoAnggaran',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    tableSaldoPreApproval = $("#tblSaldoPreApproval").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/informasi-saldo/getPreApprovalDataTables",
            type: "POST",
            data: function (d) {
//                var saldoCurrent = {};
//                saldoCurrent.statusData = "PA";
//                d.saldoCurrent = saldoCurrent;
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
//        lengthMenu: [ 10, 25, 50, 100 ],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>' +
                '<option value="10">10</option>' +
                '<option value="20">20</option>' +
                '<option value="30">30</option>' +
                '<option value="40">40</option>' +
                '<option value="50">50</option>' +
                '<option value="-1">All</option>' +
                '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        },
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: "kodeRekening",
                orderable: false,
                className: 'text-center',
//               render: function (data, type, row, meta) {
//                   return meta.row + 1;
//               }
            },
            {
                title: 'Rekening',
                data: 'kodeRekening',
                className: 'text-center',
            },
            {
                title: 'Nama Rekening',
                data: 'namaRekening',
                className: '',
                orderable: false,
            },
            {
                title: 'Saldo Awal',
                data: 'saldoAwal',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Debit',
                data: 'saldoDebet',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Kredit',
                data: 'saldoKredit',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Akhir',
                data: 'saldoAkhir',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Tambah',
                data: 'serapTambah',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Kurang',
                data: 'serapKurang',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Anggaran',
                data: 'saldoAnggaran',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
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

    tableSaldoFinalApproval = $("#tblSaldoFinalApproval").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/informasi-saldo/getFinalApprovalDataTables",
            type: "POST",
            data: function (d) {
//                var saldoCurrent = {};
//                saldoCurrent.statusData = "FA";
//                d.saldoCurrent = saldoCurrent;
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>' +
                '<option value="10">10</option>' +
                '<option value="20">20</option>' +
                '<option value="30">30</option>' +
                '<option value="40">40</option>' +
                '<option value="50">50</option>' +
                '<option value="-1">All</option>' +
                '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        },
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: 'kodeRekening',
                orderable: false,
                className: 'text-center',
//               render: function (data, type, row, meta) {
//                   return meta.row + 1;
//               }
            },
            {
                title: 'Rekening',
                data: 'kodeRekening',
                className: 'text-center',
            },
            {
                title: 'Nama Rekening',
                data: 'namaRekening',
                className: '',
                orderable: false,
            },
            {
                title: 'Saldo Awal',
                data: 'saldoAwal',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Debit',
                data: 'saldoDebet',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Kredit',
                data: 'saldoKredit',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Akhir',
                data: 'saldoAkhir',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Tambah',
                data: 'serapTambah',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Kurang',
                data: 'serapKurang',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Anggaran',
                data: 'saldoAnggaran',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
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

    tableSaldoMasterPembukuan = $("#tblSaldoMasterPembukuan").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/informasi-saldo/getSaldoDatatables",
            type: "POST",
            data: function (d) {
                d.saldo = getFiterSearch();
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>' +
                '<option value="10">10</option>' +
                '<option value="20">20</option>' +
                '<option value="30">30</option>' +
                '<option value="40">40</option>' +
                '<option value="50">50</option>' +
                '<option value="-1">All</option>' +
                '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        },
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: null,
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                title: 'Rekening',
                data: "kodeRekening",
                className: 'text-center',
            },
            {
                title: 'Nama Rekening',
                data: "namaRekening",
                className: '',
                orderable: false,
            },
            {
                title: 'Saldo Awal',
                data: 'saldoAwal',
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Debit',
                data: "saldoDebet",
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Kredit',
                data: "saldoKredit",
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Akhir',
                data: "saldoAkhir",
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Tambah',
                data: "serapTambah",
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Serap Kurang',
                data: "serapKurang",
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
            {
                title: 'Saldo Anggaran',
                data: "saldoAnggaran",
                className: 'text-right',
                orderable: false,
                render: function (data) {
                    return numeralformat(data);
                }
            },
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // SELECT ROW
    $('#tblSaldoWarkatPembukuan tbody').on('dblclick', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            tableSaldoWarkatPembukuan.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = tableSaldoWarkatPembukuan.row(tr);
            var data = row.data();
            console.log(data);
            fatchFormDetail(data);
            $('#detailDialog').modal('show');
        }
    });

    // SELECT ROW
    $('#tblSaldoPreApproval tbody').on('dblclick', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            tableSaldoPreApproval.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = tableSaldoPreApproval.row(tr);
            var data = row.data();
            console.log(data);
            fatchFormDetail(data);
            $('#detailDialog').modal('show');
        }
    });

    // SELECT ROW
    $('#tblSaldoFinalApproval tbody').on('dblclick', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            tableSaldoFinalApproval.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = tableSaldoFinalApproval.row(tr);
            var data = row.data();
            console.log(data);
            fatchFormDetail(data);
            $('#detailDialog').modal('show');
        }
    });

    // SELECT ROW
    $('#tblSaldoMasterPembukuan tbody').on('dblclick', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            tableSaldoMasterPembukuan.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = tableSaldoMasterPembukuan.row(tr);
            var data = row.data();
            console.log(data);
            fatchFormDetail(data);
            $('#detailDialog').modal('show');
        }
    });

    initEvent(tableSaldoWarkatPembukuan, tableSaldoPreApproval, tableSaldoFinalApproval, tableSaldoMasterPembukuan);
    var arrayTable = [tableSaldoWarkatPembukuan, tableSaldoPreApproval, tableSaldoFinalApproval, tableSaldoMasterPembukuan];
    activeButton(arrayTable);
//    getTotalSaldo();
//    getTotalSaldoCurrent('All');
    getTotalAsetKewajiban("ACC_SALDO_CURRENT", "", "");
});

function fatchFormDetail(data) {
    $('#txtKodeRekening').val(data.kodeRekening);
    $('#txtNamaRekening').val(data.namaRekening);
    $('#txtDebit').val(numeralformat(data.saldoDebet));
    $('#txtKredit').val(numeralformat(data.saldoKredit));
    $('#txtSaldoAwal').val(numeralformat(data.saldoAwal));
    $('#txtSaldoAkhir').val(numeralformat(data.saldoAkhir));
    $('#txtSerapTambah').val(numeralformat(data.serapTambah));
    $('#txtSerapKurang').val(numeralformat(data.serapKurang));
    $('#txtSaldoAnggaran').val(numeralformat(data.saldoAnggaran));
    $('#txtNilaiAnggaran').val(numeralformat(data.nilaiAnggaran));
}

function initEvent(tableSaldoWarkatPembukuan, tableSaldoPreApproval, tableSaldoFinalApproval, tableSaldoMasterPembukuan) {
    $('#datetimepicker3').change(function () {
        refreshTable(tableSaldoMasterPembukuan);
    });
}

function activeButton(arrayTable) {
    var idx = 0;
    $("#saldo-tabs button.tabs").each(function () {
        var currentButton = $(this);
        var tableName = idx == 0 ? "ACC_SALDO_CURRENT" : idx == 1 ? "ACC_SALDO_PA" : idx == 2 ? "ACC_SALDO_FA" : idx == 3 ? "ACC_SALDO" : "ACC_SALDO_CURRENT";
        var kodeDRI = idx == 3 ? getFiterSearch().kodeDri : "";
        var tglTransaksi = idx == 3 ? getFiterSearch().tglSaldo : "";

        currentButton.on("click", function () {
            $(".active-button").removeClass("active-button");
            currentButton.addClass("active-button");
            activeBody(this.id, arrayTable);
            getTotalAsetKewajiban(tableName, kodeDRI, tglTransaksi);
        });
        idx++;
    });

//    // Get the container element
//    var btnContainer = document.getElementById("saldo-tabs");
//
//    // Get all buttons with class="btn" inside the container
//    var btns = btnContainer.getElementsByClassName("btn");
//
//    // Loop through the buttons and add the active class to the current/clicked button
//    for (var i = 0; i < btns.length; i++) {
//        btns[i].addEventListener("click", function () {
//            var tableName = i == 0 ? "ACC_SALDO_CURRENT" : i == 1 ? "ACC_SALDO_PA" : i == 2 ? "ACC_SALDO_FA" : "ACC_SALDO_CURRENT";
//            var current = document.getElementsByClassName("active-button");
//            current[0].className = current[0].className.replace(" active-button", "");
//            this.className += " active-button";
//            activeBody(this.id, arrayTable);
//            getTotalAsetKewajiban(tableName);
//        });
//    }
}

function activeBody(id, arrayTable) {
    $(".active-body").prop("hidden", true).removeClass("active-body");
    $("#" + id.replace("Button", "Body")).prop("hidden", false).addClass("active-body");
    arrayTable.forEach(function (v) {
        var tableId = v.context[0].sTableId;
        var bodyId = id;
        if (tableId.replace("tblSaldo", "saldo").includes(bodyId.replace("Button", ""))) {
            v.ajax.reload();
//            getTotalSaldoCurrent();
        }
    });
}

function datePicker() {
    $("#datetimepicker3").off().datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "dd/mm/yyyy"
    }, "show").on("change", function () {
        refreshTable(tableSaldoMasterPembukuan);
    })
}

function getFiterSearch() {
    var tglTransaksi = $("#tglTransaksi").val().split("/");
    var saldo = {};
    saldo.kodeDri = $("#jenisDRI").val();
    saldo.tglSaldo = tglTransaksi[2] + "-" + tglTransaksi[1] + "-" + tglTransaksi[0];
    return saldo;
}

function refreshTable(table) {
    if (table.context[0].sTableId == "tblSaldoMasterPembukuan")
        getTotalAsetKewajiban("ACC_SALDO", getFiterSearch().kodeDri, getFiterSearch().tglSaldo);

    table.ajax.reload();
}

function getTotalSaldo() {
    var filter = getFiterSearch();
    let kodeDRI = filter.kodeDri;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/informasi-saldo/saldo-aset-saldo?kodeDri=" + kodeDRI + "&tglSaldo=" + filter.tglSaldo,
        success: function (resp) {
            var saldoAsetMaster = parseInt(resp.saldoAset) || 0;
            var saldoKewajibanMaster = parseInt(resp.saldoKewajiban) || 0;
            var selisihSaldoMaster = saldoAsetMaster - saldoKewajibanMaster;
            $(".saldoAsetMaster").text(numeralformat(saldoAsetMaster));
            $(".saldoKewajibanMaster").text(numeralformat(saldoKewajibanMaster));
            $(".selisihSaldoMaster").text(numeralformat(selisihSaldoMaster));
        }
    });
}

function getTotalSaldoCurrent(param) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/informasi-saldo/saldo-aset-saldo-current/" + param,
        success: function (resp) {
            var saldoAsetWarkat = parseInt(resp.saldoAset) || 0;
            var saldoAsetKewajiban = parseInt(resp.saldoKewajiban) || 0;
            var selisihSaldoWarkat = saldoAsetWarkat - saldoAsetKewajiban;
            $(".saldoAsetWarkat").text(numeralformat(saldoAsetWarkat));
            $(".saldoKewajibanWarkat").text(numeralformat(saldoAsetKewajiban));
            $(".selisihSaldoWarkat").text(numeralformat(selisihSaldoWarkat));
        }
    });
}

function getTotalAsetKewajiban(tableName, kodeDRI, tglTransaksi) {
    var obj = {
        "tableName": tableName,
        "kodeDRI": kodeDRI,
        "tglTransaksi": tglTransaksi
    };

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/informasi-saldo/total-aset-kewajiban-saldo-warkat?tableName=" + tableName + "&kodeDRI=" + kodeDRI + "&tglTransaksi=" + tglTransaksi,
        success: function (resp) {
//            console.log(resp);
            $(".saldoAsetWarkat").text(numeralformat(resp.totalAset));
            $(".saldoKewajibanWarkat").text(numeralformat(resp.totalKewajiban));
            $(".selisihSaldoWarkat").text(numeralformat(resp.selisih));
            $(".saldoAsetMaster").text(numeralformat(resp.totalAset));
            $(".saldoKewajibanMaster").text(numeralformat(resp.totalKewajiban));
            $(".selisihSaldoMaster").text(numeralformat(resp.selisih));
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}