var _jurnalIndividuals = [];
var _rekening = null;
var _rekenings = [];
var jurnalIndividual = null;

// $body = $("body");

// $(document).on({
//     ajaxStart: function() { $body.addClass("loading");    },
//     ajaxStop: function() { $body.removeClass("loading"); }
// });

jQuery(document).ready(function () {
    $('#txtStartDate').val(moment(new Date).format("YYYY-MM-DD"));
    $('#txtEndDate').val(moment(new Date).format("YYYY-MM-DD"));
    findJurnalIndividuals();
});

function findJurnalIndividuals() {
    jurnalIndividual = {
        startDate: $('#txtStartDate').val(),
        endDate: $('#txtEndDate').val(),
        idRekening: $('#txtRekening').val()
    }

    //  console.log(jurnalIndividual, "find rekening");

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(jurnalIndividual),
        url: _baseUrl + `/akunting/laporan/laporan-individual/listByDate`,
        success: function (response) {
            _jurnalIndividuals = response;
            // console.log(_jurnalIndividuals, "response")
            if (_jurnalIndividuals !== null) {
                renderTable();
            }
        },
        complete: function (resp) {
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

function renderTable() {
    var tbody = document.getElementById('jurnalIndividualTbody');
    console.log(_jurnalIndividuals)
    if (_jurnalIndividuals.length !== 0) {
        tbody.innerHTML = '';
        for (var i = 0; i < Object.keys(_jurnalIndividuals).length; i++) {
            let detailsLength = _jurnalIndividuals[i].jurnalIndividualDetails.length;
            let tr = '<tr>' +
                `        <td rowspan=${detailsLength + 1} class="text-center">${i + 1}</td>`;
            if (_jurnalIndividuals[i].jurnalIndividualDetails !== null) {
                tr +=
                    `        <td class="text-center">${_jurnalIndividuals[i].jurnalIndividualDetails[0].tglTransaksi}</td>` +
                    `        <td class="text-center">${_jurnalIndividuals[i].jurnalIndividualDetails[0].kodeRekening}</td>` +
                    `        <td>${_jurnalIndividuals[i].jurnalIndividualDetails[0].namaRekening}</td>` +
                    `        <td class="text-right">${numeralformat(_jurnalIndividuals[i].jurnalIndividualDetails[0].jumlahDebit)}</td>` +
                    `        <td class="text-right">${numeralformat(_jurnalIndividuals[i].jurnalIndividualDetails[0].jumlahKreditnumeralformat)}</td>` +
                    `        <td rowspan=${detailsLength + 1} class="text-center">${_jurnalIndividuals[i].nuwp}</td>` +
                    '      </tr>';

                for (var j = 1; j < detailsLength; j++) {
                    var detail = _jurnalIndividuals[i].jurnalIndividualDetails[j];
                    tr +=
                        `        <tr> <td class="text-center">${detail.tglTransaksi}</td>` +
                        `        <td class="text-center">${detail.kodeRekening}</td>` +
                        `        <td>${detail.namaRekening}</td>` +
                        `        <td class="text-right">${numeralformat(detail.jumlahDebit)}</td>` +
                        `        <td class="text-right">${numeralformat(detail.jumlahKredit)}</td></tr>`;
                }
            }
            tr +=
                '      <tr>' +
                `        <td colspan="5">Keterangan : ${_jurnalIndividuals[i].keterangan}</td>` +
                '      </tr>';
            tbody.innerHTML += tr;
        }
    } else {
        resetTable();
    }
}

function resetTable() {
    var tbody = document.getElementById('jurnalIndividualTbody');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"7\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}


// EXPORT PDF
function exportPdf() {
    var periode = $("#txtStartDate").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_jurnalIndividuals),
        url: _baseUrl + `/akunting/laporan/laporan-individual/export-pdf?periode=${periode}` +
            `&startDate=${jurnalIndividual.startDate}&endDate=${jurnalIndividual.endDate}&idRekening=${jurnalIndividual.idRekening}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "LaporanJurnalTransaksi.pdf";
            link.target = "_blank";
            PDFObject.embed(link.href, "#example1");
            $('#pdfModal').modal('show');

            // var byteCharacters = atob(response);
            // var byteNumbers = new Array(byteCharacters.length);
            // for (var i = 0; i < byteCharacters.length; i++) {
            //   byteNumbers[i] = byteCharacters.charCodeAt(i);
            // }
            // var byteArray = new Uint8Array(byteNumbers);
            // var file = new Blob([byteArray], { type: 'application/pdf;base64' });
            // var fileURL = URL.createObjectURL(file);
            // window.open(fileURL);

//            document.body.appendChild(link);
//            link.click();
//            document.body.removeChild(link);
        },
        complete: function (resp) {
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

// EXPORT PDF
function exportExcel() {
    var periode = $("#txtStartDate").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_jurnalIndividuals),
        url: _baseUrl + `/akunting/laporan/laporan-individual/export-excel?periode=${periode}` +
            `&startDate=${jurnalIndividual.startDate}&endDate=${jurnalIndividual.endDate}&idRekening=${jurnalIndividual.idRekening}`,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "LaporanJurnalTransaksi.xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        complete: function (resp) {
            if (resp.status !== 200) {
                showError(resp.responseText);
            }
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function datePicker() {
    $("#periodeDatepicker1").off().datepicker({
        format: "yyyy-mm-dd",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
        findJurnalIndividuals();
    });
    $("#periodeDatepicker2").off().datepicker({
        format: "yyyy-mm-dd",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
        findJurnalIndividuals();
    });
}