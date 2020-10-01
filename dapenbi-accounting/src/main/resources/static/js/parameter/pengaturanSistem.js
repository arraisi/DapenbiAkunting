jQuery(document).ready(function () {

//    $("#tglTransaksi").val(moment(new Date()).format("DD/MM/YYYY"));

    $("#aturTransaksi").on("click", function () {
        $(".atur-transaksi").prop("hidden",false);
        $(".tanda-tangan").prop("hidden", true);
    });

    $("#tandaTangan").on("click", function () {
        $(".tanda-tangan").prop("hidden", false);
        $(".atur-transaksi").prop("hidden", true);
    });

    $("#statusPage").val("update");

    $("#saveButton").on("click", function () {
//        var statusPage = $("#statusPage").val();
//        if(statusPage == "add") {
//            save();
//        } else {
//            update();
//        }
        save();
    });

//    getData('ADMIN');
    getDataCreatedDate();

    $("#batalButton").on("click", function () {
//        getData('ADMIN');
        getDataCreatedDate();
    });

    $("#addButton").on("click", function () {
        formReset();
    });

    activeButton();

});

function testButton() {
    $("#datetimepicker3").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "dd/mm/yyyy"
    }, "show");
}

function save() {
    var kodeTahunBuku = $("#kodeTahunBuku").val();
    var kodePeriode = $("#kodePeriode").val();
    var tglTransaksi = $("#tglTransaksi").val();
    var statusAktif = $('#statusAktif input:radio:checked').val();
    var statusOpen = $("#statusOpen").val();
    var noPengantarWarkat = $("#noPengantarWarkat").val();
    var jamTutup = $("#jamTutup").val();
    var dirut = $("#dirut").val();
    var div = $("#div").val();
    var kdiv = $("#kdiv").val();
    var ks = $("#ks").val();
    var createdDate = $("#createdDate").val();
    var tglSplit = tglTransaksi.split("/");
    var tglTransaksiFinal = tglSplit[2] + "-" + tglSplit[1] + "-" + tglSplit[0];

    var obj = {
        "kodeTahunBuku": kodeTahunBuku,
        "kodePeriode": kodePeriode,
        "tglTransaksi": $("#tglTransaksiFull").val(),
//        "statusAktif": statusAktif,
        "statusOpen": statusOpen,
        "noPengantarWarkat": noPengantarWarkat,
        "jamTutup": jamTutup,
        "dirut": dirut,
        "div": div,
        "kdiv": kdiv,
        "ks": ks
    }

    console.log(obj);
    console.log(tglTransaksiFinal);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/save",
        success: function () {
        },
        statusCode: {
            201: function () {
                $("#statusPage").val("update");
                getDataCreatedDate();
                showSuccess();
            }
        }
    });
}

function update() {
    var idParameter = $("#idParameter").val();
    var kodeTahunBuku = $("#kodeTahunBuku").val();
    var kodePeriode = $("#kodePeriode").val();
    var tglTransaksi = $("#tglTransaksi").val();
    var statusAktif = $('#statusAktif input:radio:checked').val();
    var noPengantarWarkat = $("#noPengantarWarkat").val();
    var jamTutup = $("#jamTutup").val();
    var dirut = $("#dirut").val();
    var div = $("#div").val();
    var kdiv = $("#kdiv").val();
    var ks = $("#ks").val();
    var createdDate = $("#createdDate").val();
    var createdBy = $("#createdBy").val();
    var tglSplit = tglTransaksi.split("/");
    var tglTransaksiFinal = tglSplit[2] + "-" + tglSplit[1] + "-" + tglSplit[0];

    var obj = {
        "idParameter": idParameter,
        "kodeTahunBuku": kodeTahunBuku,
        "kodePeriode": kodePeriode,
        "tglTransaksi": tglTransaksiFinal,
        "statusAktif": statusAktif,
        "noPengantarWarkat": noPengantarWarkat,
        "jamTutup": jamTutup,
        "dirut": dirut,
        "div": div,
        "kdiv": kdiv,
        "ks": ks,
        "createdDate": createdDate,
        "createdBy": createdBy
    }

    console.log(obj);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/update",
        success: function () {
        },
        statusCode: {
            201: function () {
                showSuccess();
            }
        }
    });
}

function getData(username) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/" + username + "/findByUsername",
        success: function (resp) {
            var tglSplit = resp.tglTransaksi.split("-");
            var tglTransaksiFinal = tglSplit[2] + "/" + tglSplit[1] + "/" + tglSplit[0];

            $("#idParameter").val(resp.idParameter);
            $("#kodeTahunBuku").val(resp.kodeTahunBuku);
            $("#kodePeriode").val(resp.kodePeriode);
            $("#tglTransaksi").val(tglTransaksiFinal);
            $("input[name='statusAktif'][value='"+resp.statusAktif+"']").prop('checked', true);
            $("#noPengantarWarkat").val(resp.noPengantarWarkat);
            $("#jamTutup").val(resp.jamTutup);
            $("#dirut").val(resp.dirut);
            $("#div").val(resp.div);
            $("#kdiv").val(resp.kdiv);
            $("#ks").val(resp.ks);
            $("#createdDate").val(resp.createdDate);

            console.log(resp);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
                if(resp.responseJSON.message == "Incorrect result size: expected 1, actual 0") {
                    console.log('Not found');
                }
            }
        }
    });
}

function getDataCreatedDate() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/findByCreatedDate",
        success: function (resp) {
            var tglSplit = resp.tglTransaksi.split("-");
            var tglTransaksiFinal = tglSplit[2] + "/" + tglSplit[1] + "/" + tglSplit[0];

            $("#idParameter").val(resp.idParameter);
            $("#kodeTahunBuku").val(resp.kodeTahunBuku.kodeTahunBuku).selectpicker("refresh");
            $("#kodePeriode").val(tglSplit[1]).selectpicker("refresh");
            $("#tglTransaksi").val(timestampToDate(resp.tglTransaksi));
            $("#tglTransaksiFull").val(resp.tglTransaksi);
            $("input[name='statusAktif'][value='"+resp.statusOpen+"']").prop('checked', true);
            $("input[name='statusAktif']").prop('disabled', true);
            $("#statusOpen").val(resp.statusOpen);
            $("#noPengantarWarkat").val(resp.noPengantarWarkat);
            $("#jamTutup").val(resp.jamTutup);
            $("#dirut").val(resp.dirut);
            $("#div").val(resp.div);
            $("#kdiv").val(resp.kdiv);
            $("#ks").val(resp.ks);
            $("#createdDate").val(resp.createdDate);
            $("#createdBy").val(resp.createdBy);

            console.log(resp);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
                if(resp.responseJSON.message == "Incorrect result size: expected 1, actual 0") {
                    console.log('Not found');
                }
            }
        }
    });
}

function formReset() {
    $("#idParameter").val("");
    $("#kodeTahunBuku").val("").selectpicker("refresh");
    $("#kodePeriode").val("").selectpicker("refresh");
    $("#tglTransaksi").val("");
    $("input[name='statusAktif']").prop('checked', false);
    $("#statusOpen").val("");
    $("#noPengantarWarkat").val("");
    $("#jamTutup").val("");
    $("#dirut").val("");
    $("#div").val("");
    $("#kdiv").val("");
    $("#ks").val("");
    $("#createdDate").val("");
    $("#statusPage").val("add");
}

function activeButton() {
    // Get the container element
    var btnContainer = document.getElementById("pengaturan-tabs");

    // Get all buttons with class="btn" inside the container
    var btns = btnContainer.getElementsByClassName("btn");

    // Loop through the buttons and add the active class to the current/clicked button
    for (var i = 0; i < btns.length; i++) {
      btns[i].addEventListener("click", function() {
        var current = document.getElementsByClassName("active-button");
        current[0].className = current[0].className.replace(" active-button", "");
        this.className += " active-button";
      });
    }
}

function timestampToDate(data) {
    var master = data.split("T")[0].split("-");
    return master[2] + "/" + master[1] + "/" + master[0];
}