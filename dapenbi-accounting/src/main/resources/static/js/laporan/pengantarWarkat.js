var table;
var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];

jQuery(document).ready(function () {
    $("#tglTransaksi").val(moment(new Date).format("DD/MM/YYYY"));

    table = $("#tblPengantar").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/laporan-pengantar-warkat/datatables",
            type: "POST",
            data: function (d) {
                var obj = {
                    "tglTransaksi": convertStringDate($("#tglTransaksi").val()),
                    "statusData": "VALID"
                };
                d.value = obj;
                return JSON.stringify(d);
            }
        },
        order: [ 1, "asc" ],
//        lengthMenu: [ 10, 25, 50, 100 ],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>'+
              '<option value="10">10</option>'+
              '<option value="20">20</option>'+
              '<option value="30">30</option>'+
              '<option value="40">40</option>'+
              '<option value="50">50</option>'+
              '<option value="-1">All</option>'+
            '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
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
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: 'noWarkat',
                orderable: false,
                width: '5%',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
               title: 'No. Warkat',
               data: 'noWarkat',
               width: '15%',
               className: 'text-center',
            },
            {
               title: 'Keterangan',
               data: 'keterangan',
               className: '',
               orderable: false,
            },
            {
               title: 'Jumlah Transaksi',
               data: 'totalTransaksi',
               className: 'text-right',
               orderable: false,
               width: '20%',
               render: function (data) {
                   return formatMoney(data);
               }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    initEvent();
});

function initEvent() {
    $("#export-pdf").click(function () {
        exportPdf($('#tblPengantar').DataTable());
    });
    $("#export-excel").click(function () {
        exportExcel($('#tblPengantar').DataTable());
    });
}

function formatMoney(amount, currency = "", decimalCount = 2, decimal = ",", thousands = ".") {
  try {
    decimalCount = Math.abs(decimalCount);
    decimalCount = isNaN(decimalCount) ? 2 : decimalCount;

    const negativeSign = amount < 0 ? "-" : "";

    let i = parseInt(amount = Math.abs(Number(amount) || 0).toFixed(decimalCount)).toString();
    let j = (i.length > 3) ? i.length % 3 : 0;

    if(currency == "")
        return negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");

    return currency + negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
  } catch (e) {
    console.log(e)
  }
}

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

function exportPdf(table) {
    var obj = getDatatableValue(table.rows().data());
    var periode = convertStringDate($("#tglTransaksi").val());
    var rawTanggal = $("#tglTransaksi").val().split("/");
    var tanggal = rawTanggal[0] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[2];
    var tanggalPengantarWarkat = rawTanggal[0] + "-" + rawTanggal[1] + "-" + rawTanggal[2];

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/laporan-pengantar-warkat/export-pdf?periode=" + periode + "&tanggal=" + tanggal + "&tanggalPengantarWarkat=" + tanggalPengantarWarkat,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "Pengantar Warkat.pdf";
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
    var periode = convertStringDate($("#tglTransaksi").val());
    var rawTanggal = $("#tglTransaksi").val().split("/");
    var tanggal = rawTanggal[0] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[2];
    var tanggalPengantarWarkat = rawTanggal[0] + "-" + rawTanggal[1] + "-" + rawTanggal[2];

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/laporan-pengantar-warkat/export-excel?periode=" + periode + "&tanggal=" + tanggal + "&tanggalPengantarWarkat=" + tanggalPengantarWarkat,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "Pengantar Warkat.xlsx";
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
        rows.push(data[i]);
    };
    refreshTable();
    return rows;
}