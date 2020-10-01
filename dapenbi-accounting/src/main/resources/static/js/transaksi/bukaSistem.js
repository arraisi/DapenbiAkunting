jQuery(document).ready(function () {
    document.querySelectorAll('.textCurrency').forEach(function(el) {
        new Cleave(el, { numeral: true,
            numeralThousandsGroupStyle: 'thousand'});
    });

    setDefaultValue();

    var messageLibur = $('#libur').text()
    if (messageLibur != '') {
        console.log($('#libur').text());
        $("#noBukaSistem").attr('readonly', true);
        $("#nilaiPasiva").attr('readonly', true);
        $("#nilaiAktiva").attr('readonly', true);
        $('#statusPemakai').attr('readonly', true);
        $('#btnBatal').attr('disabled', true);
        $('#btnSimpan').attr('disabled', true);
    }

    $("#btnSimpan").on("click", function () {
        $("#message").text();
        save();
    });

//    getData('ADMIN');
//    getDataCreatedDate();

    $("#btnBatal").on("click", function () {
        $("#message").text("");
        setDefaultValue();
    });

    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });
});

function setDefaultValue() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/buka-sistem/default-value",
        success: function (resp) {

            $("#tglTransaksiSebelum").val(resp.tglTransaksiSebelum);
            $("#tglTransaksiSekarang").val(resp.tglTransaksiSekarang);
            $("#noBukaSistem").val(resp.noBukaSistem).attr('readonly', true);
            $("#nilaiPasiva").val("");
            $("#nilaiAktiva").val("");
            $("input[name='radioBukaSistem'][value='" + resp.statusSistem + "']").prop('checked', true);
            $("#statusOpen").val(resp.statusOpen);
            $("#statusPemakai").val(resp.idOrg);

            if (resp.statusPemakai != 0) {
                $("#statusPemakai").val(resp.statusPemakai);
                $('#statusPemakai').attr('readonly', true);
            }

            console.log(resp);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function save() {
    var statusSistem = $('#radioBukaSistem input:radio:checked').val();
    var statusOpen = $("#statusOpen").val();

    var obj = {
        "noBukaSistem": $("#noBukaSistem").val(),
        "tglTransaksiSebelum": $("#tglTransaksiSebelum").val(),
        "tglTransaksiSekarang": $("#tglTransaksiSekarang").val(),
        "nilaiPasiva": parseFloat(numeralForCountformat($("#nilaiPasiva").val())),
        "nilaiAktiva": parseFloat(numeralForCountformat($("#nilaiAktiva").val())),
        "statusPemakai": $("#statusPemakai").val(),
        "statusSistem": statusSistem
    }

    // console.log(obj);
    if (isValid(obj)) {
        if (statusSistem == '1') {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Status Sistem belum ditutup.",
                showConfirmButton: !1,
                timer: 1500
            });
            return;
        }

        if (statusOpen == "None") {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Pengaturan Sistem belum aktif.",
                showConfirmButton: !1,
                timer: 1500
            });
            return;
        }


        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/transaksi/buka-sistem/save",
            success: function (resp) {
//                $("#message").text(resp);
                console.log('Saved');
                showSuccess(resp);
            },
            statusCode: {},
            complete: function (resp) {
                switch (resp.status) {
                    case 200:
                        console.log('Saved');
                        swal.fire({
                            title: "Berhasil",
                            text: "Data Tersimpan.",
                            type: "success",
                            confirmButtonText: "OK"
                        });
                        location.reload();
                        break;
                    case 400:
                        showError("Inputan tidak boleh kosong");
                        break;
                    case 404:
                        showError("Not Found");
                        break;
                    case 409:
                        swal.fire({
                            title: "Peringatan",
                            text: "Data Input Total Aktiva dan Total Pasiva dengan Data pada Database Tidak Sama",
                            type: "warning",
                            confirmButtonText: "OK"
                        });
                        break;
                }
            }
        });
    }
}

function isValid(data) {
    if (data.noBukaSistem == "") {
        showError("No. Buka Sisterm Tidak Boleh Kosong");
        return false;
    }

    if (data.nilaiPasiva == "") {
        showError("Nilai Pasiva Tidak Boleh Kosong");
        return false;
    }

    if (data.nilaiAktiva == "") {
        showError("Nilai Aktiva Tidak Boleh Kosong");
        return false;
    }

    return true;
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function numeralForCountformat(value) {
    return numeral(value).format('0.00')
}