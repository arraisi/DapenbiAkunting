var _jenisTransaksi = null;
var _jurnals = [];
var _rekening = null;
var _saldoWarkatDataTable = null;
var _currentWarkat = null;
var _currentIndexJurnal = null;
var _isEditJurnal = false;
var _totalDebit = null;
var _totalKredit = null;
var _totalTransaksi = null;
var _createdBy = null;
var _createdDate = null;

jQuery(document).ready(function () {
    paceOptions = {
        elements: {
            selectors: ['.pace-loader']
        }
    };

    saldoWarkatDatatables();
    jenisTransaksiDatatable();
    rekeningDatatable();
    findJenisWarkat();
});

// DATEPICKER
$(".datepicker-group").datepicker({
    todayHighlight: true,
    autoclose: true,
    orientation: "bottom",
    format: "yyyy-mm-dd"
});

// WARKAT DATATABLES
function saldoWarkatDatatables() {
    _saldoWarkatDataTable = $("#saldoWarkatDataTable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/transaksi/saldo/saldo-warkat/datatables',
            type: "POST",
            data: function (d) {
                d.warkatDTO = {
                    statusData: "",
                    jenisWarkat: ""
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
                        return data.substr(0, 10);
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
                        return getButtonGroup(true, true, true);
                    else return getButtonGroup(false, false, true);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON DELETE
    $('#saldoWarkatDataTable tbody').on('click', '.delete-button', function () {
        var data = _saldoWarkatDataTable.row($(this).parents('tr')).data();
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
                            _saldoWarkatDataTable.ajax.reload();
                        }
                    }
                });
            }
        });
    });

    // BUTTON EDIT
    $('#saldoWarkatDataTable tbody').on('click', '.edit-button', function () {
        var data = _saldoWarkatDataTable.row($(this).parents('tr')).data();
        // console.log(data);
        resetDialogForm();
        $("#treeGridRekening").jqxTreeGrid('updateBoundData');

        _totalDebit = 0;
        _totalKredit = 0;

        fatchFormWarkat(data);
        findJurnals(data.noWarkat);

        $("#tambahJurnalBtn").attr("hidden", false);
        $("#simpanWarkatBtn").attr("hidden", false);
        $("#submitWarkatBtn").attr("hidden", false);
        $(".noWarkat").attr("hidden", false);
    });

    // BUTTON DETAIL
    $('#saldoWarkatDataTable tbody').on('click', '.detail-button', function () {
        var data = _saldoWarkatDataTable.row($(this).parents('tr')).data();
        console.log(data);
        resetDialogForm();
        _totalDebit = 0;
        _totalKredit = 0;

        fatchFormWarkat(data);
        findJurnals(data.noWarkat);

        $("#tambahJurnalBtn").attr("hidden", true);
        $("#simpanWarkatBtn").attr("hidden", true);
        $("#submitWarkatBtn").attr("hidden", true);
        $(".noWarkat").attr("hidden", false);
    });
}

function resetDialogForm() {
    resetFormSaldoWarkat();
    resetTableJurnal();
    $(".noWarkat").attr("hidden", true);
}

function tambahDataBtnPressed() {
    resetDialogForm();
    $("#treeGridRekening").jqxTreeGrid('updateBoundData');
    $("#tambahJurnalBtn").attr("hidden", false);
    $("#simpanWarkatBtn").attr("hidden", false);
    $("#submitWarkatBtn").attr("hidden", false);
    $('#subSequenceEventDialog').modal('show');
}

function tambahJurnalBtnPressed() {
    let lastNoUrut = $("#tableJurnal").find("tr").last().children("td:first").text();
    lastNoUrut === "Tidak ada data" ? $('#txtNoUrut').val(1) : $('#txtNoUrut').val((parseInt(lastNoUrut) + 1));
    $('#txtSaldoNormal').val('');
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
                $('#subSequenceEventDialog').modal('show');
            }
        }
    });
}

function resetFormSaldoWarkat() {
    _jurnals = [];
    _totalDebit = 0;
    _totalKredit = 0;
    var d = new Date();
    var month = "0" + (d.getMonth() + 1);
    $('#txtTglTransaksi').val(d.getFullYear() + '-' + month.slice(-2) + '-' + ("0" + d.getDate()).slice(-2))
    $('#txtTglBuku').val(d.getFullYear() + '-' + month.slice(-2) + '-' + ("0" + d.getDate()).slice(-2))
    $('#txtNoWarkat').attr('readonly', false);
    $("#txtJenisTransaksi").val('');
    $("#txtNoWarkat").val('');
    $("#txtTahunBuku").val('');
    $("#txtTotalTransaksi").val('');
    $("#txtKeterangan").val('');
    $("#txtTotalDebit").val('');
    $("#txtTotalKredit").val('');
    $("#txtJenisWarkat").val('');
}

function resetTableJurnal() {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"6\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

function showJenisTransaksiDialog() {
    $('#jenisTransaksiDialog').modal('show');
}

// TRANSAKSI DATATABLES
function jenisTransaksiDatatable() {
    jenisTransaksiDatatable = $("#jenisTransaksiDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/parameter/jenis-transaksi/datatables',
            type: "POST",
            data: function (d) {
                d.transaksiDTO = {statusAktif: 1};
                return JSON.stringify(d);
            }
        },
        // order: [ 1, "asc" ],
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                data: 'kodeTransaksi',
                title: 'No.',
                width: '50px',
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                data: 'kodeTransaksi',
                title: 'Kode Transaksi',
                defaultContent: ""
            },
            {
                data: 'namaTransaksi',
                title: 'Keterangan',
                defaultContent: ""
            },
            {
                data: "statusAktif",
                width: "50px",
                title: "Status",
                orderable: false,
                className: "text-center",
                defaultContent: "",
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

    jenisTransaksiDatatable.column(3).search(1).draw();

    // SELECTED TRANSAKSI
    $('#jenisTransaksiDatatable tbody').on('click', 'tr', function () {
        jenisTransaksiDatatable.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');

        var tr = $(this).closest('tr');
        var row = jenisTransaksiDatatable.row(tr);

        _jenisTransaksi = row.data();

        $('#txtJenisTransaksi').val(_jenisTransaksi.kodeTransaksi);

        // RENDER TABLE WARKAT JURNAL
        _jurnals = _jenisTransaksi.transaksiJurnals.sort(urutkanJurnal);
        fatchTableJurnal(_jurnals);
    });
}

function fatchFormWarkat(data) {
    $("#txtJenisTransaksi").val(data.kodeTransaksi.kodeTransaksi);
    $("#txtNoWarkat").val(data.noWarkat);
    $("#txtTglTransaksi").val(data.tglTransaksi.substr(0, 10));
    $("#txtTglBuku").val(data.tglBuku.substr(0, 10));
    $("#txtTahunBuku").val(data.tahunBuku.kodeTahunBuku);
    _totalTransaksi = data.totalTransaksi;
    $("#txtTotalTransaksi").val(numeralformat(_totalTransaksi));
    $("#txtJenisWarkat").val(data.jenisWarkat);
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
            if (data[i].saldoNormal === "D") {
                tr += "<td class='pt-1 pb-1'>" + `<input type="number" id="debit-${i}" class="form-control col-debit textCurrency" placeholder='0' min="0" value='${data[i].jumlahDebit}' oninput='inputJumlahDebit(this)'/>` + "</td>" +
                    "<td class='pt-1 pb-1'>" + `<input type="number" disabled id="kredit-${i}" class="form-control col-kredit textCurrency" value='0'/>` + "</td>";
            } else {
                tr += "<td class='pt-1 pb-1'>" + `<input type="number" disabled id="debit-${i}" class="form-control col-debit textCurrency" value='0'/>` + "</td>" +
                    "<td class='pt-1 pb-1'>" + `<input type="number" id="kredit-${i}" class="form-control col-kredit textCurrency" placeholder='0' min="0" value='${data[i].jumlahKredit}' oninput='inputJumlahKredit(this)'/>` + "</td>";
            }
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
    console.log(_jurnals[_currentIndexJurnal]);
    $('#txtNoUrut').val(_jurnals[_currentIndexJurnal].noUrut);
    $('#txtSaldoNormal').val(_jurnals[_currentIndexJurnal].saldoNormal);
    $('#txtKodeRekening').val(_jurnals[_currentIndexJurnal].idRekening.kodeRekening);
    $('#txtNamaRekening').val(_jurnals[_currentIndexJurnal].idRekening.namaRekening);
    _rekening = _jurnals[_currentIndexJurnal].idRekening;
    _isEditJurnal = true;
    $('#rekeningDialog').modal('show');
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
        $('#txtKodeRekening').val(_rekening.kodeRekening);
        $('#txtNamaRekening').val(_rekening.namaRekening);
    });

    $('#treeGridRekening').on('filter', function (event) {
        $('#expandAllRekeningBtn').click();
    });
}

function pilihRekeningBtnPressed() {
    var noUrut = document.getElementById('txtNoUrut').value;
    var saldoNormal = document.getElementById('txtSaldoNormal').value;

    console.log(noUrut);
    if (_rekening === null ||
        noUrut === '' ||
        _rekening.idRekening === '' ||
        _rekening.kodeRekening === '' ||
        _rekening.namaRekening === '' ||
        saldoNormal === '') {
        showWarning('Inputan tidak boleh kosong');
        return;
    }

    if (_isEditJurnal) {
        _jurnals[_currentIndexJurnal].noUrut = noUrut;
        _jurnals[_currentIndexJurnal].idRekening.idRekening = _rekening.idRekening;
        _jurnals[_currentIndexJurnal].idRekening.kodeRekening = _rekening.kodeRekening;
        _jurnals[_currentIndexJurnal].idRekening.namaRekening = _rekening.namaRekening;
        _jurnals[_currentIndexJurnal].saldoNormal = saldoNormal;
    } else {
        _jurnals.push({
            idRekening: _rekening,
            saldoNormal: saldoNormal,
            noUrut: noUrut
        });
    }

    _isEditJurnal = false;
    _rekening = null;
    fatchTableJurnal(_jurnals.sort(urutkanJurnal));
    $('#rekeningDialog').modal('hide');
}

// BUTTON SIMPAN WARKAT DAN WARKAT JURNAL
function saveWarkatAndJurnals(status) {
    // WARKAT
    _currentWarkat = {
        noWarkat: $("#txtNoWarkat").val(),
        kodeOrg: "-",
        tglTransaksi: $("#txtTglTransaksi").val(),
        tglBuku: $("#txtTglBuku").val(),
        kodeTransaksi: {kodeTransaksi: $("#txtJenisTransaksi").val()},
        totalTransaksi: _totalTransaksi,
        jenisWarkat: $("#txtJenisWarkat").val(),
        keterangan: $("#txtKeterangan").val(),
        statusData: status,
        warkatJurnals: _jurnals,
        createdBy: _createdBy,
        createdDate: _createdDate
    }

    if (_currentWarkat.tglTransaksi === '' || _currentWarkat.tglBuku === ''
        || _currentWarkat.kodeTransaksi.kodeTransaksi === '' || _currentWarkat.jenisWarkat === '') {
        showWarning("Input form tidak boleh ada yang kosong.");
        return false;
    }

    if (_totalTransaksi !== _totalDebit || _totalTransaksi !== _totalKredit || _totalDebit !== _totalKredit) {
        showWarning("Total Transaksi tidak sama dengan Total Debit dan Total Kredit.");
        return false;
    }

    serviceSaveWarkatAndJurnal(_currentWarkat);
    $('#subSequenceEventDialog').modal('toggle');
}

function serviceSaveWarkatAndJurnal() {
    // console.log(data);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentWarkat),
        url: _baseUrl + `/api/akunting/transaksi/saldo/saldo-warkat/and/jurnals`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function () {
                showSuccess();
                _saldoWarkatDataTable.ajax.reload();
            }
        },
        complete: function (data) {
            // console.log(data);
            if (data.status === 202) {
                swal.fire({
                    title: "Gagal",
                    text: data.responseText,
                    type: "warning",
                    confirmButtonText: "Tutup"
                });
            } else if (data.status !== 200) {
                showError();
            }
            resetTableJurnal();
            resetFormSaldoWarkat();
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