var _totalTransaksi;
var _currentJurnalPendapatan;
var _jurnalPendapanDatatable;

jQuery(document).ready(function () {
    paceOptions = {
        elements: {
            selectors: ['.pace-loader']
        }
    };

    jurnalPendapanDatatable();
});

function updateStatus(status) {
    _currentJurnalPendapatan.statusData = status;
    _currentJurnalPendapatan.keterangan = $('#txtKeterangan').val();
    _currentJurnalPendapatan.catatanValidasi = $('#catatan').val();

    if (status === 'REJECT') {
        if (_currentJurnalPendapatan.catatanValidasi === '') {
            showWarning("Keterangan dan Catatan tidak boleh kosong");
            return false;
        }
    }

    $('#jurnalPendapatanDialog').modal('hide');
    $('#catatanTolakDialog').modal('hide');

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentJurnalPendapatan),
        url: _baseUrl + `/akunting/transaksi/jurnal-pendapatan/validasi`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function (data) {
                showSuccess();
                _jurnalPendapanDatatable.ajax.reload();
            }
        },
        complete: function (data) {
            if (data.status !== 200) {
                showWarning("Terjadi kesalahan, Mohon cek kesesuaian debit, kredit dengan saldo anggaran dan saldo akhir", 3500);
            }
        }
    });
}

function compareUrutanJurnal(a, b) {
    // Use toUpperCase() to ignore character casing
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

//* WARKAT *//
// WARKAT DATATABLES
function jurnalPendapanDatatable() {
    _jurnalPendapanDatatable = $("#jurnalPendapanDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/transaksi/jurnal-pendapatan/datatables',
            type: "POST",
            data: function (d) {
                d.jurnalPendapatanDTO = {
                    statusData: 'SUBMIT'
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
                data: 'kodeTransaksi',
                title: 'Transaksi',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data.kodeTransaksi;
                }
            },
            {
                data: 'keterangan',
                title: 'Keterangan',
                defaultContent: "",
                render: function (data, type, row, meta) {
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
                    return moment(data).format('yyyy-MM-DD');
                }
            },
            {
                data: 'noWarkat',
                title: 'Nama Pemakai',
                visible: false,
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return '-';
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
                class: "text-center",
                sortable: false,
                orderable: false,
                searchable: false,
                render: function (data, type, row, meta) {
                    if (data !== 'FA')
                        return `<button class='btn btn-sm btn-clean validation-button btn-primary' title='Validasi'><i class='la la-edit'></i> Validasi</button>`;
                    else return '';
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON EDIT
    $('#jurnalPendapanDatatable tbody').on('click', '.validation-button', function () {

        var data = _jurnalPendapanDatatable.row($(this).parents('tr')).data();
        // console.log(data);
        _currentJurnalPendapatan = data;
        $("#txtJenisTransaksi").val(_currentJurnalPendapatan.kodeTransaksi.kodeTransaksi);
        $("#txtNoWarkat").val(_currentJurnalPendapatan.noWarkat);
        $("#txtTglTransaksi").val(moment(_currentJurnalPendapatan.tglTransaksi).format('yyyy-MM-DD'));
        $("#txtTglBuku").val(moment(_currentJurnalPendapatan.tglBuku).format('yyyy-MM-DD'));
        $("#txtTahunBuku").val(_currentJurnalPendapatan.tahunBuku);
        _totalTransaksi = _currentJurnalPendapatan.totalTransaksi;
        $("#txtTotalTransaksi").val(numeralformat(_totalTransaksi));
        $("#txtKeterangan").val(_currentJurnalPendapatan.keterangan);
        $("#txtTglInput").val(moment(_currentJurnalPendapatan.createdDate).format('yyyy-MM-DD'));
        $("#txtUserInput").val(_currentJurnalPendapatan.createdBy);

        findJurnals();
    });
}

function findJurnals() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/api/akunting/warkat-jurnal/findByNoWarkat?noWarkat=" + _currentJurnalPendapatan.noWarkat,
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                // RENDER TABLE JURNAL
                var jurnalTransaksiList = data.sort(compareUrutanJurnal);
                var tbody = document.getElementById('tbody-jurnal');
                tbody.innerHTML = '';
                if (jurnalTransaksiList.length != 0) {
                    for (var i = 0; i < Object.keys(jurnalTransaksiList).length; i++) {
                        const jumlahDebit = jurnalTransaksiList[i].jumlahDebit === null ? '0' : jurnalTransaksiList[i].jumlahDebit;
                        const jumlahKredit = jurnalTransaksiList[i].jumlahKredit === null ? '0' : jurnalTransaksiList[i].jumlahKredit;
                        let tr = "<tr>";
                        tr += "<td>" + jurnalTransaksiList[i].noUrut + "</td>" +
                            "<td>" + jurnalTransaksiList[i].idRekening.kodeRekening + "</td>" +
                            "<td>" + jurnalTransaksiList[i].idRekening.namaRekening + "</td>" +
                            "<td class='textCurrency'>" + numeralformat(jumlahDebit) + "</td>" +
                            "<td class='textCurrency'>" + numeralformat(jumlahKredit) + "</td>" +
                            "</td></tr>";
                        tbody.innerHTML += tr;
                    }
                }
                $('#jurnalPendapatanDialog').modal('show');
            }
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}