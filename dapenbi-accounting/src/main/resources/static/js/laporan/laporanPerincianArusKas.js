var _perincianArusKasDatatable;

jQuery(document).ready(function () {
    $('#txtTanggal').val(moment(new Date).format("YYYY-MM-DD"));
    perincianArusKasDatatable();
});

function datePicker() {
    $("#tanggalDatepicker").off().datepicker({
        format: "yyyy-mm-dd",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
        refreshDatatable();
    });
}

function refreshDatatable() {
    // console.log($('#idDRI').val());
    // console.log($('#txtTanggal').val());
    _perincianArusKasDatatable.ajax.reload();
}
function perincianArusKasDatatable() {
    _perincianArusKasDatatable = $("#perincianArusKasDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/laporan-aruskas/datatables',
            type: "POST",
            data: function (d) {
                d.arusKasBulananDTO = {
                    kodeTahunBuku: null,
                    kodePeriode: null,
                    kodeArusKas: "",
                    kodeDRI: $('#idDRI').val(),
                    tanggal: $('#txtTanggal').val(),
                }
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
            ;
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
            // console.log(data);
        },
        columns: [
            {
                data: 'kodeRekening',
                title: 'Kode Rekening',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'keterangan',
                title: 'Keterangan',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    // console.log(data);
                    return data;
                }
            },
            {
                data: 'tanggal',
                title: 'Tanggal',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return moment(data).format("YYYY-MM-DD");
                }
            },
            {
                data: 'saldo',
                title: 'Saldo',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                title: "Tindakan",
                width: "100px",
                class: "text-center",
                sortable: false,
                orderable: false,
                searchable: false,
                visible: false,
                render: function (data, type, row, meta) {
                    return getButtonGroup(true, true, true);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // SELECT ROW
    $('#perincianArusKasDatatable tbody').on('click', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            _perincianArusKasDatatable.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = _perincianArusKasDatatable.row(tr);
        }
    });

    // DETAIL ROW
    $('#perincianArusKasDatatable tbody').on('click', '.detail-button', function () {
        var tr = $(this).closest('tr');
        var row = _perincianArusKasDatatable.row(tr);
    });
}

// EXPORT PDF
function exportPdf() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({}),
        url: _baseUrl + `/akunting/laporan/laporan-aruskas/export-pdf?periode=${$('#txtTanggal').val()}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "RincianArusKas.pdf";
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
        complete: function (resp) {
            enableButton();
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

// EXPORT PDF
function exportExcel() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({}),
        url: _baseUrl + `/akunting/laporan/laporan-aruskas/export-excel?periode=${$('#txtTanggal').val()}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "RincianArusKas.xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        complete: function (resp) {
            enableButton();
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
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