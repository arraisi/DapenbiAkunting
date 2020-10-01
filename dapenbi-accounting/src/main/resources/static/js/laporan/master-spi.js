var isActionBtn = false;
var statusAksi;
var idInvestasi = 0;
var tableInv = null;
var tableSpi = null;

jQuery(document).ready(function () {
    setTableInv();

    $("#btnAddInv").click(function () {
        statusAksi = "create";
        formResetInv();
        $('#masterInvDialog').modal('show');
    });

    $("#btnAddSPI").click(function () {
        let kodeInv = $('#txtSpiKdInv').val();
        if (kodeInv === '') {
            showWarning("Wait, Master Investasi belum dipilih!", 2000);
            return};
        statusAksi = "create";
        formResetSpi();
        $('#ketSPIDialog').modal('show');
    });

    $("#btnCancelSPI").click(function () {
        $('#ketSPIDialog').modal('hide');
    });

    $("#btnCancelInv").click(function () {
        $('#masterInvDialog').modal('hide');
    });

    $("#cmbIdLaporan").change(function () {
        setCmbKeterangan($(this).val(), null);
    });

    $('#masterInvDialog').on('hidden.bs.modal', function () {
        isActionBtn = false;
    });

    $('#deleteDialogInv').on('hidden.bs.modal', function () {
        isActionBtn = false;
    });
});

function setTableInv() {
    tableInv = $("#tblMasterInv").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/master-spi/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [0, "asc"],
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'Investasi',
                data: 'idInvestasi',
                orderable: false,
                className: 'text-center'
            },
            {
                title: 'Nama Investasi',
                data: 'namaInvestasi',
            },
            {
                title: 'ID Laporan',
                data: 'idLaporanHeader',
            },
            {
                title: 'ID Keterangan',
                data: 'idLaporanDetail',
            },
            {
                title: 'Status',
                data: 'statusData',
                className: 'text-center',
                render: function (data) {
                    if (data == "N") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle">';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked>'
                    }
                }
            },
            {
                searchable: false,
                sortable: false,
                orderable: false,
                class: "text-center",
                title: "Tindakan",
                width: "100px",
                defaultContent: getButtonGroup(false, false, false),
                render: function (data, type, row, meta) {
                    return getButtonGroup(true, true, false);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    tableSpi = $("#tblSPI").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        order: [1, "asc"],
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        createdRow: function (row, data, dataIndex) {
            $(row).css("height", "31px");
        },
        columns: [
            {
                title: 'SPI',
                data: 'idSpi',
                orderable: false,
                className: 'text-center'
            },
            {
                title: 'Keterasngan SPI',
                data: 'keteranganSpi',
            },
            {
                title: 'Status',
                data: 'statusData',
                className: 'text-center',
                render: function (data) {
                    if (data == "N") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle">';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked>'
                    }
                }
            },
            {
                searchable: false,
                sortable: false,
                orderable: false,
                class: "text-center",
                title: "Tindakan",
                width: "100px",
                defaultContent: getButtonGroup(false, false, false),
                render: function (data, type, row, meta) {
                    return getButtonGroup(true, true, false);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    initEventInv(tableInv, tableSpi);
}

function initEventInv(tableInv, tableSpi) {
    $("#btnSaveInv").click(function () {
        if (statusAksi === "create") saveInv(tableInv);
        else if (statusAksi === "edit") updateInv(tableInv);
    });

    $("#btnSaveSPI").click(function () {
        if (statusAksi === "create") saveSpi(tableSpi);
        else if (statusAksi === "edit") updateSpi(tableSpi);
    });

    $("#btnDeleteInv").click(function () {
        deleteInv(tableInv);
    });

    $("#btnDeleteSpi").click(function () {
        deleteSpi(tableSpi);
    });

    $('#tblMasterInv tbody').on('click', 'tr', function () {
        if (!isActionBtn) {
            tableInv.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');

            var row = tableInv.row($(this).closest('tr'));
            idInvestasi = row.data().idInvestasi;
            tableSpi.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();

            $("#txtSpiKdInv").val(row.data().idInvestasi);
            $("#divKetSPI").attr("hidden", false);
        }
    });

    $("#tblMasterInv tbody").on("click", ".edit-button", function () {
        isActionBtn = true;
        statusAksi = "edit";

        formResetInv();
        $('#masterInvDialog').modal('show');

        var data = tableInv.row($(this).parents('tr')).data();
        setValueInv(data);
    });

    $("#tblMasterInv tbody").on("click", ".delete-button", function () {
        isActionBtn = true;
        $('#deleteDialogInv').modal('show');
        var data = tableInv.row($(this).parents('tr')).data();
        $("#txtDeleteIdInv").val(data.idInvestasi);
    });

    $("#tblMasterInv tbody").on("click", ".btn-check", function () {
        isActionBtn = true;
        var data = tableInv.row($(this).parents('tr')).data();
        var status = data.statusData;
        if (status == "N") status = "Y";
        else if (status == "Y") status = "N";

        updateStatusInv(tableInv, data.idInvestasi, status)
    });

    $("#tblSPI tbody").on("click", ".edit-button", function () {
        statusAksi = "edit";

        formResetInv();
        $('#ketSPIDialog').modal('show');

        var data = tableSpi.row($(this).parents('tr')).data();
        setValueSpi(data);
    });

    $("#tblSPI tbody").on("click", ".delete-button", function () {
        $('#deleteDialogSpi').modal('show');
        var data = tableSpi.row($(this).parents('tr')).data();
        $("#txtDeleteIdSpi").val(data.idInvestasiDtl);
    });

    $("#tblSPI tbody").on("click", ".btn-check", function () {
        var data = tableSpi.row($(this).parents('tr')).data();
        var status = data.statusData;
        if (status == "N") status = "Y";
        else if (status == "Y") status = "N";

        updateStatusSpi(tableSpi, data.idInvestasiDtl, status);
    });
}

function saveInv(table) {
    var obj = getValueInv();
    if (isMasterInvValid(obj)) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/laporan/master-spi/save",
            success: function (response) {
                showSuccess();
                $('#masterInvDialog').modal('hide');
                table.ajax.reload();
            },
            statusCode: {
                201: function () {
                    console.log('Saved');
                    showSuccess();
                    $('#masterInvDialog').modal('hide');
                    table.ajax.reload();
                },
                400: function () {
                    showError("Inputan tidak boleh kosong");
                },
                404: function () {
                    showError("Not Found");
                },
                500: function () {
                    showError("Internal Server Error");
                }
            }
        });
    }
}

function saveSpi(table) {
    var obj = getValueSpi();
    if (isSpiValid(obj)) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/laporan/master-spi/save/spi",
            success: function (response) {
                showSuccess();
                $('#ketSPIDialog').modal('hide');
                table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
            },
            statusCode: {
                201: function () {
                    showSuccess();
                    $('#ketSPIDialog').modal('hide');
                    table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
                },
                400: function () {
                    showError("Inputan tidak boleh kosong");
                },
                404: function () {
                    showError("Not Found");
                },
                500: function () {
                    showError("Internal Server Error");
                }
            }
        });
    }
}

function updateInv(table) {
    var obj = getValueInv();
    if (isMasterInvValid(obj)) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/laporan/master-spi/update",
            success: function (response) {
                showSuccess();
                $('#masterInvDialog').modal('hide');
                table.ajax.reload();
                isActionBtn = false;
            },
            statusCode: {
                201: function () {
                    console.log('Saved');
                    showSuccess();
                    $('#masterInvDialog').modal('hide');
                    table.ajax.reload();
                    isActionBtn = false;
                },
                400: function () {
                    showError("Inputan tidak boleh kosong");
                },
                404: function () {
                    showError("Not Found");
                },
                500: function () {
                    showError("Internal Server Error");
                }
            }
        });
    }
}

function updateSpi(table) {
    var obj = getValueSpi();
    if (isSpiValid(obj)) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/laporan/master-spi/update/spi",
            success: function (response) {
                showSuccess();
                $('#ketSPIDialog').modal('hide');
                table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
            },
            statusCode: {
                201: function () {
                    showSuccess();
                    $('#ketSPIDialog').modal('hide');
                    table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
                },
                400: function () {
                    showError("Inputan tidak boleh kosong");
                },
                404: function () {
                    showError("Not Found");
                },
                500: function () {
                    showError("Internal Server Error");
                }
            }
        });
    }
}

function updateStatusInv(table, idInvestasi, status) {
    $.ajax({
        type: "POST",
        url: _baseUrl + "/akunting/laporan/master-spi/update/status-investasi?status=" + status + "&idInvestasi=" + idInvestasi,
        success: function (response) {
            showSuccess();
            table.ajax.reload();
            isActionBtn = false;
        },
        statusCode: {
            201: function () {
                showSuccess();
                table.ajax.reload();
                isActionBtn = false;
            },
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function updateStatusSpi(table, idInvestasiDtl, status) {
    $.ajax({
        type: "POST",
        url: _baseUrl + "/akunting/laporan/master-spi/update/status-spi?status=" + status + "&idInvestasiDtl=" + idInvestasiDtl,
        success: function (response) {
            showSuccess();
            table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
            isActionBtn = false;
        },
        statusCode: {
            201: function () {
                showSuccess();
                table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
                isActionBtn = false;
            },
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function deleteInv(table) {
    var id = $("#txtDeleteIdInv").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/master-spi/delete/" + id,
        success: function (response) {
            showSuccess();
            $('#deleteDialogInv').modal('hide');
            $("#divKetSPI").attr("hidden", true);
            table.ajax.reload();
            isActionBtn = false;
        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#deleteDialogInv').modal('hide');
                $("#divKetSPI").attr("hidden", true);
                table.ajax.reload();
                isActionBtn = false;
            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function deleteSpi(table) {
    var id = $("#txtDeleteIdSpi").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/master-spi/delete/" + id + "/spi",
        success: function (response) {
            showSuccess();
            $('#deleteDialogSpi').modal('hide');
            isActionBtn = false;
            table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#deleteDialogSpi').modal('hide');
                isActionBtn = false;
                table.ajax.url(_baseUrl + '/akunting/laporan/master-spi/datatables/' + idInvestasi + '/detail').load();
            },
            404: function () {
                showError("Not Found");
            },
            500: function () {
                showError("Internal Server Error");
            }
        }
    });
}

function formResetInv() {
    $("#txtKdInv").val("");
    $("#txtKeterangan").val("");
    $("#cmbIdLaporan").val("");
    $("#cmbIdKeterangan").val("");
}

function formResetSpi() {
    $("#txtSpiId").val("");
    $("#txtKdSpi").val("");
    $("#txtKeteranganSpi").val("");
}

function setCmbKeterangan(idLapHdr, idLapDtl) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/master-laporan/detail/" + idLapHdr,
        success: function (resp) {
            $("#cmbIdKeterangan").empty();
            $("#cmbIdKeterangan").append("<option value=''>Pilih Keterangan</option>")
            for (var i = 0; i < resp.length; i++) {
                // console.log(resp[i]);
                var id = resp[i]['idLaporanDtl'];
                // var name = resp[i]['namaRekening'];
                var name = resp[i]['judul'];
                $("#cmbIdKeterangan").append("<option value='" + id + "'>" + id + " - " + name + "</option>");
            }

            if (idLapDtl != null) $("#cmbIdKeterangan").val(idLapDtl);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function getValueInv() {
    var obj = {
        "idInvestasi": $("#txtKdInv").val(),
        "namaInvestasi": $("#txtKeterangan").val(),
        "idLaporanHeader": $("#cmbIdLaporan").val(),
        "idLaporanDetail": $("#cmbIdKeterangan").val(),
        "statusData": $("input[name='rdStatusAktif']:checked").val()
    }
    return obj;
}

function setValueInv(data) {
    $("#txtKdInv").val(data.idInvestasi);
    $("#txtKeterangan").val(data.namaInvestasi);
    $("#cmbIdLaporan").val(data.idLaporanHeader);

    setCmbKeterangan(data.idLaporanHeader, data.idLaporanDetail);

    if (data.statusData == "Y") $("#rsStatusAkifYInv").prop('checked', true);
    else $("#rsStatusAkifNInv").prop('checked', true);
}

function getValueSpi() {
    var obj = {
        "idInvestasiDtl": $("#txtSpiId").val(),
        "idInvestasi": $("#txtSpiKdInv").val(),
        "idSpi": $("#txtKdSpi").val(),
        "keteranganSpi": $("#txtKeteranganSpi").val(),
        "statusData": $("input[name='rdStatusAktif']:checked").val()
    }
    return obj;
}

function setValueSpi(data) {
    $("#txtSpiId").val(data.idInvestasiDtl);
    $("#txtKdSpi").val(data.idSpi);
    $("#txtKeteranganSpi").val(data.keteranganSpi);

    if (data.statusData == "Y") $("#rsStatusAkifYSpi").prop('checked', true);
    else $("#rsStatusAkifNSpi").prop('checked', true);
}

function isMasterInvValid(data) {
    if (data.idInvestasi == "") {
        showError("Kode Investasi Tidak Boleh Kosong");
        return false;
    }

    if (data.namaInvestasi == "") {
        showError("Nama Investasi Tidak Boleh Kosong");
        return false;
    }

    if (data.idLaporanHeader == "") {
        showError("Pilih Laporan");
        return false;
    }
    if (data.idLaporanDetail == "") {
        showError("Pilih Keterangan");
        return false;
    }
    return true;
}

function isSpiValid(data) {
    if (data.idSpi == "") {
        showError("Kode SPI Tidak Boleh Kosong");
        return false;
    }

    if (data.keteranganSpi == "") {
        showError("Keterangan SPI Tidak Boleh Kosong");
        return false;
    }
    return true;
}