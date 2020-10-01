var _totalTransaksi;
var _currentJurnalBiaya;
var _jurnalBiayaDatatable;

jQuery(document).ready(function () {
    paceOptions = {
        elements: {
            selectors: ['.pace-loader']
        }
    };

    jurnalBiayaDatatable();
});

function updateStatus(status) {
    _currentJurnalBiaya.statusData = status;
    _currentJurnalBiaya.keterangan = $('#txtKeterangan').val();
    _currentJurnalBiaya.catatanValidasi = $('#catatan').val();

    if (status === 'REJECT') {
        if (_currentJurnalBiaya.catatanValidasi === '') {
            showWarning("Keterangan dan Catatan tidak boleh kosong");
            return false;
        }
    }

    $('#jurnalBiayaDialog').modal('hide');
    $('#catatanTolakDialog').modal('hide');

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentJurnalBiaya),
        url: _baseUrl + `/akunting/transaksi/jurnal-biaya/validasi`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function (data) {
                showSuccess();
                _jurnalBiayaDatatable.ajax.reload();
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
function jurnalBiayaDatatable() {
    _jurnalBiayaDatatable = $("#jurnalBiayaDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/transaksi/jurnal-biaya/datatables',
            type: "POST",
            data: function (d) {
                d.jurnalBiayaDTO = {
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
    $('#jurnalBiayaDatatable tbody').on('click', '.validation-button', function () {

        var data = _jurnalBiayaDatatable.row($(this).parents('tr')).data();
        // console.log(data);
        _currentJurnalBiaya = data;
        $("#txtJenisTransaksi").val(_currentJurnalBiaya.kodeTransaksi.kodeTransaksi);
        $("#txtNoWarkat").val(_currentJurnalBiaya.noWarkat);
        $("#txtTglTransaksi").val(moment(_currentJurnalBiaya.tglTransaksi).format('yyyy-MM-DD'));
        $("#txtTglBuku").val(moment(_currentJurnalBiaya.tglBuku).format('yyyy-MM-DD'));
        $("#txtTahunBuku").val(_currentJurnalBiaya.tahunBuku);
        _totalTransaksi = _currentJurnalBiaya.totalTransaksi;
        $("#txtTotalTransaksi").val(numeralformat(_totalTransaksi));
        $("#txtKeterangan").val(_currentJurnalBiaya.keterangan);
        $("#txtTglInput").val(moment(_currentJurnalBiaya.createdDate).format('yyyy-MM-DD'));
        $("#txtUserInput").val(_currentJurnalBiaya.createdBy);

        findJurnals();
    });
}

function findJurnals() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/api/akunting/warkat-jurnal/findByNoWarkat?noWarkat=" + _currentJurnalBiaya.noWarkat,
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
                $('#jurnalBiayaDialog').modal('show');
            }
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}