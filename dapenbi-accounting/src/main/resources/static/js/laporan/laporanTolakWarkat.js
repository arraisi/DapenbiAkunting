var _totalTransaksi;
var _currentWarkat;
var _tableWarkatLog;

jQuery(document).ready(function () {
    warkatDatatablesLog();
});

function compareUrutanJurnal(a, b) {
    // Use toUpperCase() to ignore character casing
    const urutanA = a.noUrut;
    const urutanB = b.noUrut;

    let comparison = 0;
    if (urutanA > urutanB) {
        comparison = 1;
    } else if (urutanA < urutanB) {
        comparison = -1;
    }
    return comparison;
}

//* WARKAT *//
// WARKAT DATATABLES
function warkatDatatablesLog() {
    _tableWarkatLog = $("#tblWarkatLog").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/transaksi/saldo/saldo-warkat/datatables/log',
            type: "POST",
            data: function (d) {
                d.warkatDTO = {
                    statusData: "REJECT"
                };
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
            }
        },
        lengthMenu: [[10, 25, 50, 100, -1], ['10', '25', '50', '100', 'All']],
        pageLength: -1,
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                data: 'noWarkat',
                title: 'No. Warkat',
                className: 'text-center',
                width: "25%",
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'keterangan',
                title: 'Keterangan',
                width: "75%",
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON EDIT
    $('#tgblWarkatLog tbody').on('click', '.validation-button', function () {
        // ...
    });
}

// EXPORT PDF
function exportPdf() {
    changeBtnActive(false);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(getDatatableValue(_tableWarkatLog.rows().data())),
        url: _baseUrl + `/akunting/transaksi/saldo/saldo-warkat/log/export-pdf`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "LaporanTolakWarkat.pdf";
            link.target = "_blank";

            var byteCharacters = atob(response);
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var file = new Blob([byteArray], {type: 'application/pdf;base64'});
            var fileURL = URL.createObjectURL(file);
            window.open(fileURL);

            changeBtnActive(true);
        },
        complete: function (resp) {
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

// EXPORT EXCEL
function exportExcel() {
    changeBtnActive(false);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(getDatatableValue(_tableWarkatLog.rows().data())),
        url: _baseUrl + `/akunting/transaksi/saldo/saldo-warkat/log/export-excel`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "LaporanTolakWarkat.xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            changeBtnActive(true);
        },
        complete: function (resp) {
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

function changeBtnActive(key) {
    $('#spinnerBtnPdf').attr('hidden', key);
    $('#spinnerBtnExcel').attr('hidden', key);
    $('#export-pdf').attr('disabled', !key);
    $('#export-excel').attr('disabled', !key);
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function getDatatableValue(data) {
    var rows = [];
    for (i = 0; i < data.length; i++) {
        rows.push(data[i]);
    }
    return rows;
}