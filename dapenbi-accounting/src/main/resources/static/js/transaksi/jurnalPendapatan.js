var _jurnals = [];
var _rekening = null;
var _currentIndexJurnal = null;
var _jurnalPendapatanDatatable;
var _isEditJurnal = false;
var _totalDebit = null;
var _totalKredit = null;
var _totalTransaksi = null;
var _createdBy = null;
var _createdDate = null;

jQuery(document).ready(function () {// DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });

    jurnalPendapatanDatatable();
    rekeningDatatable();
});

// WARKAT DATATABLES
function jurnalPendapatanDatatable() {
    _jurnalPendapatanDatatable = $("#jurnalPendapatanDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/transaksi/jurnal-pendapatan/datatables',
            type: "POST",
            data: function (d) {
                d.jurnalPendapatanDTO = {
                    statusData: ""
                };
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
                data: 'noWarkat',
                title: 'No.',
                width: '50px',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                data: 'noWarkat',
                title: 'No. Warkat',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'kodeTransaksi.kodeTransaksi',
                title: 'Transaksi',
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
                    if (data === null) return '-';
                    return data;
                }
            },
            {
                data: 'totalTransaksi',
                title: 'Total Transaksi',
                className: 'text-right',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'tglBuku',
                title: 'Tgl. Input',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    if (data !== null)
                        return moment(data).format('yyyy-MM-DD');
                }
            },
            {
                data: 'statusData',
                title: 'Status Data',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'statusData',
                title: "Tindakan",
                width: "100px",
                className: "text-center",
                sortable: false,
                orderable: false,
                defaultContent: getButtonGroup(false, false, false),
                render: function (data, type, row, meta) {
                    if (data === 'DRAFT' || data === 'REJECT')
                        return getButtonGroup(true, true, false);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON DELETE
    $('#jurnalPendapatanDatatable tbody').on('click', '.delete-button', function () {
        var data = _jurnalPendapatanDatatable.row($(this).parents('tr')).data();
        swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            type: "warning",
            showCancelButton: !0,
            confirmButtonText: "Yes, delete it!"
        }).then(function (e) {
            if (e.value) {
                $.ajax({
                    type: "DELETE",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    url: _baseUrl + `/api/akunting/transaksi/saldo/saldo-warkat/`,
                    success: function (resp) {
                    },
                    statusCode: {
                        200: function () {
                            showWarning();
                            _jurnalPendapatanDatatable.ajax.reload();
                        }
                    }
                });
            }
        });
    });

    // BUTTON EDIT
    $('#jurnalPendapatanDatatable tbody').on('click', '.edit-button', function () {
        var data = _jurnalPendapatanDatatable.row($(this).parents('tr')).data();
        // console.log(data);

        _totalDebit = 0;
        _totalKredit = 0;

        fatchFormJurnalPendapatan(data);
        findJurnals(data.noWarkat);
        $(".rowWarkat").attr("hidden", false);
    });
}

function tambahDataBtnPressed() {
    resetFormJurnalPendapatan();
    hideFormJurnalBtnPressed();
    resetTableJurnal();
    $('#tambahJurnalBtn').attr('disabled', true);
    $('#tampilkanDataBtn').attr('disabled', false);
    $(".rowWarkat").attr("hidden", true);
    $('#newJurnalPendapatanDialog').modal('show');
}

function findJurnalPendapatanKredits() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/jurnal-pendapatan/findKredits",
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                // PATCH WARKAT JURNAL
                _jurnals = data.sort(urutkanJurnal);
                // console.log(_jurnals);
                _jurnals.forEach(jurnal => {
                    if (jurnal.jumlahKredit != null && !isNaN(jurnal.jumlahKredit)) {
                        _totalKredit += parseFloat(jurnal.jumlahKredit);
                    }
                    if (jurnal.jumlahDebit != null && !isNaN(jurnal.jumlahDebit)) {
                        _totalDebit += parseFloat(jurnal.jumlahDebit);
                    }
                });

                fatchTableJurnal(_jurnals);

                $('#txtTotalDebit').val(numeralformat(_totalDebit));
                $('#txtTotalKredit').val(numeralformat(_totalKredit));

                $('#txtNoWarkat').attr('readonly', true);
            }
        },
        complete: function (response) {
            $('#tambahJurnalBtn').attr('disabled', false);
            $('#tampilkanDataBtn').attr('disabled', true);
            // console.log(response, "response debits")
        }
    });
}

function findJurnals(noWarkat) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/api/akunting/warkat-jurnal/findByNoWarkat?noWarkat=" + noWarkat,
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                // PATCH WARKAT JURNAL
                _jurnals = data.sort(urutkanJurnal);
                // console.log(_jurnals);
                _jurnals.forEach(jurnal => {
                    if (jurnal.jumlahKredit != null && !isNaN(jurnal.jumlahKredit)) {
                        _totalKredit += parseFloat(jurnal.jumlahKredit);
                    }
                    if (jurnal.jumlahDebit != null && !isNaN(jurnal.jumlahDebit)) {
                        _totalDebit += parseFloat(jurnal.jumlahDebit);
                    }
                });

                fatchTableJurnal(_jurnals);

                $('#txtTotalDebit').val(numeralformat(_totalDebit));
                $('#txtTotalKredit').val(numeralformat(_totalKredit));

                $('#txtNoWarkat').attr('readonly', true);
                $('#newJurnalPendapatanDialog').modal('show');
            }
        }
    });
}

function resetFormJurnalPendapatan() {
    _jurnals = [];
    _totalDebit = 0;
    _totalKredit = 0;
    var d = new Date();
    var month = "0" + (d.getMonth() + 1);
    $('#txtTglTransaksi').val(d.getFullYear() + '-' + month.slice(-2) + '-' + ("0"+d.getDate()).slice(-2));
    $('#txtTglBuku').val(d.getFullYear() + '-' + month.slice(-2) + '-' + ("0"+d.getDate()).slice(-2));
    $('#txtNoWarkat').attr('readonly', false);
    $("#txtJenisTransaksi").val('');
    $("#txtNoWarkat").val('');
    $("#txtTahunBuku").val('');
    $("#txtTotalTransaksi").val('');
    $("#txtKeterangan").val('');
    $("#txtTotalDebit").val('');
    $("#txtTotalKredit").val('');
}

function resetTableJurnal() {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"6\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

function displayTambahJurnalForm(data) {
    let jurnalData = _jurnals.sort(urutkanJurnal);
    let lastIndex = jurnalData.length - 1;
    lastIndex === -1 ? $('#txtUrutanTambahJurnal').val(1) : $('#txtUrutanTambahJurnal').val((parseInt(jurnalData[lastIndex].noUrut) + 1));
    document.getElementById("tambahJurnalForm").style.display = data === "show" ? "block" : "none";
    document.getElementById("hideTambahJurnalFormBtn").style.display = data === "show" ? "block" : "none";
    document.getElementById("tambahJurnalBtn").style.display = data === "show" ? "none" : "block";
}

function fatchFormJurnalPendapatan(data) {
    $("#txtJenisTransaksi").val(data.kodeTransaksi.kodeTransaksi);
    $("#txtNoWarkat").val(data.noWarkat);
    $("#txtTglTransaksi").val(moment(data.tglTransaksi).format('yyyy-MM-DD'));
    $("#txtTglBuku").val(moment(data.tglBuku).format('yyyy-MM-DD'));
    $("#txtTahunBuku").val(data.tahunBuku.kodeTahunBuku);
    _totalTransaksi = data.totalTransaksi;
    $("#txtTotalTransaksi").val(numeralformat(_totalTransaksi));
    $("#txtKeterangan").val(data.keterangan);
    _createdBy = data.createdBy;
    _createdDate = data.createdDate;
}

function fatchTableJurnal(data) {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    if (data.length != 0) {
        for (var i = 0; i < Object.keys(data).length; i++) {
            var tr = "<tr>";
            tr += "<td>" + data[i].noUrut.toString() + "</td>" +
                "<td hidden>" + data[i].idRekening.idRekening + "</td>" +
                "<td>" + data[i].idRekening.kodeRekening + "</td>" +
                "<td>" + data[i].idRekening.namaRekening + "</td>";
            tr += "<td class='pt-1 pb-1'>" + `<input type="number" id="debit-${i}" class="form-control col-debit textCurrency" placeholder='0' min="0" value='${data[i].jumlahDebit}' oninput='inputJumlahDebit(this)'/>` + "</td>" +
                "<td class='pt-1 pb-1'>" + `<input type="number" id="kredit-${i}" class="form-control col-kredit textCurrency" placeholder='0' min="0" value='${data[i].jumlahKredit}' oninput='inputJumlahKredit(this)'/>` + "</td>";
            tr += "<td class='text-center'>" +
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

function inputJumlahDebit(data) {
    _totalDebit = 0;

    var index = $(data).closest("tr").index();
    data.value === '' ? _jurnals[index].jumlahDebit = 0 : _jurnals[index].jumlahDebit = data.value;

    _jurnals.forEach(jurnal => {
        if (!isNaN(jurnal.jumlahDebit) && jurnal.jumlahDebit != null) {
            _totalDebit += parseFloat(jurnal.jumlahDebit);
        }
    });

    $('#txtTotalDebit').val(numeralformat(_totalDebit));
    _totalTransaksi = _totalDebit;
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

function inputJumlahKredit(data) {
    _totalKredit = 0;

    var index = $(data).closest("tr").index();
    data.value === '' ? _jurnals[index].jumlahKredit = 0 : _jurnals[index].jumlahKredit = data.value;

    _jurnals.forEach(jurnal => {
        if (!isNaN(jurnal.jumlahKredit) && jurnal.jumlahKredit != null) {
            _totalKredit += parseFloat(jurnal.jumlahKredit);
        }
    });

    $('#txtTotalKredit').val(numeralformat(_totalKredit));
    _totalTransaksi = _totalKredit;
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

function hideFormJurnalBtnPressed() {
    _rekening = null;
    displayTambahJurnalForm('hide');

    $('#txtUrutanTambahJurnal').val('');
    $('#txtIdRekeningTambahJurnal').val('');
    $('#txtKodeRekeningTambahJurnal').val('');
    $('#txtNamaRekeningTambahJurnal').val('');
}

// DELETE JURNAL
function deleteJurnalBtnPressed(btn) {
    var index = $(btn).closest("tr").index();
    _jurnals.splice(index, 1);

    var row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);

    _totalDebit -= $(btn).closest("tr").find('td:eq(4) input').val();
    _totalKredit -= $(btn).closest("tr").find('td:eq(5) input').val();
    _totalTransaksi = _totalDebit + _totalKredit;

    $('#txtTotalDebit').val(_totalDebit);
    $('#txtTotalKredit').val(_totalKredit);
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

// EDIT JURNAL
function editJurnalBtnPressed(btn) {
    _currentIndexJurnal = $(btn).closest("tr").index();
    $('#txtUrutanTambahJurnal').val(_jurnals[_currentIndexJurnal].noUrut);
    $('#txtIdRekeningTambahJurnal').val(_jurnals[_currentIndexJurnal].idRekening.idRekening);
    $('#txtKodeRekeningTambahJurnal').val(_jurnals[_currentIndexJurnal].idRekening.kodeRekening);
    $('#txtNamaRekeningTambahJurnal').val(_jurnals[_currentIndexJurnal].idRekening.namaRekening);

    _isEditJurnal = true;
    displayTambahJurnalForm('show');
}

function showRekeningDialog() {
    $('#rekeningDialog').modal('show');
}

// REKENING DATATABLES
function rekeningDatatable() {
    var source = {
        dataType: "json",
        url: _baseUrl + "/api/akunting/parameter/rekening/findAllRekening",
        type: "GET",
        dataFields: [{
            name: 'idRekening',
            type: 'number'
        }, {
            name: 'idParent',
            type: 'number'
        }, {
            name: 'kodeRekening',
            type: 'string'
        }, {
            name: 'namaRekening',
            type: 'string'
        }, {
            name: 'levelRekening',
            type: 'number'
        }, {
            name: 'expanded',
            type: 'bool'
        }, {
            name: 'saldoNormal',
            type: 'string'
        }, {
            name: 'isSummary',
            type: 'string'
        }, {
            name: 'tipeRekening',
            type: 'string'
        }, {
            name: 'statusAktif',
            type: 'string'
        }, {
            name: 'statusNeracaAnggaran',
            type: 'string'
        }, {
            name: 'createdBy',
            type: 'string'
        }, {
            name: 'createdDate',
            type: 'date'
        }],
        hierarchy:
            {
                keyDataField: {name: 'idRekening'},
                parentDataField: {name: 'idParent'}
            },
        id: 'idRekening'
        // localData: rekenings
    };
    var dataAdapter = new $.jqx.dataAdapter(source);

    // create Tree Grid
    $("#treeGridRekening").jqxTreeGrid({
        source: dataAdapter,
        theme: 'light',
        width: '100%',
        pageable: true,
        pageSize: 10,
        pagerMode: "advanced",
        pageSizeOptions: ['10', '25', '50'],
        pagerPosition: 'bottom',
        ready: function () {
            //$("#treeGridRekening").jqxTreeGrid('selectRow', 2);
        },
        filterable: true,
        columns: [{
            text: 'Kode Rekening',
            dataField: 'kodeRekening',
            width: '20%',
            align: 'center'
        }, {
            text: 'ID Rekening',
            dataField: 'idRekening',
            width: '10%',
            align: 'center',
            cellsAlign: 'center'
        }, {
            text: 'Nama Rekening',
            dataField: 'namaRekening',
            width: '40%',
            align: 'center'
        }, {
            text: 'Level',
            dataField: 'levelRekening',
            width: '10%',
            align: 'center',
            cellsAlign: 'center',
        }, {
            text: 'Saldo Normal',
            dataField: 'saldoNormal',
            width: '10%',
            align: 'center',
            cellsAlign: 'center',
            cellsRenderer: function (row, column, value, rowData) {
                return value;
            }
        }, {
            text: 'Status Aktif',
            dataField: 'statusAktif',
            width: '10%',
            align: 'center',
            cellsAlign: 'center',
            cellsRenderer: function (row, column, value, rowData) {
                if (value == 1) {
                    return "<i class=\"fa fa-check\"></i>"
                } else {
                    return "<i class=\"fa fa-times\"></i>"
                }
            }
        }]

    });

    // ROW CLICKED
    $('#treeGridRekening').on('rowClick', function (event) {
        var clicks = $(this).data('clicks');
        if (clicks) {
            // odd clicks
            var args = event.args;
            var row = args.row;
            $("#treeGridRekening").jqxTreeGrid('collapseRow', row.idRekening);
        } else {
            var args = event.args;
            var row = args.row;
            $("#treeGridRekening").jqxTreeGrid('expandRow', row.idRekening);
        }
        $(this).data("clicks", !clicks);
    });

    // EXPAND ALL BUTTON
    $('#expandAllRekeningBtn').click(function () {
        $("#treeGridRekening").jqxTreeGrid('expandAll');
    });

    // COLAPSE ALL BUTTON
    $('#collapseAllRekeningBtn').click(function () {
        $("#treeGridRekening").jqxTreeGrid('collapseAll');
    });

    // ROW SELECTED
    $('#treeGridRekening').on('rowSelect', function (event) {
        var args = event.args;
        _rekening = args.row;
    });

    $('#treeGridRekening').on('filter', function (event) {
        $('#expandAllRekeningBtn').click();
    });
}

function pilihRekeningBtnPressed() {
    $('#txtIdRekeningTambahJurnal').val(_rekening.idRekening);
    $('#txtKodeRekeningTambahJurnal').val(_rekening.kodeRekening);
    $('#txtNamaRekeningTambahJurnal').val(_rekening.namaRekening);
}

function tambahkanJurnalBtnPressed() {
    var noUrut = document.getElementById('txtUrutanTambahJurnal').value;
    var idRekening = document.getElementById('txtIdRekeningTambahJurnal').value;
    var kodeRekening = document.getElementById('txtKodeRekeningTambahJurnal').value;
    var namaRekening = document.getElementById('txtNamaRekeningTambahJurnal').value;

    if (noUrut === '' || idRekening === '' || kodeRekening === '' || namaRekening === '') {
        showWarning('Inputan tidak boleh kosong');
        return;
    }

    if (_isEditJurnal) {
        _jurnals[_currentIndexJurnal].noUrut = noUrut;
        _jurnals[_currentIndexJurnal].idRekening.idRekening = idRekening;
        _jurnals[_currentIndexJurnal].idRekening.kodeRekening = kodeRekening;
        _jurnals[_currentIndexJurnal].idRekening.namaRekening = namaRekening;
        if (_rekening !== null)
            _jurnals[_currentIndexJurnal].saldoNormal = _rekening.saldoNormal;
    } else {
        _jurnals.push({
            idRekening: {
                idRekening: idRekening,
                kodeRekening: kodeRekening,
                namaRekening: namaRekening,
            },
            saldoNormal: _rekening.saldoNormal,
            noUrut: noUrut
        });
    }

    _isEditJurnal = false;
    fatchTableJurnal(_jurnals.sort(urutkanJurnal));
    displayTambahJurnalForm('hide');
    hideFormJurnalBtnPressed();
}

// BUTTON SIMPAN WARKAT DAN WARKAT JURNAL
function saveJurnalPendapatan(status) {
    var request = {
        tglTransaksi: $("#txtTglTransaksi").val(),
        tglBuku: $("#txtTglBuku").val(),
        kodeTransaksi: {kodeTransaksi: 'JPENDAPATAN'},
        totalTransaksi: _totalTransaksi,
        keterangan: $("#txtKeterangan").val(),
        statusData: status,
        jenisWarkat: "JPENDAPATAN",
        warkatJurnals: _jurnals,
        createdBy: _createdBy,
        createdDate: _createdDate
    };

    if (request.tglTransaksi === '' || request.tglBuku === '' || request.kodeTransaksi.kodeTransaksi === '') {
        showWarning("Input form tidak boleh ada yang kosong.");
        return false;
    }

    if (_totalTransaksi !== _totalDebit || _totalTransaksi !== _totalKredit || _totalDebit !== _totalKredit) {
        showWarning("Total Transaksi tidak sama dengan Total Debit dan Total Kredit.");
        return false;
    }

    if (!$('#tambahJurnalForm').is(':visible')) {
        serviceSaveJurnalPendapatan(request);
    } else {
        swal.fire({
            title: "Kamu memiliki perubahan warkat jurnal",
            text: "Hapus dan lanjutkan proses simpan?",
            type: "warning",
            showCancelButton: !0,
            confirmButtonText: "Iya, hapus dan lanjutkan!"
        }).then(function (e) {
            if (e.value) {
                $('#btnHideFormJurnal').click();
                serviceSaveJurnalPendapatan(request);
            }
        });
    }

    $('#newJurnalPendapatanDialog').modal('toggle');
}

function serviceSaveJurnalPendapatan(data) {
    // console.log(data);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        url: _baseUrl + `/akunting/transaksi/jurnal-pendapatan/saldo-warkat/and/jurnals`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function () {
                showSuccess();
                _jurnalPendapatanDatatable.ajax.reload();
            }
        },
        complete: function (data) {
            // console.log(data);
            if (data.status !== 200) {
                showWarning(data.responseText);
            }
            resetTableJurnal();
            resetFormJurnalPendapatan();
            hideFormJurnalBtnPressed();
        }
    });
}

function urutkanJurnal(a, b) {
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

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}