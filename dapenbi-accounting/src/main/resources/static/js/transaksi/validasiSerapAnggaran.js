jQuery(document).ready(function () {
    var tableSerap = $("#tblSerap").DataTable({
        ajax: {
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/serap/getSerapDTODatatables",
            type: "POST",
            data: function (d) {
                var serap = {};
                serap.statusData = "SUBMIT";
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
                   return formatMoney(data, "Rp. ", 2);
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
//                "defaultContent": getButtonGroup(true, true, false)
                render: function(data) {
                    return "<a href='javascript:void(0);' class='validasi-button'>Validasi</a>";
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });
//    tableSerap.column(9).search('SUBMIT').draw();

    initEvent(tableSerap);
});

function initEvent(table) {
    $("#tblSerap tbody").on("click", ".validasi-button", function () {
        var data = table.row($(this).parents("tr")).data();
        formReset();
        $("#statusModal").val("update");

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: _baseUrl + "/akunting/transaksi/serap/" + data.noSerap + "/findById",
            success: function (resp) {
                $("#noSerap").val(resp.noSerap).prop("disabled", true);
                $("#tanggal").val(timestampToDate(resp.tglSerap)).prop("disabled", true);
                $("#tahunBuku").val(resp.tahunBuku.tahun).prop("disabled", true);
                $("#totalTransaksi").val(formatMoney(resp.totalTransaksi, "", 2)).prop("disabled", true);
                $("#periode").val(resp.periode.namaPeriode).prop("disabled", true);
                $("#keteranganDebit").val(resp.keteranganDebet).prop("disabled", false);
                $("#keteranganKredit").val(resp.keteranganKredit).prop("disabled", false);
                setSerapDetail(resp.serapDetail);
            }
        });

        $("#add-form").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#setujuiSerap").on("click", function() {
        var noSerap = $("#noSerap").val();
        var aktivitas = "VALIDASI";
        var keterangan = $("#keteranganDebit").val();
        var alasan = $("#keteranganKredit").val();
        var totalTransaksi = getNumberRegEx($("#totalTransaksi").val());
        var statusData = "VALID";

        var objSerap = {};
        objSerap.noSerap = noSerap;
        objSerap.statusData = statusData;
        objSerap.keteranganDebet = keterangan;
        objSerap.keteranganKredit = alasan;

        var objValidSerap = {};
        objValidSerap.noSerap = noSerap;
        objValidSerap.aktivitas = aktivitas;
        objValidSerap.keterangan = keterangan;
        objValidSerap.totalTransaksi = totalTransaksi;
        objValidSerap.statusData = statusData;

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
            data: JSON.stringify(objValidSerap),
            url: _baseUrl + "/akunting/transaksi/validasi-serap/save",
            success: function () {

            },
            statusCode: {
                201: function() {
                    $.ajax({
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(objSerap),
                        url: _baseUrl + "/akunting/transaksi/serap/update-status-data",
                        success: function () {
                            $("#add-form").modal("hide");
                            showSuccess();
                            table.ajax.reload();
                        },
                    });
                }
            }
        });
    });

    $("#tolakSerap").on("click", function() {
//        $("#add-form").modal("hide");
        $("#reject-form").modal({
            backdrop: 'static',
            keyboard: false
        });

        $("#confirmReject").off().on("click", function() {
            var noSerap = $("#noSerap").val();
            var aktivitas = "REJECT";
            var keterangan = $("#keteranganReject").val();
            var keteranganDebetKredit = $("#keteranganDebit").val();
            var alasan = $("#keteranganKredit").val();
            var totalTransaksi = getNumberRegEx($("#totalTransaksi").val());
            var statusData = "REJECT";

            var objSerap = {};
            objSerap.noSerap = noSerap;
            objSerap.statusData = statusData;
            objSerap.catatanValidasi = keterangan;
            objSerap.keteranganDebet = keteranganDebetKredit;
            objSerap.keteranganKredit = alasan;

            var objValidSerap = {};
            objValidSerap.noSerap = noSerap;
            objValidSerap.aktivitas = aktivitas;
            objValidSerap.keterangan = keterangan;
            objValidSerap.totalTransaksi = totalTransaksi;
            objValidSerap.statusData = statusData;

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
                data: JSON.stringify(objValidSerap),
                url: _baseUrl + "/akunting/transaksi/validasi-serap/save",
                success: function() {

                },
                statusCode: {
                    201: function() {
                        $.ajax({
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(objSerap),
                            url: _baseUrl + "/akunting/transaksi/serap/update-status-data",
                            success: function () {
                                $("#reject-form").modal("hide");
                                $("#add-form").modal("hide");
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

function setSerapDetail(data) {
    $("#noContent").prop("hidden", true);
    $("#totalDebitKredit").prop("hidden", false);
    data.forEach(v => {
        var markup = "<tr class='perpindahan-rekening rekening-row'>"+
                        "<td>"+
                            v.rekening.kodeRekening+
                        "</td>"+
                        "<td>"+
                            v.rekening.namaRekening+
                        "</td>"+
                        "<td style='text-align: right'>"+
                            formatMoney(v.saldoAnggaran, "", 2)+
                        "</td>"+
                        "<td style='text-align: right'>"+
                            formatMoney(v.jumlahPenambah, "", 2)+
                        "</td>"+
                        "<td style='text-align: right'>"+
                            formatMoney(v.jumlahPengurang, "", 2)+
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
    $("#noSerap").val("").prop("disabled", true);
    $("#tanggal").val("").prop("disabled", true);
    $("#tahunBuku").val("").prop("disabled", true);
    $("#totalTransaksi").val("").prop("disabled", true);
    $("#periode").val("").prop("disabled", true);
    $("#keteranganDebit").val("").prop("disabled", false);
    $("#keteranganKredit").val("").prop("disabled", false);
    $("#totalDebit").val("0");
    $("#totalKredit").val("0");
    $("#noContent").prop("hidden", false);
    $("#totalDebitKredit").prop("hidden", true);
    $("#statusModal").val("");
    $(".perpindahan-rekening").remove();
    $("#keteranganReject").val("");
}

function timestampToDate(data) {
    var master = data.split("T")[0].split("-");
    return master[2] + "/" + master[1] + "/" + master[0];
}

function kreditValidaitonCss(value) {
    var saldoAnggaran = getNumberRegEx(value.find("td:eq(2)").text());
    var kredit = getNumberRegEx(value.find("td:eq(4)").text());

    if(parseFloat(kredit) > parseFloat(saldoAnggaran)) {
        value.find("td:eq(2)").css({color: "red"});
        value.find("td:eq(4)").css({color: "red"});
    }
}

function kreditValidationLoop() {
    var value = true;
    $("#tblPerpindahan tr.perpindahan-rekening").each(function () {
        var currentRow = $(this);
        var saldoAnggaran = getNumberRegEx(currentRow.find("td:eq(2)").text());
        var kredit = getNumberRegEx(currentRow.find("td:eq(4)").text());

        if(parseFloat(kredit) > parseFloat(saldoAnggaran)) {
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
}

function getNumberRegEx(value) {
    var res = value.replace(/\./g, "").replace(/Rp /g, "").replace(/\,/g, ".");
    return res;
}