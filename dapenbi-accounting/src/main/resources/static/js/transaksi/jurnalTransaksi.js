var _jurnals = [];
var _jenisTransaksi = null;
var _rekening = null;
var _jurnalTransaksiDatatable = null;
var _currentIndexJurnal = null;
var _isEditJurnal = false;
var _totalDebit = null;
var _totalKredit = null;
var _totalTransaksi = null;
var _createdBy = null;
var _createdDate = null;
var _tglTransaksi = null;
var _selisihAktuariaIndex = null;
var _selisihAktuariaSN = null;

jQuery(document).ready(function () {
    jurnalTransaksiDatatble();
    jenisTransaksiDatatable();
    rekeningDatatable();

    // KODE TAHUN BUKU AKTIF
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/findByCreatedDate",
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                // $( "#tglTransaksiPicker" ).datepicker( "setDate", moment(data.tglTransaksi).format('yyyy-MM-DD') );
                _tglTransaksi = moment(data.tglTransaksi).format('yyyy-MM-DD');
            }
        }
    });

    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });
});

// JURNAL TRANSAKSI DATATABLES
function jurnalTransaksiDatatble() {
    _jurnalTransaksiDatatable = $("#jurnalTransaksiDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/transaksi/saldo/saldo-warkat/datatables',
            type: "POST",
            data: function (d) {
                d.warkatDTO = {
                    statusData: "",
                    jenisWarkat: "JURNAL_TRANSAKSI",
                    startDate: '',
                    endDate: '',
                    kodeTransaksi: {kodeTransaksi: ''}
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
    $('#jurnalTransaksiDatatable tbody').on('click', '.delete-button', function () {
        var data = _jurnalTransaksiDatatable.row($(this).parents('tr')).data();
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
                            _jurnalTransaksiDatatable.ajax.reload();
                        }
                    }
                });
            }
        });
    });

    // BUTTON EDIT
    $('#jurnalTransaksiDatatable tbody').on('click', '.edit-button', function () {
        var data = _jurnalTransaksiDatatable.row($(this).parents('tr')).data();
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
        $(".rowWarkat").attr("hidden", false);
    });

    // BUTTON DETAIL
    $('#jurnalTransaksiDatatable tbody').on('click', '.detail-button', function () {
        var data = _jurnalTransaksiDatatable.row($(this).parents('tr')).data();
        // console.log(data);
        resetDialogForm();
        _totalDebit = 0;
        _totalKredit = 0;

        fatchFormWarkat(data);
        findJurnals(data.noWarkat);

        $("#tambahJurnalBtn").attr("hidden", true);
        $("#simpanWarkatBtn").attr("hidden", true);
        $("#submitWarkatBtn").attr("hidden", true);
        $(".rowWarkat").attr("hidden", false);
    });
}

function getDataJurnal() {
    if (
        _jenisTransaksi.kodeTransaksi.substring(1) !== 'BIAYA' &&
        _jenisTransaksi.kodeTransaksi.substring(1) !== 'PENDAPATAN'
    ) {
        return;
    }

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/transaksi-jurnal/findDataJurnal?tipeRekening=" + _jenisTransaksi.kodeTransaksi.substring(1),
        success: function (response) {
            if (response !== undefined || typeof response !== "undefined"){
                const concatJurnals = _jurnals.concat(response);
                _jurnals = concatJurnals;
                console.log(_jurnals, '_jurnals sesudah');
                fatchTableJurnal();
            }
        },
        statusCode: {
            200: function (data) {
                // PATCH WARKAT JURNAL

            }
        }
    });
}

function resetDialogForm() {
    resetFormSaldoWarkat();
    resetTableJurnal();
    $(".rowWarkat").attr("hidden", true);
}

function tambahDataBtnPressed() {
    resetDialogForm();
    $("#treeGridRekening").jqxTreeGrid('updateBoundData');
    $("#tambahJurnalBtn").attr("hidden", false);
    $("#simpanWarkatBtn").attr("hidden", false);
    $("#submitWarkatBtn").attr("hidden", false);
    $('#newWarkatDialog').modal('show');
}

function tambahJurnalBtnPressed() {
    let jurnalData = _jurnals.sort(urutkanJurnal);
    let lastIndex = jurnalData.length - 1;
    lastIndex === -1 ? $('#txtUrutanTambahJurnal').val(1) : $('#txtUrutanTambahJurnal').val((parseInt(jurnalData[lastIndex].noUrut) + 1));
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

                fatchTableJurnal();

                $('#txtNoWarkat').attr('readonly', true);
                $('#newWarkatDialog').modal('show');
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

    $('#txtTglTransaksi').val(_tglTransaksi);
    // $('#txtTglTransaksi').val(d.getFullYear() + '-' + month.slice(-2) + '-' + ("0" + d.getDate()).slice(-2))
    $('#txtTglBuku').val(d.getFullYear() + '-' + month.slice(-2) + '-' + ("0" + d.getDate()).slice(-2))
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
                d.transaksiDTO = {
                    jenisWarkat: 'JURNAL_TRANSAKSI',
                    statusAktif: 1
                };
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
        // console.log(_jenisTransaksi);
        // if (_jenisTransaksi.kodeTransaksi === 'JPENDAPATAN') {
        //     findJurnalPendapatanKredits();
        // }

        $('#txtJenisTransaksi').val(_jenisTransaksi.kodeTransaksi);

        // RENDER TABLE WARKAT JURNAL
        _jurnals = _jenisTransaksi.transaksiJurnals.sort(urutkanJurnal);
        // console.log(_jurnals);

        $('#getDataJurnalBtn').attr('disabled', false);
        fatchTableJurnal();
    });
}

function fatchFormWarkat(data) {
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

function fatchTableJurnal() {
    // console.log(data);
    let data = _jurnals.sort(urutkanJurnal);
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    if (data.length !== 0) {
        _selisihAktuariaIndex = null;
        for (var i = 0; i < Object.keys(data).length; i++) {
            if (data[i].idRekening.kodeRekening === "6010101011") {
                _selisihAktuariaIndex = i;
            }
            let jumlahDebit = data[i].jumlahDebit === undefined ? '0' : data[i].jumlahDebit;
            let jumlahKredit = data[i].jumlahKredit === undefined ? '0' : data[i].jumlahKredit;
            let urutan = data[i].noUrut === null ? i + 1 : data[i].noUrut.toString();
            var tr = "<tr>";
            tr += "<td>" + urutan + "</td>" +
                "<td hidden>" + data[i].idRekening.idRekening + "</td>" +
                "<td>" + data[i].idRekening.kodeRekening + "</td>" +
                "<td>" + data[i].idRekening.namaRekening + "</td>";

            tr += "<td class='pt-1 pb-1'>" + `<input type="text" id="debit-${i}" class="form-control col-debit textCurrency" placeholder='0' value='${numeralformat(jumlahDebit)}' oninput='inputJumlahDebit(this)'/>` + "</td>" +
                "<td class='pt-1 pb-1'>" + `<input type="text" id="kredit-${i}" class="form-control col-kredit textCurrency" placeholder='0' value='${numeralformat(jumlahKredit)}' oninput='inputJumlahKredit(this)'/>` + "</td>";

            tr += "<td class='text-center'>" +
                `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Delete' value="Delete" onclick="deleteJurnalBtnPressed(this)"><i class='la la-trash'></i></a>` +
                `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Edit' value="Edit" onclick='editJurnalBtnPressed(this)'><i class='la la-edit'></i></a>` +
                "</td>" +
                "</td></tr>";
            tbody.innerHTML += tr;
        }
        tbody.innerHTML += "\n" +
            "<tr style=\"height: 31px\">\n" +
            "   <td colspan=\"3\" style=\"padding:0.65rem 1rem\"><strong>Total</strong>\n" +
            "   </td>\n" +
            "   <td class=\"text-right textCurrency\" id=\"txtTotalDebit\" style=\"padding:0.65rem 1rem\"><strong>0</strong>" +
            "   </td>\n" +
            "   <td class=\"text-right textCurrency\" id=\"txtTotalKredit\" style=\"padding:0.65rem 1rem\"><strong>0</strong>" +
            "   </td>\n" +
            "   <td class=\"text-right\">\n" +
            "   </td>\n" +
            "</tr>";

        // console.log(_jurnals, "BEFORE");
        countSummary();
        if (_selisihAktuariaIndex !== null) {
            if (data[_selisihAktuariaIndex].saldoNormal === 'D') {
                _jurnals[_selisihAktuariaIndex].jumlahDebit = _totalKredit;
                $(`#debit-${_selisihAktuariaIndex}`).val(numeralformat(_totalKredit));
            } else{
                _jurnals[_selisihAktuariaIndex].jumlahKredit = _totalDebit;
                $(`#kredit-${_selisihAktuariaIndex}`).val(numeralformat(_totalDebit));
            }
            // console.log(_jurnals, "AFTER");
            countSummary();
        }
    } else {
        resetTableJurnal();
    }
}

function countSummary() {
    _totalKredit = 0;
    _totalDebit = 0;

    _jurnals.forEach(jurnal => {
        if (!isNaN(jurnal.jumlahDebit) || jurnal.jumlahDebit != null) {
            _totalDebit += parseFloat(jurnal.jumlahDebit);
        }

        if (!isNaN(jurnal.jumlahKredit) || jurnal.jumlahKredit != null) {
            _totalKredit += parseFloat(jurnal.jumlahKredit);
        }
    });

    $('#txtTotalDebit').text(numeralformat(_totalDebit));
    $('#txtTotalKredit').text(numeralformat(_totalKredit));
    _totalTransaksi = _totalKredit;
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

function countDebit() {
    _totalDebit = 0;
    _jurnals.forEach(jurnal => {
        if (!isNaN(jurnal.jumlahDebit) && jurnal.jumlahDebit != null) {
            _totalDebit += parseFloat(jurnal.jumlahDebit);
        }
    });
    $('#txtTotalDebit').text(numeralformat(_totalDebit));
    _totalTransaksi = _totalDebit;
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

function countKredit() {
    _totalKredit = 0;

    _jurnals.forEach(jurnal => {
        if (!isNaN(jurnal.jumlahKredit) && jurnal.jumlahKredit != null) {
            _totalKredit += parseFloat(jurnal.jumlahKredit);
        }
    });

    $('#txtTotalKredit').text(numeralformat(_totalKredit));
    _totalTransaksi = _totalKredit;
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

function inputJumlahDebit(data) {
    var index = $(data).closest("tr").index();
    data.value === '' ? _jurnals[index].jumlahDebit = '0' : _jurnals[index].jumlahDebit = data.value.replace(/,/g, '');
    $(`#debit-${index}`).val(data.value.replace(/[^0-9\.]/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ","));
    countDebit();
}

function inputJumlahKredit(data) {
    var index = $(data).closest("tr").index();
    data.value === '' ? _jurnals[index].jumlahKredit = '0' : _jurnals[index].jumlahKredit = data.value.replace(/,/g, '');
    $(`#kredit-${index}`).val(data.value.replace(/[^0-9\.]/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ","));
    countKredit();
}

// DELETE JURNAL
function deleteJurnalBtnPressed(btn) {
    var index = $(btn).closest("tr").index();
    _jurnals.splice(index, 1);

    var row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);

    fatchTableJurnal();
    // _totalDebit -= $(btn).closest("tr").find('td:eq(4) input').val();
    // _totalKredit -= $(btn).closest("tr").find('td:eq(5) input').val();
    // _totalTransaksi = _totalDebit + _totalKredit;
    //
    // $('#txtTotalDebit').val(_totalDebit);
    // $('#txtTotalKredit').val(_totalKredit);
    // $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

// EDIT JURNAL
function editJurnalBtnPressed(btn) {
    _currentIndexJurnal = $(btn).closest("tr").index();
    // console.log(_jurnals[_currentIndexJurnal]);
    $('#txtUrutanTambahJurnal').val(_jurnals[_currentIndexJurnal].noUrut);
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
    var noUrut = document.getElementById('txtUrutanTambahJurnal').value;
    var saldoNormal = document.getElementById('txtSaldoNormal').value;

    // console.log(noUrut);
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
    fatchTableJurnal();
    $('#rekeningDialog').modal('hide');
}

// BUTTON SIMPAN WARKAT DAN WARKAT JURNAL
function saveWarkatAndJurnals(status) {
    var request = {
        noWarkat: $("#txtNoWarkat").val(),
        kodeOrg: "-",
        tglTransaksi: $("#txtTglTransaksi").val(),
        tglBuku: $("#txtTglBuku").val(),
        kodeTransaksi: {kodeTransaksi: $("#txtJenisTransaksi").val()},
        totalTransaksi: _totalTransaksi,
        keterangan: $("#txtKeterangan").val(),
        statusData: status,
        jenisWarkat: 'JURNAL_TRANSAKSI',
        warkatJurnals: _jurnals,
        createdBy: _createdBy,
        createdDate: _createdDate
    };

    // console.log(request);

    if (request.jenisWarkat === null || request.tglTransaksi === '' || request.tglBuku === '' || request.kodeTransaksi.kodeTransaksi === '') {
        showWarning("Input form tidak boleh ada yang kosong.");
        return false;
    }

    var totTransRound = Number(Number(_totalTransaksi).toFixed(2));
    var totDebRound = Number(Number(_totalDebit).toFixed(2));
    var totKredRound = Number(Number(_totalDebit).toFixed(2));

    if (totTransRound !== totDebRound || totTransRound !== totKredRound || totDebRound !== totKredRound) {
        showWarning("Total Transaksi tidak sama dengan Total Debit dan Total Kredit.");
        return false;
    }

    serviceSaveWarkatAndJurnal(request);
    $('#newWarkatDialog').modal('toggle');
}

function serviceSaveWarkatAndJurnal(data) {
    // console.log(data);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        url: _baseUrl + `/akunting/transaksi/jurnal-pajak/saldo-warkat/and/jurnals`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function () {
                showSuccess();
                _jurnalTransaksiDatatable.ajax.reload();
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

                fatchTableJurnal();

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