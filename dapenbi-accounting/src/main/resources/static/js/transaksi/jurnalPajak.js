var tableRekening;
var dataRekening;
var transaksiJurnal;
jQuery(document).ready(function () {

    var tableSerap = $("#tblJurnalPajak").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/jurnal-pajak/getJurnalPajakDatatables",
            type: "POST",
            data: function (d) {
                var warkat = {
                    "kodeTransaksi": {},
                    "jenisWarkat": "PAJAK",
                    "statusData": ""
                };
                warkat.kodeTransaksi.kodeTransaksi = "";
                d.warkat = warkat;
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
               data: 'noWarkat',
               orderable: false,
               className: 'text-center',
               render: function (data, type, row, meta) {
                   return meta.row + 1;
               }
            },
            {
               title: 'No. Transaksi',
               data: 'noWarkat',
               className: 'text-center',
            },
            {
               title: 'Transaksi',
               data: 'tglTransaksi',
               className: 'text-center',
               orderable: false,
               render: function (data) {
                   var master = data.split("T")[0].split("-");
                   return master[2] + "-" + master[1] + "-" + master[0];
               }
            },
            {
               title: 'Keterangan',
               data: 'keterangan',
               className: '',
               orderable: false,
            },
            {
               title: 'Total Transaksi',
               data: 'totalTransaksi',
               className: 'text-right',
               orderable: false,
               render: function (data) {
                   return formatMoney(data, "Rp. ", 2);
               }
            },
            {
               title: 'Tgl. Input',
               data: 'createdDate',
               className: 'text-center',
               orderable: false,
               render: function (data) {
                   var master = data.split("T")[0].split("-");
                   return master[2] + "-" + master[1] + "-" + master[0];
               }
            },
            {
               title: 'Nama Pemakai',
               data: 'createdBy',
               className: '',
               orderable: false,
            },
            {
               title: 'Status Data',
               data: 'statusData',
               className: '',
               orderable: false
            },
            {
                title: 'Tindakan',
                data: null,
                searchable: false,
                sortable: false,
                className: 'text-center',
//                "defaultContent": getButtonGroup(true, true, false)
                render: function(data) {
                    if(data.statusData == 'REJECT-JP') {
                        return getButtonGroup(false, true, false);
                    } else if(data.statusData == 'DRAFT') {
                        return getButtonGroup(true, true, false);
                    } else {
                        return getButtonGroup(false, false, false)
                    }
                }
            }
        ],
        fnRowCallback : function (nRow, aData, iDisplayIndex, iDisplayIndexFull, oSettings) {
            var info = this.api().page.info();
            var page = info.page;
            var length = info.length;
            var index = (page * length + (iDisplayIndex + 1));
            $('td:eq(0)', nRow).html(index);

            return nRow;
        },
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    initEvent(tableSerap);
//    getPengaturanSistem();
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

        if(dataRekening.saldoCurrent.saldoAwal == null) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Saldo Awal Rekening belum ditentukan.",
                showConfirmButton: !1,
                timer: 1500
            });
            return;
        }

        var markup = "<tr class='perpindahan-rekening rekening-row'>"+
                        "<td>"+
                            "<input type='text' class='form-control' style='text-align: center'>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' value='"+dataRekening.idRekening+"' hidden>"+
                            "<input type='text' class='form-control' value='"+dataRekening.namaRekening+"' readonly>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input type='text' class='form-control' value='"+dataRekening.saldoCurrent.saldoAkhir+"' style='text-align: right' readonly>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' onkeyup='getTotalDebitKredit(); generateTotalTransaksi()' value='0' style='text-align: right' onkeypress='onlyNumberKey(event)'>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' onkeyup='getTotalDebitKredit(); generateTotalTransaksi()' value='0' style='text-align: right' onkeypress='onlyNumberKey(event)'>"+
                        "</td>"+
                        "<td>"+
                            "<button type='button' class='btn btn-danger delete-row' style='height: 27.59px'>Hapus</button>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input class='rekening-created-date' type='text' class='form-control' value='' hidden>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input class='rekening-created-by' type='text' class='form-control' value='' hidden>"+
                        "</td>"+
                        "<td hidden>"+
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
    });

    $("#tblJurnalPajak tbody").on("click", ".delete-button", function () {
        var data = table.row($(this).parents("tr")).data();

        $("#delete-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#deleteButton").off().on("click", function (e) {
            var obj = {
                "noWarkat": data.noWarkat
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/transaksi/jurnal-pajak/delete",
                success: function (resp) {
                    $("#delete-form").modal("hide");
                    showWarning();
                    table.ajax.reload();
                }
            });
        });
    });

    $("#tblJurnalPajak tbody").on("click", ".edit-button", function () {
        var data = table.row($(this).parents("tr")).data();
        formReset();
        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/jurnal-pajak/findById?id=" + data.noWarkat,
            success: function (resp) {
//                console.log(resp);
                $("#noTransaksi").val(resp.noWarkat);
                $("#tglTransaksi").val(timestampToDate(resp.tglTransaksi));
                $("#tglBuku").val(timestampToDate(resp.tglBuku));
                $("#totalTransaksi").val(formatMoney(resp.totalTransaksi, ""));
                $("#keterangan").val(resp.keterangan);
                $("#createdDate").val(resp.createdDate);
                $("#createdBy").val(resp.createdBy);
                $("#statusData").val(resp.statusData);
                setSerapDetail(resp.warkatJurnals);
            }
        });

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });
}

function testButton() {
    $("#datetimepicker3").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "dd/mm/yyyy"
    }, "show");
}

function tglBuku() {
    $("#tglBukuDate").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "dd/mm/yyyy"
    }, "show");
}

function getFormData() {
    var jenisTransaksi = $("#jenisTransaksi").val();
    var noWarkat = $("#noTransaksi").val();
    var tanggalTransaksi = $("#tglTransaksi").val();
    var tanggalBuku = $("#tglBuku").val();
    var totalTransaksi = getNumberRegEx($("#totalTransaksi").val());
    var keterangan = $("#keterangan").val();
    var tglTransaksiSplit = tanggalTransaksi.split("/");
    var tglTransaksiFinal = tglTransaksiSplit[2] + "-" + tglTransaksiSplit[1] + "-" + tglTransaksiSplit[0];
    var tglBukuSplit = tanggalBuku.split("/");
    var tglBukuFinal = tglBukuSplit[2] + "-" + tglBukuSplit[1] + "-" + tglBukuSplit[0];
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
        var col4 = currentRow.find("td:eq(4) input").val();
        var col6 = currentRow.find("td:eq(6) input").val();
        var col7 = currentRow.find("td:eq(7) input").val();
        var col8 = currentRow.find("td:eq(8) input").val();
        var obj = {
            "idRekening": {
                "idRekening": col1
            },
            "noUrut": col0,
            "jumlahDebit": col3,
            "jumlahKredit": col4,
            "createdDate": col6,
            "createdBy": col7,
            "idWarkatJurnal": col8
        };
        arrayVal.push(obj);
//        console.log(arrayVal);
    });

    var obj = {
        "kodeTransaksi": jenisTransaksi,
        "noWarkat": noWarkat,
        "tglTransaksi": tglTransaksiFinal.includes("undefined") ? "" : tglTransaksiFinal,
        "tglBuku": tglBukuFinal.includes("undefined") ? "" : tglBukuFinal,
        "keterangan": keterangan,
        "totalTransaksi": totalTransaksi,
        "jenisWarkat": "PAJAK",
        "statusData": statusData,
        "createdDate": createdDate,
        "createdBy": createdBy,
        "warkatJurnals": arrayVal
    }
    return obj;
}

function save(table, value) {
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

    if(obj.warkatJurnals.length == 0) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Rekening tidak boleh kosong.",
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

    if(warkatJurnalsValidation() == false) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form Perpindahan Rekening belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/api/akunting/transaksi/saldo/saldo-warkat/and/jurnals",
        success: function () {
        },
        statusCode: {
            200: function () {
//                console.log('Saved');
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

    if(obj.warkatJurnals.length == 0) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Rekening tidak boleh kosong.",
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
        url: _baseUrl + "/api/akunting/transaksi/saldo/saldo-warkat/and/jurnals",
        success: function () {
        },
        statusCode: {
            200: function () {
//                console.log('Saved');
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
    $("#noTransaksi").val("");
    $("#datetimepicker3").datepicker("destroy");
    $("#tglTransaksi").val("");
    $("#tglBukuDate").datepicker("destroy");
    $("#tglBuku").val("");
    $("#totalTransaksi").val("");
    $("#keterangan").val("");
    $("#idRekening").val("");
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
                    "levelRekening": "6"
                };
                d.rekening = rekening;
                return JSON.stringify(d);
            }
        },
        order: [ 1, "asc" ],
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
               data: 'idRekening',
               orderable: false,
               className: 'text-center',
               render: function (data, type, row, meta) {
                   return meta.row + 1;
               }
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
//            {
//               title: 'Saldo Normal',
//               data: 'saldoNormal',
//               className: 'text-center',
//               orderable: false,
//            },
            {
               title: 'Saldo Akhir',
               data: 'saldoCurrent.saldoAkhir',
               className: 'text-right',
               orderable: false,
               visible: false,
               render: function (data) {
                  if(data == null) {
                    return data;
                  } else {
                    return formatMoney(data, "Rp. ");
                  }
               }
            },
        ],
        fnRowCallback : function (nRow, aData, iDisplayIndex, iDisplayIndexFull, oSettings) {
            var info = this.api().page.info();
            var page = info.page;
            var length = info.length;
            var index = (page * length + (iDisplayIndex + 1));
            $('td:eq(0)', nRow).html(index);

            return nRow;
        },
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    $('#tblRekening tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
//            if($(this).find("td:eq(1)").text() != 6) {
//                return;
//            }

            tableRekening.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var data = tableRekening.row(".selected").data();
            dataRekening = data;
            transaksiJurnal = data;
            $("#idRekening").val(dataRekening.kodeRekening + " - " + dataRekening.namaRekening);
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
        var col2 = currentRow.find("td:eq(3) input").val();
        var col3 = currentRow.find("td:eq(4) input").val();
        if(col2 == "") {
           col2 = "0";
        } else if(col3 == "") {
           col3 = "0";
        }

        totalDebit = totalDebit + parseInt(col2);
        totalKredit = totalKredit + parseInt(col3);
    });
    $("#totalDebit").val(totalDebit);
    $("#totalKredit").val(totalKredit);
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
                            "<input type='text' class='form-control' value='"+v.noUrut+"' style='text-align: center'>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' value='"+v.idRekening.idRekening+"' hidden>"+
                            "<input type='text' class='form-control' value='"+v.idRekening.namaRekening+"' readonly>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input type='text' class='form-control' value='"+v.idRekening.saldoCurrent.saldoAkhir+"' style='text-align: right' readonly>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' onkeyup='getTotalDebitKredit(); generateTotalTransaksi()' value='"+v.jumlahDebit+"' style='text-align: right' onkeypress='onlyNumberKey(event)'>"+
                        "</td>"+
                        "<td>"+
                            "<input type='text' class='form-control' onkeyup='getTotalDebitKredit(); generateTotalTransaksi()' value='"+v.jumlahKredit+"' style='text-align: right' onkeypress='onlyNumberKey(event)'>"+
                        "</td>"+
                        "<td>"+
                            "<button type='button' class='btn btn-danger delete-row' style='height: 27.59px'>Hapus</button>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input class='rekening-created-date' type='text' class='form-control' value='"+v.createdDate+"' hidden>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input class='rekening-created-by' type='text' class='form-control' value='"+v.createdBy+"' hidden>"+
                        "</td>"+
                        "<td hidden>"+
                            "<input class='serap-detail-id' type='text' class='form-control' value='"+v.idWarkatJurnal+"' hidden>"+
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
    var totalDebit = $("#totalDebit").val();
    var totalKredit = $("#totalKredit").val();
    if(parseInt(totalDebit) != parseInt(totalKredit)) {
        return false;
    } else {
        return true
    }
}

function onlyNumberKey(evt) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57))
        evt.preventDefault();
//        return false;
//    return true;
}

function kreditValidation(event) {
    event.closest("tr").find("td:eq(4) input").css({borderColor: ""});
    var saldoAnggaran = event.closest("tr").find("td:eq(2) input").val();
    var kredit = event.closest("tr").find("td:eq(4) input").val();

    if(parseInt(kredit) > parseInt(saldoAnggaran)) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Saldo Anggaran tidak cukup.",
            showConfirmButton: !1,
            timer: 1500
        });
        event.closest("tr").find("td:eq(4) input").val("0");
        getTotalDebitKredit();
    }
//    console.log(saldoAnggaran, kredit);
}

function kreditValidaitonCss(value) {
    var saldoAnggaran = value.find("td:eq(2) input").val();
    var kredit = value.find("td:eq(4) input").val();

    if(parseInt(kredit) > parseInt(saldoAnggaran)) {
        value.find("td:eq(4) input").css({borderColor: "red"});
    }
}

function duplicateRekeningCheck(value) {
    var returnValue = true;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var idRekening = currentRow.find("td:eq(1) input").val();

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
//    console.log(obj);
    var found = Object.keys(obj).filter(function(key) {
            if((key != "createdBy") && (key != "createdDate") && (key != "noWarkat")) {
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

function formatMoney(amount, currency, decimalCount = 0, decimal = ",", thousands = ".") {
  try {
    decimalCount = Math.abs(decimalCount);
    decimalCount = isNaN(decimalCount) ? 2 : decimalCount;

    const negativeSign = amount < 0 ? "-" : "";

    let i = parseInt(amount = Math.abs(Number(amount) || 0).toFixed(decimalCount)).toString();
    let j = (i.length > 3) ? i.length % 3 : 0;

    if(currency == "") {
        return negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
    } else {
        return currency + negativeSign + (j ? i.substr(0, j) + thousands : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousands) + (decimalCount ? decimal + Math.abs(amount - i).toFixed(decimalCount).slice(2) : "");
    }
  } catch (e) {
    console.log(e)
  }
};

function cekTotalTransaksi() {
    var returnValue = true;
    var totalTransaksi = getNumberRegEx($("#totalTransaksi").val());
    var totalDebit = $("#totalDebit").val();
    var totalKredit = $("#totalKredit").val();
    var avg = (parseInt(totalDebit) + parseInt(totalKredit)) / 2;

    if(avg != parseInt(totalTransaksi)) {
        returnValue = false;
    }

    return returnValue;
}

function warkatJurnalsValidation() {
    var value = true;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var noUrut = currentRow.find("td:eq(0) input").val();
        var saldoAnggaran = currentRow.find("td:eq(2) input").val();
        var debit = currentRow.find("td:eq(3) input").val();
        var kredit = currentRow.find("td:eq(4) input").val();

        var obj = {
            "noUrut": noUrut,
            "saldoAnggaran": saldoAnggaran,
            "debit": debit,
            "kredit": kredit
        };

        var found = Object.keys(obj).filter(function(key) {
            return obj[key] === "";
        });

        if(found.length > 0) {
            value = false;
            return value;
        } else {
            return true;
        }
    });
    return value;
}

function generateTotalTransaksi() {
    var totalDebit = $("#totalDebit").val();
    var totalKredit = $("#totalKredit").val();
    var avg = (parseInt(totalDebit) + parseInt(totalKredit)) / 2;
    var finalString = formatMoney(avg, "");
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