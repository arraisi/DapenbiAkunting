var _totalTransaksi;
var _currentWarkat;
var tableWarkat;
var _jurnals = [];

jQuery(document).ready(function () {
    paceOptions = {
        elements: {
            selectors: ['.pace-loader']
        }
    };

    warkatDatatables();
});

function updateStatusWarkat(status) {
    _currentWarkat.statusData = status;
    _currentWarkat.keterangan = $('#txtKeterangan').val();
    _currentWarkat.catatanValidasi = $('#catatan').val();
    _currentWarkat.warkatJurnals = _jurnals;

    if (status === 'REJECT') {
        if (_currentWarkat.catatanValidasi === '') {
            showWarning("Keterangan dan Catatan tidak boleh kosong");
            return false;
        }
    }

    $('#warkatDialog').modal('hide');
    $('#catatanTolakDialog').modal('hide');

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentWarkat),
        url: _baseUrl + `/akunting/sse/final-approval`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function (data) {
                showSuccess();
                tableWarkat.ajax.reload();
            }
        },
        complete: function (data) {
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
            // console.log(data);
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
function warkatDatatables() {
    tableWarkat = $("#tblWarkat").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/transaksi/saldo/saldo-warkat/datatables',
            type: "POST",
            data: function (d) {
                d.warkatDTO = {
                    statusData: "PA",
                    jenisWarkat: "SSE",
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
                    return data.substr(0, 10);
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
                title: "Tindakan",
                width: "100px",
                class: "text-center",
                sortable: false,
                orderable: false,
                searchable: false,
                render: function (data, type, row, meta) {
                    return `<button class='btn btn-sm btn-clean validation-button btn-primary' title='Validasi'><i class='la la-edit'></i> Validasi</button>`;
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON EDIT
    $('#tblWarkat tbody').on('click', '.validation-button', function () {
        var data = tableWarkat.row($(this).parents('tr')).data();
        // console.log(data);
        _currentWarkat = data;
        $("#txtJenisTransaksi").val(_currentWarkat.kodeTransaksi.kodeTransaksi);
        $("#txtNoWarkat").val(_currentWarkat.noWarkat);
        $("#txtTglTransaksi").val(_currentWarkat.tglTransaksi.substr(0, 10));
        $("#txtTglBuku").val(_currentWarkat.tglBuku.substr(0, 10));
        $("#txtTahunBuku").val(_currentWarkat.tahunBuku);
        _totalTransaksi = _currentWarkat.totalTransaksi;
        $("#txtTotalTransaksi").val(numeralformat(_totalTransaksi));
        $("#txtKeterangan").val(_currentWarkat.keterangan);
        $("#txtTglInput").val(_currentWarkat.createdDate.substr(0, 10));
        $("#txtUserInput").val(_currentWarkat.createdBy);
        $('#catatan').val('');

        findWarkatJurnals();
    });
}

function findWarkatJurnals() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/api/akunting/warkat-jurnal/findByNoWarkat?noWarkat=" + _currentWarkat.noWarkat,
        success: function (response) {
            // console.log(response, "RESPONSE");
            if (response !== undefined) {
                _jurnals = response;
            }
        },
        statusCode: {
            200: function (data) {
                // RENDER TABLE WARKAT JURNAL
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
                $('#warkatDialog').modal('show');
            }
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}