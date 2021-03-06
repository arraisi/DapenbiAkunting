var _totalTransaksi;
var _currentJurnalTransaksi;
var _jurnalTransaksiDatatable;
var _jurnals = [];
var _totalDebit = 0;
var _totalKredit = 0;
var _totalTransaksi = 0;

jQuery(document).ready(function () {
    jurnalTransaksiDatatable();
});

function updateStatus(status) {
    _currentJurnalTransaksi.statusData = status;
    _currentJurnalTransaksi.keterangan = $('#txtKeterangan').val();
    _currentJurnalTransaksi.catatanValidasi = $('#catatan').val();

    if (status === 'REJECT') {
        if (_currentJurnalTransaksi.catatanValidasi === '') {
            showWarning("Keterangan dan Catatan tidak boleh kosong");
            return false;
        }
    }

    $('#jurnalTransaksiDialog').modal('hide');
    $('#catatanTolakDialog').modal('hide');

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentJurnalTransaksi),
        url: _baseUrl + `/api/akunting/transaksi/saldo/validasi`,
        success: function (response) {
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function (data) {
                showSuccess();
                _jurnalTransaksiDatatable.ajax.reload();
            }
        },
        complete: function (data) {
            enableButton();
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
function jurnalTransaksiDatatable() {
    _jurnalTransaksiDatatable = $("#jurnalTransaksiDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/api/akunting/transaksi/saldo/saldo-warkat/datatables',
            type: "POST",
            data: function (d) {
                d.warkatDTO = {
                    statusData: "SUBMIT",
                    jenisWarkat: "JURNAL_TRANSAKSI",
                    startDate: '',
                    endDate: '',
                    kodeTransaksi: {kodeTransaksi: ''}
                };
                return JSON.stringify(d);
            }
        },
        pageLength: -1,
        lengthMenu: [[10, 25, 50, 100, -1], ['10', '25', '50', '100', 'All']],
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
    $('#jurnalTransaksiDatatable tbody').on('click', '.validation-button', function () {

        var data = _jurnalTransaksiDatatable.row($(this).parents('tr')).data();
        // console.log(data);
        _currentJurnalTransaksi = data;
        $("#txtJenisTransaksi").val(_currentJurnalTransaksi.kodeTransaksi.kodeTransaksi);
        $("#txtNoWarkat").val(_currentJurnalTransaksi.noWarkat);
        $("#txtTglTransaksi").val(moment(_currentJurnalTransaksi.tglBuku).format('yyyy-MM-DD'));
        $("#txtTglBuku").val(moment(_currentJurnalTransaksi.tglBuku).format('yyyy-MM-DD'));
        $("#txtTahunBuku").val(_currentJurnalTransaksi.tahunBuku);
        _totalTransaksi = _currentJurnalTransaksi.totalTransaksi;
        $("#txtTotalTransaksi").val(numeralformat(_totalTransaksi));
        $("#txtKeterangan").val(_currentJurnalTransaksi.keterangan);
        $("#txtTglInput").val(moment(_currentJurnalTransaksi.createdDate).format('yyyy-MM-DD'));
        $("#txtUserInput").val(_currentJurnalTransaksi.createdBy);

        findJurnals();
    });
}

function findJurnals() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/api/akunting/warkat-jurnal/findByNoWarkat?noWarkat=" + _currentJurnalTransaksi.noWarkat,
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                // RENDER TABLE JURNAL
                _jurnals = data.sort(compareUrutanJurnal);
                var tbody = document.getElementById('tbody-jurnal');
                tbody.innerHTML = '';
                if (_jurnals.length != 0) {
                    for (var i = 0; i < Object.keys(_jurnals).length; i++) {
                        const jumlahDebit = _jurnals[i].jumlahDebit === null ? '0' : _jurnals[i].jumlahDebit;
                        const jumlahKredit = _jurnals[i].jumlahKredit === null ? '0' : _jurnals[i].jumlahKredit;
                        let tr = "<tr>";
                        tr += "<td>" + _jurnals[i].noUrut + "</td>" +
                            "<td>" + _jurnals[i].idRekening.kodeRekening + "</td>" +
                            "<td>" + _jurnals[i].idRekening.namaRekening + "</td>" +
                            "<td class='textCurrency'>" + numeralformat(jumlahDebit) + "</td>" +
                            "<td class='textCurrency'>" + numeralformat(jumlahKredit) + "</td>" +
                            "</td></tr>";
                        tbody.innerHTML += tr;
                    }
                    tbody.innerHTML += "\n" +
                        "<tr style=\"height: 31px\">\n" +
                        "   <td colspan=\"3\" style=\"padding:0.65rem 1rem\"><strong>Total</strong>\n" +
                        "   </td>\n" +
                        "   <td class=\"text-right textCurrency\" id=\"txtTotalDebit\" style=\"padding:0.65rem 1rem; font-weight: bold;\"><strong>0</strong>" +
                        "   </td>\n" +
                        "   <td class=\"text-right textCurrency\" id=\"txtTotalKredit\" style=\"padding:0.65rem 1rem; font-weight: bold;\"><strong>0</strong>" +
                        "   </td>\n" +
                        "</tr>";
                    countSummary();
                }
                $('#jurnalTransaksiDialog').modal('show');
            }
        }
    });
}

function countSummary() {
    _totalKredit = 0;
    _totalDebit = 0;

    _jurnals.forEach(jurnal => {
        if (!isNaN(jurnal.jumlahDebit) && jurnal.jumlahDebit != null) {
            _totalDebit += parseFloat(jurnal.jumlahDebit);
        }

        if (!isNaN(jurnal.jumlahKredit) && jurnal.jumlahKredit != null) {
            _totalKredit += parseFloat(jurnal.jumlahKredit);
        }
    });

    _totalTransaksi = numeralForCountformat(_totalTransaksi);
    _totalDebit = numeralForCountformat(_totalDebit);
    _totalKredit = numeralForCountformat(_totalKredit);

    $('#txtTotalDebit').text(numeralformat(_totalDebit));
    $('#txtTotalKredit').text(numeralformat(_totalKredit));
    _totalTransaksi = _totalKredit;
    $('#txtTotalTransaksi').val(numeralformat(_totalTransaksi));
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function numeralForCountformat(value) {
    return numeral(value).format('0.00')
}