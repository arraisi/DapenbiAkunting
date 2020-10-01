var kdThnBukuBerjalan;
var kdThnBukuBerikut;
var statusAksi;
var fileLampiran;
var rekeningSelected;
var today = new Date();
var nextYear = today.setFullYear(today.getFullYear() + 1);
var _currentAnggaran;
var hot;
var hotSatker;

jQuery(document).ready(function () {
    setDefaultValue();
    mataAnggranDatatables();
    setSatkerTable();

    $("#btnReupload").hide();
    $("#btnDownload").hide();
    $("#dataExcel").hide();

    var table = $("#tblAnggaran").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/anggaran-akunting/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        createdRow: function (row, data, index) {
            if (data.statusAktif === '1') {
                $(row).addClass('rowActive');
                console.log(row);
            }
        },
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
                title: 'Nama Rekening',
                data: 'idRekening.namaRekening',
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
                    if (data != null) return data.substr(0, 10);
                    else return "";
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
                    if (data === 'DRAFT' || data === 'REJECT')
                        return getButtonGroup(true, true, false);
                    else return getButtonGroup(false, true, false);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    $("#tblAnggaran tbody").on("click", ".edit-button", function () {
        $('.fieldStatusAktif').attr('hidden', false);
        statusAksi = "edit";
        $('#newAnggaranDialog').modal('show');
        var data = table.row($(this).parents('tr')).data();
        console.log(data);
        rekeningSelected = data.idRekening;
        document.getElementById("txtStatusAktif").checked = data.statusAktif === '1';
        fileLampiran = data.fileLampiran;
        excelJson(data.fileLampiran);
        _currentAnggaran = data;

        if (data.statusData !== 'DRAFT' || data.statusData === 'REJECT') {
            disableFormAnggaranTahunan('off');
            $("#tahunBuku option").filter(function () {
                return $.trim($(this).text()) == data.tahunBuku.tahun;
            }).prop('selected', true);
            $('#tahunBuku').selectpicker('refresh');
            dynamicLabel();
        }
        setAnggaranDialogValue(data);

        setReadonly(false);
    });

    $("#tblAnggaran tbody").on("click", ".delete-button", function () {
        $('#deleteDialog').modal('show');
        var data = table.row($(this).parents('tr')).data();
        $("#txtDeleteNoAnggaran").val(data.noAnggaran);
    });

    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });

    $("#btnAdd").click(function () {
        disabledButton(false);
        $('.fieldStatusAktif').attr('hidden', true);
        statusAksi = "create";
        formReset();
//        console.log(moment(nextYear).format("YYYY"));
        $("#tahunBuku option").filter(function () {
            return $.trim($(this).text()) == moment(nextYear).format("YYYY");
        }).prop('selected', true);
        $('#tahunBuku').selectpicker('refresh');

        $('#newAnggaranDialog').modal('show');

        setReadonly(true);
    });

    $("#btnBtlAnggaran").click(function () {
        // updateIsEditRekening(0);
        $('#newAnggaranDialog').modal('hide');
        disableFormAnggaranTahunan('on');
    });

    // $("#txtMataAnggaran").click(function () {
    //     $('#mataAnggaranDialog').modal('show');
    // });


    // $("#uploadFile").change(function () {
    //     if (!rekeningSelected) {
    //         showError("Mata Anggaran Belum Dipilih");
    //         $('#uploadFile').val("");
    //         return;
    //     }
    //     upload();
    // });

    // $("#btnSubmitMataAnggaran").click(function () {
    //     console.log('Ok Mata Anggaran');
    //     createExcelFile(table);
    //     // if (rekeningSelected.isEdit === 1) {
    //     //     swal.fire({
    //     //         title: "Pemberitahuan",
    //     //         text: "Rekening yang dipilih sedang dalam perubahaan pengguna lain.",
    //     //         type: "warning",
    //     //         confirmButtonText: "OK"
    //     //     });
    //     //     return;
    //     // } else {
    //     //     updateIsEditRekening(1);
    //     // }
    // });
    setInputMoneyFormat();
    initEvent(table);
});

function setSatkerTable() {
    var tableSatker = $("#tblAnggaranSatker").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/anggaran-akunting/datatables-satker',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        createdRow: function (row, data, index) {
            if (data.statusAktif === '1') {
                $(row).addClass('rowActive');
                console.log(row);
            }
        },
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
                title: 'Nama Rekening',
                data: 'idRekening.namaRekening',
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
                    if (data != null) return data.substr(0, 10);
                    else return "";
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
                    return getButtonGroup(false, true, false);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    $("#tblAnggaranSatker tbody").on("click", ".edit-button", function () {
        refreshForm();
        statusAksi = "create";
        var data = tableSatker.row($(this).parents('tr')).data();
        console.info(data);
        $('#newAnggaranDialog').modal('show');
        excelJsonSatker(data.idRekening, data.tahunBuku);
        $("#tahunBuku option").filter(function () {
            return $.trim($(this).text()) == data.tahunBuku.tahun;
        }).prop('selected', true);
        $('#tahunBuku').selectpicker('refresh');
        $('#tahunBuku').prop('disabled', true);
        dynamicLabel();
        $('#txtIdMataAnggaran').val(data.idRekening.idRekening);
        $('#txtMataAnggaran').val(data.idRekening.kodeRekening + " - " + data.idRekening.namaRekening);
        rekeningSelected = data.idRekening;
        createExcelFile();
        // _currentAnggaran = data;
        //
        // if (data.statusData !== 'DRAFT' || data.statusData === 'REJECT') {
        //     disableFormAnggaranTahunan('off');
        //     $("#tahunBuku option").filter(function () {
        //         return $.trim($(this).text()) == data.tahunBuku.tahun;
        //     }).prop('selected', true);
        //     $('#tahunBuku').selectpicker('refresh');
        //     dynamicLabel();
        // }
        // setAnggaranDialogValue(data);
        //
        // setReadonly(false);
    });
}

function disableFormAnggaranTahunan(status) {
    $("#tahunBuku").attr("disabled", status === 'off');
    $("#txtMataAnggaran").attr("disabled", status === 'off');
    $("#cbPeriode").attr("disabled", status === 'off');
    $("#txtPerkiraanRelasi").attr("readOnly", status === 'off');
    $("#txtProyeksi1").attr("readOnly", status === 'off');
    $("#txtProyeksi2").attr("readOnly", status === 'off');
    $("#btnAmbilRelasi").attr("disabled", status === 'off');
    $("#btnAmbilDataAT").attr("disabled", status === 'off');
    $("#btnSimpanAnggaran").attr("hidden", status === 'off');
    $("#btnSubmitAnggaran").attr("hidden", status === 'off');
}

function initEvent(table) {
    $("#btnSimpanAnggaran").click(function () {
        console.log("button simpan pressed");
        if (isValid()) {
            simpanBtnPressed(true);
            disabledButton(true);
            $("#txtAnggaranZZ").val(setMoneyFormat(hot.getData()[8][1]));
            saveAnggaran(hot.getData(), table);
        }
    });

    $("#btnSubmitAnggaran").click(function () {
        console.log("button submit pressed");
        submitBtnPressed(true);
        disabledButton(true);
        $("#txtAnggaranZZ").val(setMoneyFormat(hot.getData()[8][1]));
        submitAnggaran(hot.getData(), table);
    });

    $("#btnDeleteAnggaran").click(function () {
        deleteById(table);
    });
}

function disabledButton(key) {
    $('#btnSimpanAnggaran').attr("disabled", key);
    $('#btnBtlAnggaran').attr("disabled", key);
    $('#btnSubmitAnggaran').attr("disabled", key);
}

function simpanBtnPressed(key) {
    $('#iconSimpanBtn').attr("hidden", key);
    $('#spinnerSimpanBtn').attr("hidden", !key);
}

function submitBtnPressed(key) {
    $('#iconSubmitBtn').attr("hidden", key);
    $('#spinnerSubmitBtn').attr("hidden", !key);
}

function mataAnggranDatatables() {
    tableTransaksi = $("#tblMataAnggaran").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + "/akunting/transaksi/serap/getSerapRekeningDatatables",
            type: "POST",
            data: function (d) {
                var rekening = {
                    "tipeRekening": "",
                    "levelRekening": null,
                    "isSummary": "N"
                };
                d.rekening = rekening;
                return JSON.stringify(d);
            }
        },
        // order: [ 1, "asc" ],
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        'processing': true,
        'language': {
            'loadingRecords': '&nbsp;',
            'processing': '<div class="spinner"></div>'
        },
        columns: [
            {
                title: 'No.',
                data: 'kodeRekening',
                orderable: false,
                className: 'text-center',
                width: '50px',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
                defaultContent: "",
                title: 'Kode Rekening',
                data: 'kodeRekening'
            },
            {
                defaultContent: "",
                title: 'Nama Rekening',
                data: 'namaRekening'
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // SELECTED
    $('#tblMataAnggaran tbody').on('click', 'tr', function () {

        tableTransaksi.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        var tr = $(this).closest('tr');
        var row = tableTransaksi.row(tr);
        // console.log(row.data(), "row transaksi selected");

        // $('#txtMataAnggaran').val(row.data().namaRekening);
        // $('#txtIdMataAnggaran').val(row.data().idRekening);
        rekeningSelected = row.data();
        console.log(rekeningSelected)
    });
}

function getDataRelasi(hot) {
    var idxTahunBuku = $("#tahunBuku").find("option:selected").index();
//    console.log(idxTahunBuku);
    var kodeTahunBuku = $("#tahunBuku option").eq(idxTahunBuku - 1).val();
    var mataAnggaran = $('#txtIdMataAnggaran').val();
//    console.log(kodeTahunBuku);
//    return;
    if (mataAnggaran == "") {
        showError("Pilih Mata Anggaran");
    } else {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/anggaran/anggaran-akunting/data-relasi?mataAnggaran=" + mataAnggaran + "&kodeTahunBuku=" + kodeTahunBuku,
            success: function (resp) {
                console.log(resp, "data realisasi");
                $("#txtRealisasi").val(setMoneyFormat(resp));
                hot.setDataAtCell(5, 1, $("#txtRealisasi").val());
            },
            statusCode: {
                500: function (resp) {
                    console.log(resp.responseJSON.message);
                }
            }
        });
    }
}

function getDataAT(hot) {
    var idxTahunBuku = $("#tahunBuku").find("option:selected").index();
    //    console.log(idxTahunBuku);
    var kodeTahunBuku = $("#tahunBuku option").eq(idxTahunBuku - 1).val();
    var mataAnggaran = $('#txtIdMataAnggaran').val();
    if (mataAnggaran == "") {
        showError("Pilih Mata Anggaran");
    } else {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/anggaran/anggaran-akunting/data-at?mataAnggaran=" + mataAnggaran + "&kodeTahunBuku=" + kodeTahunBuku,
            success: function (resp) {
                $("#txtAnggaranXX").val(setMoneyFormat(resp));
                hot.setDataAtCell(7, 1, $("#txtAnggaranXX").val());
            },
            statusCode: {
                500: function (resp) {
                    console.log(resp.responseJSON.message);
                }
            }
        });
    }
}

function setDefaultValue() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/anggaran/anggaran-akunting/default-value",
        success: function (resp) {
            // $("#txtNoAt").val(resp.noAT).prop("disabled", true);
            $("#txtNoAt").attr("disabled", true);
//            $("#lbAnggaranZZ").text("Anggaran Tahun Buku " + resp.tahunBukuBerikut.kodeTahunBuku + " / " + resp.tahunBukuBerikut.tahun);
//            $("#btnAmbilDataAT").html("Ambil Data AT " + resp.tahunBukuBerjalan.kodeTahunBuku + " / " + resp.tahunBukuBerjalan.tahun);
//            $("#lbAnggaranXX").html("Anggaran Tahun Buku " + resp.tahunBukuBerjalan.kodeTahunBuku + " / " + resp.tahunBukuBerjalan.tahun);
//            $("#lbPerikraanRelasi").html("Perkiraan Realisasi s/d Des. " + resp.tahunBukuBerjalan.tahun);
//            $("#lbRealisasi").html("Relasi 1 JAN s/d " + resp.periode.namaPeriode.substr(0, 3) + ". " + resp.tahunBukuBerjalan.tahun);
//            $("#lbProyeksi1").html("Proyeksi s/d Jun. " + resp.tahunBukuBerikut.tahun);
//            $("#lbProyeksi2").html("Proyeksi s/d Des. " + resp.tahunBukuBerikut.tahun);
            dynamicLabel();

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
    return result;
}

function update(table, statusData) {
    var obj = getValue();
    console.log(obj, 'object update');
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/anggaran/anggaran-akunting/update?statusData=" + statusData,
        success: function (response) {
            $('#newAnggaranDialog').modal('hide');
            showSuccess();
            table.ajax.reload();
        },
        statusCode: {
            201: function () {
                console.log('updated');
                $('#newAnggaranDialog').modal('hide');
                showSuccess();
                table.ajax.reload();

            },
            400: function () {
//                alert('Inputan tidak boleh kosong');
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
//                alert('Not Found');
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }, complete: function (resp) {
            simpanBtnPressed(false);
            submitBtnPressed(false);
            disabledButton(false);
        }
    });
}

function deleteById(table) {
    var noAnggaran = $("#txtDeleteNoAnggaran").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: _baseUrl + "/akunting/anggaran/anggaran-akunting/delete/" + noAnggaran,
        success: function (response) {
            $('#deleteDialog').modal('hide');
            showSuccess();
            table.ajax.reload();
        },
        statusCode: {
            201: function () {
                console.log('Deleted');
                $('#deleteDialog').modal('hide');
                showSuccess();
                table.ajax.reload();

            },
            400: function () {
//                alert('Inputan tidak boleh kosong');
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
//                alert('Not Found');
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function formReset() {
    $("#txtMataAnggaran").val("");
    $("#txtIdMataAnggaran").val("");
    $("#txtNoAt").val("");
    $("#txtRealisasi").val("");
    $("#txtPerkiraanRelasi").val("");
    $("#txtAnggaranXX").val("");
    $("#txtAnggaranZZ").val("");
    $("#txtProyeksi1").val("");
    $("#txtProyeksi2").val("");
    // $("#uploadFile").val("");
    $("#dataExcel").hide();
    $("#btnReupload").hide();

    setDefaultValue();
}

function setAnggaranDialogValue(data) {
    console.log(data, "data dialog");
    // $("#tahunBuku").val(data.tahunBuku.kodeTahunBuku);
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

function getValue() {
    var noAnggaran = $("#txtNoAt").val();
    var idRekening = {
        "idRekening": $("#txtIdMataAnggaran").val(),
        "kodeRekening": $("#txtMataAnggaran").val()
    };
    var tahunBuku = {
        "kodeTahunBuku": $('#tahunBuku').val()
    };
    var kodePeriode = {
        "kodePeriode": $("#cbPeriode").val()
    };
    var anggaranLalu = $("#txtAnggaranXX").val();
    var realisasi = $("#txtRealisasi").val();
    var perkiraan = $("#txtPerkiraanRelasi").val();
    var totalAnggaran = $("#txtAnggaranZZ").val();
    var proyeksi1 = $("#txtProyeksi1").val();
    var proyeksi2 = $("#txtProyeksi2").val();

    var obj = {
        "noAnggaran": noAnggaran,
        "idRekening": idRekening,
        "tahunBuku": tahunBuku,
        "kodePeriode": kodePeriode,
        "anggaranLalu": anggaranLalu.replace(/,/g, ""),
        "realisasi": realisasi.replace(/,/g, ""),
        "perkiraan": perkiraan.replace(/,/g, ""),
        "totalAnggaran": totalAnggaran.replace(/,/g, ""),
        "proyeksi1": proyeksi1.replace(/,/g, ""),
        "proyeksi2": proyeksi2.replace(/,/g, ""),
        "fileLampiran": fileLampiran
    };
    return obj;
}

function save(table, statusData) {
    var obj = getValue();
    console.log(obj, "object save");
    if (isValid()) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/anggaran/anggaran-akunting/save?statusData=" + statusData,
            success: function (response) {
                $('#newAnggaranDialog').modal('hide');
                showSuccess();
                table.ajax.reload();
            },
            statusCode: {
                201: function () {
                    // console.log('Saved');
                    $('#newAnggaranDialog').modal('hide');
                    showSuccess();
                    table.ajax.reload();
                },
                400: function () {
                    showError("Inputan tidak boleh kosong");
                },
                404: function () {
                    showError("Not Found");
                },
                500: function () {
                    showError("Internal Server Error");
                }
            }, complete: function (resp) {
                simpanBtnPressed(false);
                submitBtnPressed(false);
                disabledButton(false);
            }
        });
    }
}

function createExcelFile() {
    if (!rekeningSelected) {
        return;
    }
    var data = new FormData();
    $.ajax({
        url: _baseUrl + '/akunting/anggaran/anggaran-akunting/create-excel/'
            + rekeningSelected.kodeRekening + '?tahunAnggaran=' + $("#tahunBuku").find("option:selected").text()
            + '&kdThnBuku=' + $("#tahunBuku").find("option:selected").val()
            + '&idRekening=' + rekeningSelected.idRekening,
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        method: 'POST',
        type: 'POST',
        success: function (resp) {
            $("#dataExcel").show();
            console.log('Response Create File Excel');
            console.log(resp);
            showExcel(resp);
        },
        statusCode: {
            404: function () {
                showError("Failed Create Excel" + rekeningSelected.kodeRekening);
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

// function upload() {
//     var data = new FormData();
//     jQuery.each(jQuery('#uploadFile')[0].files, function (i, file) {
//         data.append('file', file);
//         data.append('kodeRekening', rekeningSelected ? rekeningSelected.kodeRekening : null);
//         data.append('tahunAnggaran', null);
//     });
//
//     $.ajax({
//         url: _baseUrl + '/akunting/anggaran/anggaran-akunting/upload',
//         data: data,
//         cache: false,
//         contentType: false,
//         processData: false,
//         method: 'POST',
//         type: 'POST', // For jQuery < 1.9
//         success: function (resp) {
//             $("#dataExcel").show();
//             showExcel(resp);
//         },
//         statusCode: {
//             400: function () {
//                 showError("File upload failed");
//             },
//             404: function () {
//                 showError("File upload failed");
//             },
//             500: function () {
//                 showError("File upload failed");
//             }
//         }
//     });
// }

function reupload(data) {
    var obj = {
        "filePath": fileLampiran,
        "listData": data
    };
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + '/akunting/anggaran/anggaran-akunting/reupload',
        success: function (resp) {
            showSuccess();
        },
        statusCode: {
            400: function () {
                showError("File upload failed");
            },
            404: function () {
                showError("File upload failed");
            },
            500: function () {
                showError("File upload failed");
            }
        },
        complete: function () {
            simpanBtnPressed(false);
            submitBtnPressed(false);
            disabledButton(false);
        }
    });
}

function saveAnggaran(data, table) {
    findAnggaranByIdRekening(data, table, "save");
}

function serviceSaveAnggaran(data, table) {
    console.log("service save");
    var obj = {
        "filePath": fileLampiran,
        "listData": data
    };
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + '/akunting/anggaran/anggaran-akunting/reupload',
        success: function (resp) {
            if (statusAksi === "create") save(table, "DRAFT");
            else if (statusAksi === "edit") update(table, "DRAFT");
        },
        statusCode: {
            400: function () {
                showError("File upload failed");
            },
            404: function () {
                showError("File upload failed");
            },
            500: function () {
                showError("File upload failed");
            }
        }
    });
}

function findAnggaranByIdRekening(data, table, action) {
    // console.log(rekeningSelected.idRekening);
    return $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/anggaran/anggaran-akunting/findByIdRekening/${rekeningSelected.idRekening}`,
        success: function (resp, textStatus, xhr) {
            if (xhr.status === 200) {
                console.log(resp);
                if (statusAksi === "edit" || resp.statusData === 'APPROVE') {
                    if (action === "save") serviceSaveAnggaran(data, table);
                    else if (action === "submit") serviceSubmitAnggaran(data, table);
                } else {
                    showWarning(`Mata Anggaran : ${resp.idRekening.namaRekening} sudah ada dan belum divalidasi!`);
                    simpanBtnPressed(false);
                    submitBtnPressed(false);
                    disabledButton(false);
                }
            } else if (xhr.status === 204) {
                if (action === "save") serviceSaveAnggaran(data, table);
                else if (action === "submit") serviceSubmitAnggaran(data, table);
            }
        }, statusCode: {
            200: function (resp) {
                console.log(resp);
                if (statusAksi === "edit" || resp.statusData === 'APPROVE') {
                    if (action === "save") serviceSaveAnggaran(data, table);
                    else if (action === "submit") serviceSubmitAnggaran(data, table);
                } else {
                    showWarning(`Mata Anggaran : ${resp.idRekening.namaRekening} sudah ada dan belum divalidasi!`);
                    simpanBtnPressed(false);
                    submitBtnPressed(false);
                    disabledButton(false);
                }
            },
            204: function () {
                if (action === "save") serviceSaveAnggaran(data, table);
                else if (action === "submit") serviceSubmitAnggaran(data, table);
            }
        }
    });
}

function submitAnggaran(data, table) {
    findAnggaranByIdRekening(data, table, "submit");
}

function serviceSubmitAnggaran(data, table) {
    console.log("service submit");
    var obj = {
        "filePath": fileLampiran,
        "listData": data
    };
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + '/akunting/anggaran/anggaran-akunting/reupload',
        success: function (resp) {
            console.log(resp, 'reps reupload');
            if (statusAksi == "create") save(table, "SUBMIT");
            else if (statusAksi == "edit") update(table, "SUBMIT");
        },
        statusCode: {
            400: function () {
                showError("File upload failed");
            },
            404: function () {
                showError("File upload failed");
            },
            500: function () {
                showError("File upload failed");
            }
        }
    });
}

function excelJsonSatker(rekening, tahunBuku) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/anggaran/anggaran-akunting/excel-json" +
            "?idRekening=" + rekening.idRekening +
            "&kodeTahunBuku=" + tahunBuku.kodeTahunBuku,
        success: function (resp) {
            $("#dataExcel").show();
            // $("#btnReupload").show();
            showExcelSatker(resp);
        }
    });
}

function excelJson(filePath) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/anggaran/anggaran-akunting/excel-json/file-path?filePath=" + filePath,
        success: function (resp) {
            $("#dataExcel").show();
            // $("#btnReupload").show();
            showExcel(resp);
        }
    });
}

function showExcel(resp) {
    var dataList = [];
    if (resp !== undefined)
        dataList = resp.listData;
    if (dataList.length <= 1) {
        dataList = [
            ['ANGGARAN TAHUNAN', ''],
            [$("#tahunBuku").find("option:selected").text(), ''],
            ['ANGGARAN PENGELUARAN', ''],
            [$("#txtMataAnggaran").val(), ''],
            ['', ''],
            [$("#lbRealisasi").html(), $("#txtRealisasi").val()],
            [$("#lbPerikraanRelasi").html(), $("#txtPerkiraanRelasi").val()],
            [$("#lbAnggaranXX").html(), $("#txtAnggaranXX").val()],
            [$("#lbAnggaranZZ").html(), $("#txtAnggaranZZ").val()]
        ];
    }
    hot = new Handsontable(document.getElementById('dataExcel'), {
        data: dataList,
        rowHeaders: true,
        colHeaders: true,
        width: '100%',
        height: 300,
        startRows: 10,
        startCols: 10,
        minSpareCols: 20,
        minSpareRows: 1,
        contextMenu: true,
        formulas: true,
        manualColumnResize: true,
        manualRowResize: true,
        mergeCells: true,
        cell: [
            {row: 0, col: 0, className: "htCenter htMiddle"},
            {row: 1, col: 0, className: "htCenter htMiddle"},
            {row: 2, col: 0, className: "htCenter htMiddle"},
            {row: 3, col: 0, className: "htCenter htMiddle"},
            {row: 5, col: 1, className: "htRight htMiddle"},
            {row: 6, col: 1, className: "htRight htMiddle"},
            {row: 7, col: 1, className: "htRight htMiddle"},
            {row: 8, col: 1, className: "htRight htMiddle"}
        ],
        licenseKey: 'non-commercial-and-evaluation',
        // cells: function (row, col, prop) {
        //     var cellProperties = {};
        //     // if (row === 0 && col === 0) {
        //     //     cellProperties.readOnly = true;
        //     // }
        //     return cellProperties;
        // },
        afterChange: function (changes, source) {
            if (!changes) {
                return;
            }
            $.each(changes, function (index, element) {
                $("#txtAnggaranZZ").val(setMoneyFormat(hot.getData()[8][1]));
            });
        },

    });

    $("#btnReupload").click(function () {
        $("#txtAnggaranZZ").val(setMoneyFormat(hot.getData()[8][1]));
        reupload(hot.getData());
    });

    var exportPlugin = hot.getPlugin('exportFile');
    $("#btnDownload").click(function () {
        exportPlugin.downloadFile('csv', {
            bom: false,
            columnDelimiter: ',',
            columnHeaders: false,
            exportHiddenColumns: true,
            exportHiddenRows: true,
            fileExtension: 'csv',
            filename: $("#txtMataAnggaran").val() + ' [YYYY]-[MM]-[DD]',
            mimeType: 'text/csv',
            rowDelimiter: '\r\n',
            rowHeaders: true
        });
    });

    fileLampiran = resp.filePath;
    if (hot.getData()[8][1] != null) {
        $("#txtAnggaranZZ").val(setMoneyFormat(hot.getData()[8][1]));
    }

    // $("#btnReupload").show();
    $("#btnDownload").show();

    $("#btnAmbilRelasi").click(function () {
        getDataRelasi(hot);
    });

    $("#btnAmbilDataAT").click(function () {
        getDataAT(hot);
    });

    $("#txtPerkiraanRelasi").change(function () {
        hot.setDataAtCell(6, 1, $("#txtPerkiraanRelasi").val());
    });
}

function showExcelSatker(resp) {
    var dataList = [];
    if (resp !== undefined)
        dataList = resp.listData;
    hotSatker = new Handsontable(document.getElementById('dataExcelSatker'), {
        data: dataList,
        rowHeaders: true,
        colHeaders: true,
        width: '100%',
        height: 300,
        startRows: 10,
        startCols: 10,
        minSpareCols: 20,
        minSpareRows: 1,
        contextMenu: true,
        formulas: true,
        manualColumnResize: true,
        manualRowResize: true,
        mergeCells: true,
        cell: [
            {row: 0, col: 0, className: "htCenter htMiddle"},
            {row: 1, col: 0, className: "htCenter htMiddle"},
            {row: 2, col: 0, className: "htCenter htMiddle"},
            {row: 3, col: 0, className: "htCenter htMiddle"},
            {row: 4, col: 0, className: "htCenter htMiddle"},
            {row: 5, col: 1, className: "htRight htMiddle"},
            {row: 6, col: 1, className: "htRight htMiddle"},
            {row: 7, col: 1, className: "htRight htMiddle"},
            {row: 8, col: 1, className: "htRight htMiddle"},
            {row: 9, col: 1, className: "htRight htMiddle"}
        ],
        licenseKey: 'non-commercial-and-evaluation'
    });

    // fileLampiran = resp.filePath;
}

function isValid() {
    if ($("#txtMataAnggaran").val() === "") {
        showError("Pilih Mata Anggaran");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    // if ($("#txtNoAt").val() == "") {
    //     showError("No AT Tidak Boleh Kosong");
    //     return false;
    // }

    if ($("#txtRealisasi").val() === "") {
        showError("Relasi Tidak Boleh Kosong");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    if ($("#txtPerkiraanRelasi").val() === "") {
        showError("Perkiraan Relasi Tidak Boleh Kosong");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    if ($("#txtAnggaranXX").val() === "") {
        showError("Anggaran Berjalan Tidak Boleh Kosong");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    if ($("#txtAnggaranZZ").val() === "") {
        showError("Anggaran Tahun Berikut Tidak Boleh Kosong");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    if ($("#txtProyeksi1").val() === "") {
        showError("Proyeksi Tidak Boleh Kosong");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    if ($("#txtProyeksi2").val() === "") {
        showError("Proyeksi Tidak Boleh Kosong");
        simpanBtnPressed(false);
        submitBtnPressed(false);
        disabledButton(false);
        return false;
    }

    // if ($("#uploadFile").val() == "") {
    //     showError("Pilih Detail Anggaran");
    //     return false;
    // }

    return true;
}

function setReadonly(value) {
    $("#txtRealisasi").attr('readonly', value);
    $("#txtAnggaranXX").attr('readonly', value);
    $("#txtAnggaranZZ").attr('readonly', value);
}

function refreshForm() {
    $("#txtNoAt").val("");
    $("#txtIdMataAnggaran").val("");
    $("#txtMataAnggaran").val("");
    $('#tahunBuku').val("");
    $("#txtAnggaranXX").val("");
    $("#txtRealisasi").val("");
    $("#txtPerkiraanRelasi").val("");
    $("#txtAnggaranZZ").val("");
    $("#txtProyeksi1").val("");
    $("#txtProyeksi2").val("");
    setReadonly(true);
}
