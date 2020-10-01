var tableRekening;
var dataRekening;
var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];
jQuery(document).ready(function () {

    var tableSerap = $("#tblSerap").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/serap/getSerapDTODatatables",
            type: "POST",
            data: function (d) {
                var serap = {};
                serap.statusData = "";
                d.serap = serap;
                return JSON.stringify(d);
            }
        },
        order: [ 1, "asc" ],
//        lengthMenu: [ 10, 25, 50, 100 ],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>'+
              '<option value="10">10</option>'+
              '<option value="20">20</option>'+
              '<option value="30">30</option>'+
              '<option value="40">40</option>'+
              '<option value="50">50</option>'+
              '<option value="-1">All</option>'+
            '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        },
        serverSide: true,
        columns: [
            {
               title: 'No.',
               data: 'noSerap',
               orderable: false,
               className: 'text-center',
               render: function (data, type, row, meta) {
                   return meta.row + 1;
               }
            },
            {
               title: 'No. Serap',
               data: 'noSerap',
               className: 'text-center',
            },
            {
               title: 'Tahun Buku',
               data: 'tahunBuku.tahun',
               className: '',
               orderable: false,
            },
            {
               title: 'Periode',
               data: 'kodePeriode',
               className: '',
               orderable: false,
            },
            {
               title: 'Tanggal',
               data: 'tglSerap',
               className: '',
               orderable: false,
               render: function (data) {
                    var master = data.split("T")[0].split("-");
                    return master[2] + "-" + master[1] + "-" + master[0];
               }
            },
            {
               title: 'Keterangan',
               data: 'keteranganDebet',
               className: '',
               orderable: false,
            },
            {
               title: 'Total Transaksi',
               data: 'totalTransaksi',
               className: 'text-right',
               orderable: false,
               render: function (data) {
                    return formatMoney(data, "", 2);
               }
            },
            {
               title: 'Tgl. Input',
               data: 'createdDate',
               className: '',
               orderable: false,
               render: function (data) {
                   var master = data.split("T")[0].split("-");
                   return master[2] + "-" + master[1] + "-" + master[0];
               }
            },
            {
               title: 'User Input',
               data: 'createdBy',
               className: '',
               orderable: false,
            },
            {
               title: 'Status Data',
               data: 'statusData',
               className: '',
               orderable: false,
            },
            {
                title: 'Tindakan',
                data: null,
                searchable: false,
                sortable: false,
                className: 'text-center',
                width: '10%',
//                "defaultContent": getButtonGroup(true, true, false)
                render: function(data) {
                    if((data.statusData == 'DRAFT') || (data.statusData == 'REJECT')) {
                        return getButtonGroup(true, true, false);
                    } else if (data.statusData == 'APPROVE') {
                        return getButtonGroup(false, false, true, true, "PDF", "la la-file-pdf-o", true, "EXCEL", "la la-file-excel-o");
                    } else {
                        return getButtonGroup(false, false, true);
                    }
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    initEvent(tableSerap);
    getPengaturanSistem();
    getRekeningDatatables();
    idRekeningValueCheck();
});

function initEvent(table) {
    $(document).find('.child-modal').on('hidden.bs.modal', function() {
        $('body').addClass('modal-open');
    });

    $("#addButton").on("click", function () {
        formReset();
        getPengaturanSistem();
        generateNoSerap();
    });

    $("#saveSerapButton").on("click", function () {
        var value = $("#statusModal").val();

        if(checkTotalDebitKredit() == false) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Total Debit dan Total Kredit tidak sama.",
                showConfirmButton: !1,
                timer: 1500
            });
        } else if(kreditValidationLoop() == false) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Kredit tidak boleh melebihi Saldo Anggaran.",
                showConfirmButton: !1,
                timer: 1500
            });
        } else {
            if(value == "update") {
                update(table, "DRAFT");
    //            console.log('Update');
            } else {
    //            console.log('Save');
                save(table, "DRAFT");
            }
        }
    });

    $("#submitSerapButton").on("click", function () {
        var value = $("#statusModal").val();

        if(checkTotalDebitKredit() == false) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Total Debit dan Total Kredit tidak sama.",
                showConfirmButton: !1,
                timer: 1500
            });
        } else if(kreditValidationLoop() == false) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Kredit tidak boleh melebihi Saldo Anggaran.",
                showConfirmButton: !1,
                timer: 1500
            });
        } else {
            if(value == "update") {
                update(table, "SUBMIT");
            } else {
                save(table, "SUBMIT");
            }
        }
    });

    $("#addRekening").click(function () {
//        console.log(dataRekening);
        if(duplicateRekeningCheck(dataRekening) == false) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Rekening sudah dipilih sebelumnya.",
                showConfirmButton: !1,
                timer: 1500
            });
            return;
        }

        if(dataRekening.saldoCurrent.saldoAnggaran == null) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Saldo Anggaran Rekening belum ditentukan.",
                showConfirmButton: !1,
                timer: 1500
            });
            return;
        }

        var markup = "<tr class='perpindahan-rekening rekening-row'>"+
                        "<td>"+
                            "<input type='text' class='form-control' value='"+dataRekening.idRekening+"' hidden>"+
                            "<input type='text' class='form-control' value='"+dataRekening.namaRekening+"' readonly>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' value='"+formatMoney(dataRekening.saldoCurrent.saldoAnggaran, "", 2)+"' style='text-align: right' readonly>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' onkeyup='getTotalDebitKredit(); generateTotalTransaksi(); currencyWhileTyping($(this))' value='0' style='text-align: right' onkeypress='onlyNumberKey(event)' onfocusout='setFieldToZero($(this))'>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' onkeyup='getTotalDebitKredit(); kreditValidation($(this)); generateTotalTransaksi(); currencyWhileTyping($(this))' value='0' style='text-align: right' onkeypress='onlyNumberKey(event)' onfocusout='setFieldToZero($(this))'>"+
                        "</td>"+
                        "<td>"+
                            "<button type='button' class='btn btn-danger delete-row' style='height: 27.59px'>Hapus</button>"+
                        "</td>"+
                        "<td>"+
                            "<input class='rekening-created-date' type='text' class='form-control' value='' hidden>"+
                        "</td>"+
                        "<td>"+
                            "<input class='rekening-created-by' type='text' class='form-control' value='' hidden>"+
                        "</td>"+
                        "<td>"+
                            "<input class='serap-detail-id' type='text' class='form-control' value='' hidden>"+
                        "</td>"+
                     "</tr>";
        $("#tblPerpindahan tbody").append(markup);
        $("#noContent").prop("hidden", true);
        $("#totalDebitKredit").prop("hidden", false);
    });

    $("#tblPerpindahan").on("click", ".delete-row", function() {
        $(this).closest("tr").remove();
        getTotalDebitKredit();
        generateTotalTransaksi();
    });

    $("#tblSerap tbody").on("click", ".delete-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#delete-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteButton").off().on("click", function (e) {
            var obj = {
                "noSerap": data.noSerap
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/transaksi/serap/delete",
                success: function (resp) {
                    $("#delete-form").modal("hide");
                    showWarning();
                    table.ajax.reload();
                }
            });
        });
    });

    $("#tblSerap tbody").on("click", ".edit-button", function () {
        var data = table.row($(this).parents("tr")).data();
        formReset();
        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/serap/" + data.noSerap + "/findById",
            success: function (resp) {
//                console.log(resp);
                $("#noSerap").val(resp.noSerap).prop("disabled", true);
                $("#tanggal").val(timestampToDate(resp.tglSerap));
                $("#idTahunBuku").val(resp.tahunBuku.kodeTahunBuku);
                $("#tahunBuku").val(resp.tahunBuku.tahun);
                $("#totalTransaksi").val(formatMoney(resp.totalTransaksi, "", 2));
                $("#idPeriode").val(resp.periode.kodePeriode);
                $("#periode").val(resp.periode.namaPeriode);
                $("#keteranganDebit").val(resp.keteranganDebet);
                $("#keteranganKredit").val(resp.keteranganKredit);
                $("#createdDate").val(resp.createdDate);
                $("#createdBy").val(resp.createdBy);
                $("#statusData").val(resp.statusData);
                setSerapDetail(resp.serapDetail);
            }
        });

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblSerap tbody").on("click", ".detail-button", function () {
        var data = table.row($(this).parents("tr")).data();
        formReset();
        $(".for-detail").prop("hidden", true);

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/serap/" + data.noSerap + "/findById",
            success: function (resp) {
                $("#noSerap").val(resp.noSerap).prop("disabled", true);
                $("#tanggal").val(timestampToDate(resp.tglSerap));
                $("#idTahunBuku").val(resp.tahunBuku.kodeTahunBuku);
                $("#tahunBuku").val(resp.tahunBuku.tahun);
                $("#totalTransaksi").val(formatMoney(resp.totalTransaksi, "", 2));
                $("#idPeriode").val(resp.periode.kodePeriode);
                $("#periode").val(resp.periode.namaPeriode);
                $("#keteranganDebit").val(resp.keteranganDebet);
                $("#keteranganKredit").val(resp.keteranganKredit);
                $("#createdDate").val(resp.createdDate);
                $("#createdBy").val(resp.createdBy);
                $("#statusData").val(resp.statusData);
                setSerapDetail(resp.serapDetail);
                $(".for-detail").prop("hidden", true);
                $(".read-detail").prop("readonly", true);
            }
        });

        $("#okButton").prop("hidden", false);

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#okButton").on("click", function () {
        setTimeout(function () {
            $(".for-detail").prop("hidden", false);
            $("#okButton").prop("hidden", true);
            $(".read-detail").prop("readonly", false);
        }, 500);
    });

    $("#tblSerap tbody").on("click", ".additional1-button", function () {
        var data = table.row($(this).parents("tr")).data();
        console.log(data);
        exportPDF(data.noSerap);
    });

    $("#tblSerap tbody").on("click", ".additional2-button", function () {
        var data = table.row($(this).parents("tr")).data();
        console.log(data);
        exportEXCEL(data.noSerap);
    });
}

function testButton() {
    $("#datetimepicker3").off().datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "dd/mm/yyyy"
    }, "show").on("change", function () {
        var months = ["JANUARI", "FEBRUARI", "MARET", "APRIL", "MEI", "JUNI", "JULI", "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER", "DESEMBER"];
        var selected = $(this)[0].children[0].value;
        var split = selected.split("/");
        var kodeTahunBuku = tahunBukuList.find(x => x.tahun === split[2]).kodeTahunBuku;
        var bulan = periodeList.find(x => x.kodePeriode === split[1]).namaPeriode;
        $("#tahunBuku").val(split[2]);
        $("#periode").val(bulan);
        $("#idTahunBuku").val(kodeTahunBuku);
        $("#idPeriode").val(split[1]);
    });
}

function getFormData() {
    var noSerap = $("#noSerap").val();
    var tanggal = $("#tanggal").val();
    var tahunBuku = $("#idTahunBuku").val();
    var totalTransaksi = getNumberRegEx($("#totalTransaksi").val());
    var periode = $("#idPeriode").val();
    var ketDebit = $("#keteranganDebit").val();
    var ketKredit = $("#keteranganKredit").val();
    var tglSplit = tanggal.split("/");
    var tglFinal = tglSplit[2] + "-" + tglSplit[1] + "-" + tglSplit[0];
    var createdDate = $("#createdDate").val();
    var createdBy = $("#createdBy").val();
    var statusData = $("#statusData").val();

    var arrayVal = new Array();
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var col0 = currentRow.find("td:eq(0) input").val();
        var col1 = currentRow.find("td:eq(1) input").val();
        var col2 = currentRow.find("td:eq(2) input").val();
        var col3 = currentRow.find("td:eq(3) input").val();
        var col5 = currentRow.find("td:eq(5) input").val();
        var col6 = currentRow.find("td:eq(6) input").val();
        var col7 = currentRow.find("td:eq(7) input").val();
        var obj = {
            "rekening": {
                "idRekening": col0
            },
            "saldoAnggaran": getNumberRegEx(col1),
            "jumlahPenambah": getNumberRegEx(col2),
            "jumlahPengurang": getNumberRegEx(col3),
            "createdDate": col5,
            "createdBy": col6,
            "idSerapDetail": col7
        };
        arrayVal.push(obj);
//        console.log(arrayVal);
    });

    var obj = {
        "noSerap": noSerap,
        "tglSerap": tglFinal.includes("undefined") ? "" : tglFinal,
        "tahunBuku": {
            "kodeTahunBuku": tahunBuku
        },
        "kodePeriode": periode,
        "keteranganDebet": ketDebit,
        "keteranganKredit": ketKredit,
        "totalTransaksi": totalTransaksi,
        "statusData": statusData,
        "createdDate": createdDate,
        "createdBy": createdBy,
        "serapDetail": arrayVal
    }
    return obj;
}

function save(table, value) {
    var obj = getFormData();
    obj.statusData = value;
//    console.log(obj);

    if(formValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    if(obj.serapDetail.length <= 1) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form Rekening belum terpenuhi.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    if(cekTotalTransaksi() == false) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Total Transaksi tidak terpenuhi.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/serap/save",
        success: function () {
        },
        statusCode: {
            201: function () {
                console.log('Saved');
                $("#add-form").modal("hide");
                showSuccess();
                table.ajax.reload();
            },
            400: function () {
                alert('Inputan tidak boleh kosong');
            },
            404: function () {
                alert('Not Found');
            }
        }
    });
}

function update(table, value) {
    var obj = getFormData();
    obj.statusData = value;

    if(formValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    if(obj.serapDetail.length <= 1) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form Rekening belum terpenuhi.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    if(cekTotalTransaksi() == false) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Total Transaksi tidak terpenuhi.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/serap/update",
        success: function () {
        },
        statusCode: {
            201: function () {
                console.log('Saved');
                $("#add-form").modal("hide");
                showSuccess();
                table.ajax.reload();
            },
            400: function () {
                alert('Inputan tidak boleh kosong');
            },
            404: function () {
                alert('Not Found');
            }
        }
    });
}

function getPengaturanSistem() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/findByCreatedDate",
        success: function (resp) {
//            console.log(resp);
            $("#tahunBuku").val(resp.kodeTahunBuku.tahun);
            $("#periode").val(resp.kodePeriode.namaPeriode);
            $("#idTahunBuku").val(resp.kodeTahunBuku.kodeTahunBuku);
            $("#idPeriode").val(resp.kodePeriode.kodePeriode);
        },
        statusCode: {

        }
    });
}

function formReset() {
    $("#noSerap").val("");
    $("#datetimepicker3").datepicker("destroy");
    $("#tanggal").val("");
//    $("#tahunBuku").val("");
    $("#totalTransaksi").val("");
//    $("#periode").val("");
    $("#keteranganDebit").val("");
    $("#keteranganKredit").val("");
    $("#idRekening").val("").selectpicker("refresh");
    $(".perpindahan-rekening").remove();
    dataRekening = "";
    tableRekening.$('tr.selected').removeClass('selected');
    idRekeningValueCheck();
    $("#totalDebit").val("0");
    $("#totalKredit").val("0");
    $("#noContent").prop("hidden", false);
    $("#totalDebitKredit").prop("hidden", true);
    $("#statusModal").val("");
//    getPengaturanSistem();
}

function getRekeningDatatables() {
    tableRekening = $("#tblRekening").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/serap/getSerapRekeningDatatables",
            type: "POST",
            data: function (d) {
                    var rekening = {
                        "tipeRekening": "",
                        "levelRekening": "6",
                        "isSummary": "N"
                    };
                    d.rekening = rekening;
                return JSON.stringify(d);
            }
        },
        order: [ 2, "asc" ],
        processing: true,
        language: {
            lengthMenu: 'Baris Data : <select>'+
              '<option value="10">10</option>'+
              '<option value="20">20</option>'+
              '<option value="30">30</option>'+
              '<option value="40">40</option>'+
              '<option value="50">50</option>'+
              '<option value="-1">All</option>'+
            '</select>',
            processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
        },
        serverSide: true,
        columns: [
            {
               title: 'No.',
               data: 'kodeRekening',
               orderable: false,
               className: 'text-center',
               render: function (data, type, row, meta) {
                   return meta.row + 1;
               }
            },
            {
               title: 'Level Rekening',
               data: 'levelRekening',
               className: 'text-right',
               orderable: false,
//               visible: false
            },
            {
               title: 'Kode Rekening',
               data: 'kodeRekening',
               className: 'text-center',
               orderable: false
            },
            {
               title: 'Nama Rekening',
               data: 'namaRekening',
               className: '',
               orderable: false,
            },
            {
               title: 'Saldo Anggaran',
               data: 'saldoCurrent.saldoAnggaran',
               className: 'text-right',
               orderable: false,
               render: function (data) {
                  if(data == null) {
                    return data;
                  } else {
                    return formatMoney(data, "Rp. ", 2);
                  }
               }
            },
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    $('#tblRekening tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
            if($(this).find("td:eq(1)").text() != 6) {
                return;
            }

            tableRekening.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var data = tableRekening.row(".selected").data();
            dataRekening = data;
            $("#idRekening").val(data.kodeRekening + " - " + data.namaRekening);
        }
    });

    $("#batalRekening").on("click", function () {
        tableRekening.$('tr.selected').removeClass('selected');
        dataRekening = "";
        $("#idRekening").val("");
        idRekeningValueCheck();
    });

    $("#pilihRekening").on("click", function () {
        idRekeningValueCheck();
        $("#rekening-form").modal("hide");
    });
}

function idRekeningValueCheck() {
    if($("#idRekening").val() == "") {
        $("#addRekening").prop("disabled", true);
    } else {
        $("#addRekening").prop("disabled", false);
    }
}

function getTotalDebitKredit() {
    var totalDebit = 0;
    var totalKredit = 0;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var col2 = currentRow.find("td:eq(2) input").val();
        var col3 = currentRow.find("td:eq(3) input").val();
        if(col2 == "") {
           col2 = "0";
        } else if(col3 == "") {
           col3 = "0";
        }
        var intCol2 = getNumberRegEx(col2);
        var intCol3 = getNumberRegEx(col3);

        totalDebit = totalDebit + parseFloat(intCol2);
        totalKredit = totalKredit + parseFloat(intCol3);
    });
    $("#totalDebit").val(formatMoney(totalDebit, "", 2));
    $("#totalKredit").val(formatMoney(totalKredit, "", 2));
}

function timestampToDate(data) {
    var master = data.split("T")[0].split("-");
    return master[2] + "/" + master[1] + "/" + master[0];
}

function setSerapDetail(data) {
    $(".perpindahan-rekening").remove();
    dataRekening = "";
    $("#idRekening").val("");
    tableRekening.$('tr.selected').removeClass('selected');
    idRekeningValueCheck();
    data.forEach(v => {
//        console.log(v);
        var markup = "<tr class='perpindahan-rekening rekening-row'>"+
                        "<td>"+
                            "<input type='text' class='form-control' value='"+v.rekening.idRekening+"' hidden>"+
                            "<input type='text' class='form-control' value='"+v.rekening.namaRekening+"' readonly>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' value='"+formatMoney(v.saldoAnggaran, "", 2)+"' style='text-align: right' readonly>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control read-detail' onkeyup='getTotalDebitKredit(); generateTotalTransaksi(); currencyWhileTyping($(this))' value='"+formatMoney(v.jumlahPenambah, "", 2)+"' style='text-align: right' onkeypress='onlyNumberKey(event)' onfocusout='setFieldToZero($(this))'>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control read-detail' onkeyup='getTotalDebitKredit(); kreditValidation($(this)); generateTotalTransaksi(); currencyWhileTyping($(this))' value='"+formatMoney(v.jumlahPengurang, "", 2)+"' style='text-align: right' onkeypress='onlyNumberKey(event)' onfocusout='setFieldToZero($(this))'>"+
                        "</td>"+
                        "<td class='for-detail'>"+
                            "<button type='button' class='btn btn-danger delete-row' style='height: 27.59px'>Hapus</button>"+
                        "</td>"+
                        "<td class='for-detail'>"+
                            "<input class='rekening-created-date' type='text' class='form-control' value='"+v.createdDate+"' hidden>"+
                        "</td>"+
                        "<td class='for-detail'>"+
                            "<input class='rekening-created-by' type='text' class='form-control' value='"+v.createdBy+"' hidden>"+
                        "</td>"+
                        "<td class='for-detail'>"+
                            "<input class='serap-detail-id' type='text' class='form-control' value='"+v.idSerapDetail+"' hidden>"+
                        "</td>"+
                     "</tr>";
        $("#tblPerpindahan tbody").append(markup);
        $("#noContent").prop("hidden", true);
        $("#totalDebitKredit").prop("hidden", false);
        kreditValidaitonCss($("#tblPerpindahan tr.perpindahan-rekening").last());
        getTotalDebitKredit();
    });

}

function checkTotalDebitKredit() {
    var totalDebit = getNumberRegEx($("#totalDebit").val());
    var totalKredit = getNumberRegEx($("#totalKredit").val());
    if(parseFloat(totalDebit) != parseFloat(totalKredit)) {
        return false;
    } else {
        return true
    }
}

function onlyNumberKey(evt) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57) && (ASCIICode != 44))
        evt.preventDefault();
//        return false;
//    return true;
}

function kreditValidation(event) {
    event.closest("tr").find("td:eq(3) input").css({borderColor: ""});
    var saldoAnggaran = getNumberRegEx(event.closest("tr").find("td:eq(1) input").val());
    var kredit = getNumberRegEx(event.closest("tr").find("td:eq(3) input").val());

    if(parseFloat(kredit) > parseFloat(saldoAnggaran)) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Saldo Anggaran tidak cukup.",
            showConfirmButton: !1,
            timer: 1500
        });
        event.closest("tr").find("td:eq(3) input").val("0");
        getTotalDebitKredit();
    }
//    console.log(saldoAnggaran, kredit);
}

function kreditValidaitonCss(value) {
    var saldoAnggaran = getNumberRegEx(value.find("td:eq(1) input").val());
    var kredit = getNumberRegEx(value.find("td:eq(3) input").val());

    if(parseFloat(kredit) > parseFloat(saldoAnggaran)) {
        value.find("td:eq(3) input").css({borderColor: "red"});
    }
}

function kreditValidationLoop() {
    var value = true;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var saldoAnggaran = getNumberRegEx(currentRow.find("td:eq(1) input").val());
        var kredit = getNumberRegEx(currentRow.find("td:eq(3) input").val());

        if(parseFloat(kredit) > parseFloat(saldoAnggaran)) {
            value = false;
            return value;
        } else {
            return true;
        }
    });
    return value;
}

function duplicateRekeningCheck(value) {
    var returnValue = true;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var idRekening = currentRow.find("td:eq(0) input").val();

        if(value.idRekening == idRekening) {
            returnValue = false;
            return returnValue;
        } else {
            return true;
        }
    });
    return returnValue;
}

function formValidation(obj) {
    var found = Object.keys(obj).filter(function(key) {
            if((key != "createdBy") && (key != "createdDate")) {
//                console.log(key);
                return obj[key] === "";
            }
    });
    if(found.length > 0) {
        return true;
    } else {
        return false;
    }
}

function formatMoney(amount, currency = "", decimalCount = 2, decimal = ",", thousands = ".") {
  try {
    decimalCount = Math.abs(decimalCount);
    decimalCount = isNaN(decimalCount) ? 2 : decimalCount;

    const negativeSign = amount < 0 ? "-" : "";

    let i = parseInt(amount = Math.abs(Number(amount) || 0).toFixed(decimalCount)).toString();
    let j = (i.length > 3) ? i.length % 3 : 0;

    if(currency == "")
        return negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");

    return currency + negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
  } catch (e) {
    console.log(e)
  }
}

function cekTotalTransaksi() {
    var returnValue = true;
    var totalTransaksi = getNumberRegEx($("#totalTransaksi").val());
    var totalDebit = getNumberRegEx($("#totalDebit").val());
    var totalKredit = getNumberRegEx($("#totalKredit").val());
    var avg = (parseFloat(totalDebit) + parseFloat(totalKredit)) / 2;

    if(avg != parseFloat(totalTransaksi)) {
        returnValue = false;
    }

    return returnValue;
}

function setFieldToZero(value) {
    if(value[0].value == "") {
//        console.log(value);
        value.val(0);
    }
}

function generateNoSerap() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/serap/getNoSerap",
        success: function (resp) {
            $("#noSerap").val(resp).prop("disabled", true);
        }
    });
}

function getDataRekening(value) {
    var idRekening = $("#idRekening").val();
//    console.log(idRekening);
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/api/akunting/parameter/rekening/findById/"+idRekening,
        success: function (resp) {
//            console.log(resp);
            dataRekening = resp;
            idRekeningValueCheck();
        }
    });
}

function generateTotalTransaksi() {
    var totalDebit = getNumberRegEx($("#totalDebit").val());
    var totalKredit = getNumberRegEx($("#totalKredit").val());
    var avg = (parseFloat(totalDebit) + parseFloat(totalKredit)) / 2;
    var finalString = formatMoney(avg, "", 2);
    $("#totalTransaksi").val(finalString);
}

function getNumberRegEx(value) {
    var res = value.replace(/\./g, "").replace(/\,/g, ".");
    return res;
}

function currencyWhileTyping(value) {
    value.val(function (index, value) {
        if (value != "") {
        //return '$' + value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            var decimalCount;
            value.match(/\,/g) === null ? decimalCount = 0 : decimalCount = value.match(/\,/g);

            if (decimalCount.length > 1) {
                value = value.slice(0, -1);
            }

            var components = value.toString().split(",");
            if (components.length === 1)
                components[0] = value;
            components[0] = components[0].replace(/\D/g, '').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
            if (components.length === 2) {
                components[1] = components[1].replace(/\D/g, '').replace(/^\d{3}$/, '');
            }

            if (components.join(',') != '')
                return components.join(',');
            else
                return '';
        }
    });
}

function exportPDF(noSerap) {
    var rawTanggal = moment(new Date).format("DD-MM-YYYY").split("-");
    var tanggal = rawTanggal[0] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[2];
    var obj = {
        "noSerap": noSerap,
        "tanggal": tanggal
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/serap/export-pdf",
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "Serap " + tanggal + ".pdf";
            link.target = "_blank";
//            PDFObject.embed(link.href);

            var byteCharacters = atob(response);
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var file = new Blob([byteArray], { type: 'application/pdf;base64' });
            var fileURL = URL.createObjectURL(file);
            window.open(fileURL);

//            document.body.appendChild(link);
//            link.click();
//            document.body.removeChild(link);
        },
        statusCode: {
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function() {
                showError("Internal Server Error");
            }
        }
    });
}

function exportEXCEL(noSerap) {
    var rawTanggal = moment(new Date).format("DD-MM-YYYY").split("-");
    var tanggal = rawTanggal[0] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[2];
    var obj = {
        "noSerap": noSerap,
        "tanggal": tanggal
    };

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/transaksi/serap/export-excel",
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "Serap " + tanggal + ".xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        statusCode: {
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function() {
                showError("Internal Server Error");
            }
        }
    });
}