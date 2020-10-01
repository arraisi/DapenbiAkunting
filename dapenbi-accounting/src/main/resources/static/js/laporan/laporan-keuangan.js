var table;

jQuery(document).ready(function () {
    $("#idTahunBuku").val(pengaturanSistem.kodeTahunBuku).selectpicker("refresh");
    $("#idPeriode").val(pengaturanSistem.kodePeriode).selectpicker("refresh");

    table = $("#tblLaporan").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/laporan-keuangan/getLaporanKeuanganDatatables',
            type: "POST",
            data: function (d) {
                var order  = [];
                order.push({
                    "column": 0,
                    "dir": "asc"
                });
                var laporanKeuangan = {};
                laporanKeuangan.idLaporanHeader = $("#idLaporan").val();
                laporanKeuangan.kodeTahunBuku = $("#idTahunBuku").val();
                laporanKeuangan.kodePeriode = $("#idPeriode").val();
                laporanKeuangan.kodeDRI = $("#idDRI").val();
                d.laporanKeuangan = laporanKeuangan;
                d.order = order;
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
        lengthMenu: [[-1], ['All']],
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'Judul',
                data: null,
                className: '',
                orderable: false,
//                width: '20%',
                render: function (data) {
                    var levelAkun = data.levelAkun > 1 ? (data.levelAkun * 5) : 0;
                    var cetakGaris = data.cetakGaris == '1' ? "text-decoration: underline;" : "";
                    var cetakMiring = data.cetakMiring == '1' ? "font-style: italic;" : "";
                    var cetakTebal = data.cetakTebal == '1' ? "font-weight: bold;" : "";
                    var warna = "color: " + data.warna + ";";
                    var res = "<div style='padding-left: "+levelAkun+"px;"+cetakGaris+""+cetakMiring+""+cetakTebal+""+warna+"'>"+data.judul+"</div>";
                    return res;
                }
            },
            {
                title: 'Saldo Berjalan',
                data: 'saldoBerjalan',
                className: "text-right",
                orderable: false,
                render: function (data) {
                    return formatMoney(data);
                }
            },
            {
                title: 'Saldo Sebelum',
                data: 'saldoSebelum',
                className: "text-right",
                orderable: false,
                render: function (data) {
                    return formatMoney(data);
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
        bLengthChange: true,
        responsive: true
    });

    initEvent();
});

function initEvent() {
    $("#export-pdf").click(function () {
        exportPdf($('#tblLaporan').DataTable());
    });
    $("#export-excel").click(function () {
        exportExcel($('#tblLaporan').DataTable());
    });
}

function exportPdf(table) {
    var obj = getDatatableValue(table.rows().data());
    var namaLaporan = $("#idLaporan").find("option:selected").text();
    var idLaporanHeader = $("#idLaporan").val();
    var kodeTahunBuku = $("#idTahunBuku").val();
    var kodePeriode = $("#idPeriode").val();
    var kodeDRI = $("#idDRI").val();
//    var periode = convertStringDate($("#tglPeriode").val());
//    console.log(obj);
//    return;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-keuangan/export-pdf?namaLaporan=" + namaLaporan +
            "&idLaporanHeader=" + idLaporanHeader +
            "&kodeTahunBuku=" + kodeTahunBuku +
            "&kodePeriode=" + kodePeriode +
            "&kodeDRI=" + kodeDRI,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "Laporan Keuangan.pdf";
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
    var namaLaporan = $("#idLaporan").find("option:selected").text();
    var idLaporanHeader = $("#idLaporan").val();
    var kodeTahunBuku = $("#idTahunBuku").val();
    var kodePeriode = $("#idPeriode").val();
    var kodeDRI = $("#idDRI").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-keuangan/export-excel?namaLaporan=" + namaLaporan +
            "&idLaporanHeader=" + idLaporanHeader +
            "&kodeTahunBuku=" + kodeTahunBuku +
            "&kodePeriode=" + kodePeriode +
            "&kodeDRI=" + kodeDRI,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "Laporan Keuangan.xlsx";
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
    for(i=0; i<data.length; i++) {
        data[i].tabs = "";
        for (j=0;j<data[i].levelAkun;j++) {
            data[i].tabs += "  ";
        }

        data[i].namaRekening = data[i].namaRekening == null ? "" : data[i].namaRekening;
//        data[i].saldoAkhir = (data[i].saldoAkhir == null) || (data[i].saldoAkhir == "") ? "0,00" : formatMoney(data[i].saldoAkhir.toString());
        data[i].saldoBerjalan = (data[i].saldoBerjalan == null) || (data[i].saldoBerjalan == "") ? "0,00" : (data[i].saldoBerjalan < 0 ? "("+ formatMoney(Math.abs(data[i].saldoBerjalan.toString())) +")" : formatMoney(data[i].saldoBerjalan.toString()));
        data[i].saldoSebelum = (data[i].saldoSebelum == null) || (data[i].saldoSebelum == "") ? "0,00" : (data[i].saldoSebelum < 0 ? "("+ formatMoney(Math.abs(data[i].saldoSebelum.toString())) +")" : formatMoney(data[i].saldoSebelum.toString()));
        rows.push(data[i]);
    };
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

function refreshTable() {
    table.ajax.reload();
}