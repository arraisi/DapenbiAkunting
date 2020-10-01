var _currentSPIHdr = {
    idSPIHdr: -1,
    kodeTahunBuku: {
        kodeTahunBuku: ''
    },
    kodePeriode: {
        kodePeriode: ''
    },
    tglSPI: moment(new Date).format("YYYY-MM-DD")
};
var _currentSPIDetails = [];
var _currentDetailsMaster = [];
var _spiHeaderDatatable;
var _spiDetailDatatable;
var _pengaturanSistem;
var months = ["Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"];

jQuery(document).ready(function () {
    $('#txtTglSPI').val(moment(new Date).format("YYYY-MM-DD"));
    findPengaturanSistem();
    spiHeaderDatatable();
    spiDetailDatatable();

    // DATEPICKER
    $(".datepicker-group").datepicker({
        todayHighlight: true,
        autoclose: true,
        orientation: "bottom",
        format: "yyyy-mm-dd"
    });
});

function save(statusData) {
    if (_currentSPIHdr.idSPIHdr === -1) _currentSPIHdr.idSPIHdr = null;
    _currentSPIHdr.kodeTahunBuku.kodeTahunBuku = $('#txtKodeTahunBuku').val();
    _currentSPIHdr.kodeTahunBuku.namaTahunBuku = $('#txtTahunBuku').val();
    _currentSPIHdr.kodePeriode.kodePeriode = $('#txtPeriode').val();
    _currentSPIHdr.tglSPI = new Date($('#txtTglSPI').val());
    _currentSPIHdr.statusData = statusData;
    _currentSPIHdr.spiDetails = _currentSPIDetails;

    if (_currentSPIHdr.kodePeriode.kodePeriode === null) {
        showWarning("Inputan tidak boleh kosong.")
        return;
    }

    $('#spiDialog').modal('hide');

    console.log(_currentSPIHdr, "obj save");
    serviceSaveHeaderAndDetails();
}

function findPengaturanSistem() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/parameter/pengaturan-sistem/findDTOByStatusAktif",
        success: function (response) {
            // console.log(response, "RESPONSE");
            _pengaturanSistem = response;
        },
        statusCode: {
            500: function (data) {
                showError();
            }
        }
    });
}

// function periodeOnChanged() {
//     _currentSPIHdr.idSPIHdr = null;
//     _currentSPIHdr.kodePeriode = $('#txtPeriode').val();
//     console.log(_currentSPIHdr);
//     _spiDetailDatatable.ajax.reload();
// }

// SPI HDR DATATABLES
function spiHeaderDatatable() {
    _spiHeaderDatatable = $("#spiHeaderDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/transaksi-spi/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                data: 'kodeTahunBuku',
                title: 'Tahun Buku',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data.kodeTahunBuku;
                }
            },
            {
                data: 'kodePeriode',
                title: 'Kode Periode',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data.kodePeriode;
                }
            },
            {
                data: 'tglSPI',
                title: 'Tanggal',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return moment(data).format("YYYY-MM-DD");
                }
            },
            {
                data: 'createdDate',
                title: 'Tanggal Input',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return moment(data).format("YYYY-MM-DD");
                }
            },
            {
                data: 'createdBy',
                title: 'User Input',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'statusData',
                title: 'Status Data',
                className: 'text-center',
                defaultContent: "",
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                title: "Tindakan",
                width: "100px",
                class: "text-center",
                sortable: false,
                orderable: false,
                searchable: false,
                render: function (data, type, row, meta) {
                    return getButtonGroup(true, true, true, true, "PDF", "la la-file-pdf-o", true, "EXCEL", "la la-file-excel-o");
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // SELECT ROW
    $('#spiHeaderDatatable tbody').on('click', 'tr', function () {
        if (!$(this).hasClass('selected')) {
            _spiHeaderDatatable.$('tr.selected').removeClass('selected');
            var tr = $(this).closest('tr');
            tr.addClass('selected');
            var row = _spiHeaderDatatable.row(tr);
            _currentSPIHdr = row.data();
            // console.log(_currentSPIHdr);
            _spiDetailDatatable.ajax.reload();
        }
    });

    // EDIT ROW
    $('#spiHeaderDatatable tbody').on('click', '.edit-button', function () {
        var tr = $(this).closest('tr');
        var row = _spiHeaderDatatable.row(tr);
        _currentSPIHdr = row.data();
        // console.log(_currentSPIHdr);
        $('#txtPeriode').val(_currentSPIHdr.kodePeriode.kodePeriode).selectpicker("refresh");
        $('#txtKodeTahunBuku').val(_currentSPIHdr.kodeTahunBuku.kodeTahunBuku);
        $('#txtTahunBuku').val(_currentSPIHdr.kodeTahunBuku.namaTahunBuku.slice(-4));
        $('#txtTglSPI').val(_currentSPIHdr.tglSPI.substr(0, 10));

        $('#simpanSPIBtn').attr('hidden', false);
        $('#submitSPIBtn').attr('hidden', false);
        findSPIDetails();
    });

    // DETAIL ROW
    $('#spiHeaderDatatable tbody').on('click', '.detail-button', function () {
        var tr = $(this).closest('tr');
        var row = _spiHeaderDatatable.row(tr);
        _currentSPIHdr = row.data();
        // console.log(_currentSPIHdr);
        $('#txtPeriode').val(_currentSPIHdr.kodePeriode.kodePeriode);
        $('#txtKodeTahunBuku').val(_currentSPIHdr.kodeTahunBuku.kodeTahunBuku);
        $('#txtTahunBuku').val(_currentSPIHdr.kodeTahunBuku.namaTahunBuku.slice(-4));
        $('#txtTglSPI').val(_currentSPIHdr.tglSPI.substr(0, 10));
        $('#simpanSPIBtn').attr('hidden', true);
        $('#submitSPIBtn').attr('hidden', true);
        findSPIDetails();
    });

    $('#tambahDataBtn').on('click', function (data) {
        // console.log('button tambah data');
        resetForm();
        resetTableDetails();
        $('#simpanSPIBtn').attr('hidden', false);
        $('#submitSPIBtn').attr('hidden', false);
    })

    // BUTTON DELETE
    $('#spiHeaderDatatable tbody').on('click', '.delete-button', function () {
        var data = _spiHeaderDatatable.row($(this).parents('tr')).data();
        swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            type: "warning",
            showCancelButton: !0,
            confirmButtonText: "Yes, delete it!"
        }).then(function (e) {
            if (e.value) {
                $.ajax({
                    type: "DELETE",
                    contentType: "application/json",
                    data: JSON.stringify(_currentSPIHdr.idSPIHdr),
                    url: _baseUrl + `/akunting/laporan/transaksi-spi/delete`,
                    success: function (resp) {
                        showSuccess();
                        _spiHeaderDatatable.ajax.reload();
                        _spiDetailDatatable.ajax.reload();
                    },
                    statusCode: {
                        500: function () {
                            showError();
                        }
                    }
                });
            }
        });
    });

    $("#spiHeaderDatatable tbody").on("click", ".additional1-button", function () {
        var data = _spiHeaderDatatable.row($(this).parents("tr")).data();
//        console.log(data);
        exportPDF(data.idSPIHdr);
    });

    $("#spiHeaderDatatable tbody").on("click", ".additional2-button", function () {
        var data = _spiHeaderDatatable.row($(this).parents("tr")).data();
//        console.log(data);
        exportEXCEL(data.idSPIHdr);
    });
}

function findSPIDetails() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/laporan/transaksi-spi/findByIdSPIHdr/${_currentSPIHdr.idSPIHdr}`,
        success: function (response) {
            // console.log(response, "RESPONSE");
            _currentSPIDetails = response;
            fatchTableDetails(_currentSPIDetails.sort(resetOrders));
            $('#spiDialog').modal('show');
        },
        statusCode: {
            500: function (data) {
                showError();
            }
        }
    });
}

// temporary
function findInvestasiDetails() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/laporan/master-spi/findAllDetails/${$('#txtInvestasiHdr').val()}`,
        success: function (response) {
            console.log(response, "RESPONSE");
            _currentDetailsMaster = response;
            fatchTableDetailsMaster(response);
        },
        statusCode: {
            500: function (data) {
                showError();
            }
        }
    });
}

// SPI DTL DATATABLES
function spiDetailDatatable() {
    _spiDetailDatatable = $("#spiDetailDatatable").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/transaksi-spi/datatables/detail',
            type: "POST",
            data: function (d) {
                d.spiDetailDTO = _currentSPIHdr;
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                data: 'idSPIDtl',
                title: 'No.',
                className: 'text-center',
                defaultContent: "",
                width: '8%',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'keteranganSPI',
                title: 'SPI',
                // className: 'text-center',
                defaultContent: "",
                width: '30%',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                data: 'nilaiPerolehan',
                title: 'Nilai Perolehan',
                className: 'text-right',
                defaultContent: "",
                width: '20%',
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'nilaiWajar',
                title: 'Nilai Wajar',
                className: 'text-right',
                defaultContent: "",
                width: '20%',
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            },
            {
                data: 'nilaiSPI',
                title: 'Nilai SPI',
                className: 'text-right',
                defaultContent: "",
                width: '20%',
                render: function (data, type, row, meta) {
                    return numeralformat(data);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });
}

function serviceSaveHeaderAndDetails() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(_currentSPIHdr),
        url: _baseUrl + `/akunting/laporan/transaksi-spi/save/header/details`,
        success: function (response) {
            _spiHeaderDatatable.ajax.reload();
            _spiDetailDatatable.ajax.reload();
            resetForm();
            resetTableDetails();
            // console.log(response, "RESPONSE SAVE WARKAT");
        },
        statusCode: {
            200: function () {
                showSuccess();
            }
        },
        complete: function (data) {
            // console.log(data);
            if (data.status === 202) {
                swal.fire({
                    title: "Gagal",
                    text: data.responseText,
                    type: "warning",
                    confirmButtonText: "Tutup"
                });
            } else if (data.status === 500) {
                showError();
            }
        }
    });
}

function numeralformat(value) {
    return numeral(value).format('0,0.00')
}

function fatchTableDetails(data) {
    // console.log(data);
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    if (data.length !== 0) {
        for (var i = 0; i < Object.keys(data).length; i++) {
            let id = data[i].idSPIDtl === null ? 'Generate New' : data[i].idSPIDtl;
            var tr = "<tr>";
            tr += "<td class='text-center' style='vertical-align: middle'>" + id + "</td>" +
                "<td style='vertical-align: middle'>" + data[i].keteranganSPI + "</td>" +
                "<td class='text-right' style='vertical-align: middle'>" + numeralformat(data[i].nilaiPerolehan) + "</td>" +
                "<td class='text-right' style='vertical-align: middle'>" + numeralformat(data[i].nilaiWajar) + "</td>" +
                "<td class='text-right' style='vertical-align: middle'>" + numeralformat(data[i].nilaiSPI) + "</td>" +
                "<td class='text-center'>" +
                `<a href='javascript:void(0);' class='btn btn-sm btn-clean btn-icon btn-icon-md' title='Delete' value="Delete" onclick="deleteDetailMasterBtnPressed(this)"><i class='la la-trash'></i></a>` +
                "</td>";
            tr += "</tr>";
            tbody.innerHTML += tr;
        }
    } else {
        resetTableDetails();
    }
}

// temporary
function fatchTableDetailsMaster(data) {
    // console.log(data);
    var tbody = document.getElementById('tbody-detail');
    tbody.innerHTML = '';
    if (data.length !== 0) {
        for (var i = 0; i < Object.keys(data).length; i++) {
            var tr = "<tr>";
            tr += "<td>" + data[i].keteranganSpi + "</td>" +
                "<td class='text-right' style='vertical-align: middle'>" + `<input type="text" id="nilaiPerolehan-${i}" class="form-control text-right" placeholder='0' oninput='inputNilaiPerolehan(this)'/>` + "</td>" +
                "<td class='text-right' style='vertical-align: middle'>" + `<input type="text" id="nilaiWajar-${i}" class="form-control text-right" placeholder='0' oninput='inputNilaiWajar(this)'/>` + "</td>" +
                "<td class='text-right' style='vertical-align: middle'>" + `<input type="text" id="nilaiSPI-${i}" class="form-control text-right" placeholder='0' readonly/>` + "</td></tr>";
            tbody.innerHTML += tr;
        }
    } else {
        resetTableDetailsMaster();
    }
}

// temporary
function inputNilaiPerolehan(data) {
    var index = $(data).closest("tr").index();
    data.value === '' ? _currentDetailsMaster[index].nilaiPerolehan = '0' : _currentDetailsMaster[index].nilaiPerolehan = data.value.replace(/,/g, '');
    $(`#nilaiPerolehan-${index}`).val(numeralformat(data.value));
    countNilaiSPI(index);
}

// temporary
function inputNilaiWajar(data) {
    var index = $(data).closest("tr").index();
    data.value === '' ? _currentDetailsMaster[index].nilaiWajar = '0' : _currentDetailsMaster[index].nilaiWajar = data.value.replace(/,/g, '');
    $(`#nilaiWajar-${index}`).val(numeralformat(data.value));
    countNilaiSPI(index);
}

// temporary
function countNilaiSPI(index) {
    let nilaiSPI = $(`#nilaiWajar-${index}`).val().replace(/,/g, '') - $(`#nilaiPerolehan-${index}`).val().replace(/,/g, '');
    _currentDetailsMaster[index].nilaiSPI = nilaiSPI;
    $(`#nilaiSPI-${index}`).val(numeralformat(nilaiSPI));
}

// temporary
function deleteDetailMasterBtnPressed(data) {
    var index = $(data).closest("tr").index();
    console.log(_currentSPIDetails[index]);
    swal.fire({
        title: "Are you sure?",
        text: "You won't be able to revert this!",
        type: "warning",
        showCancelButton: !0,
        confirmButtonText: "Yes, delete it!"
    }).then(function (e) {
        if (e.value) {
            if (_currentSPIDetails[index].idSPIDtl != null) {
                $.ajax({
                    type: "DELETE",
                    contentType: "application/json",
                    data: JSON.stringify(_currentSPIDetails[index].idSPIDtl),
                    url: _baseUrl + `/akunting/laporan/transaksi-spi/detail/delete`,
                    success: function (resp) {
                        showSuccess();
                        _spiDetailDatatable.ajax.reload();
                    },
                    statusCode: {
                        500: function () {
                            showError();
                        }
                    }
                });
            }
            _currentSPIDetails.splice(index, 1);
            var row = data.parentNode.parentNode;
            row.parentNode.removeChild(row);
        }
    });
}

// temporary
function simpanDetailMaster() {
    console.log(_currentDetailsMaster);
    _currentDetailsMaster.map(value => {
        let object = {
            idSPIDtl: null,
            keteranganSPI: value.keteranganSpi,
            nilaiPerolehan: value.nilaiPerolehan,
            nilaiWajar: value.nilaiWajar,
            nilaiSPI: value.nilaiSPI,
            kodeTahunBuku: {kodeTahunBuku: $('#txtKodeTahunBuku').val()},
            kodePeriode: {kodePeriode: $('#txtPeriode').val()},
            tglSPI: $('#txtTglSPI').val(),
            idInvestasi: value.idInvestasi,
            idSPI: value.idSpi
        }
        _currentSPIDetails.push(object);
    });
    fatchTableDetails(_currentSPIDetails.sort(resetOrders));
}

function resetForm() {
    _currentSPIHdr.idSPIHdr = -1;
    $('#txtPeriode').val('').selectpicker('refresh');
    $('#txtKodeTahunBuku').val(_pengaturanSistem.kodeTahunBuku.kodeTahunBuku);
    $('#txtTahunBuku').val(_pengaturanSistem.kodeTahunBuku.namaTahunBuku.slice(-4));
}

function resetTableDetails() {
    var tbody = document.getElementById('tbody-jurnal');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"6\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

function resetTableDetailsMaster() {
    var tbody = document.getElementById('tbody-detail');
    tbody.innerHTML = '';
    var tr = "<tr style=\"background-color: #f0f0f0\">";
    tr += "<td colspan=\"4\" class=\"text-center\">Tidak ada data</td>" +
        "</tr>";
    tbody.innerHTML += tr;
}

function resetOrders(a, b) {
    const urutanA = a.idSPIDtl;
    const urutanB = b.idSPIDtl;

    let comparison = 0;
    if (urutanA > urutanB) {
        comparison = 1;
    } else if (urutanA < urutanB) {
        comparison = -1;
    }
    return comparison;
}

function exportPDF(kodeSPIHDR) {
//    var obj = getDatatableValue(table.rows().data());
    var obj = {};
    var rawTanggal = moment(new Date).format("DD/MM/YYYY").split("/");
    var tanggal = rawTanggal[0] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[2];

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-spi/export-pdf?tanggal=" + tanggal + "&kodeSPIHDR=" + kodeSPIHDR,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/pdf;base64, " + response;
            link.download = "Laporan SPI " + tanggal + ".pdf";
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

function exportEXCEL(kodeSPIHDR) {
    var obj = {};
    var rawTanggal = moment(new Date).format("DD/MM/YYYY").split("/");
    var tanggal = rawTanggal[0] + " " + months[rawTanggal[1]-1] + " " + rawTanggal[2];

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/laporan-spi/export-excel?tanggal=" + tanggal + "&kodeSPIHDR=" + kodeSPIHDR,
        success: function (response) {
            var link = document.createElement("a");
            link.href = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64, " + response;
            link.download = "Laporan SPI " + tanggal + ".xlsx";
            link.target = "_blank";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
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