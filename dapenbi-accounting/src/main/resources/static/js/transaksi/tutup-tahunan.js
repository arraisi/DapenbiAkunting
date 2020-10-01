let kodePeriode;
let kodeThnBuku;

jQuery(document).ready(function () {
//    console.log(pengaturanSistem);
//    console.log(totalTransaksiWarkatDRI2);
//    console.log(totalTransaksiWarkatDRI3);
//    console.log(totalTransaksiSaldoDRI1);
//    console.log(totalTransaksiSaldoDRI2);
    initEvent();
});

function initEvent() {
    setForm();

    var totalWarkat = parseInt($("#totalWarkat").text());
    var totalWP = parseInt($("#totalValidasiWP").text());
    var totalPA = parseInt($("#totalPAWP").text());
    var totalFA = parseInt($("#totalFAWP").text());
//    console.log(totalWP, totalPA, totalFA);

    if (!areEqual(totalWarkat, totalWP, totalPA, totalFA) || totalTransaksiSaldoDRI3 > 0) {
//        console.log("True");
        $("#btnPost").prop("disabled", true);
    } else {
//        console.log("False");
        $("#btnPost").prop("disabled", false);
    }

    $("#btnPost").on("click", function () {
//        console.log(pengaturanSistem.statusOpen, totalTransaksiSaldoDRI1);
        $("#btnPost").prop("disabled", true);

//        return;
        if (pengaturanSistem.statusOpen == "O" && totalTransaksiSaldoDRI1 == 0) {
//            console.log("True");
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Tutup harian belum dilakukan.",
                showConfirmButton: !1,
                timer: 1500
            });
            $("#btnPost").prop("disabled", false);
            return;
        }

        if (!totalTransaksiWarkatDRI2.totalWarkat > 0) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Jurnal Pajak belum terpenuhi.",
                showConfirmButton: !1,
                timer: 1500
            });
            $("#btnPost").prop("disabled", false);
            return;
        }

//        if (totalTransaksiWarkatDRI2.totalWarkat > 0 && !totalTransaksiWarkatDRI3.totalWarkat > 0) {
//            swal.fire({
//                position: "top-right",
//                type: "error",
//                title: "Jurnal Transaksi belum terpenuhi.",
//                showConfirmButton: !1,
//                timer: 1500
//            });
//            return;
//        }

        var stringDate = timestampToDate(pengaturanSistem.tglTransaksi);
        var obj = {
            "tglTransaksi": convertStringDate(stringDate),
            "kodeTahunBuku": tahunBuku.kodeTahunBuku,
            "kodePeriode": periode.kodePeriode,
            "kodeDRI": totalTransaksiSaldoDRI2 > 0 ? "3" : "2"
        };

        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + `/akunting/transaksi/tutup-tahunan/post-tahunan`,
            success: function (response) {
                // console.log(response, "RESPONSE SAVE");
            },
            statusCode: {
                200: function () {
                    $("#btnPost").prop("disabled", false);
                    showSuccess();
                },
                204: function () {
                    showSuccess("Data is up to date");
                },
                500: function () {
                    $("#btnPost").prop("disabled", false);
                    showWarning("Internal Server Error!");
                }
            }
        });
    });
}

function setForm() {
//    $("#txtTglTransaksi").val(timestampToDate(pengaturanSistem.tglTransaksi));
//    $("#txtKodeThnBuku").val(tahunBuku.tahun);
//    $("#txtKodePeriode").val(periode.namaPeriode);
//    $("#txtTransaksiWP").val(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkat : totalTransaksiWarkatDRI2.totalWarkat);
//    $("#txtValidasiWP").val(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkatValid : totalTransaksiWarkatDRI2.totalWarkatValid);
//    $("#txtValidasiPA").val(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkatPA : totalTransaksiWarkatDRI2.totalWarkatPA);
//    $("#txtValidasiFA").val(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkatFA : totalTransaksiWarkatDRI2.totalWarkatFA);

    // for new ui
    $('.tahunBuku').text(`TAHUN BUKU ${tahunBuku.tahun}`);
    $('.periode').text(`PERIODE ${periode.namaPeriode}`);
    $('.tglTransaksi').text("TRANSAKSI " + moment(pengaturanSistem.tglTransaksi).format('DD/MM/YYYY'));
    $('#totalWarkat').text(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkat : totalTransaksiWarkatDRI2.totalWarkat);
    $('#totalValidasiWP').text(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkatValid : totalTransaksiWarkatDRI2.totalWarkatValid);
    $('#totalPAWP').text(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkatPA : totalTransaksiWarkatDRI2.totalWarkatPA);
    $('#totalFAWP').text(totalTransaksiSaldoDRI2 > 0 ? totalTransaksiWarkatDRI3.totalWarkatFA : totalTransaksiWarkatDRI2.totalWarkatFA);
    // for new ui
    $("#tutupTahunanText").text(totalTransaksiSaldoDRI2 > 0 ? "Anda Akan Melakukan Proses Tutup Tahunan DRI Final" : totalTransaksiSaldoDRI1 > 0 ? "Anda Akan Melakukan Proses Tutup Tahunan DRI Pajak" : "Tutup harian belum dilakukan.");
}

function timestampToDate(data) {
    var master = data.split("T")[0].split("-");
    return master[2] + "/" + master[1] + "/" + master[0];
}

function convertStringDate(value) {
    if (value == "")
        return "";
    else
        var raw = value.split("/");
        var res = raw[2] + "-" + raw[1] + "-" + raw[0];
        return res;
}

function dateToDatetime(date) {
    var d = new Date(); // for now
    var h = d.getHours();
    h = (h < 10) ? ("0" + h) : h;

    var m = d.getMinutes();
    m = (m < 10) ? ("0" + m) : m;

    var s = d.getSeconds();
    s = (s < 10) ? ("0" + s) : s;

    date += "T" + h + ":" + m + ":" + s;
    return date;
}

function areEqual(){
   var len = arguments.length;
   for (var i = 1; i< len; i++){
      if (arguments[i] === null || arguments[i] !== arguments[i-1])
         return false;
   }
   return true;
}