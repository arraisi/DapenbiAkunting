var namaLaporan = "";
var judulLaporan = "";
var periode = "";

jQuery(document).ready(function () {
    $("#cmbLaporan").selectpicker();

    hideTable();
    initTable();
    initEvent();
});


// DATEPICKER
$(".datepicker-group").datepicker({
    todayHighlight: true,
    autoclose: true,
    orientation: "bottom",
    format: "mm/yyyy",
    viewMode: "months",
    minViewMode: "months"
});

function initTable() {
    var resp = {"data": [], "columns": [{"title": "One", "data": "a"}]};
    $('#tableOjk').DataTable({
        data: resp.data,
        columns: resp.columns
    });
}

function initEvent() {
    $("#cmbLaporan").change(function () {
        var cmbText = $("#cmbLaporan option:selected").text();
        var cmbVal = $("#cmbLaporan option:selected").val();

        namaLaporan = cmbVal;
        judulLaporan = cmbText;

        if (cmbVal == "") {
            hideTable();
        }
    });

    $("#btnProses").click(function () {
        if ($("#txtPeriode").val() === "" || namaLaporan === "") {
            showError("Pilih Laporan dan Periode");
            return;
        }

        let periodeArray = $("#txtPeriode").val().split("/");
        let kodePeriode = periodeArray[0];
        let kodeTahunBuku = periodeArray[1];

        console.log(kodePeriode);
        console.log(kodeTahunBuku);

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + `/akunting/laporan/laporan-ojk/proses?` +
                `namaLaporan=${namaLaporan}&kodePeriode=${kodePeriode}&kodeTahunBuku=${kodeTahunBuku}`,
            success: function (response) {
                console.log(response);
                $('#btnTampil').click();
            },
            statusCode: {
                404: function () {
                    showError("Not Found");
                },
                500: function () {
                    showError("Internal Server Error");
                }
            }
        });
    });

    $("#btnTampil").click(function () {
        if (namaLaporan == "") {
            showError("Pilih Laporan");
            return;
        } else {
            $("#labelPeriode").text("periode: " + $("#txtPeriode").val());
        }
        $("#labelJudul").text(judulLaporan);
        $("#labelPeriode").text(periode);
        refreshTable();
    });

    $("#btnExportPdf").click(function () {
        exportPdf($('#tableOjk').DataTable());
    });

    $("#btnExportExcel").click(function () {
        exportExcel($('#tableOjk').DataTable());
    });

    $("#btnGenerateExel").click(function () {
        var $this = $(this);
        var loadingText = '<i class="fa fa-circle-o-notch fa-spin"></i>';
        if ($(this).html() !== loadingText) {
            $this.data('original-text', $(this).html());
            $this.html(loadingText);
        }
        generateExcel($this);
    });
}

function hideTable() {
    $("#labelJudul").hide();
    $("#labelPeriode").hide();

    $("#divTableOjk").hide();

    $("#btnExportPdf").hide();
    $("#btnExportExcel").hide();
}

function refreshTable() {
    hideTable();
    $("#labelJudul").show();
    $("#labelPeriode").show();
    $("#divTableOjk").show();

    var cmbVal = $("#cmbLaporan option:selected").val();
    var monthYear = $("#txtPeriode").val();
    var monthYearSplit = monthYear.split("/");

    // console.log("bulan tahun : " + monthYear);
    console.log("bulan : " + monthYearSplit[0]);
    console.log("tahun : " + monthYearSplit[1]);

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/laporan-ojk/datatable?namaLap=" + cmbVal + "&periode="
            + monthYearSplit[0] + "&tahun=" + monthYearSplit[1],
        success: function (response) {
            console.log(response);
            $('#tableOjk').DataTable().clear().destroy();
            $('#tableOjk').empty();
            $('#tableOjk').DataTable({
                paging: false,
                data: response.data,
                columns: response.columns
            });

            if (response.data.length > 0) showExportBtn();
        },
        statusCode: {
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function exportPdf(table) {
    var cmbVal = $("#cmbLaporan option:selected").val();
    var obj = getDatatableValue(table.rows().data());

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-ojk/export-pdf?namaLaporan=" + cmbVal + "&periode=" + "01/" + $("#txtPeriode").val(),
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = judulLaporan + ".pdf";
            link.target = "_blank";
            PDFObject.embed(link.href, "#example1");
            $('#pdfModal').modal('show');

            /*document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);*/
        },
        statusCode: {
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function exportExcel(table) {
    var cmbVal = $("#cmbLaporan option:selected").val();
    var obj = getDatatableValue(table.rows().data());

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-ojk/export-excel?namaLaporan=" + cmbVal + "&periode=" + "01/" + $("#txtPeriode").val(),
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = judulLaporan + ".xlsx";
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
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function generateExcel($this) {
    var monthYear = $("#txtPeriode").val();
    var monthYearSplit = monthYear.split("/");

    if (monthYear === "" || monthYear == undefined) {
        showError("Pilih Periode");
        $this.html($this.data('original-text'));
        return;
    }

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/laporan-ojk/generate-excel?periode=" +
            monthYearSplit[0] + "&tahun=" + monthYearSplit[1],
        success: function (response) {
            var sampleArr = base64ToArrayBuffer(response.data);
            saveByteArray(response.fileName, sampleArr);
            $this.html($this.data('original-text'));
        },
        statusCode: {
            400: function () {
                showError("Inputan tidak boleh kosong");
                $this.html($this.data('original-text'));
            },
            404: function () {
                showError("Not Found");
                $this.html($this.data('original-text'));
            },
            500: function () {
                showError("Internal Server Error");
                $this.html($this.data('original-text'));
            }
        }
    });
}

function base64ToArrayBuffer(base64) {
    var binaryString = window.atob(base64);
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
        var ascii = binaryString.charCodeAt(i);
        bytes[i] = ascii;
    }
    return bytes;
}

function saveByteArray(reportName, byte) {
    var blob = new Blob([byte], {type: "application/octet-stream"});
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    var fileName = reportName;
    link.download = fileName;
    link.click();
};

function showExportBtn() {
    $("#btnExportPdf").show();
    $("#btnExportExcel").show();
}

function getDatatableValue(data) {
    var rows = [];
    for (i = 0; i < data.length; i++) {
        rows.push(data[i]);
    }
    return rows;
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function numeralForCountformat(value) {
    return numeral(value).format('0.00')
}
