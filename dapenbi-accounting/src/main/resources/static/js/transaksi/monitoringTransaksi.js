var jurnalTransaksiList = [];
var debitMap = new Map();
var kreditMap = new Map();
var totalDebit = 0;
var totalKredit = 0;
var warkatJurnalSelected;

jQuery(document).ready(function () {
    setDefaultValue();

    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });

    $( "#fStatusKerja" ).change(function() {
      refreshTable();
    });

    $( "#fStatusData" ).change(function() {
      refreshTable();
    });

    $( "#fTglTransaksi" ).change(function() {
      refreshTable();
    });

});

function refreshTable() {
    var obj = {
        "idOrg": $("#fStatusKerja").val(),
        "statusData": $("#fStatusData").val(),
        "tglTransaksi": $("#fTglTransaksi").val()
    }
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/monitoring-transaksi/datatables",
        success: function (resp) {
            if(resp.length > 0) {
                console.log(resp);
                $("#tblWarkat").DataTable().clear().rows.add(resp).draw();
            } else {
                var data=[];
                $("#tblWarkat").DataTable().clear().rows.add(data).draw();
            }
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function resetTableJurnal() {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"6\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

function compareUrutanWarkatJurnal(a, b) {
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
function warkatDatatables(data) {
    tableWarkat = $("#tblWarkat").DataTable({
        lengthMenu: [10, 25, 50, 100],
        serverSide: false,
        processing: true,
        data: data,
        language: {
            'loadingRecords': '&nbsp;',
            'processing': '<div class="spinner"></div>'
        },
        columns: [
            {
                defaultContent: "",
                title: 'No.',
                data: 'noWarkat',
                className: 'text-center',
                width: '50px',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                defaultContent: "",
                title: 'No. Warkat',
                data: 'noWarkat',
                className: 'text-center',
            },
            {
                defaultContent: "",
                title: 'Transaksi',
                data: 'tglTransaksi',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return data.substr(0, 10);
                }
            },
            {
                defaultContent: "-",
                title: 'Keterangan',
                data: 'keterangan',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Total Transaksi',
                data: 'totalTransaksi',
                className: 'text-right',
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                defaultContent: "",
                title: 'Tgl. Input',
                data: 'createdDate',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return data.substr(0, 10);
                }
            },
            {
                defaultContent: "",
                title: 'Nama Pemakai',
                data: 'createdBy'
            },
            {
                defaultContent: "",
                title: 'Status Data',
                data: 'statusData',
                className: 'text-center',
                render: function (data, type, row, meta) {
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
                    return getButtonGroup(false, false, true);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON detail
        $('#tblWarkat tbody').on('click', '.detail-button', function () {

            var data = tableWarkat.row($(this).parents('tr')).data();

            // console.log(data);
            // PATCH WARKAT
            $("#txtJenisTransaksi").val(data.kodeTransaksi.kodeTransaksi);
            $("#txtNoWarkat").val(data.noWarkat);
            $("#txtTglTransaksi").val(data.tglTransaksi.substr(0, 10));
            $("#txtTglBuku").val(data.tglBuku.substr(0, 10));
            $("#txtTotalTransaksi").val(data.totalTransaksi);
            $("#txtKeterangan").val(data.keterangan);
            $("#txtTglInput").val(data.createdDate.substr(0, 10));
            $("#txtUserInput").val(data.createdBy);
            createdBy = data.createdBy;
            createdDate = data.createdDate;

            // PATCH WARKAT JURNAL
            jurnalTransaksiList = data.warkatJurnals.sort(compareUrutanWarkatJurnal);
            var tbody = document.getElementById('tbody-jurnal');
            var tfoot = document.getElementById('tfoot-jurnal');
            tbody.innerHTML = '';
            if (jurnalTransaksiList.length != 0) {
                totalDebit = 0;
                totalKredit = 0;
                for (var i = 0; i < Object.keys(jurnalTransaksiList).length; i++) {
                    var tr = "<tr>";
                    tr += "<td align='center'>" + jurnalTransaksiList[i].noUrut.toString() + "</td>" +
                        "<td hidden>" + jurnalTransaksiList[i].idRekening.idRekening + "</td>" +
                        "<td align='center'>" + jurnalTransaksiList[i].idRekening.kodeRekening + "</td>" +
                        "<td>" + jurnalTransaksiList[i].idRekening.namaRekening + "</td>" +
                        "<td>" + `<input type="number" id="debit-${i}" class="form-control col-debit" placeholder='0' min="0" value='${jurnalTransaksiList[i].jumlahDebit}' readonly/>` + "</td>" +
                        "<td>" + `<input type="number" id="kredit-${i}" class="form-control col-kredit" placeholder='0' min="0" value='${jurnalTransaksiList[i].jumlahKredit}' readonly/>` + "</td>" +
                        "</td></tr>";
                    tbody.innerHTML += tr;

                    debitMap.set(`debit-${i}`, jurnalTransaksiList[i].jumlahDebit);
                    kreditMap.set(`kredit-${i}`, jurnalTransaksiList[i].jumlahKredit);
                    totalDebit += jurnalTransaksiList[i].jumlahDebit;
                    totalKredit += jurnalTransaksiList[i].jumlahKredit;
                }

                var trFoot ="<tr><td></td><td></td><td></td>" +
                        "<td>" + `<input type="number" id="debit-${i}" class="form-control col-debit" placeholder='0' min="0" value='${totalDebit}' readonly/>` + "</td>" +
                        "<td>" + `<input type="number" id="kredit-${i}" class="form-control col-kredit" placeholder='0' min="0" value='${totalKredit}' readonly/>` + "</td>" +
                        "</tr>";
                tfoot.innerHTML = trFoot;
            } else {
                resetTableJurnal()
            }

            $('#newWarkatDialog').modal('show');
        });
}

function getDataTables() {
    var obj = {
        "idOrg": $("#fStatusKerja").val(),
        "statusData": $("#fStatusData").val(),
        "tglTransaksi": $("#fTglTransaksi").val()
    }
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/monitoring-transaksi/datatables",
        success: function (resp) {
            if(resp.length > 0) {
                console.log(resp);
                warkatDatatables(resp);
            } else {
                var data=[];
                warkatDatatables(data);
            }
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function setDefaultValue() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/monitoring-transaksi/default-value",
        success: function (resp) {
                $("#fTglTransaksi").val(resp.tglTransaksi.substr(0, 10));

            if(resp.statusPemakai != 0 && resp.statusPemakai != null) {
                $("#fStatusKerja").val(resp.idOrg);
            }
            getDataTables();
            console.log(resp);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}