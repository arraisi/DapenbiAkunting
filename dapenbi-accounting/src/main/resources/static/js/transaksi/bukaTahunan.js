var _currentTahunBuku = null;
var _currentPeriode = null;
var _currentPengaturanSistem = null;

jQuery(document).ready(function () {
    var date = new Date();
    $("#txtTglTransaksi").val(moment(date).format("YYYY-MM-DD"));
    let month = "0" + (date.getMonth() + 1);

    // default value
    findTahunBukuByTahun(date.getFullYear());
    findPeriodeByKode(month.slice(-2));
    findPengaturanSistem();

    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });
});

function findTahunBukuByTahun(tahun) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/api/akunting/parameter/tahun-buku/findByTahun?tahun=${tahun}`,
        success: function (resp) {
            console.log(resp, "tahun buku");
            _currentTahunBuku = resp;
            $("#txtTahunBuku").val(_currentTahunBuku.namaTahunBuku);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function findPeriodeByKode(monthCode) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/api/akunting/parameter/periode/${monthCode}/findById`,
        success: function (resp) {
            console.log(resp, "tahun buku");
            _currentPeriode = resp;
            _currentPeriode.kodePeriode = "01"
            _currentPeriode.namaPeriode = "JANUARI"
            $("#txtPeriode").val(_currentPeriode.namaPeriode);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function findLastDRI() {
    let tglAktifParameter = moment(_currentPengaturanSistem.tglTransaksi).format('YYYY-MM-DD');
    console.log(tglAktifParameter, 'tglAktifParameter')
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/transaksi/buka-tahunan/lastDRI?tglTransaksi=${tglAktifParameter}`,
        success: function (resp) {
            _lastDRI = resp;
            $("#txtStatusDRI").val(_lastDRI);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function findPengaturanSistem() {
// FIND PENGATURAN SISTEM
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/parameter/pengaturan-sistem/findDTOByStatusAktif`,
        success: function (response) {
            _currentPengaturanSistem = response;
            console.log(_currentPengaturanSistem, "_currentPengaturanSistem");
            _currentPengaturanSistem.statusOpen === "O" ? $("#sistemTerbuka").prop("checked", true) : $("#sistemTertutup").prop("checked", true);
            findLastDRI();
        },
        complete: function (response) {
            if (response.status !== 200)
                showWarning("Error code : " + response.status);
        }
    });
}

function bukaSistem() {
    var statusSistem = $('#radioBukaSistem input:radio:checked').val();
    let obj = {
        kodeDRI: $("#txtStatusDRI").val(),
        tglTransaksiSekarang: $("#txtTglTransaksi").val(),
        kodeTahunBuku: _currentTahunBuku.kodeTahunBuku,
        kodePeriode: _currentPeriode.kodePeriode
    }

    if (statusSistem == '0') {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Status Sistem belum ditutup.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    console.log(obj);
    if (obj.kodeDRI !== '3') {
        showWarning("Kode DRI tidak sesuai.");
        showSuccess();
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + `/akunting/transaksi/buka-tahunan/buka`,
        success: function (resp) {
            console.log(resp, 'resp');
            showSuccess();
        },
        complete: function (resp) {
            enableButton();
            switch (resp.status) {
                case 404:
                    showError("Not Found");
                    break;
                case 500:
                    showError("Internal Server Error")
                    break;
            }
        }
    });
}