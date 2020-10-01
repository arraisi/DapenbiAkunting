jQuery(document).ready(function () {

    var tableSerap = $("#tblJurnalPajak").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/jurnal-pajak/datatables",
            type: "POST",
            data: function (d) {
                d.jurnalPajakDTO =  {
                    "jenisWarkat": "PAJAK",
                    "statusData": "SUBMIT"
                };
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
                render: function(data) {
                    return "<a href='javascript:void(0);' class='validasi-button'>Validasi</a>";
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
});

function initEvent(table) {
    $("#tblJurnalPajak tbody").on("click", ".validasi-button", function () {
        var data = table.row($(this).parents("tr")).data();
        formReset();
        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/jurnal-pajak/findById?id=" + data.noWarkat,
            success: function (resp) {
                $("#noTransaksi").val(resp.noWarkat).prop("disabled", true);
                $("#tglTransaksi").val(timestampToDate(resp.tglTransaksi)).prop("disabled", true);
                $("#tglBuku").val(timestampToDate(resp.tglBuku)).prop("disabled", true);
                $("#totalTransaksi").val(formatMoney(resp.totalTransaksi, "", 2)).prop("disabled", true);
                $("#keterangan").val(resp.keterangan).prop("disabled", true);
                $("#createdDate").val(timestampToDate(resp.createdDate)).prop("disabled", true);
                $("#createdBy").val(resp.createdBy).prop("disabled", true);
                $("#statusData").val(resp.statusData).prop("disabled", true);
                setWarkatJurnal(resp.warkatJurnals);
            }
        });

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#setujuiJurnalPajak").on("click", function() {
        var noWarkat = $("#noTransaksi").val();
        var aktivitas = "APPROVE-FA";
        var keterangan = $("#keterangan").val();
        var totalTransaksi = $("#totalTransaksi").val();
        var statusData = "FA";

        var objWarkat = {};
        objWarkat.noWarkat = noWarkat;
        objWarkat.statusData = statusData;

        var objWarkatLog = {};
        objWarkatLog.noWarkat = noWarkat;
        objWarkatLog.aktivitas = aktivitas;
        objWarkatLog.keterangan = keterangan;
        objWarkatLog.totalTransaksi = totalTransaksi;
        objWarkatLog.statusData = statusData;

        var warkatJurnals = getWarkatJurnal();

        if(kreditValidationLoop() == false) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Kredit tidak boleh melebihi Saldo Anggaran.",
                showConfirmButton: !1,
                timer: 1500
            });

            return;
        }

        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(objWarkatLog),
            url: _baseUrl + "/akunting/transaksi/validasi-jurnal-pajak/save",
            success: function () {

            },
            statusCode: {
                201: function() {
                    $.ajax({
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(objWarkat),
                        url: _baseUrl + "/akunting/transaksi/jurnal-pajak/update-status-data",
                        success: function () {
                            $.ajax({
                                type: "POST",
                                contentType: "application/json",
                                data: JSON.stringify(warkatJurnals),
                                url: _baseUrl + "/akunting/transaksi/validasi-jurnal-pajak/update-saldo-current",
                                success: function () {
                                    $("#add-form").modal("hide");
                                    showSuccess();
                                    table.ajax.reload();
                                }
                            });
                        },
                    });
                }
            }
        });
    });

    $("#tolakJurnalPajak").on("click", function() {
        $("#add-form").modal("hide");
        $("#reject-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#confirmReject").off().on("click", function() {
            var noWarkat = $("#noTransaksi").val();
            var aktivitas = "REJECT-FA";
            var keterangan = $("#keteranganReject").val();
            var totalTransaksi = $("#totalTransaksi").val();
            var statusData = "REJECT-JP";

            var objWarkat = {};
            objWarkat.noWarkat = noWarkat;
            objWarkat.statusData = statusData;
            objWarkat.catatanValidasi = keterangan;

            var objWarkatLog = {};
            objWarkatLog.noWarkat = noWarkat;
            objWarkatLog.aktivitas = aktivitas;
            objWarkatLog.keterangan = keterangan;
            objWarkatLog.totalTransaksi = totalTransaksi;
            objWarkatLog.statusData = statusData;

            if(keterangan == "") {
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
                data: JSON.stringify(objWarkatLog),
                url: _baseUrl + "/akunting/transaksi/validasi-jurnal-pajak/save",
                success: function() {

                },
                statusCode: {
                    201: function() {
                        $.ajax({
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(objWarkat),
                            url: _baseUrl + "/akunting/transaksi/jurnal-pajak/update-status-data",
                            success: function () {
                                $("#reject-form").modal("hide");
                                showSuccess();
                                table.ajax.reload();
                            },
                        });
                    }
                }
            });
        });

    });
}

function setWarkatJurnal(data) {
    $("#noContent").prop("hidden", true);
    $("#totalDebitKredit").prop("hidden", false);
    data.forEach(v => {
        var markup = "<tr class='perpindahan-rekening rekening-row'>"+
                        "<td class='text-center'>"+
                            v.noUrut+
                        "</td>"+
                        "<td class='text-center'>"+
                            v.idRekening.kodeRekening+
                        "</td>"+
                        "<td>"+
                            v.idRekening.namaRekening+
                        "</td>"+
//                        "<td style='text-align: right'>"+
//                            v.idRekening.saldoCurrent.saldoAnggaran+
//                        "</td>"+
                        "<td style='text-align: right'>"+
                            formatMoney(v.jumlahDebit, "", 2)+
                        "</td>"+
                        "<td style='text-align: right'>"+
                            formatMoney(v.jumlahKredit, "", 2)+
                        "</td>"+
                        "<td style='text-align: right' hidden>"+
                            v.idRekening.idRekening+
                        "</td>"+
                     "</tr>";
        $("#tblPerpindahan tbody").append(markup);
        kreditValidaitonCss($("#tblPerpindahan tr.perpindahan-rekening").last());
        getTotalDebitKredit();
    });
}

function getTotalDebitKredit() {
    var totalDebit = 0;
    var totalKredit = 0;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var col2 = currentRow.find("td:eq(3)").text();
        var col3 = currentRow.find("td:eq(4)").text();
        var intCol2 = getNumberRegEx(col2);
        var intCol3 = getNumberRegEx(col3);

        totalDebit = totalDebit + parseFloat(intCol2);
        totalKredit = totalKredit + parseFloat(intCol3);
    });
    $("#totalDebit").text(formatMoney(totalDebit, "", 2));
    $("#totalKredit").text(formatMoney(totalKredit, "", 2));
}

function formReset() {
    $("#noTransaksi").val("");
    $("#tglTransaksi").val("");
    $("#tglBuku").val("");
    $("#totalTransaksi").val("");
    $("#keterangan").val("");
    $("#idRekening").val("");
    $(".perpindahan-rekening").remove();
    $("#totalDebit").val("0");
    $("#totalKredit").val("0");
    $("#noContent").prop("hidden", false);
    $("#totalDebitKredit").prop("hidden", true);
    $("#statusModal").val("");
}

function timestampToDate(data) {
    var master = data.split("T")[0].split("-");
    return master[2] + "/" + master[1] + "/" + master[0];
}

function kreditValidaitonCss(value) {
    var saldoAnggaran = value.find("td:eq(2)").text();
    var kredit = value.find("td:eq(4)").text();

    if(parseInt(kredit) > parseInt(saldoAnggaran)) {
        value.find("td:eq(2)").css({color: "red"});
        value.find("td:eq(4)").css({color: "red"});
    }
}

function kreditValidationLoop() {
    var value = true;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var saldoAnggaran = currentRow.find("td:eq(2)").text();
        var kredit = currentRow.find("td:eq(4)").text();

        if(parseInt(kredit) > parseInt(saldoAnggaran)) {
            value = false;
            return value;
        } else {
            return true;
        }
    });
    return value;
}

function formatMoney(amount, currency = "", decimalCount = 0, decimal = ",", thousands = ".") {
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
};

function getWarkatJurnal() {
    var arrayVal = new Array();
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var col2 = currentRow.find("td:eq(2)").text();
        var col3 = currentRow.find("td:eq(3)").text();
        var col4 = currentRow.find("td:eq(4)").text();
        var col5 = currentRow.find("td:eq(5)").text();
        var obj = {
            "idRekening": {
                "idRekening": col5
            },
//            "saldoAnggaran": col2,
            "jumlahDebit": col3,
            "jumlahKredit": col4
        };
        arrayVal.push(obj);
    });
    return arrayVal;
}

function getNumberRegEx(value) {
    var res = value.replace(/\./g, "").replace(/Rp /g, "").replace(/\,/g, ".");
    return res;
}