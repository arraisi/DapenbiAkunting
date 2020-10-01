var _createdBy = null;
var _currentBudgetReview = null;
var _currentDaftarRekening = [];
var _budgetReviewDatatable = null;
var _currentIndexRekening = null;
var _periode = null;
var _exportData = null;
var _periodeList = [];

jQuery(document).ready(function () {

    $("#idPeriode").val(pengaturanSistem.kodePeriode).selectpicker("refresh");
    $("#idTahunBuku").val(pengaturanSistem.kodeTahunBuku).selectpicker("refresh");

    paceOptions = {
        elements: {
            selectors: ['.pace-loader']
        }
    };

    budgetReviewDatatable();
});

function findDetails(condition) {
    console.log(_currentBudgetReview);
    _currentDaftarRekening = [];
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/anggaran/penyusunan-anggaran-new/details?noAnggaran=${_currentBudgetReview.noAnggaran}`,
        success: function (response) {
            console.log(response);
            _currentDaftarRekening = response;
            fatchFormBudgetReview(condition);
            $('#budgetReviewDialog').modal('show');
        },
        complete: function (response) {
            if (response.status != 200)
                showSuccess("Data kosong.");
        }
    });
}

// FIND PENGATURAN SISTEM
function findPengaturanSistem() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/parameter/pengaturan-sistem/findByStatusAktif`,
        success: function (response) {
            // console.log(response, "Pengaturan sistem");
            _periode = _periodeList.find(value => value.kodePeriode === response.kodePeriode);

            $('#txtKodePeriode').val(_periode.namaPeriode);
            $('#txtTriwulan').val(_periode.triwulan);
            $('#txtKodeThnBuku').val(response.kodeTahunBuku);
        },
        complete: function (response) {
            if (response.status !== 200)
                showWarning("Error code : " + response.status);
        }
    });
}

// FIND All PERIODE
function findAllPeriode() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/api/akunting/parameter/periode/list`,
        success: function (response) {
            _periodeList = response;
            // console.log(_periodeList);
        },
        complete: function (response) {
            findPengaturanSistem();
            if (response.status !== 200)
                showWarning("Error code : " + response.status);
        }
    });
}

// FIND DATA REKENING
function findDataRekening() {
    console.log(_currentBudgetReview)
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentBudgetReview),
        url: _baseUrl + `/akunting/anggaran/penyusunan-anggaran-new/find/daftar-rekening?kodeTahunBuku=${$("#idTahunBuku").val()}`,
        success: function (response) {
            console.log(response.length);
            if (response.length === 0) {
                showWarning("Tidak ada data.");
                Pace.stop();
            } else {
                _currentDaftarRekening = response;
                fatchTableRekening();
                Pace.stop();
            }
        },
        complete: function (response) {
            if (response.status != 200)
                showSuccess("Data kosong.");
        }
    });
}

// GENERATE NUMBER BUDGETREVIEW
function generateNumberBudgetReview() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/transaksi/budget-review/generateNumber`,
        success: function (response) {
            // console.log(response, "RESPONSE PERIODE");
        },
        statusCode: {
            200: function (data) {
                // console.log(data, "200");
                $('#txtNoAnggaran').val(data);
                $('#budgetReviewDialog').modal('show');
            }
        },
        complete: function (response) {
            if (response.status !== 200)
                showWarning("Error code : " + response.status);
        }
    });
}

// GET DATA REKENING BUTTON
function dapatkanDataBtnPressed() {
    Pace.start()
    let kodeTahunBuku = $("#idTahunBuku").val();
    let idPeriode = $("#idPeriode").val();

    findDataRekening();
}

// TAMBAH DATA FUNC
function tambahDataBtnPressed() {
    $('.edit-section').attr("hidden", false);
    resetFormBudgetReview();
    // generateNumberBudgetReview();
    disableFormAnggaranTahunan("on");
    $('#budgetReviewDialog').modal('show');
}

// RESET FORM BUDGET REVIEW
function resetFormBudgetReview(){
    resetTableDaftarRekening();

    $("#idPeriode").val(pengaturanSistem.kodePeriode).selectpicker("refresh");
    $("#idTahunBuku").val(pengaturanSistem.kodeTahunBuku).selectpicker("refresh");
    $('#txtKeterangan').val('');
    $('#txtNoAnggaran').val('');
}

// FATCH FORM BUDGET REVIEW
function fatchFormBudgetReview(condition) {
    fatchTableRekening(condition);
    $('#txtNoAnggaran').val(_currentBudgetReview.noAnggaran);
    $('#idPeriode').val(_currentBudgetReview.kodePeriode.kodePeriode).selectpicker("refresh");
    $('#idTahunBuku').val(_currentBudgetReview.kodeThnBuku.kodeTahunBuku).selectpicker("refresh");
    $('#txtKeterangan').val(_currentBudgetReview.keterangan);
}

// RESET TABLE REKENING
function resetTableDaftarRekening() {
    _currentDaftarRekening = [];
    var tbody = document.getElementById('tbody-detail');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">" +
        "<td colspan=\"8\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

// BUDGET REVIEW DATATABLE
function budgetReviewDatatable() {
    _budgetReviewDatatable = $("#budgetReviewDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/penyusunan-anggaran-new/datatable',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");

            if (data.statusAktif === '1') {
                $(row).addClass('rowApprove');
                console.log(row);
            }
        },
        columns: [
            {
                title: 'No.',
                data: 'noAnggaran',
                className: 'text-center',
                width: '50px',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                defaultContent: "",
                title: 'No. AT',
                data: 'noAnggaran',
                render: function (data, type, row, meta) {
                    // console.log(data, type, row, meta);
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Tahun Buku',
                data: 'kodeThnBuku',
                render: function (data, type, row, meta) {
                    return data.namaTahunBuku;
                }
            },
            {
                defaultContent: "",
                title: 'Periode',
                data: 'kodePeriode',
                render: function (data, type, row, meta) {
                    return data.namaPeriode;
                }
            },
            {
                defaultContent: "",
                title: 'Versi',
                data: 'versi',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Tgl. Input',
                data: 'createdDate',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return moment(data).format('DD-MM-YYYY');
                }
            },
            {
                defaultContent: "",
                title: 'User Input',
                data: 'createdBy',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Status Data',
                data: 'statusData',
                render: function (data, type, row, meta) {
                    // console.log(data);
                    return data;
                }
            },
            {
                searchable: false,
                sortable: false,
                orderable: false,
                class: "text-center",
                title: "Tindakan",
                width: "100px",
                data: 'statusData',
                defaultContent: getButtonGroup(false, false, false),
                render: function (data, type, row, meta) {
                    // console.log(data);
                    if (data === 'DRAFT' || data === 'REJECT')
                        return getButtonGroup(true, true, false);
                    else return getButtonGroup(false, false, true, true, "PDF", "la la-file-pdf-o");
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON DELETE
    $('#budgetReviewDatatable tbody').on('click', '.delete-button', function () {
        var data = _budgetReviewDatatable.row($(this).parents('tr')).data();
        var obj = {noAnggaran: data.noAnggaran}
        // console.log(data);
        swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            type: "warning",
            showCancelButton: !0,
            confirmButtonText: "Yes, delete it!"
        }).then(function (e) {
            if (e.value) {
                // console.log(data);
                $.ajax({
                    type: "DELETE",
                    contentType: "application/json",
                    data: JSON.stringify(obj),
                    url: _baseUrl + `/akunting/anggaran/penyusunan-anggaran-new`,
                    success: function (resp) {
                    },
                    statusCode: {
                        200: function () {
                            showWarning();
                            _budgetReviewDatatable.ajax.reload();
                        }
                    }
                });
            }
        });
    });

    // BUTTON EDIT
    $('#budgetReviewDatatable tbody').on('click', '.edit-button', function () {
        var data = _budgetReviewDatatable.row($(this).parents('tr')).data();
        console.log(data);
        $('.edit-section').attr("hidden", true);
        _currentBudgetReview = data;
        disableFormAnggaranTahunan("on");
        findDetails("edit");
    });

    // BUTTON DETAILS
    $('#budgetReviewDatatable tbody').on('click', '.detail-button', function () {
        var data = _budgetReviewDatatable.row($(this).parents('tr')).data();
        console.log(data);
        $('.edit-section').attr("hidden", true);
        _currentBudgetReview = data;
        disableFormAnggaranTahunan("off");
        findDetails("detail");
    });

    $("#budgetReviewDatatable tbody").on("click", ".additional1-button", function () {
        var data = _budgetReviewDatatable.row($(this).parents("tr")).data();
        console.log(data);
        _exportData = data;
        $('#exportDialog').modal('show');
    });

    // $("#budgetReviewDatatable tbody").on("click", ".additional2-button", function () {
    //     var data = _budgetReviewDatatable.row($(this).parents("tr")).data();
    //    console.log(data);
    //     exportEXCEL(data);
    // });
}

function fatchTableRekening(condition) {
    if (_currentDaftarRekening != null) {
        if (_currentDaftarRekening.length > 0) {
            var tbody = document.getElementById('tbody-detail');
            tbody.innerHTML = '';
            for (var i = 0; i < Object.keys(_currentDaftarRekening).length; i++) {
                let totalAnggaran = _currentDaftarRekening[i].totalAnggaran != null ? numeralformat(_currentDaftarRekening[i].totalAnggaran) : 0;
                let anggaranLalu = numeralformat(_currentDaftarRekening[i].anggaranLalu);
                let tr = "<tr>";
                if (condition == "detail"){
                    console.log("detail");
                    tr += "<td>" + _currentDaftarRekening[i].idRekening.kodeRekening + "</td>" +
                    "<td hidden>" + _currentDaftarRekening[i].idRekening.idRekening + "</td>" +
                    "<td>" + _currentDaftarRekening[i].idRekening.namaRekening + "</td>" +
                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].anggaranLalu) + "</td>" +
                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].totalAnggaran) + "</td>" +
                    "</tr>";
                } else {
                    console.log("edit");
                    tr += "<td>" + _currentDaftarRekening[i].idRekening.kodeRekening + "</td>" +
                    "<td hidden>" + _currentDaftarRekening[i].idRekening.idRekening + "</td>" +
                    "<td>" + _currentDaftarRekening[i].idRekening.namaRekening + "</td>" +
                    "<td class='pt-1 pb-1'>" + `<input type="text" id="anggaranLalu-${i}" class="form-control col-debit textCurrency" placeholder='0' value='${anggaranLalu}' oninput='inputAnggaranLalu(this)'/>` + "</td>" +
                    "<td class='pt-1 pb-1'>" + `<input type="text" id="totalAnggaran-${i}" class="form-control col-debit textCurrency" placeholder='0' value='${totalAnggaran}' oninput='inputTotalAnggaran(this)'/>` + "</td>" +
                    "</tr>";
                }
                tbody.innerHTML += tr;
            }
        }
    }
}

// EDIT JURNAL
function rekeningEditBtnPressed(btn) {
    _currentIndexRekening = $(btn).closest("tr").index();
    // console.log(_currentDaftarRekening[_currentIndexRekening]);
    $('#txtKeteranganEdit').val(_currentDaftarRekening[_currentIndexRekening].keterangan);
    $('#budgetReviewEditDialog').modal('show');
}

function displayFromRekening(data) {
    document.getElementById("formRekening").style.display = data === "show" ? "block" : "none";
    document.getElementById("batalEditRekeningBtn").style.display = data === "show" ? "block" : "none";
    document.getElementById("dapatkanDataBtn").style.display = data === "show" ? "none" : "block";
}

function simpanRekeningBtnPressed() {
    var rekening = {
        nomor: document.getElementById('txtNomor').value,
        idRekening: document.getElementById('txtIdRekening').value,
        namaRekening: document.getElementById('txtRekening').value,
        anggaran: document.getElementById('txtAnggaran').value,
        realisasi: document.getElementById('txtRealisasi').value,
        persen: document.getElementById('txtPersen').value,
        saldo: document.getElementById('txtSaldo').value,
        keterangan: document.getElementById('txtKeteranganDetail').value,
    }

    if (rekening.nomor === '' || rekening.idRekening === '' || rekening.namaRekening === '' || rekening.anggaran === '' || rekening.realisasi === '' || rekening.persen === '' || rekening.saldo === '') {
        showWarning('Inputan tidak boleh kosong');
        return;
    }

    _currentDaftarRekening[_currentIndexRekening] = rekening;
    fatchTableRekening(_currentDaftarRekening);
    displayFromRekening('hide');
    resetFormRekening();
}

function batalEditRekeningBtnPressed() {
    resetFormRekening();
    displayFromRekening('hide');
}

function resetFormRekening() {
    $('#txtNomor').val('');
    $('#txtIdRekening').val('');
    $('#txtRekening').val('');
    $('#txtAnggaran').val('');
    $('#txtRealisasi').val('');
    $('#txtPersen').val('');
    $('#txtSaldo').val('');
    $('#txtKeteranganDetail').val('');
}

function simpanBtnPressed(statusData) {
    var details = [];
    _currentDaftarRekening.map(rekening => {
        details.push({
            idRekening: {idRekening: rekening.idRekening.idRekening},
            anggaranLalu: rekening.anggaranLalu,
            totalAnggaran: rekening.totalAnggaran
        })
    });

    _currentBudgetReview = {
        noAnggaran: "",
        kodeThnBuku: {kodeTahunBuku: $("#idTahunBuku").val()},
        kodePeriode: {kodePeriode: $("#idPeriode").val()},
        versi: null,
        statusData: statusData,
        keterangan: $("#txtKeterangan").val(),
        createdBy: null,
        createdDate: null,
        penyusunanAnggaranAkuntingDetail: details
    };

    if ($('#formRekening').is(':visible')) {
        swal.fire({
            title: "Perubahaan Data",
            text: "Ingin simpan perubahaan?",
            type: "warning",
            confirmButtonText: "Iya, simpan!",
            showCancelButton: true,
            cancelButtonText: 'Batal!',
            reverseButtons: true
        }).then((result) => {
            if (result.value) {
                simpanRekeningBtnPressed();
                serviceSaveBudgetReview();
            } else if (
                /* Read more about handling dismissals below */
                result.dismiss === Swal.DismissReason.cancel
            ) {
                return;
            }
        });
    } else {
        serviceSaveBudgetReview();
    }
    $('#budgetReviewDialog').modal('toggle');
}

function simpanPerubahanBtnPressed() {
    _currentDaftarRekening[_currentIndexRekening].keterangan = $('#txtKeteranganEdit').val();
    fatchTableRekening();
    $('#budgetReviewEditDialog').modal('toggle');
}

function serviceSaveBudgetReview() {
    console.log(_currentBudgetReview);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentBudgetReview),
        url: _baseUrl + `/akunting/anggaran/penyusunan-anggaran-new/save-header-and-detail`,
        success: function (response) {
            resetFormBudgetReview();
            resetFormRekening();
            showSuccess();
            _budgetReviewDatatable.ajax.reload();
        },

    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function inputTotalAnggaran(data) {
    var index = $(data).closest("tr").index();
    data.value === '' ? _currentDaftarRekening[index].totalAnggaran = '0' : _currentDaftarRekening[index].totalAnggaran = data.value.replace(/,/g, '');
    $(`#totalAnggaran-${index}`).val(data.value.replace(/[^0-9\.]/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ","));
}

function inputAnggaranLalu(data) {
    var index = $(data).closest("tr").index();
    data.value === '' ? _currentDaftarRekening[index].anggaranLalu = '0' : _currentDaftarRekening[index].anggaranLalu = data.value.replace(/,/g, '');
    $(`#anggaranLalu-${index}`).val(data.value.replace(/[^0-9\.]/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ","));
}

function disableFormAnggaranTahunan(status) {
    $("#idTahunBuku").attr("disabled", status === 'off');
    $("#idPeriode").attr("disabled", status === 'off');
    $("#txtKeterangan").attr("readOnly", status === 'off');
    $("#dapatkanDataBtn").attr("hidden", status === 'off');
    $("#simpanBtn").attr("hidden", status === 'off');
    $("#submitBtn").attr("hidden", status === 'off');
    $("#btnBtlAnggaran").attr("hidden", status === 'on');
}

function okBtnPressed() {
    $('#budgetReviewDialog').modal('toggle');
}

function exportPDF() {

    $('#exportDialog').modal('toggle');
//    var obj = getDatatableValue(table.rows().data());
    var obj = {};

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/penyusunan-anggaran-new/export-pdf?noAnggaran=" + _exportData.noAnggaran + "&tipeRekening="
            + $("#tipeRekeing").val() + "&kodeTahunBuku=" + _exportData.kodeThnBuku.kodeTahunBuku,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            PDFObject.embed(link.href, "#example1");
            $('#pdfModal').modal('show');
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

function exportEXCEL(kodeSPIHDR) {
    var obj = {};

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/penyusunan-anggaran-new/export-excel?noAnggaran=" + tanggal + "&kodeSPIHDR=" + kodeSPIHDR,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "Laporan SPI " + tanggal + ".xlsx";
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