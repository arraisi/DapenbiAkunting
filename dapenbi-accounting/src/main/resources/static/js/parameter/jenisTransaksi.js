var _createdBy;
var _createdDate;
var _isEditJurnal = false;
var _currentIndexJurnal;
var _currentRekening;
var _currentTransaksi;
var _currentKodeTransaksi;
var _currentTransaksiJurnal = [];
var _transaksiJurnals = [];
var _rekeningDatatable;
var _jenisTransaksiDatatable;
var _transaksiJurnalDatatable;
var _transaksiJurnal = {
    "kodeTransaksi": {
        "kodeTransaksi": '^$'
    }
}

jQuery(document).ready(function () {
    paceOptions = {
        elements: {
            selectors: ['.pace-loader']
        }
    };

    jenisTransaksiDatatable();
    transaksiJurnalDatatable();
    rekeningDatatable();
});

function tambahDataBtnPressed() {
    resetFormTransaksi();
    resetFormJurnal();
    $('#transaskiDialog').modal('show');
}

// TRANSAKSI DATATABLES
function jenisTransaksiDatatable() {
    _jenisTransaksiDatatable = $("#jenisTransaksiDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/parameter/jenis-transaksi/datatables',
            type: "POST",
            data: function (d) {
                d.transaksiDTO = {
                    jenisWarkat: '',
                    statusAktif: ''
                };
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        columns: [
            {
                data: 'kodeTransaksi',
                title: 'No.',
                width: '50px',
                orderable: false,
                visible: false,
                className: 'text-center clickAble',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                data: 'kodeTransaksi',
                title: 'Kode Transaksi',
                className: 'clickAble',
                defaultContent: "",
            },
            {
                data: 'namaTransaksi',
                title: 'Keterangan',
                className: 'clickAble',
                defaultContent: '',
            },
            {
                data: "statusAktif",
                title: "Status",
                class: "text-center",
                width: "50px",
                orderable: false,
                defaultContent: "",
                render: function (data, type, full, meta) {
                    if (data !== null) {
                        if (data === '1') {
                            return '<i class="fa fa-check-square"></i>'
                        } else return `<i class="fa fa-square"></i>`;
                    } else return "-";
                }
            },
            {
                title: "Tindakan",
                class: "text-center",
                width: "100px",
                searchable: false,
                sortable: false,
                orderable: false,
                "defaultContent": getButtonGroup(true, true, false)
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // SELECT ROW
    $('#jenisTransaksiDatatable tbody').on('click', 'tr td.clickAble', function () {
        if (!$(this).hasClass('selected')) {
            _jenisTransaksiDatatable.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = _jenisTransaksiDatatable.row(tr);
            var kodeTransaksi = row.data().kodeTransaksi;
            console.log(row.data());
            // $('#txtKodeTransaksiJurnal').val(kodeTransaksi);

            _transaksiJurnal.kodeTransaksi.kodeTransaksi = kodeTransaksi;
            // _transaksiJurnalDatatable.column(1).search(kodeTransaksi).draw();
            _transaksiJurnalDatatable.ajax.reload();
            Pace.restart();
        }
    });

    // BUTTON EDIT
    $('#jenisTransaksiDatatable tbody').on('click', '.edit-button', function () {
        _transaksiJurnals = [];
        resetTableJurnal();
        var data = _jenisTransaksiDatatable.row($(this).parents('tr')).data();
        console.log(data);
        _currentTransaksi = data;
        _currentKodeTransaksi = data.kodeTransaksi;

        fatchFormTransaksi(data);
        getTransaksiJurnalByKodeTransaksi();
    });

    // BUTTON DELETE
    $('#jenisTransaksiDatatable tbody').on('click', '.delete-button', function () {
        var data = _jenisTransaksiDatatable.row($(this).parents('tr')).data();

        swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            type: "warning",
            showCancelButton: !0,
            confirmButtonText: "Yes, delete it!"
        }).then(function (e) {
            if (e.value) {
                // console.log(data);
                serviceDeleteTransaksi(data);
            }
        });
    });
}

// FATCH FORM TRANSAKSI
function fatchFormTransaksi(data) {
    _createdBy = data.createdBy;
    _createdDate = data.createdDate;
    if (data.kodeTransaksi !== null) $('#txtKodeTransaksi').val(data.kodeTransaksi);
    if (data.namaTransaksi !== null) $('#txtNamaTransaksi').val(data.namaTransaksi);
    data.statusAktif === "1" ? $("#statusAktifYa").prop("checked", true) : $("#statusAktifTidak").prop("checked", true);
}

// GET TRANSAKSI JURNAL
function getTransaksiJurnalByKodeTransaksi() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/api/akunting/parameter/transaksi-jurnal/findByKodeTransaksi/` + _currentKodeTransaksi,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE");
        },
        statusCode: {
            200: function (data) {
                // PATCH TRANSAKSI JURNAL
                _currentTransaksiJurnal = data.sort(urutkanJurnal);
                console.log(data, "data jurnal");
                console.log(_currentTransaksiJurnal, "current jurnal");
                fatchTableTransaksiJurnal(_currentTransaksiJurnal);
                $('#txtKodeTransaksi').attr('readonly', true);
            }
        },
        complete: function (data) {
            $('#transaskiDialog').modal('show');
        }
    });

}

// JURNAL DATATABLES
function transaksiJurnalDatatable() {
    _transaksiJurnalDatatable = $("#transaksiJurnalDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/parameter/transaksi-jurnal/datatables',
            type: "POST",
            data: function (d) {
                d.transaksiJurnal = _transaksiJurnal;
                return JSON.stringify(d);
            }
        },
        order: [0, "asc"],
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                data: 'noUrut',
                title: 'No.',
                width: '50px',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    console.log(data);
                    return data;
                }
            },
            {
                data: 'kodeTransaksi',
                title: 'Kode Transaksi',
                defaultContent: "",
                visible: false,
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'idRekening',
                title: 'Rekening',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data.kodeRekening;
                }
            },
            {
                data: 'idRekening.namaRekening',
                title: 'Keterangan',
                defaultContent: ""
            },
            {
                data: 'saldoNormal',
                title: 'Saldo Normal',
                className: 'text-center',
                defaultContent: "",
            },
            {
                title: "Tindakan",
                width: "100px",
                class: "text-center",
                visible: false,
                sortable: false,
                orderable: false,
                searchable: false,
                "defaultContent": getButtonGroup(false, false, false)
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // var regex = "^(0)$";
    // _transaksiJurnalDatatable.columns([1]).search(regex, true, false).draw();
}

// REKENING DATATABLES
function rekeningDatatable() {
    _rekeningDatatable = $("#rekeningDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/parameter/rekening/datatables',
            type: "POST",
            data: function (d) {
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
            $('#txtSaldoNormal').val(_currentRekening.saldoNormal);

            var jurnal = _currentTransaksiJurnal.find(({idRekening}) => idRekening.kodeRekening === _currentRekening.kodeRekening);
            // console.log(jurnal);
            if (jurnal !== undefined) {
                showWarning("Rekening ini sudah dipilih.");
                _currentRekening = null;
            }
        }
    });
}

// PILIH REKENING
function simpanRekeningBtnPressed() {
    var noUrut = document.getElementById("txtNoUrut").value;
    var saldoNormal = document.getElementById("txtSaldoNormal").value;

    if (noUrut === '') {
        showWarning('Inputan tidak boleh kosong');
        return;
    }

    if (_isEditJurnal) {
        _currentTransaksiJurnal.splice(_currentIndexJurnal, 1);
    }

    _currentTransaksiJurnal.push({
        noUrut: noUrut,
        idRekening: _currentRekening,
        saldoNormal: saldoNormal
    });

    fatchTableTransaksiJurnal(_currentTransaksiJurnal.sort(urutkanJurnal));
    resetFormJurnal();
}

// TAMBAH JURNAL
function tambahJurnalBtnPressed() {
    let lastNoUrut = $("#tableTransaksiJurnal").find("tr").last().children("td:first").text();
    lastNoUrut === "Tidak ada data" ? $('#txtNoUrut').val(1) : $('#txtNoUrut').val((parseInt(lastNoUrut) + 1));
    $('#rekeningDialog').modal('show');
}

// FATCH TABLE JURNAL
function fatchTableTransaksiJurnal(data) {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    if (data.length != 0) {
        for (var i = 0; i < Object.keys(data).length; i++) {
            var tr = "<tr>";
            tr += "<td class=\"text-center\">" + data[i].noUrut.toString() + "</td>" +
                "<td hidden>" + data[i].idRekening.idRekening + "</td>" +
                "<td class=\"text-center\">" + data[i].idRekening.kodeRekening + "</td>" +
                "<td>" + data[i].idRekening.namaRekening + "</td>" +
                "<td class=\"text-center\">" + data[i].saldoNormal + "</td>" +
                "<td class=\"text-center\">" +
                `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Delete' value="Delete" onclick="deleteJurnalBtnPressed(this)"><i class='la la-trash'></i></a>` +
                `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Edit' value="Edit" onclick='editJurnalBtnPressed(this)'><i class='la la-edit'></i></a>` +
                "</td>" +
                "</td></tr>";
            tbody.innerHTML += tr;
        }
    } else {
        resetTableJurnal();
    }
}

// DELETE JURNAL
function deleteJurnalBtnPressed(btn) {
    var index = $(btn).closest("tr").index();
    _currentTransaksiJurnal.splice(index, 1);

    if (_currentTransaksiJurnal.length === 0) {
        resetTableJurnal();
    } else {
        fatchTableTransaksiJurnal(_currentTransaksiJurnal.sort(urutkanJurnal));
    }
}

// EDIT JURNAL
function editJurnalBtnPressed(btn) {
    _currentIndexJurnal = $(btn).closest("tr").index();
    $('#txtNoUrut').val(_currentTransaksiJurnal[_currentIndexJurnal].noUrut);
    $('#txtSaldoNormal').val(_currentTransaksiJurnal[_currentIndexJurnal].saldoNormal);
    _currentRekening = _currentTransaksiJurnal[_currentIndexJurnal].idRekening;
    _isEditJurnal = true;
    tambahJurnalBtnPressed();
}

// RESET TABLE JURNAL
function resetTableJurnal() {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"5\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

// RESET FORM JURNAL
function resetFormJurnal() {
    _isEditJurnal = false;
    $('#txtNoUrut').val('');
    $('#txtSaldoNormal').val('');
    _currentRekening = null;
    _rekeningDatatable.$('tr.selected').removeClass('selected');
}

function resetFormTransaksi() {
    _currentTransaksiJurnal = [];
    resetTableJurnal();
    $('#txtKodeTransaksi').attr('readonly', false);
    $('#txtKodeTransaksi').val('');
    $('#txtJenisWarkat').val('');
    $('#txtNamaTransaksi').val('');
    $("#statusAktifYa").prop("checked", false);
    $("#statusAktifTidak").prop("checked", false);
}

function simpanTransaksiBtnPressed() {
    _currentTransaksi.kodeTransaksi= document.getElementById("txtKodeTransaksi").value;
    _currentTransaksi.namaTransaksi= document.getElementById("txtNamaTransaksi").value;
    _currentTransaksi.statusAktif= $('input[name=txtStatusAktif]:checked', '#formTransaksi').val();
    _currentTransaksi.createdBy= _createdBy;
    _currentTransaksi.createdDate= _createdDate;
    _currentTransaksi.transaksiJurnals= _currentTransaksiJurnal;
    console.log(_currentTransaksi, "obj transaksi for save");

    if (_currentTransaksi.namaTransaksi === ''){
        showWarning("Inputan tidak boleh kosong.");
        return false;
    }

    serviceSaveTransaksi(_currentTransaksi);
    $('#transaskiDialog').modal('toggle');
}

function serviceSaveTransaksi(data) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        url: _baseUrl + `/api/akunting/parameter/jenis-transaksi/`,
        success: function (response) {
            console.log(response, "RESPONSE SAVE");
        },
        statusCode: {
            201: function () {
                showSuccess();
                _rekeningDatatable.ajax.reload();
                _jenisTransaksiDatatable.ajax.reload();
                _transaksiJurnalDatatable.ajax.reload();
            },
            400: function () {
                showWarning("Inputan tidak boleh kosong", 5000);
            },
            409: function () {
                showWarning("Kode Transaksi sudah terdaftar.", 5000);
            }
        },
        complete: function () {
            resetFormTransaksi();
        }
    });
}

function serviceDeleteTransaksi(data) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        url: _baseUrl + "/api/akunting/parameter/jenis-transaksi/delete",
        success: function (resp) {
        },
        statusCode: {
            200: function () {
                showWarning("Data berhasil dihapus.");
                _jenisTransaksiDatatable.ajax.reload();
            }
        }
    });
}

// MENGURUTKAN JURNAL LIST/TABLE
function urutkanJurnal(a, b) {
    // Use toUpperCase() to ignore character casing
    const urutanA = a.noUrut.toUpperCase();
    const urutanB = b.noUrut.toUpperCase();

    let comparison = 0;
    if (urutanA > urutanB) {
        comparison = 1;
    } else if (urutanA < urutanB) {
        comparison = -1;
    }
    return comparison;
}