var kdThnBukuBerjalan;
var kdThnBukuBerikut;
var statusAksi;
var fileLampiran;
var rowData;
var _currentBudgetReview = null;
var _currentDaftarRekening = [];

jQuery(document).ready(function () {
    setDefaultValue();

    $("#dataExcel").hide();

    var table = $("#tblAnggaran").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/validasi-anggaran-new/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
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
            },
            {
                title: 'Tahun Buku',
                data: 'kodeThnBuku',
                render: function (data, type, row, meta) {
                    return data.namaTahunBuku;
                }
            },
            {
                defaultContent: "",
                title: 'Periode',
                data: 'kodePeriode',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return data.namaPeriode;
                }
            },
            {
                title: 'Versi',
                data: 'versi',
                className: 'text-center'
            },
            {
                title: 'Tgl Input',
                data: 'createdDate',
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return moment(data).format("DD-MM-YYYY");
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
        var data = table.row($(this).parents('tr')).data();
        _currentBudgetReview = data;
        findDetails();
    });

    $("#btnBtlAnggaran").click(function () {
        $('#newAnggaranDialog').modal('hide');
    });

    setInputMoneyFormat();
    initEvent(table);
});

function initEvent(table) {
    $("#btnTolak").click(function () {
      reject(table);
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
    var noAnggaran = $("#txtNoAnggaran").val();
    console.log(_currentDaftarRekening);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/validasi-anggaran-new/" + noAnggaran + "/approve",
        success: function (response) {
            if (_currentBudgetReview.kodeThnBuku.kodeTahunBuku == pengaturanSistem.kodeTahunBuku) {
                console.log("True");
                updateSaldoCurrentPAFA(noAnggaran);
            }

            showSuccess();
            table.ajax.reload();
            $('#budgetReviewDialog').modal('hide');
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
    var noAnggaran = $("#txtNoAnggaran").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/validasi-anggaran-new/" + noAnggaran + "/reject",
        success: function (response) {
            showSuccess();
            table.ajax.reload();
            $('#budgetReviewDialog').modal('hide');
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

function updateSaldoCurrentPAFA(noAnggaran) {
    var obj = {};
    console.log(obj);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + '/akunting/anggaran/validasi-anggaran-new/update-saldo-current-pa-fa?noAnggaran=' + noAnggaran,
        success: function (resp) {
        },
        statusCode: {
            400: function () {
                showError("Save on Saldo Current, FA, PA failed");
            },
            404: function () {
                showError("Save on Saldo Current, FA, PA failed");
            },
            500: function () {
                showError("Internal Server Error.");
            }
        }
    });
}

function findDetails() {
    console.log(_currentBudgetReview);
    _currentDaftarRekening = [];
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/anggaran/penyusunan-anggaran-new/details?noAnggaran=${_currentBudgetReview.noAnggaran}`,
        success: function (response) {
            console.log(response);
            _currentDaftarRekening = response;
            fatchFormBudgetReview();
            $('#budgetReviewDialog').modal('show');
        },
        complete: function (response) {
            if (response.status != 200)
                showSuccess("Data kosong.");
        }
    });
}

function fatchFormBudgetReview() {
    fatchTableRekening();
    console.log(_currentBudgetReview.kodePeriode.kodePeriode);
    console.log(_currentBudgetReview.kodeThnBuku.kodeTahunBuku);
    $('#txtNoAnggaran').val(_currentBudgetReview.noAnggaran);
    $('#idPeriode').val(_currentBudgetReview.kodePeriode.kodePeriode).selectpicker("refresh");
    $('#idTahunBuku').val(_currentBudgetReview.kodeThnBuku.kodeTahunBuku).selectpicker("refresh");
    $('#txtKeterangan').val(_currentBudgetReview.keterangan);

    if (_currentBudgetReview.kodeThnBuku.kodeTahunBuku == pengaturanSistem.kodeTahunBuku){
        $('#btnSetuju').attr("disabled", false);
    } else{
        $('#btnSetuju').attr("disabled", true);
    }

}

function fatchTableRekening() {
    if (_currentDaftarRekening != null) {
        if (_currentDaftarRekening.length > 0) {
            var tbody = document.getElementById('tbody-detail');
            tbody.innerHTML = '';
            for (var i = 0; i < Object.keys(_currentDaftarRekening).length; i++) {
                let tr = "<tr>";
                tr += "<td>" + _currentDaftarRekening[i].idRekening.kodeRekening + "</td>" +
                    "<td hidden>" + _currentDaftarRekening[i].idRekening.idRekening + "</td>" +
                    "<td>" + _currentDaftarRekening[i].idRekening.namaRekening + "</td>" +
                    "<td class='textCurrency text-right'>" + numeralformat(_currentDaftarRekening[i].anggaranLalu) + "</td>" +
//                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].realisasi) + "</td>" +
//                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].realisasi) + "</td>" +
                    "<td class='textCurrency text-right'>" + numeralformat(_currentDaftarRekening[i].totalAnggaran) + "</td>" +
//                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].saldo) + "</td>" +
//                    "<td class='textCurrency'>" + numeralformat(_currentDaftarRekening[i].saldo) + "</td>" +
//                    "<td>" + _currentDaftarRekening[i].keterangan + "</td>" +
//                    "<td class='text-center'>" +
//                    `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Edit' value="Edit" onclick='rekeningEditBtnPressed(this)'><i class='la la-edit'></i></a>` +
//                    "</td>" +
                    "</tr>";
                tbody.innerHTML += tr;
            }
        }
    }
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}