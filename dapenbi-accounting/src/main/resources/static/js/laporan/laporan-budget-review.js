var table;

jQuery(document).ready(function (){
    var sistemKodePeriode = pengaturanSistem.kodePeriode;
    var sistemTriwulan;
    if (sistemKodePeriode == "01" || sistemKodePeriode == "02" || sistemKodePeriode == "03") sistemTriwulan = "TW1";
    else if (sistemKodePeriode == "04" || sistemKodePeriode == "05" || sistemKodePeriode == "06") sistemTriwulan = "TW2";
    else if (sistemKodePeriode == "07" || sistemKodePeriode == "08" || sistemKodePeriode == "09") sistemTriwulan = "TW3";
    else sistemTriwulan = "TW4";
    $("#idTahunBuku").val(pengaturanSistem.kodeTahunBuku).selectpicker("refresh");
    $("#idTriwulan").val(sistemTriwulan).selectpicker("refresh");

    table = $("#tblLaporanBudgetReview").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/laporan-budget-review/datatables',
            type: "POST",
            data: function (body) {
                var request = {};
                var order  = [];
                var length  = -1;
                order.push({
                    "column": 0,
                    "dir": "asc"
                });
                request.triwulan = $("#idTriwulan").val();
                request.kodeTahunBuku = $("#idTahunBuku").val();
                request.noBudgetReview = $("#noBudgetReview").val();
                body.request = request;
                body.length = length;
                body.order = order;
                return JSON.stringify(body);
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
        rowCallBack: function (row, data, index) {
            console.log(row, data);
        },
        ordering: false,
        lengthMenu: [[-1, 10, 25, 50, 100], ['All', '10', '25', '50', '100']],
        pageLength: -1,
        processing: true,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");

            if (data.levelRekening === 3) {
                $(row).addClass('rowLvl3');
                console.log(row);
            }

            if (data.levelRekening === 2) {
                $(row).addClass('rowLvl2');
                console.log(row);
            }

            if (data.levelRekening === 0) {
                $(row).addClass('rowLvl0');
                console.log(row);
            }
        },
        columns: [
            {
                title: 'MA',
                data: null,
                className: '',
                orderable: false,
                width: '10%',
                render: function (data) {
                    if (data.levelRekening == 6)
                        return "<div style='padding-left: 20px'>" + data.noMataAnggaran + "</div>";
                    else
                        return data.noMataAnggaran;
                }
            },
            {
                title: 'Mata Anggaran (MA)',
                data: null,
                orderable: false,
                width: '15%',
                render: function (data) {
                    if (data.levelRekening == 2)
                        return "<div style='margin-left: -40px'>" + data.namaMataAnggaran + "</div>";
                    else if (data.levelRekening == 3 || data.levelRekening == 0)
                        return "<div style='margin-left: -60px'>" + data.namaMataAnggaran + "</div>";
                    else
                        return data.namaMataAnggaran;
                }
            },
            {
                title: 'Anggaran Tahunan',
                data: null,
                className: "text-right",
                orderable: false,
                width: '15%',
                render: function (data) {
                    return formatMoney(data.anggaranTahunan);
                }
            },
            {
                title: 'Realisasi',
                data: null,
                className: "text-right",
                orderable: false,
                width: '15%',
                render: function (data) {
                    return formatMoney(data.realisasi);
                }
            },
            {
                title: 'Persen (%)',
                data: null,
                className: "text-right",
                orderable: false,
                width: '10%',
                render: function (data) {
                    return formatMoney(data.persen);
                }
            },
            {
                title: 'Saldo Anggaran Tahunan',
                data: null,
                className: "text-right",
                orderable: false,
                width: '15%',
                render: function (data) {
                    return formatMoney(data.saldoAnggaranTahunan);
                }
            },
            {
                title: 'Keterangan',
                data: null,
                orderable: false,
                width: '20%',
                render: function (data) {
                    return data.keterangan == null ? "" : data.keterangan;
                }
            }
        ],
        bFilter: true,
        bLengthChange: false,
        responsive: true,
        info: false,
        paging: false
    });

    initEvent();
});

function initEvent() {
    $("#export-pdf").click(function () {
        exportPdf($('#tblLaporanBudgetReview').DataTable());
    });
    $("#export-excel").click(function () {
        exportExcel($('#tblLaporanBudgetReview').DataTable());
    });
}

function exportPdf(table) {
    var obj = getDatatableValue(table.rows().data());
    var triwulan = $("#idTriwulan").find("option:selected").text().substr($("#idTriwulan").find("option:selected").text().length - 3);
    var tahunBuku = $("#idTahunBuku").find("option:selected").text();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-budget-review/export-pdf?triwulan=" + triwulan + "&tahunBuku=" + tahunBuku,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "laporan-budget-review"+".pdf";
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
    var triwulan = $("#idTriwulan").find("option:selected").text().substr($("#idTriwulan").find("option:selected").text().length - 3);
    console.log(triwulan);
    var tahunBuku = $("#idTahunBuku").val();
    console.log(obj);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-budget-review/export-excel?triwulan=" + triwulan + "&tahunBuku=" + tahunBuku,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "laporan-budget-review"+".xlsx";
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

        data[i].anggaranTahunan = (data[i].anggaranTahunan == null) || (data[i].anggaranTahunan == "") ? "0,00" : formatMoney(data[i].anggaranTahunan.toString());
        data[i].realisasi = (data[i].realisasi == null) || (data[i].realisasi == "") ? "0,00" : formatMoney(data[i].realisasi.toString());
        data[i].persen = (data[i].persen == null) || (data[i].persen == "") ? "0,00" : formatMoney(data[i].persen.toString());
        data[i].saldoAnggaranTahunan = (data[i].saldoAnggaranTahunan == null) || (data[i].saldoAnggaranTahunan == "") ? "0,00" : formatMoney(data[i].saldoAnggaranTahunan.toString());
        data[i].keterangan = (data[i].keterangan == null) || (data[i].keterangan == "") ? "" : data[i].keterangan;
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
    getListBudgetReview();
}

function getListBudgetReview() {
    var kodeTahunBuku = $("#idTahunBuku").val();
    var triwulan = $('#idTriwulan').val();
    console.log(kodeTahunBuku, triwulan);

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/laporan-budget-review/listBudgetReview" +
            "?kodeTahunBuku=" + kodeTahunBuku + "&triwulan=" + triwulan,
        success: function (resp) {
            console.log(resp, "list budget review");
            console.log(resp.length, "list budget review");
            if (resp.length == 0) {
                $('#noBudgetReview')
                    .empty()
                    .append('<option value="No Data Available" selected>No Data Available</option>')
                    .selectpicker("refresh");
                ;
                console.log("masuk 0");
            } else {
                $('#noBudgetReview')
                    .empty()
                ;
                var x = document.getElementById("noBudgetReview");
                resp.map(value => {
                    var option = document.createElement("option");
                    option.value = value;
                    option.text = value;
                    x.add(option);
                    $('.selectpicker').selectpicker('refresh');
                });
                console.log("masuk > 0");
            }
            table.ajax.reload();
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}