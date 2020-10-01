var menu = [
    {
        "title": "AN",
        "url": _baseUrl + '/akunting/home/aset-netto/get',
        "idHTML": "anMenu",
        "classHTML": "btn tabs active-button",
        "hide": "",
        "titleDesc": "Aset Neto",
        "graph": "bar"
    },
    {
        "title": "PAN",
        "url": _baseUrl + '/akunting/home/perubahan-aset-netto/get',
        "idHTML": "panMenu",
        "classHTML": "btn tabs",
        "hide": "",
        "titleDesc": "Perubahan Aset Neto",
        "graph": "bar"
    },
    {
        "title": "NEC",
        "url": _baseUrl + '/akunting/home/neraca/get',
        "idHTML": "necMenu",
        "classHTML": "btn tabs",
        "hide": "",
        "titleDesc": "Neraca",
        "graph": "bar"
    },
    {
        "title": "PHU",
        "url": _baseUrl + '/akunting/home/perhitungan-hasil-usaha/get',
        "idHTML": "phuMenu",
        "classHTML": "btn tabs",
        "hide": "",
        "titleDesc": "Perhitungan Hasil Usaha",
        "graph": ""
    },
    {
        "title": "AKA",
        "url": _baseUrl + '/akunting/home/arus-kas/get',
        "idHTML": "akaMenu",
        "classHTML": "btn tabs",
        "hide": "",
        "titleDesc": "Arus Kas",
        "graph": "bar"
    },
    {
        "title": "Anggaran",
        "url": _baseUrl + '/akunting/home/anggaran-tahunan/get',
        "idHTML": "agMenu",
        "classHTML": "btn tabs",
        "hide": "",
        "titleDesc": "Anggaran",
        "graph": "ag"
    },
    {
        "title": "ROI",
        "url": _baseUrl + '/akunting/home/roi-roa/get',
        "idHTML": "roiMenu",
        "classHTML": "btn tabs",
        "hide": "",
        "titleDesc": "ROI",
        "graph": "roi"
    },

];
var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];

var activeUrl;
var today;
var activeGraph;

jQuery(document).ready(function () {
    today = pengaturanSistem.tglTransaksi.split("T")[0].split("-");
//    console.log(pengaturanSistem);
    $("#kodePeriode").val(today[1]).selectpicker("refresh");
    $("#kodeTahunBuku").val($("#kodeTahunBuku").find("option:contains('"+today[0]+"')").val()).selectpicker("refresh");
//    $(".date").datepicker({}).on("change", function (evt) {console.log(evt)});
    initEvent();
//    console.log($("#myChart").width());
});

function initEvent() {
    $("#kodePeriode").on("change", function () {
        graph(activeGraph, activeUrl);
    });

    $("#kodeTahunBuku").on("change", function () {
        graph(activeGraph, activeUrl);
    });

    $("#kodeDRI").on("change", function () {
        graph(activeGraph, activeUrl);
    });

    $("#export-pdf").on("click", function () {
        exportPdf();
    });

    activeUrl = menu[0].url;
    activeGraph = menu[0].graph;
    graph(activeGraph, activeUrl);
//    getAsetNetto($("#grafik").val(), activeUrl);
//    dateFieldControl($("#periode").val());
    setMenu(menu);
    activeButton();
//    chart('horizontalBar');
};

function getJson(url, callback) {
    var request = new XMLHttpRequest();

    request.open('GET', url, true);

    request.onload = function () {
        if (request.status >= 200 && request.status < 400) {
            var json = JSON.parse(request.responseText);
            callback(json);
        } else {
            console.error("There was an error connecting to the server. The error code was " + request.status);
            return;
        }
    };

    request.onerror = function () {
        console.error("There was an error connecting to the server.");
        return;
    };

    request.send();
}

function postJson(url, obj, callback) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: url,
        success: function (resp) {
            callback(resp);
        }
    });
}

function graph(graph, url) {
    activeUrl = url;
    activeGraph = graph;
    var obj = {};
    var kodePeriode = $("#kodePeriode").val();
    var kodeTahunBuku = $("#kodeTahunBuku").val();
    var kodeDRI = $("#kodeDRI").val();
//    var url = _baseUrl + '/akunting/home/aset-netto/get';
//    var url = url;
    obj.kodePeriode = kodePeriode;
    obj.kodeTahunBuku = kodeTahunBuku;
    obj.kodeDRI = kodeDRI;

    postJson(url, obj, function (data) {
        var uraian = data.map(v => parseInt(v.uraian) ? months[parseInt(v.uraian)-1] : v.uraian);
        var periode = data.map(v => v.periode).filter((v, i, d) => d.indexOf(v) == i);
//        console.log(periode);
        var totalSaldoBerjalan = data.map(v => v.totalSaldoBerjalan);
        var totalSaldoSebelum = data.map(v => v.totalSaldoSebelum);

        var totalPeriode = [];
        totalPeriode.push(totalSaldoBerjalan);
//        totalPeriode.push(totalSaldoSebelum);

        var data2;
        var label2;
        var data3;
        var label3;
        if (graph == "") {
//            console.log(data);
            var phu = data[0];
            totalPeriode = phu;
            uraian = phu.map(v => v.uraian);
            var phup = data[1];
            data2 = phup;
            var phub = data[2];
            data3 = phub;
//            console.log(phu);
//            console.log(phup);
//            console.log(phub);
        } else if (graph == "ag") {
            var aseto = data[0];
            totalPeriode = aseto;
            uraian = aseto.map(v => v.uraian);
            var asetb = data[1];
            data2 = asetb;
            label2 = asetb.map(v => v.uraian);
            var asetp = data[2];
            data3 = asetp;
            label3 = asetp.map(v => v.uraian);
        } else if (graph == "roi") {
//            console.log(data);
            totalPeriode = data;
            uraian = data.map(v => v.uraian);
        }
        chart(graph, uraian, totalPeriode, label2, data2, label3, data3);
    });
}

function chart(type, label, data, label2, data2, label3, data3) {
    var labelString = ["Saldo Berjalan", "Saldo Sebelum"];
    var value = data.map((v, index) => {
//        console.log(index);
        var obj = {
            "values": [],
            "text": labelString[index]
        };
        obj.values = v;
        return obj;
    });

//    console.log(data);
    var myConfig = {};
    if (type != "ag" && type != "" && type != "roi") {
        myConfig = {
          "graphset": [{
            "type": type,
            "height": "400px",
            "plotarea": {
              'adjust-layout': true
            },
            "plot": {
                "tooltip": {
                    "text": "%t: %v",
                    "thousands-separator": ".",
                    "decimalsSeparator": ","
                },
                "styles": ["red","orange","yellow","green","blue","purple","brown","black"],
                "barMaxWidth": "110",
    //            "barSpace": "10%"
            },
            "scale-x": {
              "label": { /* Scale Title */
                "text": "",
              },
              "labels": label /* Scale Labels */
            },
            "scale-y": {
    //          "short": true,
              "thousands-separator": "."
            },
            "series": value
          }]
        };
    } else if (type == "") {
//        console.log(data2);
        var graphset = [
            {
                "type": "pie",
                "height": '300px',
                "width": '50%',
                "x": '0%',
                "y": '0%',
                "legend": {
                    "x": "0%",
                    "y": "300px",
//                    'adjust-layout': true,
                    "visible": data2.length == 0 ? "false" : "true"
//                    "layout": "1x5"
                },
                "title": {
                    "x": "0%",
                    "text": "Pendapatan",
//                    'adjust-layout': true
                },
                "plot": {
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    }
                },
                "series": data2.map(v => {
                    var obj = {
                        "values": [v.totalSaldoBerjalan],
                        "text": v.uraian
                    }
                    return obj;
                })
            },
            {
                "type": "pie",
                "height": '300px',
                "width": '50%',
                "x": '50%',
                "y": '0%',
                "legend": {
                    "x": "0%",
                    "y": "300px",
//                    'adjust-layout': true,
                    "visible": data3.length == 0 ? "false" : "true"
                },
                "title": {
                    "text": "Beban",
//                    'adjust-layout': true
                },
                "plot": {
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    }
                },
                "series": data3.map(v => {
                    var obj = {
                        "values": [v.totalSaldoBerjalan],
                        "text": v.uraian
                    }
                    return obj;
                })
            },
            {
                "type": "bar",
                "height": '400px',
                "plotarea": {
                  'adjust-layout': true
                },
                "title": {
                    "text": "Pendapatan Hasil Usaha",
                    'adjust-layout': true
                },
                "width": '100%',
                "legend": {
                    "align": "center",
                    "vertical-align": "bottom"
                },
                "x": '0%',
                "y": '450px',
                "plot": {
//                    "barsOverlap": screen.width == 1920 ? '61%' : screen.width == 1366 ? '27%' : '10%',
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    },
//                    "styles": ["red","orange","yellow","green","blue","purple","brown","black"],
                    "barMaxWidth": "170",
                    "aspect": 'histogram',
                },
                "scale-x": {
                  "label": { /* Scale Title */
                    "text": "",
                  },
                  "labels": label /* Scale Labels */
                },
                "scale-y": {
                  "thousands-separator": "."
                },
                "series": [
                    {
                        "values": data.map(v => v.totalPendapatan),
                        "text": "Total Pendapatan"
                    },
                    {
                        "values": data.map(v => v.totalBeban),
                        "text": "Total Beban"
                    }
                ]
            }
        ];

        myConfig = {
//          "layout": "1x1",
          "graphset": graphset
        };
    } else if (type == "ag") {
//        console.log(data);
        var graphset = [
            {
                "type": "line",
                "height": '300px',
                "width": "95%",
                "plotarea": {
                  'adjust-layout': true
                },
                "legend": {
                    "layout": "1x2"
                },
                "title": {
                    "text": "Aset",
                    "adjust-layout": true
                },
                "plot": {
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    },
                    "barMaxWidth": "110",
                },
                "scale-x": {
                  "label": { /* Scale Title */
                    "text": "",
                  },
                  "labels": label /* Scale Labels */
                },
                "scale-y": {
                  "thousands-separator": "."
                },
                "series": [
                    {
                        "values": data.map(v => v.totalAnggaran),
                        "text": "Total Anggaran"
                    },
                    {
                        "values": data.map(v => v.totalRealisasi),
                        "text": "Total Realisasi"
                    }
                ]
            },
            {
                "type": "line",
                "height": '300px',
                "width": "95%",
                "x": "0px",
                "y": "350px",
                "plotarea": {
                  'adjust-layout': true
                },
                "legend": {
                    "layout": "1x2"
                },
                "title": {
                    "text": "Pendapatan",
                    "adjust-layout": true
                },
                "plot": {
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    },
                    "barMaxWidth": "110",
                },
                "scale-x": {
                  "label": { /* Scale Title */
                    "text": "",
                  },
                  "labels": label3 /* Scale Labels */
                },
                "scale-y": {
                  "thousands-separator": "."
                },
                "series": [
                    {
                        "values": data3.map(v => v.totalAnggaran),
                        "text": "Total Anggaran"
                    },
                    {
                        "values": data3.map(v => v.totalRealisasi),
                        "text": "Total Realisasi"
                    }
                ]
            },
            {
                "type": "line",
                "height": '300px',
                "width": "95%",
                "x": "0px",
                "y": "700px",
                "plotarea": {
                  'adjust-layout': true
                },
                "legend": {
                    "layout": "1x2"
                },
                "title": {
                    "text": "Beban",
                    "adjust-layout": true
                },
                "plot": {
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    },
                    "barMaxWidth": "110",
                },
                "scale-x": {
                  "label": { /* Scale Title */
                    "text": "",
                  },
                  "labels": label2 /* Scale Labels */
                },
                "scale-y": {
                  "thousands-separator": "."
                },
                "series": [
                    {
                        "values": data2.map(v => v.totalAnggaran),
                        "text": "Total Anggaran"
                    },
                    {
                        "values": data2.map(v => v.totalRealisasi),
                        "text": "Total Realisasi"
                    }
                ]
            }
        ];
        myConfig = {
          "graphset": graphset
        };
    } else if (type == "roi") {
        var graphset = [
            {
                "type": "bar",
                "height": '400px',
                "plotarea": {
                  'adjust-layout': true
                },
                "title": {
                    "text": "ROI dan ROA",
                    'adjust-layout': true
                },
                "width": '100%',
                "legend": {
                    "align": "center",
                    "vertical-align": "bottom"
                },
                "plot": {
//                    "barsOverlap": screen.width == 1920 ? '61%' : screen.width == 1366 ? '27%' : '10%',
                    "tooltip": {
                        "text": "%t: %v",
                        "thousands-separator": ".",
                        "decimalsSeparator": ","
                    },
//                    "styles": ["red","orange","yellow","green","blue","purple","brown","black"],
                    "barMaxWidth": "170",
                    "aspect": 'histogram',        /* "cone" | "cylinder" | "pyramid" | "histogram" */
//                    "stacked": false
                },
                "scale-x": {
                  "label": { /* Scale Title */
                    "text": "",
                  },
                  "labels": label /* Scale Labels */
                },
                "scale-y": {
                  "thousands-separator": "."
                },
                "series": [
                    {
                        "values": data.map(v => v.totalROI),
                        "text": "Total ROI"
                    },
                    {
                        "values": data.map(v => v.totalROA),
                        "text": "Total ROA"
                    }
                ]
            }
        ];
        myConfig = {
          "graphset": graphset
        };
    }

//    console.log(myConfig);

    zingchart.render({
      id: 'myChart',
      data: myConfig,
      height: type != "" && type != "ag" ? '' : '1000px',
      width: '100%'
    });
}

function changeGraph(graph) {
    getAsetNetto(graph, activeUrl);
}

function dateFieldControl(value) {
    formFilterReset();
    $(".date").prop("hidden", true);
    for (var i = 0; i <= value; i++) {
        $("#date" + i + "picker").prop("hidden", false);
    }
//    changeGraph($("#grafik").val());
}

function datePicker(value) {
//    console.log(value[0].offsetParent.id);
    var parentId = value[0].offsetParent.id;
    $("#" + parentId).datepicker({
        format: "mm/yyyy",
        startView: "months",
        minViewMode: "months",
        autoclose: true,
        onSelect: function (evt) {console.log(evt)}
    }, "show").on("change", function () {changeGraph($("#grafik").val())});
}

function formFilterReset() {
    $(".input-date").val("");
}

function customStringDate(value) {
    if (value == "")
        return "";

    var split = value.split("/");
    var res = split[1] + "-" + split[0];
    return res;
}

function setMenu(data) {
    data.forEach(v => {
        var markup = "<button type='button' class='"+v.classHTML+"' id='"+v.idHTML+"' "+v.hide+">"+v.title+"</button>";
        $("#menu-tabs").append(markup);
    });
}

function activeButton() {
    var idx = 0;
    $("#menu-tabs button.tabs").each(function () {
//        console.log($(this));
        var currentButton = $(this);
        var url = menu[idx].url;
        var title = menu[idx].titleDesc;
        var type = menu[idx].graph;
        currentButton.on("click", function () {
            $("#title").text(title);
            $("#kodePeriode").val(today[1]).selectpicker("refresh");
            $(".active-button").removeClass("active-button");
            currentButton.addClass("active-button");
            graph(type, url);
        });
        idx++;
    });
}

function exportPdf() {
    var namaLaporan = $("#title").text();
    var chart = "";
    var obj = {
        "chart": "",
        "namaLaporan": namaLaporan
    };
    zingchart.exec('myChart', 'getimagedata', {
        filetype : 'png',
        callback : function(imagedata) {
//          console.log(imagedata);
          obj.chart = imagedata;
          $.ajax({
              type: "POST",
              contentType: "application/json",
              data: JSON.stringify(obj),
              url: _baseUrl + "/akunting/home/export-pdf",
              success: function (response) {
                  var link = document.createElement("a");
                  link.href = "data:application/pdf;base64, " + response;
                  link.download = "Dashboard.pdf";
                  link.target = "_blank";

                  var byteCharacters = atob(response);
                  var byteNumbers = new Array(byteCharacters.length);
                  for (var i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                  }
                  var byteArray = new Uint8Array(byteNumbers);
                  var file = new Blob([byteArray], { type: 'application/pdf;base64' });
                  var fileURL = URL.createObjectURL(file);
                  window.open(fileURL);
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
    });
}