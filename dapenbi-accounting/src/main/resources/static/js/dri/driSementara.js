var table;
var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];

jQuery(document).ready(function () {
    $("#tglPeriode").val(moment(new Date).format("DD/MM/YYYY"));
    $("#ttd").val(pengaturanSistem.kdiv);
//    console.log(pengaturanSistem);

    table = $("#tblPeriode").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/dri/getDRISementaraDatatables',
            type: "POST",
            data: function (d) {
                var driSementara = {};
                var order  = [];
                order.push({
                    "column": 0,
                    "dir": "asc"
                });
//                driSementara.tglPeriode = convertStringDate($("#tglPeriode").val());
                driSementara.tglPeriode = "";
                driSementara.kodeRekening = $("#idRekening").val();
                driSementara.idRekening = null;
                d.driSementara = driSementara;
                d.order = order;
                requestBody = d;
                return JSON.stringify(d);
            }
        },
        drawCallback: function (settings) {
            if (settings.json.recordsFiltered > 0) {
                $("#export-pdf").prop("disabled", false);
                $("#export-excel").prop("disabled", false);
            } else {
                $("#export-pdf").prop("disabled", true);
                $("#export-excel").prop("disabled", true);
            };
        },
        ordering: false,
        lengthMenu: [[10, 25, 50, 100, -1], ['10', '25', '50', '100', 'All']],
        pageLength: -1,
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'Kode Rekening',
                data: null,
                className: '',
                orderable: false,
                width: '8%',
                render: function (data) {
                    if (data.kodeRekeningDebit == null) {
                        return "";
                    }

                    if (data.levelRekeningDebit != '1') {
                        return "<div style='padding-left: 20px'>"+data.kodeRekeningDebit+"</div>";
                    }
                    return data.kodeRekeningDebit;
                }
            },
            {
                title: 'Nama Rekening',
                data: null,
                orderable: false,
                width: '30%',
                render: function (data) {
                    var cetakTebal = data.cetakTebalDebit == '1' ? "font-weight: bold;" : "";
                    if (data.namaRekeningDebit == null) {
                        return "";
                    }

                    if (data.levelRekeningDebit != '1' && !data.namaRekeningDebit.includes("TOTAL DEBET")) {
                        return "<div style='padding-left: 20px;"+cetakTebal+"'>"+data.namaRekeningDebit+"</div>";
                    }
                    return "<div style='"+cetakTebal+"'>"+data.namaRekeningDebit+"</div>";
                }
            },
            {
                title: 'Saldo',
                data: null,
                className: "text-right",
                orderable: false,
                width: '12%',
                render: function (data) {
                    if ((data.levelRekeningDebit == 6) || (data.levelRekeningDebit == null)) {
                        return data.saldoAkhirDebit == null ? "" : formatMoney(data.saldoAkhirDebit);
                    } else {
                        return "";
                    }
                }
            },
            {
                title: 'Kode Rekening',
                data: null,
                className: '',
                orderable: false,
                width: '8%',
                render: function (data) {
                    if (data.kodeRekeningKredit == null) {
                        return "";
                    }

                    if (data.levelRekeningKredit != '1') {
                        return "<div style='padding-left: 20px'>"+data.kodeRekeningKredit+"</div>";
                    }
                    return data.kodeRekeningKredit;
                }
            },
            {
                title: 'Nama Rekening',
                data: null,
                orderable: false,
                width: '30%',
                render: function (data) {
                    var cetakTebal = data.cetakTebalKredit == '1' ? "font-weight: bold;" : "";
                    if (data.namaRekeningKredit == null) {
                        return "";
                    }

                    if (data.levelRekeningKredit != '1' && !data.namaRekeningKredit.includes("TOTAL KREDIT")) {
                        return "<div style='padding-left: 20px;"+cetakTebal+"'>"+data.namaRekeningKredit+"</div>";
                    }
                    return "<div style='"+cetakTebal+"'>"+data.namaRekeningKredit+"</div>";
                }
            },
            {
                title: 'Saldo',
                data: null,
                className: "text-right",
                orderable: false,
                width: '12%',
                render: function (data) {
                    if ((data.levelRekeningKredit == 6) || (data.levelRekeningKredit == null)) {
                        return data.saldoAkhirKredit == null ? "" : formatMoney(data.saldoAkhirKredit);
                    } else {
                        return "";
                    }
                }
            },
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
        bLengthChange: false,
        responsive: true,
        paging: false,
        info: false
    });

    initEvent();
});

function initEvent() {
    $("#export-pdf").click(function () {
        exportPdf($('#tblPeriode').DataTable());
    });
    $("#export-excel").click(function () {
        exportExcel($('#tblPeriode').DataTable());
    });
}

function exportPdf(table) {
    var obj = getDatatableValue(table.rows().data());
    var periode = convertStringDate($("#tglPeriode").val());
    var rawTanggal = periode.split("-");
    var tanggal = rawTanggal[2] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[0];
    var ttd = $("#ttd").val();
//    console.log(tanggal);
//    return;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/dri/export-pdf?periode=" + periode + "&ttd=" + ttd + "&tanggal=" + tanggal,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "DRI Sementara.pdf";
            link.target = "_blank";
            PDFObject.embed(link.href, "#example1");
            $('#pdfModal').modal('show');

            // var byteCharacters = atob(response);
            // var byteNumbers = new Array(byteCharacters.length);
            // for (var i = 0; i < byteCharacters.length; i++) {
            //   byteNumbers[i] = byteCharacters.charCodeAt(i);
            // }
            // var byteArray = new Uint8Array(byteNumbers);
            // var file = new Blob([byteArray], { type: 'application/pdf;base64' });
            // var fileURL = URL.createObjectURL(file);
            // window.open(fileURL);

//            document.body.appendChild(link);
//            link.click();
//            document.body.removeChild(link);
        },
        statusCode: {
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function() {
                showError("Internal Server Error");
            }
        }
    });
}

function exportExcel(table) {
    var obj = getDatatableValue(table.rows().data());
    var periode = convertStringDate($("#tglPeriode").val());
    var rawTanggal = periode.split("-");
    var tanggal = rawTanggal[2] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[0];
    var ttd = $("#ttd").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/dri/export-excel?periode=" + periode + "&ttd=" + ttd + "&tanggal=" + tanggal,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "DRI Sementara.xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        statusCode: {
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function() {
                showError("Internal Server Error");
            }
        }
    });
}

function getDatatableValue(data) {
    var rows = [];
    var saldoDebit = [];
    var saldoKredit = [];
    var totalSaldoDebit = 0;
    var totalSaldoKredit = 0;
    var obj = {};

    for(i=0; i<data.length; i++) {
//        data[i].saldoAkhir = (data[i].saldoAkhir == null) || (data[i].saldoAkhir == "") ? "0,00" : formatMoney(data[i].saldoAkhir.toString());
        rows.push(data[i]);

//        var saldo = 0;
//        if (data[i].saldoNormal == "D") {
//            totalSaldoDebit+=data[i].saldoAkhirFormatted;
//            saldoDebit.push(data[i]);
//        } else if (data[i].saldoNormal == "K") {
//            totalSaldoKredit+=data[i].saldoAkhirFormatted;
//            saldoKredit.push(data[i]);
//        }
    };

//    if (saldoDebit.length > saldoKredit.length) {
//        var diff = saldoDebit.length - saldoKredit.length;
//        for (i=0; i<diff; i++) {
//            var objDebit = {
//                "idRekening": null,
//                "isSummary": null,
//                "kodeRekening": "",
//                "levelRekening": null,
//                "namaRekening": "",
//                "saldoAkhir": "",
//                "saldoAkhirFormatted": null,
//                "saldoNormal": null,
//                "tglPeriode": null
//            };
//
//            saldoKredit.push(objDebit);
//        }
//    } else {
//        var diff = saldoKredit.length - saldoDebit.length;
//        for (i=0; i<diff; i++) {
//            var objKredit = {
//                "idRekening": null,
//                "isSummary": null,
//                "kodeRekening": "",
//                "levelRekening": null,
//                "namaRekening": "",
//                "saldoAkhir": "",
//                "saldoAkhirFormatted": null,
//                "saldoNormal": null,
//                "tglPeriode": null
//            };
//
//            saldoDebit.push(objKredit);
//        }
//    }
//
//    obj.oddPage = saldoDebit;
//    obj.evenPage = saldoKredit;
//    obj.totalSaldoDebit = formatMoney(totalSaldoDebit);
//    obj.totalSaldoKredit = formatMoney(totalSaldoKredit);
    refreshTable();
    return rows;
}

function formatMoney(amount, decimalCount = 2, decimal = ",", thousands = ".") {
  try {
    decimalCount = Math.abs(decimalCount);
    decimalCount = isNaN(decimalCount) ? 2 : decimalCount;

    const negativeSign = amount < 0 ? "-" : "";

    let i = parseInt(amount = Math.abs(Number(amount) || 0).toFixed(decimalCount)).toString();
    let j = (i.length > 3) ? i.length % 3 : 0;

    return negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
  } catch (e) {
    console.log(e)
  }
};

function datePicker() {
    $("#periodeDatepicker").off().datepicker({
        format: "dd/mm/yyyy",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {refreshTable()});
}

function convertStringDate(value) {
    if (value == "")
        return "";
    else
//        console.log(value);
        var raw = value.split("/");
        var res = raw[2] + "-" + raw[1] + "-" + raw[0];
        return res;
}

function refreshTable() {
    table.ajax.reload();
}