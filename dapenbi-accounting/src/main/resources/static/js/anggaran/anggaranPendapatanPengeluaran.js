var table;

jQuery(document).ready(function (){
    $("#idTahunBuku").val(pengaturanSistem.kodeTahunBuku).selectpicker("refresh");

    table = $("#tblLaporanBudgetReview").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/anggaran-pendapatan-pengeluaran/datatables',
            type: "POST",
            data: function (body) {
                var request = {};
                var order  = [];
                var length  = -1;
                order.push({
                    "column": 0,
                    "dir": "asc"
                });
                request.kodeTahunBuku = $("#idTahunBuku").val();
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

            if (data.pendapatan === "Jumlah" && data.pengeluaran === "Jumlah") {
                $(row).addClass('rowTotal');
                console.log(row);
            }
        },
        columns: [
            {
                title: 'Sandi M.A',
                data: null,
                className: '',
                orderable: false,
                width: '10%',
                render: function (data) {
                    return data.sandiMaPendapatan;
                }
            },
            {
                title: 'Pendapatan',
                data: null,
                orderable: false,
                width: '17%',
                render: function (data) {
                    return data.pendapatan;
                }
            },
            {
                title: 'Jumlah (Rp)',
                data: null,
                className: "text-right",
                orderable: false,
                width: '13%',
                render: function (data) {
                    return data.jumlahPendapatan == null ? "" : formatMoney(data.jumlahPendapatan);
                }
            },
            {
                title: 'Sandi M.A',
                data: null,
                className: '',
                orderable: false,
                width: '10%',
                render: function (data) {
                    return data.sandiMaPengeluaran;
                }
            },
            {
                title: 'Pengeluaran',
                data: null,
                orderable: false,
                width: '17%',
                render: function (data) {
                    if (data.pengeluaran === "SURPLUS/ (DEFISIT) TERMASUK IURAN")
                        return "<strong>" + data.pengeluaran + "</strong>";
                    else
                        return data.pengeluaran;
                }
            },
            {
                title: 'Jumlah (Rp)',
                data: null,
                className: "text-right",
                orderable: false,
                width: '13%',
                render: function (data) {
                    if (data.pengeluaran === "SURPLUS/ (DEFISIT) TERMASUK IURAN" && data.jumlahPengeluaran < 0)
                        return "("+formatMoney(Math.abs(data.jumlahPengeluaran))+")";
                    else
                        return formatMoney(data.jumlahPengeluaran);
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
    var tahunBuku = $("#idTahunBuku").find("option:selected").text();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/anggaran-pendapatan-pengeluaran/export-pdf?tahunBuku=" + tahunBuku,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "anggaran-pendapatan-pengeluaran"+".pdf";
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

function exportExcel(table) {
    var obj = getDatatableValue(table.rows().data());
    var tahunBuku = $("#idTahunBuku").find("option:selected").text();
    console.log(obj);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/anggaran-pendapatan-pengeluaran/export-excel?tahunBuku=" + tahunBuku,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "anggaran-pendapatan-pengeluaran"+".xlsx";
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
        data[i].jumlahPendapatan = data[i].jumlahPendapatan == null ? "" : (data[i].jumlahPendapatan == 0 ? "0,00" : formatMoney(data[i].jumlahPendapatan.toString()));
        data[i].jumlahPengeluaran = data[i].jumlahPengeluaran == null ? "" : (data[i].jumlahPengeluaran == 0 ? "0,00" : (data[i].pengeluaran == "SURPLUS/ (DEFISIT) TERMASUK IURAN" && data[i].jumlahPengeluaran < 0 ? "("+formatMoney(Math.abs(data[i].jumlahPengeluaran).toString())+")" : formatMoney(data[i].jumlahPengeluaran.toString())));
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