var kdThnBukuBerjalan;
var kdThnBukuBerikut;
var statusAksi;
var fileLampiran;
var rowData;

jQuery(document).ready(function () {
    setDefaultValue();

    $("#dataExcel").hide();

    var table = $("#tblAnggaran").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/validasi-anggaran/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: 'noAnggaran',
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
                title: 'No. AT',
                data: 'noAnggaran',
                className: 'text-center',
            },
            {
                title: 'Tahun Buku',
                data: 'tahunBuku.namaTahunBuku',
                className: 'text-center'
            },
            {
                title: 'Kode Rekening',
                data: 'idRekening.kodeRekening',
                className: 'text-center'
            },
            {
                title: 'Versi',
                data: 'versi',
                className: 'text-center'
            },
            {
                title: 'Total Anggaran',
                data: 'totalAnggaran',
                className: 'dt-right',
                render: $.fn.dataTable.render.number(',', '.', 2)
            },
            {
                title: 'Tgl Input',
                data: 'createdDate',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return data.substr(0, 10);
                }
            },
            {
                title: 'Nama Pemakai',
                data: 'createdBy'
            },
            {
                title: 'Status Data',
                data: 'statusData',
                className: 'text-center'
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

    $("#tblAnggaran tbody").on("click", ".detail-button", function () {
        statusAksi = "edit";
        $('#newAnggaranDialog').modal('show');
        var data = table.row($(this).parents('tr')).data();
        rowData = data;
        console.log(rowData);
        fileLampiran = data.fileLampiran;

        $("#tahunBuku option").filter(function () {
            return $.trim($(this).text()) == data.tahunBuku.tahun;
        }).prop('selected', true);
        $('#tahunBuku').selectpicker('refresh');
        dynamicLabel();
        excelJson();
        setAnggaranDialogValue(data);
    });

    $("#btnBtlAnggaran").click(function () {
        $('#newAnggaranDialog').modal('hide');
    });

    setInputMoneyFormat();
    initEvent(table);
});

function initEvent(table) {
    $("#btnTolak").click(function () {
//        reject(table);
        $('#newAnggaranDialog').modal('hide');
        $("#reject-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#confirmReject").off().on("click", function () {
            var noAnggaran = $("#txtNoAt").val();
            var aktivitas = "REJECT";
            var keterangan = $("#keteranganReject").val();
            var totalTransaksi = $("#totalTransaksi").val();
            var statusData = "REJECT";

            var objSerap = {};
            objSerap.noAnggaran = noAnggaran;
            objSerap.statusData = statusData;
            objSerap.catatanValidasi = keterangan;

            var objValidSerap = {};
            objValidSerap.noAnggaran = noAnggaran;
            objValidSerap.aktivitas = aktivitas;
            objValidSerap.keterangan = keterangan;
            objValidSerap.totalTransaksi = totalTransaksi;
            objValidSerap.statusData = statusData;

            if (keterangan == "") {
                swal.fire({
                    position: "top-right",
                    type: "error",
                    title: "Keterangan tidak boleh kosong.",
                    showConfirmButton: !1,
                    timer: 1500
                });
                return;
            }

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(objSerap),
                url: _baseUrl + "/akunting/anggaran/validasi-anggaran/reject",
                success: function () {
                    $("#reject-form").modal("hide");
                    table.ajax.reload();
                },
                statusCode: {
                    201: function () {
//                        $.ajax({
//                            type: "POST",
//                            contentType: "application/json",
//                            data: JSON.stringify(objSerap),
//                            url: _baseUrl + "/akunting/transaksi/serap/update-status-data",
//                            success: function () {
//                                $("#reject-form").modal("hide");
//                                showSuccess();
//                                table.ajax.reload();
//                            },
//                        });
                    }
                }
            });
        });
    });

    $("#btnSetuju").click(function () {
        approve(table);
    });
}

function setDefaultValue() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/anggaran/penyusunan-anggaran/default-value",
        success: function (resp) {
            $("#lbAnggaranZZ").text("Anggaran Tahun Buku " + resp.tahunBukuBerikut.kodeTahunBuku + " / " + resp.tahunBukuBerikut.tahun);
            $("#lbAnggaranXX").html("Anggaran Tahun Buku " + resp.tahunBukuBerjalan.kodeTahunBuku + " / " + resp.tahunBukuBerjalan.tahun);
            $("#lbPerikraanRelasi").html("Perkiraan Realisasi s/d Des. " + resp.tahunBukuBerjalan.tahun);
            $("#lbRealisasi").html("Relasi 1 JAN s/d " + resp.periode.namaPeriode.substr(0, 3) + ". " + resp.tahunBukuBerjalan.tahun);
            $("#lbProyeksi1").html("Proyeksi s/d Jun. " + resp.tahunBukuBerikut.tahun);
            $("#lbProyeksi2").html("Proyeksi s/d Des. " + resp.tahunBukuBerikut.tahun);

            $("#cbPeriode").val(resp.periode.kodePeriode);

            kdThnBukuBerjalan = resp.tahunBukuBerjalan.kodeTahunBuku;
            kdThnBukuBerikut = resp.tahunBukuBerikut.kodeTahunBuku;
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function setInputMoneyFormat() {
    $("#txtPerkiraanRelasi").simpleMoneyFormat();
    $("#txtProyeksi1").simpleMoneyFormat();
    $("#txtProyeksi2").simpleMoneyFormat();
}

function setMoneyFormat(value) {
    value = '' + value;
    var result = '';
    var valueArray = value.split('');
    var resultArray = [];
    var counter = 0;
    var temp = '';
    for (var i = valueArray.length - 1; i >= 0; i--) {
        temp += valueArray[i];
        counter++
        if (counter == 3) {
            resultArray.push(temp);
            counter = 0;
            temp = '';
        }
    }
    ;
    if (counter > 0) {
        resultArray.push(temp);
    }
    for (var i = resultArray.length - 1; i >= 0; i--) {
        var resTemp = resultArray[i].split('');
        for (var j = resTemp.length - 1; j >= 0; j--) {
            result += resTemp[j];
        }
        ;
        if (i > 0) {
            result += ','
        }
    }
    ;
    return result;
}

function approve(table) {
    var obj;
    var noAnggaran = $("#txtNoAt").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/validasi-anggaran/" + noAnggaran +
            "/approve?idRekening=" + $("#txtIdMataAnggaran").val() + "&kdTahunBuku=" + rowData.tahunBuku.kodeTahunBuku,
        success: function (response) {
            if (rowData.tahunBuku.kodeTahunBuku == kdThnBukuBerjalan) {
                console.log("True");
                updateSaldoCurrentPAFA(rowData.idRekening, rowData.totalAnggaran);
            }

            showSuccess();
            table.ajax.reload();
            $('#newAnggaranDialog').modal('hide');
        },
        statusCode: {
            201: function () {
                console.log('Updated');
                showSuccess();
                table.ajax.reload();
                $('#newAnggaranDialog').modal('hide');
            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function reject(table) {
    var obj;
    var noAnggaran = $("#txtNoAt").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/validasi-anggaran/" + noAnggaran + "/reject?kdTahunBuku=" + rowData.tahunBuku.kodeTahunBuku,
        success: function (response) {
            showSuccess();
            table.ajax.reload();
//            $('#newAnggaranDialog').modal('hide');
        },
        statusCode: {
            201: function () {
                console.log('Updated');
                showSuccess();
                table.ajax.reload();
                $('#newAnggaranDialog').modal('hide');

            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function setAnggaranDialogValue(data) {
    $("#txtMataAnggaran").val(data.idRekening.namaRekening);
    $("#txtIdMataAnggaran").val(data.idRekening.idRekening);
    $("#txtNoAt").val(data.noAnggaran);
    $("#txtRealisasi").val(setMoneyFormat(data.realisasi));
    $("#txtPerkiraanRelasi").val(setMoneyFormat(data.perkiraan));
    $("#txtAnggaranXX").val(setMoneyFormat(data.anggaranLalu));
    $("#txtAnggaranZZ").val(setMoneyFormat(data.totalAnggaran));
    $("#txtProyeksi1").val(setMoneyFormat(data.proyeksi1));
    $("#txtProyeksi2").val(setMoneyFormat(data.proyeksi2));
}

function excelJson() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/anggaran/penyusunan-anggaran/excel-json?filePath=" + fileLampiran,
        success: function (resp) {
            $("#dataExcel").show();
            $("#btnReupload").show();
            showExcel(resp);
        }
    });
}

function showExcel(resp) {
    var hot = new Handsontable(document.getElementById('dataExcel'), {
        data: resp.listData,
        startRows: 5,
        startCols: 5,
        minSpareCols: 1,
        minSpareRows: 1,
        rowHeaders: true,
        colHeaders: true,
        contextMenu: true,
        useFormula: true,
        width: '100%',
        height: 300,
        licenseKey: 'non-commercial-and-evaluation',
    });

    fileLampiran = resp.filePath;
    $("#txtAnggaranZZ").val(setMoneyFormat(resp.listData[8][1]));
}

function updateSaldoCurrentPAFA(idRekening, totalAnggaran) {
    var obj = {
        "idRekening": {},
        "totalAnggaran": ""
    };
    obj.idRekening = idRekening;
    obj.totalAnggaran = totalAnggaran;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + '/akunting/anggaran/penyusunan-anggaran/update-saldo-current-pa-fa',
        success: function (resp) {
        },
        statusCode: {
            400: function () {
                showError("File upload failed");
            },
            404: function () {
                showError("File upload failed");
            },
            500: function () {
                showError("Internal Server Error.");
            }
        }
    });
}