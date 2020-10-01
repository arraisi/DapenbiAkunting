var _realisasiDatatable;
var _periodeMin1 = null;
var _periode = null;
var rekeningList = [];
var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];

jQuery(document).ready(function () {
    $('#txtPeriode').val(moment(new Date).format("YYYY-MM-DD"));
    formatPeriode(moment(new Date).format("YYYY-MM-DD"));
    realisasiDatatables();
    findByListTipeRekening();
});

// FIND All PERIODE
function findByListTipeRekening() {
    $('#txtTipeRekening').val() === 'ASET_OPR' ? $('#txtJudul').val('REALISASI ANGGARAN INVESTASI MATERIIL / AKTIVA OPERASIONAL') : $('#txtJudul').val('REALISASI ANGGARAN PENGELUARAN');
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/api/akunting/parameter/rekening/findByListTipeRekening?tipeRekening=${$('#txtTipeRekening').val()}`,
        success: function (response) {
            $('#txtRekening')
                .empty()
                .append('<option value="" selected>Semua Mata Anggaran</option>')
            ;
            var x = document.getElementById("txtRekening");
            response.map(value => {
                var option = document.createElement("option");
                option.value = value.idRekening;
                option.text = value.kodeRekening + ' - ' + value.namaRekening;
                x.add(option);
                $('.selectpicker').selectpicker('refresh');
            });
            resfreshDatatable();
        },
        complete: function (response) {
            if (response.status !== 200)
                showWarning("Error code : " + response.status);
        }
    });
}

function datePicker() {
    $("#periodeDatepicker").off().datepicker({
        format: "yyyy-mm-dd",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
        // findJurnalIndividuals();
        let periode = $('#txtPeriode').val();
        formatPeriode(periode);
        _realisasiDatatable.ajax.reload();
    });
}

function formatPeriode(value) {
    // console.log(value);
    let periode = value;
    let periodeArray = periode.split('-');
    let month = parseInt(periodeArray[1]);

    // console.log(periodeArray, "periodeArray");

    _periodeMin1 = periodeArray[0] + ('0' + (month - 1)).slice(-2);
    _periode = periodeArray[0] + ('0' + month).slice(-2) + periodeArray[2];
}

// DATATABLES
function realisasiDatatables() {
    _realisasiDatatable = $("#realisasiDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/realisasi/datatables',
            type: "POST",
            data: function (d) {
                d.realisasiDTO = {
                    periode: _periode, // YYYYMMDD
                    periodeMin1: _periodeMin1, // YYYYMM
                    jenisRealisasi: "PENGELUARAN",
                    idRekening: $('#txtRekening').val(),
                    tipeRekening: $('#txtTipeRekening').val()
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
        order: [[0, "asc"], [10, "desc"]],
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                data: 'kodeRekening',
                title: 'MA',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'namaRekening',
                title: 'Nama Mata Anggaran (MA)',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'anggaranTahunan',
                title: 'Anggaran Tahunan',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'serapTambah',
                title: 'Serap / Tap Tambah',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'serapKurang',
                title: 'Serap / Tap Kurang',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'nilaAT',
                title: 'AT Baru',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'realisasiBulanLalu',
                title: 'Realisasi s/d Bulan Lalu',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'realisasiBulanIni',
                title: 'Realisasi s/d Bulan Ini',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'totalRealisasi',
                title: 'Total Realisasi',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'persen',
                title: 'Persen (%)',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data.toFixed(2) + " %";
                }
            },
            {
                data: 'levelRekening',
                title: 'Level',
                className: 'text-center',
                defaultContent: "",
                visible: false,
                render: function (data, type, row, meta) {
                    return data;
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });
}

function resfreshDatatable() {
    _realisasiDatatable.ajax.reload();
}

function getDatatableValue(data) {
    var rows = [];
    for (i = 0; i < data.length; i++) {
        rows.push(data[i]);
    }
    return rows;
}

// EXPORT PDF
function exportPdf() {
    // console.log(_realisasiDatatable.rows().data());
    changeBtnActive(false);
    var periode = moment(new Date).format("YYYY/MM/DD");
    let idRekening = $('#txtRekening').val() === undefined ? '' : $('#txtRekening').val();
    var periodeString = convertStringDate(periode);
    var rawTanggal = periodeString.split("-");
    var tanggalIDN = rawTanggal[0] + " " + months[rawTanggal[1] - 1] + " " + rawTanggal[2];
    console.log(tanggalIDN);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(getDatatableValue(_realisasiDatatable.rows().data())),
        url: _baseUrl + `/akunting/laporan/realisasi-pengeluaran/export-pdf?tipeRekening=${$('#txtTipeRekening').val()}&periode=${$('#txtPeriode').val()}&judul=${$('#txtJudul').val()}&tanggal=${tanggalIDN}` +
            `&judul=${$('#txtJudul').val()}&bulan=${_periode}&bulanMin1=${_periodeMin1}&idRekening=${idRekening}&ttd=${pengaturanSistem.kdiv}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "RealisaiAnggaranPengeluaran.pdf";
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
            changeBtnActive(true);
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

// EXPORT EXCEL
function exportExcel() {
    changeBtnActive(false);
    // console.log(_realisasiDatatable.rows().data());
    var periode = moment(new Date).format("YYYY-MM-DD");
    let idRekening = $('#txtRekening').val() === undefined ? '' : $('#txtRekening').val();
    var periodeString = convertStringDate(periode);
    var rawTanggal = periodeString.split("-");
    var tanggalIDN = rawTanggal[0] + " " + months[rawTanggal[1] - 1] + " " + rawTanggal[2];

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(getDatatableValue(_realisasiDatatable.rows().data())),
        url: _baseUrl + `/akunting/laporan/realisasi-pengeluaran/export-excel?tipeRekening=${$('#txtTipeRekening').val()}&periode=${$('#txtPeriode').val()}&judul=${$('#txtJudul').val()}&tanggal=${tanggalIDN}` +
            `&judul=${$('#txtJudul').val()}&bulan=${_periode}&bulanMin1=${_periodeMin1}&idRekening=${idRekening}&ttd=${pengaturanSistem.kdiv}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "RealisaiAnggaranPengeluaran.xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        complete: function (resp) {
            changeBtnActive(true);
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

function convertStringDate(value) {
    if (value == "")
        return "";
    else
//        console.log(value);
        var raw = value.split("/");
    var res = raw[2] + "-" + raw[1] + "-" + raw[0];
    return res;
}