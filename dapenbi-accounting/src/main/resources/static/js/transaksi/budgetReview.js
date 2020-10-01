var _actionEdit = false;
var _createdBy = null;
var _createdDate = null;
var _currentPengaturanSistem = null;
var _currentBudgetReview = null;
var _currentDaftarRekening = [];
var _budgetReviewDatatable = null;
var _currentIndexRekening = null;
var _periode = null;
var _periodeList = [];

jQuery(document).ready(function () {
    budgetReviewDatatable();
    findAllPeriode();
    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    }, "show").on("change", function () {
        let date = $('#txtCreatedDate').val();
        let dateArray = date.split('-');
        _periode = _periodeList.find(value => value.kodePeriode === dateArray[1]);

        $('#txtKodePeriode').val(_periode.kodePeriode);
        $('#txtNamaPeriode').val(_periode.namaPeriode);
        $('#txtTriwulan').val(_periode.triwulan);
    });
});

function findDetails() {
    // console.log(_currentBudgetReview);
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/transaksi/budget-review/details?noBudgetReview=${_currentBudgetReview.noBudgetReview}`,
        success: function (response) {
            _currentDaftarRekening = response.sort(urutkan);
            fatchFormBudgetReview();
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
        url: _baseUrl + `/akunting/parameter/pengaturan-sistem/findDTOByStatusAktif`,
        success: function (response) {
            console.log(response, "Pengaturan sistem");
            _currentPengaturanSistem = response;
            resetFormBudgetReview();
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
    // console.log(_currentBudgetReview)
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentBudgetReview),
        url: _baseUrl + `/akunting/transaksi/budget-review/find/daftar-rekening`,
        success: function (response) {
            // console.log(response.length);
            if (response.length === 0) {
                showWarning("Tidak ada data.")
            } else {
                _currentDaftarRekening = response.sort(urutkan);
                fatchTableRekening();
            }
        },
        complete: function (response) {
            if (response.status != 200)
                showSuccess("Data kosong.");
        }
    });
}

// GET DATA REKENING BUTTON
function dapatkanDataBtnPressed() {
    let triwulan = parseInt($('#txtTriwulan').val().slice(-1));
    // console.log(triwulan, 'triwulan')
    let lastMontTriwulan = 0;
    switch (triwulan) {
        case 1 :
            lastMontTriwulan = 3
            break;
        case 2 :
            lastMontTriwulan = 6
            break;
        case 3 :
            lastMontTriwulan = 9
            break;
        case 4 :
            lastMontTriwulan = 12
            break;
    }

    let date = new Date();
    let lastDay = new Date(date.getFullYear(), lastMontTriwulan, 0);
    let month = "0" + (lastDay.getMonth() + 1);
    console.log(_currentPengaturanSistem)
    let tglTransaksi = `${_currentPengaturanSistem.kodeTahunBuku.tahun}-${month.slice(-2)}-${lastDay.getDate()}`;

    _currentBudgetReview = {
        noBudgetReview: $("#txtNoBudgetReview").val(),
        kodeThnBuku: {
            kodeTahunBuku: $("#txtKodeThnBuku").val()
        },
        kodePeriode: {
            kodePeriode: _periode.kodePeriode,
            namaPeriode: _periode.namaPeriode,
            triwulan: lastMontTriwulan
        },
        createdBy: _createdBy,
        createdDate: $("#txtCreatedDate").val(),
        tglTransaksi: tglTransaksi
    };
    // console.log(_currentBudgetReview);
    findDataRekening();
}

// TAMBAH DATA FUNC
function tambahDataBtnPressed() {
    _actionEdit = false;
    $('.edit-section').attr("hidden", false);
    resetFormBudgetReview();
    // generateNumberBudgetReview();
    $('#budgetReviewDialog').modal('show');
}

// RESET FORM BUDGET REVIEW
function resetFormBudgetReview() {
    resetTableDaftarRekening();

    _periode = _periodeList.find(value => value.kodePeriode === _currentPengaturanSistem.kodePeriode.kodePeriode);

    $('#txtKodePeriode').val(_periode.kodePeriode);
    $('#txtNamaPeriode').val(_periode.namaPeriode);
    $('#txtTriwulan').val(_periode.triwulan);
    $('#txtKodeThnBuku').val(_currentPengaturanSistem.kodeTahunBuku.kodeTahunBuku);
    $("#txtCreatedDate").val(moment(new Date).format("YYYY-MM-DD"));
    $('#txtKeterangan').val('');
    $('#txtNoBudgetReview').val('');

}

// FATCH FORM BUDGET REVIEW
function fatchFormBudgetReview() {
    // console.log(_periodeList.find(value => value.kodePeriode === _currentBudgetReview.kodePeriode.kodePeriode))
    fatchTableRekening();
    // console.log(_currentBudgetReview);
    $('#txtNoBudgetReview').val(_currentBudgetReview.noBudgetReview);
    _createdDate = _currentBudgetReview.createdDate;
    $('#txtCreatedDate').val(moment(_createdDate).format("YYYY-MM-DD"));
    $('#txtKodePeriode').val(_currentBudgetReview.kodePeriode.kodePeriode);
    $('#txtNamaPeriode').val(_periodeList.find(value => value.kodePeriode === _currentBudgetReview.kodePeriode.kodePeriode).namaPeriode);
    $('#txtKodeThnBuku').val(_currentBudgetReview.kodeThnBuku.kodeTahunBuku);
    $('#txtTriwulan').val(_currentBudgetReview.triwulan);
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
            url: _baseUrl + '/akunting/transaksi/budget-review/datatables',
            type: "POST",
            data: function (d) {
                d.budgetReviewDTO = {
                    statusData: ''
                }
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'No.',
                data: 'noBudgetReview',
                className: 'text-center',
                width: '50px',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
                title: 'No. Budget Review',
                data: 'noBudgetReview',
                render: function (data, type, row, meta) {
                    // console.log(data, type, row, meta);
                    return data;
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
                title: 'Tahun Buku',
                data: 'kodeThnBuku',
                render: function (data, type, row, meta) {
                    return data.kodeTahunBuku;
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
                title: 'Periode',
                data: 'kodePeriode',
                render: function (data, type, row, meta) {
                    return data.kodePeriode;
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
                title: 'Triwulan',
                data: 'triwulan',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
                title: 'Tgl. Input',
                data: 'createdDate',
                render: function (data, type, row, meta) {
                    return moment(data).format('yyyy-MM-DD');
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
                title: 'User Input',
                data: 'createdBy',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                className: 'text-center',
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
                className: 'text-center',
                title: "Tindakan",
                width: "100px",
                data: 'statusData',
                defaultContent: getButtonGroup(false, false, false),
                render: function (data, type, row, meta) {
                    // console.log(data);
                    if (data === 'DRAFT' || data === 'REJECT')
                        return getButtonGroup(true, true, false);
                    else return getButtonGroup(false, false, true);
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
        var obj = {noBudgetReview: data.noBudgetReview}
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
                    url: _baseUrl + `/akunting/transaksi/budget-review/`,
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
        // console.log(data);
        _actionEdit = true;
        $('.edit-section').attr("hidden", false);
        _currentBudgetReview = data;
        // console.log(_currentBudgetReview)
        findDetails();
    });

    // BUTTON DETAILS
    $('#budgetReviewDatatable tbody').on('click', '.detail-button', function () {
        var data = _budgetReviewDatatable.row($(this).parents('tr')).data();
        $('.edit-section').attr("hidden", true);
        _currentBudgetReview = data;
        findDetails();
    });
}

function fatchTableRekening() {
    if (_currentDaftarRekening != null) {
        if (_currentDaftarRekening.length > 0) {
            var tbody = document.getElementById('tbody-detail');
            tbody.innerHTML = '';
            for (var i = 0; i < Object.keys(_currentDaftarRekening).length; i++) {
                let tr = "<tr>";
                tr += "<td class='text-center'>" + _currentDaftarRekening[i].idRekening.kodeRekening + "</td>" +
                    "<td hidden>" + _currentDaftarRekening[i].idRekening.idRekening + "</td>" +
                    "<td>" + _currentDaftarRekening[i].idRekening.namaRekening + "</td>" +
                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].anggaranTahunan) + "</td>" +
                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].realisasi) + "</td>" +
                    "<td class='text-center'>" + _currentDaftarRekening[i].persen + "</td>" +
                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].saldo) + "</td>" +
                    "<td>" + _currentDaftarRekening[i].keterangan + "</td>" +
                    "<td class='text-center'>" +
                    `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Edit' value="Edit" onclick='rekeningEditBtnPressed(this)'><i class='la la-edit'></i></a>` +
                    "</td>" +
                    "</tr>";
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

function simpanBtnPressed(statusData) {
    var budgetDetails = [];
    _currentDaftarRekening.map(rekening => {
        budgetDetails.push({
            idBudgetReviewDtl: _actionEdit ? rekening.idBudgetReviewDtl : null,
            idRekening: {idRekening: rekening.idRekening.idRekening},
            anggaranTahunan: rekening.anggaranTahunan,
            realisasi: rekening.realisasi,
            persen: rekening.persen,
            saldo: rekening.saldo,
            createdBy: _actionEdit ? rekening.createdBy : null,
            createdDate: _actionEdit ? rekening.createdDate : null,
            keterangan: rekening.keterangan
        })
    });

    _currentBudgetReview = {
        noBudgetReview: $("#txtNoBudgetReview").val(),
        kodeThnBuku: {kodeTahunBuku: $("#txtKodeThnBuku").val()},
        kodePeriode: {kodePeriode: $("#txtKodePeriode").val()},
        triwulan: $("#txtTriwulan").val(),
        statusData: statusData,
        keterangan: $("#txtKeterangan").val(),
        createdBy: _createdBy,
        createdDate: $("#txtCreatedDate").val(),
        budgetReviewDetails: budgetDetails.sort(urutkan)
    };

    // console.log(_currentBudgetReview);
    serviceSaveBudgetReview();
    $('#budgetReviewDialog').modal('toggle');
}

function simpanPerubahanBtnPressed() {
    _currentDaftarRekening[_currentIndexRekening].keterangan = $('#txtKeteranganEdit').val();
    fatchTableRekening();
    $('#budgetReviewEditDialog').modal('toggle');
}

function serviceSaveBudgetReview() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentBudgetReview),
        url: _baseUrl + `/akunting/transaksi/budget/and/details`,
        success: function (response) {
            resetFormBudgetReview();
            // resetFormRekening();
            showSuccess();
            _budgetReviewDatatable.ajax.reload();
        },

    });
}

function urutkan(a, b) {
    const urutanA = a.idRekening.kodeRekening;
    const urutanB = b.idRekening.kodeRekening;

    let comparison = 0;
    if (urutanA > urutanB) {
        comparison = 1;
    } else if (urutanA < urutanB) {
        comparison = -1;
    }
    return comparison;
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}