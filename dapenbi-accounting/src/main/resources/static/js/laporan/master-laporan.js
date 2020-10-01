var namaRekening;
var txtRekening;
var rowId = 2;
var statusAksi;
var idRekening;
var tableMasterLaporan;
var tableRumusLaporan;
var tableRekeningLaporan;

jQuery(document).ready(function () {
    $("#tglPeriode").val(moment(new Date).format("DD/MM/YYYY"));

    mataAnggranDatatables();

    tableMasterLaporan = $("#tblMasterLap").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/laporan/master-laporan/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
//        ordering: false,
        order: [ 0, "asc" ],
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
        serverSide: true,
        columns: [
            {
                title: 'Urutan',
                data: 'urutan',
                visible: false,
                orderable: true
            },
            {
                title: 'No.',
                data: 'urutan',
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
                title: 'Nama Laporan',
                data: 'namaLaporan',
            },
            {
                title: 'Keterangan',
                data: 'keterangan',
                orderable: false
            },
            {
                title: 'Status',
                data: 'statusData',
                className: 'text-center',
                orderable: false,
                render: function (data) {
                    if(data == "0") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" disabled>';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked disabled>'
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
                render: function (data) {
//                    return "<a href='javascript:void(0);' class='edit-button'>Edit</a>&nbsp &#124 &nbsp<a href='javascript:void(0);' class='delete-button'>Delete</a>&nbsp &#124 &nbsp<a href='javascript:void(0);' class='proses-button'>Proses</a>";
                    return getButtonGroup(false, true, false);
                }
//                defaultContent: getButtonGroup(false, false, false),
//                render: function (data, type, row, meta) {
//                    return getButtonGroup(true, true, false);
//                }
            }
        ],
        bFilter: false,
        bLengthChange: false,
        responsive: true
    });

    initEvent(tableMasterLaporan);
});

function initEvent(table) {
    $("input[name='radioJenis']").click(function(){
        var radioValue = $("input[name='radioJenis']:checked").val();
//        console.log(radioValue);
        if (radioValue == "HARIAN") {
            $("#idDRI").val("").selectpicker("refresh");
            $("#idDRI option[value='2']").prop("disabled", true);
            $("#idDRI option[value='3']").prop("disabled", true);
            $("#idDRI").selectpicker("refresh");
        } else {
            $("#idDRI option[value='2']").prop("disabled", false);
            $("#idDRI option[value='3']").prop("disabled", false);
            $("#idDRI").selectpicker("refresh");
        }

    });

    $("#buttonSaveMasterLaporan").on("click", function() {
        var status = $("#statusModalMasterLaporan").val();
        if (status == "update")
            updateMasterLaporan();
        else
            saveMasterLaporan();
    });

    $("#buttonSaveRumusLaporan").on("click", function() {
        var status = $("#statusModalRumusLaporan").val();
        if (status == "update")
            updateRumusLaporan();
        else
            saveRumusLaporan();
    });

    $("#buttonSaveRekeningLaporan").on("click", function() {
        var status = $("#statusModalRekeningLaporan").val();
        if (status == "update")
            updateRekeningLaporan();
        else
            saveRekeningLaporan();
    });

    laporanRumusDatatables(0);
    laporanRekeningDatatables(0);

    $('#tblRumusLaporan tbody').on('click', 'td', function () {
//        console.log($(this)[0].cellIndex);
        if ($(this)[0].cellIndex == 4)
            return;

        var row = $(this).closest("tr");

        if (row.hasClass('selected')) {
            row.removeClass('selected');
            laporanRekeningDatatables(0);
        } else {
            tableRumusLaporan.$('tr.selected').removeClass('selected');
            row.addClass('selected');
            var data = tableRumusLaporan.row(".selected").data();
            laporanRekeningDatatables(data.idLaporanDtl);
        }
    });

    $('#tblMasterLap tbody').on('click', 'td', function () {
        if ($(this)[0].cellIndex == 4)
            return;

        var row = $(this).closest("tr");

        if (row.hasClass('selected')) {
            row.removeClass('selected');
            laporanRumusDatatables(0);
            laporanRekeningDatatables(0);
        } else {
            tableMasterLaporan.$('tr.selected').removeClass('selected');
            row.addClass('selected');
            var data = tableMasterLaporan.row(".selected").data();
            laporanRumusDatatables(data.idLaporanHdr);
            laporanRekeningDatatables(0);
        }
    });

    $("#addButtonMasterLaporan").on("click", function () {
        resetFormMasterLaporan();
    });

    $("#addButtonRumusLaporan").on("click", function () {
        resetFormRumusLaporan();
        var selected = tableMasterLaporan.rows(".selected").count();
        if (selected == 0) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Pilih Master Laporan terlebih dahulu.",
                showConfirmButton: !1,
                timer: 1500
            });
        } else {
            $("#add-form-rumus-laporan").modal({
                backdrop: 'static',
                keyboard: false
            });
        }
    });

    $("#addButtonRekeningLaporan").on("click", function () {
        resetFormRekeningLaporan();
        var selected = tableRumusLaporan.rows(".selected").count();
        if (selected == 0) {
            swal.fire({
                position: "top-right",
                type: "error",
                title: "Pilih Rumus Laporan terlebih dahulu.",
                showConfirmButton: !1,
                timer: 1500
            });
        } else {
            $("#add-form-rekening-laporan").modal({
                backdrop: 'static',
                keyboard: false
            });
        }
    });

//    $("#tblMasterLap tbody").on("click", ".proses-button", function () {
//        resetFormProsesMasterLaporan();
//        var data = tableMasterLaporan.row($(this).parents("tr")).data();
//        $("#idLaporanHdrProses").val(data.idLaporanHdr);
//        $("#namaLaporanProses").val(data.namaLaporan);
//        $("#namaTabelProses").val(data.namaTabel);
//        $("#form-proses-laporan").modal({
//            backdrop: 'static',
//            keyboard: false
//        });
//    });

    $("#prosesDataButton").on("click", function () {
        resetFormProsesMasterLaporan();
        $("#form-proses-laporan").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblMasterLap tbody").on("click", ".edit-button", function () {
//        formReset();
//        statusAksi = "edit";
//        $('#masterLaporanDialog').modal('show');
//        var data = table.row( $(this).parents('tr') ).data();
//        setMasterLapValue(data);

        var data = tableMasterLaporan.row($(this).parents("tr")).data();
//        console.log(data);
        $("#statusModalMasterLaporan").val("update");
        $("#idLaporanHdr").val(data.idLaporanHdr);
        $("#namaLaporan").val(data.namaLaporan);
        $("#keteranganMasterLaporan").val(data.keterangan);
        $("#namaTabel").val(data.namaTabel).selectpicker("refresh");
        $("#urutanMasterLaporan").val(data.urutan);
        $("input[name='statusAktifMasterLaporan'][value='" + data.statusData + "']").prop('checked', true);

        $("#add-form-master-laporan").modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $("#tblRumusLaporan tbody").on("click", ".edit-button", function () {
        var data = tableRumusLaporan.row($(this).parents("tr")).data();
        $("#statusModalRumusLaporan").val("update");
        $("#idLaporanDtl").val(data.idLaporanDtl);
        $("#kodeRumus").val(data.kodeRumus);
        $("#levelAkun").val(data.levelAkun);
        $("#rumusLaporan").val(data.rumus);
        $("#judul").prop("checked", (data.cetakJudul != null && data.cetakJudul == "1" ? true : false));
        $("#garisBawah").prop("checked", (data.cetakGaris != null && data.cetakGaris == "1" ? true : false));
        $("#spi").prop("checked", (data.spi != null && data.spi == "Y" ? true : false));
        $("#judulLaporan").val(data.judul);
        $("input[name='statusAktifRumusLaporan'][value='" + data.statusData + "']").prop('checked', true);
        $("#urutanDtl").val(data.urutan);
        $("#saldoSebelum").val(formatMoney(data.saldoSebelum));
        $("#statusRumusLaporan").val(data.statusRumus).selectpicker("refresh");

        $("#add-form-rumus-laporan").modal({
            backdrop: 'static',
            keyboard: false
        });
//        console.log(data);
    });

    $("#tblRekeningLaporan tbody").on("click", ".edit-button", function () {
        var data = tableRekeningLaporan.row($(this).parents("tr")).data();
        $("#statusModalRekeningLaporan").val("update");
        $("#idLaporanRek").val(data.idLaporanRek);
        $("#idRekeningLaporanRek").val(data.idRekening).selectpicker("refresh");
        $("#rumusUrutan").val(data.rumusUrutan);
        $("input[name='operatorRekeningLaporan'][value='" + data.rumusOperator + "']").prop('checked', true);
        $("input[name='statusAktifRekeningLaporan'][value='" + data.statusData + "']").prop('checked', true);

        $("#add-form-rekening-laporan").modal({
            backdrop: 'static',
            keyboard: false
        });
        console.log(data);
    });

    $("#tblMasterLap tbody").on("click", ".delete-button", function () {
        $('#deleteDialog').modal('show');
        var data = table.row( $(this).parents('tr') ).data();
        $("#txtDeleteId").val(data.idLaporanHdr);
    });

    $("#tblRumusLaporan tbody").on("click", ".delete-button", function () {
        $('#deleteRumusDialog').modal('show');
        var data = tableRumusLaporan.row( $(this).parents('tr') ).data();
        $("#btnDeleteRumusLaporan").off().on("click", function (e) {
            var obj = {
                "idLaporanDtl": data.idLaporanDtl
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/laporan/master-laporan/laporan-detail/delete",
                success: function (resp) {
                    $("#deleteRumusDialog").modal("hide");
                    showSuccess();
                    tableRumusLaporan.ajax.reload();
                    tableRekeningLaporan.ajax.reload();
                }
            });
        });
    });

    $("#tblRekeningLaporan tbody").on("click", ".delete-button", function () {
        $('#deleteRekeningDialog').modal('show');
        var data = tableRekeningLaporan.row( $(this).parents('tr') ).data();
        $("#btnDeleteRekeningLaporan").off().on("click", function (e) {
            var obj = {
                "idLaporanRek": data.idLaporanRek
            };

            $.ajax({
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(obj),
                url: _baseUrl + "/akunting/laporan/master-laporan/laporan-rekening/delete",
                success: function (resp) {
                    $("#deleteRekeningDialog").modal("hide");
                    showSuccess();
                    tableRekeningLaporan.ajax.reload();
                }
            });
        });
    });

    $("#btnDelete").click(function(){
        deleteById(table);
    });

    $("#btnAdd").click(function(){
        formReset();
        statusAksi = "create";
        $('#masterLaporanDialog').modal('show');
    });

    $("#addRow").click(function () {
        newRow();
        setUrutan();
    });

    $(document).on('click', '#removeRow', function () {
        $(this).closest('#inputFormRow').remove();
        setUrutan();
    });

    $("#btnSave").click(function() {
        if(statusAksi == "create") save(table);
        else if(statusAksi == "edit") update(table);
    });

    $("#btnRekOk").click(function() {
//        console.log(elementId.replace(/^\D+/g, "#idRekening"));
        var fieldRekeningId = elementId.replace(/^\D+/g, "#idRekening");
        $(fieldRekeningId).val(idRekening);
        $(elementId).val(namaRekening);
    });
    
    $("#btnCancel").click(function(){
        $('#masterLaporanDialog').modal('hide');
    });

    $("#buttonSaveProsesLaporan").on("click", function () {
        prosesMasterLaporan();
    });
}

function newRow() {
    var html = '';
    html += '<div class="form-group kt-form__group row laporan-detail" id="inputFormRow">';
    html += '<div class="">';
    html += '<input type="text" id="urutan' + rowId +'"';
    html += 'class="form-control kt-input urutan" style="width: 30px" readonly/>';
    html += '</div>';
    html += '<div class="" hidden>';
    html += '<input type="text" id="idRekening' + rowId +'"';
    html += 'class="form-control kt-input idRekening"/>';
    html += '</div>';
    html += '<div class="widthDetail10">';
    html += '<input type="text" name="test[]" id="txtRekening' + rowId +'"';
    html += 'class="form-control kt-input txtRekening" onclick="showRekeningDialog(this.id)"/>';
    html += '</div>';
    html += '<div class="widthDetail20">';
    html += '<input type="text" id="txtJudul' + rowId + '"';
    html += 'class="form-control kt-input txtJudul"/>';
    html += '</div>';
    html += '<div class="widthDetail5">';
    html += '<input type="number" id="txtLevel' + rowId + '"';
    html += 'class="form-control kt-input txtLevel"/>';
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkShow' + rowId + '"';
    html += 'class="form-check-input checkShow"/>';
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkBold' + rowId +'"';
    html += 'class="form-check-input checkBold"/>'
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkItalic' + rowId + '"';
    html += 'class="form-check-input checkItalic"/>';
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkUnderline' + rowId + '"';
    html += 'class="form-check-input checkUnderline"/>';
    html += '</div>';
    html += '<div class="">';
    html += '<input type="text" id="txtWarna' + rowId + '"';
    html += 'class="form-control kt-input txtWarna" style="width: 70px"/>';
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkSpi' + rowId + '"';
    html += 'class="form-check-input checkSpi"/>';
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkPajak' + rowId + '"';
    html += 'class="form-check-input checkPajak"/>';
    html += '</div>';
    html += '<div class="widthDetail5 checkbox">';
    html += '<input type="checkbox" id="checkJudul' + rowId + '"';
    html += 'class="form-check-input checkJudul"/>';
    html += '</div>';
    html += '<div class="widthDetail10">';
    html += '<input type="text" id="txtRumus' + rowId + '"';
    html += 'class="form-control kt-input txtRumus"/>';
    html += '</div>';
    html += '<div class="widthDetail10">';
    html += '<button id="removeRow" type="button" class="btn btn-danger">Remove';
    html += '</button>';
    html += '</div>';
    html += '</div> ';
    $('#newRow').append(html);
    rowId++;
}

function newRowWithValue(data) {
    var html = '';
    html += '<div class="form-group kt-form__group row laporan-detail" id="inputFormRow">';
        html += '<div class="">';
        html += '<input type="text" id="urutan' + rowId +'"';
        html += 'class="form-control kt-input urutan" style="width: 30px" readonly/>';
        html += '</div>';
        html += '<div class="" hidden>';
        html += '<input type="text" id="idRekening' + rowId +'"';
        html += 'class="form-control kt-input idRekening"/>';
        html += '</div>';
        html += '<div class="widthDetail10">';
        html += '<input type="text" name="test[]" id="txtRekening' + rowId +'"';
        html += 'class="form-control kt-input txtRekening" onclick="showRekeningDialog(this.id)"/>';
        html += '</div>';
        html += '<div class="widthDetail20">';
        html += '<input type="text" id="txtJudul' + rowId + '"';
        html += 'class="form-control kt-input txtJudul"/>';
        html += '</div>';
        html += '<div class="widthDetail5">';
        html += '<input type="number" id="txtLevel' + rowId + '"';
        html += 'class="form-control kt-input txtLevel"/>';
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkShow' + rowId + '"';
        html += 'class="form-check-input checkShow"/>';
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkBold' + rowId +'"';
        html += 'class="form-check-input checkBold"/>'
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkItalic' + rowId + '"';
        html += 'class="form-check-input checkItalic"/>';
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkUnderline' + rowId + '"';
        html += 'class="form-check-input checkUnderline"/>';
        html += '</div>';
        html += '<div class="">';
        html += '<input type="text" id="txtWarna' + rowId + '"';
        html += 'class="form-control kt-input txtWarna" style="width: 70px"/>';
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkSpi' + rowId + '"';
        html += 'class="form-check-input checkSpi"/>';
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkPajak' + rowId + '"';
        html += 'class="form-check-input checkPajak"/>';
        html += '</div>';
        html += '<div class="widthDetail5 checkbox">';
        html += '<input type="checkbox" id="checkJudul' + rowId + '"';
        html += 'class="form-check-input checkJudul"/>';
        html += '</div>';
        html += '<div class="widthDetail10">';
        html += '<input type="text" id="txtRumus' + rowId + '"';
        html += 'class="form-control kt-input txtRumus"/>';
        html += '</div>';
        html += '<div class="widthDetail10">';
        html += '<button id="removeRow" type="button" class="btn btn-danger">Remove';
        html += '</button>';
        html += '</div>';
        html += '</div> ';
    $('#newRow').append(html);

    $("#urutan" + rowId).val(data.urutan);
    $("#idRekening" + rowId).val(data.idRekening);
    $("#txtRekening" + rowId).val(data.namaRekening);
    $("#txtJudul" + rowId).val(data.judul);
    $("#txtLevel" + rowId).val(data.levelAkun);
    $("#checkShow" + rowId).prop('checked', data.tampilNilai);
    $("#checkBold" + rowId).prop('checked', data.cetakTebal);
    $("#checkItalic" + rowId).prop('checked', data.cetakMiring);
    $("#checkUnderline" + rowId).prop('checked', data.cetakGaris);
    $("#txtWarna" + rowId).val(data.warna);
    $("#checkPajak" + rowId).prop('checked', data.pajak);
    $("#checkSpi" + rowId).prop('checked', data.spi);
    $("#checkJudul" + rowId).prop('checked', data.cetakJudul);
    $("#txtRumus" + rowId).val(data.rumus);
    rowId++;
}

function mataAnggranDatatables() {
    tableTransaksi = $("#tblMataAnggaran").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/anggaran/penyusunan-anggaran/mata-anggaran/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: true,
        'processing': true,
        'language': {
            'loadingRecords': '&nbsp;',
            'processing': '<div class="spinner"></div>'
        },
        columns: [
            {
                title: 'No.',
                data: 'kodeRekening',
                orderable: false,
                className: 'text-center',
                width: '50px',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
                defaultContent: "",
                title: 'Kode Rekening',
                data: 'kodeRekening'
            },
            {
                defaultContent: "",
                title: 'Nama Rekening',
                data: 'namaRekening'
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

//    // SELECTED
    $('#tblMataAnggaran tbody').on('click', 'tr', function () {

        tableTransaksi.$('tr.selected').removeClass('selected');
        $(this).addClass('selected');
        var tr = $(this).closest('tr');
        var row = tableTransaksi.row(tr);
        namaRekening = row.data().namaRekening;
        idRekening = row.data().idRekening;
    });
}

function save(table) {
    var obj = getValue();
    if(isValid(obj)) {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(obj),
            url: _baseUrl + "/akunting/laporan/master-laporan/save",
            success: function (response) {
                showSuccess();
                $('#masterLaporanDialog').modal('hide');
                table.ajax.reload();
            },
            statusCode: {
                201: function () {
                    console.log('Saved');
                    showSuccess();
                    $('#masterLaporanDialog').modal('hide');
                    table.ajax.reload();
                },
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
}

function update(table) {
    var obj = getValue();
//    console.log(obj);
//    return;
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/update",
        success: function (response) {
            $('#masterLaporanDialog').modal('hide');
            showSuccess();
            table.ajax.reload();
        },
        statusCode: {
            201: function () {
                console.log('updated');
                $('#masterLaporanDialog').modal('hide');
                showSuccess();
                table.ajax.reload();

            },
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

function deleteById(table) {
    var id = $("#txtDeleteId").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/master-laporan/delete/" + id,
        success: function (response) {
            $('#deleteDialog').modal('hide');
            showSuccess();
            table.ajax.reload();
        },
        statusCode: {
            201: function () {
                console.log('Deleted');
                $('#deleteDialog').modal('hide');
                showSuccess();
                table.ajax.reload();
                tableRumusLaporan.ajax.reload();
                tableRekeningLaporan.ajax.reload();
            },
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

function formReset() {
    $("#txtNamaLaporan").val("");
    $("#txtKeterangan").val("");
    $("#txtIdLaporan").val("");
    $("#urutan1").val("");
    $("#idRekening1").val("");
    $("#txtRekening1").val("");
    $("#txtJudul1").val("");
    $("#txtLevel1").val("");
    $("#checkShow1").prop('checked', false);
    $("#checkBold1").prop('checked', false);
    $("#checkItalic1").prop('checked', false);
    $("#checkUnderline1").prop('checked', false);
    $("#newRow").empty();
}

function setMasterLapValue(data) {
    $("#txtNamaLaporan").val(data.namaLaporan);
    $("#txtKeterangan").val(data.keterangan);
    $("#txtIdLaporan").val(data.idLaporanHdr);
    getDetailValue(data.idLaporanHdr);
}

function setDetailLapValue(data) {
    $("#urutan1").val(data.urutan);
    $("#idRekening1").val(data.idRekening);
    $("#txtRekening1").val(data.namaRekening);
    $("#txtJudul1").val(data.judul);
    $("#txtLevel1").val(data.levelAkun);
    $("#checkShow1").prop('checked', data.tampilNilai);
    $("#checkBold1").prop('checked', data.cetakTebal);
    $("#checkItalic1").prop('checked', data.cetakMiring);
    $("#checkUnderline1").prop('checked', data.cetakGaris);
    $("#txtWarna1").val(data.warna);
    $("#checkPajak1").prop('checked', data.pajak);
    $("#checkSpi1").prop('checked', data.spi);
    $("#checkJudul1").prop('checked', data.cetakJudul);
    $("#txtRumus1").val(data.rumus);
}

function getDetailValue(idLapHdr) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/laporan/master-laporan/detail/" + idLapHdr,
        success: function (resp) {
//            console.log(resp);
            var response = resp.map(function (data) {data.urutan = parseInt(data.urutan); return data;});
//            console.log(response);
            var result = response.sort(compareValues('urutan'));
//            console.log(result);

            setDetailLapValue(result[0])
            for(i = 1; i < result.length; i++) {
                newRowWithValue(result[i]);
            }
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function showRekeningDialog(id) {
    elementId = "#" + id;
    $('#rekeningDialog').modal('show');
}

function getCheckBoxValue(id) {
    if ($(id).is(':checked')) {
        return true;
    } else {
        return false;
    }
}

function getValue() {
    var idLapHdr = $("#txtIdLaporan").val();
    var namaLaporan = $("#txtNamaLaporan").val();
    var keterangan = $("#txtKeterangan").val();

    var obj = {
        "idLaporanHdr": idLapHdr,
        "namaLaporan": namaLaporan,
        "keterangan": keterangan,
        "laporanDetails": getDetailJson()
    }
    return obj;
}

function isValid(data) {
    console.log(data.laporanDetails.length);
    console.log(data);

    if(data.namaLaporan == "") {
        showError("Nama Laporan Tidak Boleh Kosong");
        return false;
    }

    if(data.laporanDetails.length  <= 0) {
        showError("Detail Laporan Tidak Boleh Kosong");
        return false;
    } else {
        for(i = 0; i < data.laporanDetails.length; i++) {
            var detail = data.laporanDetails[i];
            if(detail.namaRekening == "" && detail.judul == "") {
                showError("Rekening Detail Laporan Tidak Boleh Kosong");
                return false;
            }

            if(detail.judul == "") {
                showError("Judul Detail Laporan Tidak Boleh Kosong");
                return false;
            }
        }
    }
    return true;
}

function getDetailJson() {
    var urutan = $(".urutan").map(function(){ return this.value}).get();
//    console.log(urutan);
    var idRekening = $(".idRekening").map(function(){ return this.value}).get();
    var rekening = $(".txtRekening").map(function(){ return this.value}).get();
    var judul = $(".txtJudul").map(function(){ return this.value}).get();
    var level = $(".txtLevel").map(function(){ return this.value}).get();
    var show = $(".checkShow").map(function(){ return getCheckBoxValue(this)}).get();
    var bold = $(".checkBold").map(function(){ return getCheckBoxValue(this)}).get();
    var italic = $(".checkItalic").map(function(){ return getCheckBoxValue(this)}).get();
    var underline = $(".checkUnderline").map(function(){ return getCheckBoxValue(this)}).get();
    var warna = $(".txtWarna").map(function(){ return this.value}).get();
    var spi = $(".checkSpi").map(function(){ return getCheckBoxValue(this)}).get();
    var pajak = $(".checkPajak").map(function(){ return getCheckBoxValue(this)}).get();
    var cetakJudul = $(".checkJudul").map(function(){ return getCheckBoxValue(this)}).get();
    var rumus = $(".txtRumus").map(function(){ return this.value}).get();

    var list = new Array();
    for(i = 0; i < rekening.length; i++) {
        var obj = {
            "urutan": urutan[i],
            "idRekening": idRekening[i],
            "namaRekening": rekening[i],
            "judul": judul[i],
            "levelAkun": level[i],
            "tampilNilai": show[i],
            "cetakTebal": bold[i],
            "cetakMiring": italic[i],
            "cetakGaris": underline[i],
            "warna": warna[i],
            "spi": spi[i],
            "pajak": pajak[i],
            "cetakJudul": cetakJudul[i],
            "rumus": rumus[i]
        };
        list.push(obj);
    }
    return list;
}

function setUrutan() {
    var idx = 1;
    $(".laporan-detail").each(function () {
//        console.log($(this).closest("div").find("input.urutan"));
        $(this).closest("div").find("input.urutan").val(idx);
        idx++;
    });
}

function compareValues(key, order = 'asc') {
  return function innerSort(a, b) {
    if (!a.hasOwnProperty(key) || !b.hasOwnProperty(key)) {
      // property doesn't exist on either object
      return 0;
    }

    const varA = (typeof a[key] === 'string')
      ? a[key].toUpperCase() : a[key];
    const varB = (typeof b[key] === 'string')
      ? b[key].toUpperCase() : b[key];

    let comparison = 0;
    if (varA > varB) {
      comparison = 1;
    } else if (varA < varB) {
      comparison = -1;
    }
    return (
      (order === 'desc') ? (comparison * -1) : comparison
    );
  };
}

function saveMasterLaporan() {
//    var idLaporanHdr =
}

function resetFormMasterLaporan() {
    $("#statusModalMasterLaporan").val("");
    $("#idLaporanHdr").val("");
    $("#namaLaporan").val("");
    $("#keteranganMasterLaporan").val("");
    $("input[name='statusAktifMasterLaporan'][value='0']").prop('checked', true);
}

function laporanRumusDatatables(idLaporanHdr) {
    if (tableRumusLaporan != undefined) {
        tableRumusLaporan.clear();
        tableRumusLaporan.destroy();
    }

    tableRumusLaporan = $("#tblRumusLaporan").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + "/akunting/laporan/master-laporan/laporan-rumus/datatables",
            type: "POST",
            data: function (d) {
                var obj = {};
                obj.laporanHeader = idLaporanHdr;
                d.laporanDetail = obj;
                return JSON.stringify(d);
            }
        },
        order: [0, "asc"],
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: 'idLaporanDtl',
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
                title: 'Judul',
                data: 'judul',
                orderable: true
            },
            {
                title: 'Kode',
                data: 'kodeRumus',
                orderable: false,
                className: 'text-center'
            },
            {
                title: 'Status',
                data: 'statusData',
                className: 'text-center',
                orderable: false,
                render: function (data) {
                    if(data == "0") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" disabled>';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked disabled>'
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
                    return getButtonGroup(false, true, false);
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });
}

function laporanRekeningDatatables(idLaporanDtl) {
    if (tableRekeningLaporan != undefined) {
        tableRekeningLaporan.clear();
        tableRekeningLaporan.destroy();
    }
    tableRekeningLaporan = $("#tblRekeningLaporan").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + "/akunting/laporan/master-laporan/laporan-rekening/datatables",
            type: "POST",
            data: function (d) {
                var obj = {};
                obj.idLaporanDtl = idLaporanDtl;
                d.laporanRekening = obj;
                return JSON.stringify(d);
            }
        },
        order: [0, "asc"],
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
        serverSide: true,
        columns: [
            {
                title: 'No.',
                data: 'idLaporanDtl',
                orderable: false,
                className: 'text-center',
                render: function (data, type, row, meta) {
                    return meta.row + meta.settings._iDisplayStart + 1;
                }
            },
            {
                title: 'Kode Rekening',
                data: 'kodeRekening',
                orderable: false,
                className: ''
            },
            {
                title: 'Nama Rekening',
                data: 'namaRekening',
                orderable: false
            },
            {
                title: 'Operator',
                data: 'rumusOperator',
                orderable: false,
                className: 'text-center',
                render: function (data) {
                    if(data == "0") {
                        return '-';
                    } else {
                        return '+'
                    }
                }
            },
            {
                title: 'Status',
                data: 'statusData',
                className: 'text-center',
                orderable: false,
                render: function (data) {
                    if(data == "0") {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" disabled>';
                    } else {
                        return '<input class="btn-check" type="checkbox" style="vertical-align: middle" checked disabled>'
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
                    return getButtonGroup(false, true, false);
                }
            }
        ],
        bFilter: false,
        bLengthChange: false,
        responsive: true,
        info: true
    });
}

function getFormMasterLaporan() {
    var idLaporanHdr = $("#idLaporanHdr").val();
    var namaLaporan = $("#namaLaporan").val();
    var keterangan = $("#keteranganMasterLaporan").val();
    var namaTabel = $("#namaTabel").val();
    var urutan = $("#urutanMasterLaporan").val();
    var statusAktif = $('#statusAktifMasterLaporan input:radio:checked').val();
    var obj = {
        "idLaporanHdr": idLaporanHdr,
        "namaLaporan": namaLaporan,
        "keterangan": keterangan,
        "statusData": statusAktif,
        "namaTabel": namaTabel,
        "urutan": urutan
    };
    return obj;
}

function saveMasterLaporan() {
    var obj = getFormMasterLaporan();
//    console.log(obj);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/save",
        success: function (response) {

        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#add-form-master-laporan').modal('hide');
                tableMasterLaporan.ajax.reload();
            },
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

function updateMasterLaporan() {
    var obj = getFormMasterLaporan();
//    console.log(obj);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/update",
        success: function (response) {

        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#add-form-master-laporan').modal('hide');
                tableMasterLaporan.ajax.reload();
            },
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

function resetFormRumusLaporan() {
    $("#idLaporanDtl").val("");
    $("#kodeRumus").val("");
    $("#levelAkun").val("");
    $("#rumusLaporan").val("");
    $("#judul").prop("checked", false);
    $("#judulLaporan").val("");
    $("#garisBawah").prop("checked", false);
    $("#spi").prop("checked", false);
    $("input[name='statusAktifRumusLaporan'][value='0']").prop('checked', true);
    $("#statusModalRumusLaporan").val("");
    $("#urutanDtl").val("");
    $("#saldoSebelum").val("");
    $("#statusRumusLaporan").val("ALL").selectpicker("refresh");
}

function getFormRumusLaporan() {
    var laporanHeader = tableMasterLaporan.row(".selected").data();
    var idLaporanDtl = $("#idLaporanDtl").val();
    var kodeRumus = $("#kodeRumus").val();
    var levelAkun = $("#levelAkun").val();
    var rumus = $("#rumusLaporan").val();
    var judul = $("#judul").is(":checked") ? "1" : "0";
    var judulLaporan = $("#judulLaporan").val();
    var garisBawah = $("#garisBawah").is(":checked") ? "1" : "0";
    var spi = $("#spi").is(":checked") ? "Y" : "N";
    var statusAktif = $('#statusAktifRumusLaporan input:radio:checked').val();
    var urutan = $("#urutanDtl").val();
    var saldoSebelum = $("#saldoSebelum").val();
    var statusRumus = $("#statusRumusLaporan").val();
    var obj = {
        "idLaporanDtl": idLaporanDtl,
        "laporanHeader": laporanHeader.idLaporanHdr,
        "kodeRumus": kodeRumus,
        "levelAkun": levelAkun,
        "rumus": rumus,
        "judul": judulLaporan,
        "cetakGaris": garisBawah,
        "cetakJudul": judul,
        "spi": spi,
        "statusData": statusAktif,
        "urutan": urutan,
        "cetakMiring": "0",
        "cetakTebal": "0",
        "saldoSebelum": getNumberRegEx(saldoSebelum),
        "statusRumus": statusRumus
    };
    return obj;
}

function saveRumusLaporan() {
    var obj = getFormRumusLaporan();

    if (formRumusValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/laporan-detail/save",
        success: function (response) {

        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#add-form-rumus-laporan').modal('hide');
                tableRumusLaporan.ajax.reload();
            },
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

function updateRumusLaporan() {
    var obj = getFormRumusLaporan();
//    console.log(obj);
    if (formRumusValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/laporan-detail/update",
        success: function (response) {

        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#add-form-rumus-laporan').modal('hide');
                tableRumusLaporan.ajax.reload();
            },
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

function resetFormRekeningLaporan() {
    $("#idLaporanRek").val("");
    $("#idRekeningLaporanRek").val("").selectpicker("refresh");
    $("#rumusUrutan").val("");
    $("input[name='operatorRekeningLaporan'][value='0']").prop('checked', true);
    $("input[name='statusAktifRekeningLaporan'][value='0']").prop('checked', true);
    $("#statusModalRekeningLaporan").val("");
}

function getFormRekeningLaporan() {
    var laporanDetail = tableRumusLaporan.row(".selected").data();
    var idLaporanDtl = laporanDetail.idLaporanDtl;
    var idLaporanRek = $("#idLaporanRek").val();
    var idRekening = $("#idRekeningLaporanRek").val();
    var rumusUrutan = $("#rumusUrutan").val();
    var rumusOperator = $("#operatorRekeningLaporan input:radio:checked").val();
    var statusData = $("#statusAktifRekeningLaporan input:radio:checked").val();
    var obj = {
        "idLaporanDtl": idLaporanDtl,
        "idLaporanRek": idLaporanRek,
        "idRekening": idRekening,
        "rumusUrutan": rumusUrutan,
        "rumusOperator": rumusOperator,
        "statusData": statusData
    };
    return obj;
}

function saveRekeningLaporan() {
    var obj = getFormRekeningLaporan();

    if (formDetailValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/laporan-rekening/save",
        success: function (response) {

        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#add-form-rekening-laporan').modal('hide');
                tableRekeningLaporan.ajax.reload();
            },
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

function updateRekeningLaporan() {
    var obj = getFormRekeningLaporan();
    //    console.log(obj);

    if (formDetailValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/laporan-rekening/update",
        success: function (response) {

        },
        statusCode: {
            201: function () {
                showSuccess();
                $('#add-form-rekening-laporan').modal('hide');
                tableRekeningLaporan.ajax.reload();
            },
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

function resetFormProsesMasterLaporan() {
//    console.log(pengaturanSistem);
    $("#tglPeriode").val(moment(new Date).format("DD/MM/YYYY"));
    $("input[name='radioJenis'][value='HARIAN']").prop('checked', true);
    $("#idDRI").val("").selectpicker("refresh");
    $("#namaLaporanProses").val("");
    $("#idLaporanHdrProses").val("");
    $("#idTahunBuku").val(pengaturanSistem.kodeTahunBuku).selectpicker("refresh").prop("disabled", true);
    $("#idPeriode").val(pengaturanSistem.kodePeriode).selectpicker("refresh").prop("disabled", true);
    $("#namaTabelProses").val("");
    $("#buttonSaveProsesLaporan").prop("disabled", false);
    $("#batalProsesButton").prop("disabled", false);
    $("#prosesSpinner").prop("hidden", true);
}

function getFormProsesMasterLaporan() {
    var tanggal = convertStringDate($("#tglPeriode").val());
    var jenis = $("#radioJenis input:radio:checked").val();
    var dri = $("#idDRI").val();
    var namaLaporan = $("#namaLaporanProses").val();
    var idLaporanHdr = $("#idLaporanHdrProses").val();
    var kodeTahunBuku = $("#idTahunBuku").val();
    var kodePeriode = $("#idPeriode").val();
    var namaTabel = $("#namaTabelProses").val();
    var obj = {
        "tanggal": tanggal,
        "jenis": jenis,
        "dri": dri,
//        "namaLaporan": namaLaporan,
//        "idLaporanHdr": idLaporanHdr,
        "kodeTahunBuku": kodeTahunBuku,
        "kodePeriode": kodePeriode,
//        "namaTabel": namaTabel
    };
    return obj;
}

function prosesMasterLaporan() {
    $("#buttonSaveProsesLaporan").prop("disabled", true);
    $("#batalProsesButton").prop("disabled", true);
    $("#prosesSpinner").prop("hidden", false);

    var obj = getFormProsesMasterLaporan();

    if(formValidation(obj) == true) {
        swal.fire({
            position: "top-right",
            type: "error",
            title: "Form belum lengkap.",
            showConfirmButton: !1,
            timer: 1500
        });
        $("#buttonSaveProsesLaporan").prop("disabled", false);
        $("#batalProsesButton").prop("disabled", false);
        $("#prosesSpinner").prop("hidden", true);
        return;
    }
//    console.log(obj);
//    return;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(obj),
        url: _baseUrl + "/akunting/laporan/master-laporan/proses",
        success: function (response) {
            $('#form-proses-laporan').modal('hide');
            showSuccess();
        },
        statusCode: {
            201: function () {

            },
            400: function () {
                showError("Inputan tidak boleh kosong");
            },
            404: function () {
                showError("Not Found");
            },
            500: function() {
                $('#form-proses-laporan').modal('hide');
                showError("Internal Server Error");
            }
        }
    });
}

function onlyNumberKey(evt) {
    var ASCIICode = (evt.which) ? evt.which : evt.keyCode
    if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57) && (ASCIICode != 44) && (ASCIICode != 45))
        evt.preventDefault();
//        return false;
//    return true;
}

function datePicker() {
    $("#periodeDatepicker").off().datepicker({
        format: "dd/mm/yyyy",
        todayHighlight: true,
        autoclose: true
    }, "show").on("change", function () {
//        console.log($(this));
        var selected = $(this)[0].children[0].value;
//        console.log(selected);
        var split = selected.split("/");
        var tahun = $("#idTahunBuku").find("option:contains('"+split[2]+"')").val();
//        console.log(tahun);
        $("#idTahunBuku").val(tahun).selectpicker("refresh");
        $("#idPeriode").val(split[1]).selectpicker("refresh");
    });
}

function convertStringDate(value) {
    if (value == "")
        return "";
    else
//        console.log(value);
        var raw = value.split("/");
        var res = raw[2] + "-" + raw[1] + "-" + raw[0];
        return res;
}

function formValidation(obj) {
    var found = Object.keys(obj).filter(function(key) {
        return obj[key] === "";
    });
    if(found.length > 0) {
        return true;
    } else {
        return false;
    }
}

function formRumusValidation(obj) {
    var found = Object.keys(obj).filter(function(key) {
        if (key != "rumus" && key != "idLaporanDtl" && key != "laporanHeader" && key != "saldoSebelum")
            return obj[key] === "";
    });
    if(found.length > 0) {
        return true;
    } else {
        return false;
    }
}

function formDetailValidation(obj) {
    var found = Object.keys(obj).filter(function(key) {
        if (key != "idLaporanDtl" && key != "idLaporanRek")
            return obj[key] === "";
    });
    if(found.length > 0) {
        return true;
    } else {
        return false;
    }
}

function formatMoney(amount, currency = "", decimalCount = 2, decimal = ",", thousands = ".") {
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

function currencyWhileTyping(value) {
    value.val(function (index, value) {
        if (value != "") {
        //return '$' + value.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            var decimalCount;
            var negativeSign = value.includes("-") ? "-" : "";
//            console.log(negativeSign);
            value.match(/\,/g) === null ? decimalCount = 0 : decimalCount = value.match(/\,/g);

            if (decimalCount.length > 1) {
                value = value.slice(0, -1);
            }

            var components = value.toString().split(",");
            if (components.length === 1)
                components[0] = value;
            components[0] = components[0].replace(/\D/g, '').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
            if (components.length === 2) {
                components[1] = components[1].replace(/\D/g, '').replace(/^\d{3}$/, '');
            }

            if (components.join(',') != '')
                return negativeSign ? negativeSign + components.join(',') : components.join(',');
            else
                return '';
        }
    });
}

function getNumberRegEx(value) {
    var res = value.replace(/\./g, "").replace(/\,/g, ".");
    return res;
}