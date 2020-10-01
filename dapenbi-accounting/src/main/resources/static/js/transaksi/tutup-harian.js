var statusOpen;

jQuery(document).ready(function () {
    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });
    var d = new Date();
    var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
        "Agustus", "September", "Oktober", "November", "Desember"];

    // KODE PERIODE
//     $.ajax({
//         type: "GET",
//         contentType: "application/json",
//         url: _baseUrl + `/api/akunting/parameter/periode/${months[d.getMonth()]}/findByNamePeriode`,
//         success: function (response) {
//             // console.log(response, "RESPONSE PERIODE");
//         },
//         statusCode: {
//             200: function (data) {
// //                var kodePeriode = data.kodePeriode;
//             }
//         }
//     });

    // KODE TAHUN BUKU AKTIF
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/findByCreatedDate",
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                // console.log(data)
                $('#txtKodeThnBuku').val(data.kodeTahunBuku.tahun);
                $('.tahunBuku').text(`Tahun Buku ${data.kodeTahunBuku.tahun}`);
                $("#kodeTahunBuku").val(data.kodeTahunBuku.kodeTahunBuku);
                $('#txtTglTransaksi').val(moment(data.tglTransaksi).format('YYYY-MM-DD'));
                $('.tglTransaksi').text("Tanggal Transaksi " + moment(data.tglTransaksi).format('DD/MM/YYYY'));
                $("#kodePeriode").val(data.kodePeriode.kodePeriode);
                let kodePeriodeParse = data.kodePeriode.kodePeriode.includes('0') ? parseInt(data.kodePeriode.kodePeriode.slice(-1)) : parseInt(data.kodePeriode.kodePeriode);
                let namaPeriode = months[kodePeriodeParse - 1];
                $('#txtKodePeriode').val(namaPeriode);
                $('.periode').text(`Periode ${namaPeriode}`);
                statusOpen = data.statusOpen;
            }
        }
    });

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/transaksi/tutup-pembukuan-harian/`,
        success: function (response) {
            // console.log(response, "RESPONSE TUTUP HARIAN");
        },
        statusCode: {
            200: function (data) {
                // $('#txtTahunBuku').val(data.)
                console.log(data, "total warkat");
                $('#totalWarkat').text(data.totalWarkat);
                $('#totalValidasiWP').text(data.totalWarkatValid);
                $('#totalPAWP').text(data.totalWarkatPA);
                $('#totalFAWP').text(data.totalWarkatFA);

                $('#txtTransaksiWP').val(data.totalWarkat);
                $('#txtValidasiWP').val(data.totalWarkatValid);
                $('#txtValidasiPA').val(data.totalWarkatPA);
                $('#txtValidasiFA').val(data.totalWarkatFA);

                var values = [data.totalWarkatValid, data.totalWarkatPA, data.totalWarkatFA];

                for (var i = 0; i < values.length; i++) {
                    if (values[i] !== data.totalWarkat) {
                        break;
                    }
                }
            },
            500: function (data) {
                showError();
                $('#warkatDialog').modal('hide');
            }
        }
    });
});

function post() {
    $('#btnPost').attr('disabled', true);

    const tglTransaksi = $('#txtTglTransaksi').val();
    const kodeThnBuku = $('#kodeTahunBuku').val();
    const kodePeriode = $('#kodePeriode').val();
    var WP = $('#txtTransaksiWP').val();
    var valid = $('#txtValidasiWP').val();
    var PA = $('#txtValidasiPA').val();
    var FA = $('#txtValidasiFA').val();

    var data = {
        kodeThnBuku: kodeThnBuku,
        kodePeriode: kodePeriode
    };

    // console.log(areEqual(WP, valid, PA, FA));

    if (!areEqual(WP, valid, PA, FA)) {
//        console.log('True');
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Jumlah transaksi tidak sama.",
            showConfirmButton: !1,
            timer: 1500
        });
        $('#btnPost').attr('disabled', false);
        return;
    }
//    console.log('False');
//    return;

    if (statusOpen === "C") {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Sistem Buku sudah tutup.",
            showConfirmButton: !1,
            timer: 1500
        });
        $('#btnPost').attr('disabled', false);
        return;
    }

    // console.log(data);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        url: _baseUrl + `/akunting/transaksi/tutup-pembukuan-harian/post`,
        success: function (response) {
            // console.log(response, "RESPONSE POST");
        },
        complete: function (resp) {
            if (resp.status === 200) {
                setStatusOpen();
                showSuccesAlarm();
            } else if (resp.status === 202) {
                showSuccess(resp.responseText);
            } else {
                showError();
                $('#btnPost').attr('disabled', false);
            }
        }
    });
}

function showSuccesAlarm() {
    Swal.fire(
        'Berhasil!',
        'Transaksi ditutup!',
        'success'
    )
}

function timestampToDate(data) {
    var master = data.split("T")[0].split("-");
    return master[2] + "/" + master[1] + "/" + master[0];
}

function setStatusOpen() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/findByCreatedDate",
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                statusOpen = data.statusOpen;
            }
        }
    });
}

function areEqual() {
    var len = arguments.length;
    for (var i = 1; i < len; i++) {
        if (arguments[i] === null || arguments[i] !== arguments[i - 1])
            return false;
    }
    return true;
}