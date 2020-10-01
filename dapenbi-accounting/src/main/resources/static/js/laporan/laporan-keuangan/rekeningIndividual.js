var _rekeningDatatable;
var _currentRekening = null;
var _currentAllRekening = [];

jQuery(document).ready(function () {
    resetForm();
    findPengaturanSistem();
    rekeningDatatable();

    if(PDFObject.supportsPDFs){
        console.log("Yay, this browser supports inline PDFs.");
    } else {
        console.log("Boo, inline PDFs are not supported by this browser");
    }
});

// FIND PENGATURAN SISTEM
function findPengaturanSistem() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/parameter/pengaturan-sistem/findDTOByStatusAktif`,
        success: function (response) {
            $('#txtKodeTahunBuku').val(response.kodeTahunBuku.kodeTahunBuku);
            $('#txtNamaTahunBuku').val(response.kodeTahunBuku.namaTahunBuku);
            $('#txtKodePeriode').val(response.kodePeriode.kodePeriode);
            $('#txtNamaPeriode').val(response.kodePeriode.namaPeriode);
        },
        complete: function (response) {
            if (response.status !== 200)
                showWarning("Error code : " + response.status);
        }
    });
}

// REKENING DATATABLES
function rekeningDatatable() {
    _rekeningDatatable = $("#rekeningDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/parameter/rekening/getDatatables',
            type: "POST",
            data: function (d) {
                d.rekeningDTO = {
                    levelRekening: null,
                    isSummary: 'N'
                };
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        columns: [
            {
                data: 'kodeRekening',
                title: 'Kode Rekening',
                className: "text-center",
                defaultContent: "",
            },
            {
                data: 'namaRekening',
                title: 'Nama Rekening',
                defaultContent: "",
            },
            {
                data: 'levelRekening',
                title: 'Level',
                className: "text-center",
                defaultContent: "",
            },
            {
                data: 'saldoNormal',
                title: 'Saldo Normal',
                className: "text-center",
                defaultContent: "",
            },
            {
                defaultContent: "",
                class: "text-center",
                title: "Status",
                data: "statusAktif",
                width: "50px",
                orderable: false,
                render: function (data, type, full, meta) {
                    if (data !== null) {
                        if (data === '1') {
                            return '<i class="fa fa-check-square"></i>'
                        } else return `<i class="fa fa-square"></i>`;
                    } else return "-";
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    _rekeningDatatable.columns([2]).search(6, true, false).draw();

    $('#rekeningDatatable tbody').on('click', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            _rekeningDatatable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var tr = $(this).closest('tr');
            var row = _rekeningDatatable.row(tr);

            _currentRekening = row.data();
        }
    });
}

// PILIH REKENING
function pilihRekeningBtnPressed() {
    // console.log(_currentRekening);
    $('#txtKodeRekening').val(_currentRekening.kodeRekening);
    $('#txtNamaRekening').val(_currentRekening.namaRekening);
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function datePicker() {
    $("#periodeDatepicker1").off().datepicker({
        format: "yyyy-mm-dd",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
    });
    $("#periodeDatepicker2").off().datepicker({
        format: "yyyy-mm-dd",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
    });
}

function changeBtnActive(key) {
    $('#spinnerBtnPdf').attr('hidden', key);
    $('#spinnerBtnExcel').attr('hidden', key);
    $('#export-pdf').attr('disabled', !key);
    $('#export-excel').attr('disabled', !key);
}

// EXPORT PDF
function exportPdf() {
    changeBtnActive(false);
    var kodeRekening = _currentRekening === null ? '' : _currentRekening.kodeRekening;
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({}),
        url: _baseUrl + `/akunting/laporan/buku-besar/export-pdf?startDate=${$('#tglPeriode1').val()}&endDate=${$('#tglPeriode2').val()}&kodeRekening=${kodeRekening}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "LaporanRekeningIndividual.pdf";
            link.target = "_blank";
            // PDFObject.embed(link.href, "#example1");
            // $('#pdfModal').modal('show');

            var byteCharacters = atob(response);
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var file = new Blob([byteArray], {type: 'application/pdf;base64'});
            var fileURL = URL.createObjectURL(file);
            window.open(fileURL);

           // document.body.appendChild(link);
           // link.click();
           // document.body.removeChild(link);
        },
        complete: function (resp) {
            changeBtnActive(true);
            if (resp.status === 204) {
                showWarning(`Tidak ada data ditemukan pada tanggal ${$('#tglPeriode1').val()} s/d ${$('#tglPeriode2').val()}`)
            } else if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

function exportExcel() {
    changeBtnActive(false);
    var kodeRekening = _currentRekening === null ? '' : _currentRekening.kodeRekening;
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({}),
        url: _baseUrl + `/akunting/laporan/buku-besar/export-excel?startDate=${$('#tglPeriode1').val()}&endDate=${$('#tglPeriode2').val()}&kodeRekening=${kodeRekening}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "LaporanRekeningIndividual.xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        complete: function (resp) {
            changeBtnActive(true);
            if (resp.status === 204) {
                showWarning(`Tidak ada data ditemukan pada tanggal ${$('#tglPeriode1').val()} s/d ${$('#tglPeriode2').val()}`)
            } else if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

function resetForm() {
    $("#tglPeriode1").val(moment(new Date).format("YYYY-MM-DD"));
    $("#tglPeriode2").val(moment(new Date).format("YYYY-MM-DD"));
    $('#txtKodeRekening').val('');
    $('#txtNamaRekening').val('');
    _currentRekening = null;
}