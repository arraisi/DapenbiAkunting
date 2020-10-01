// variable
var rekeningSelected;
var createdBy;
var createdDate;
var tableRekening;

jQuery(document).ready(function () {
    var source = {
        dataType: "json",
        url: _baseUrl + "/api/akunting/parameter/rekening/findAllRekening",
        type: "GET",
        dataFields: [{
            name: 'idRekening',
            type: 'number'
        }, {
            name: 'idParent',
            type: 'number'
        }, {
            name: 'kodeRekening',
            type: 'string'
        }, {
            name: 'namaRekening',
            type: 'string'
        }, {
            name: 'levelRekening',
            type: 'number'
        }, {
            name: 'expanded',
            type: 'bool'
        }, {
            name: 'saldoNormal',
            type: 'string'
        }, {
            name: 'isSummary',
            type: 'string'
        }, {
            name: 'tipeRekening',
            type: 'string'
        }, {
            name: 'statusAktif',
            type: 'string'
        }, {
            name: 'statusNeracaAnggaran',
            type: 'string'
        }, {
            name: 'createdBy',
            type: 'string'
        }, {
            name: 'createdDate',
            type: 'date'
        }],
        hierarchy:
            {
                keyDataField: {name: 'idRekening'},
                parentDataField: {name: 'idParent'}
            },
        id: 'idRekening'
        // localData: rekenings
    };
    var dataAdapter = new $.jqx.dataAdapter(source);

    // create Tree Grid
    $("#treeGrid").jqxTreeGrid({
        source: dataAdapter,
        theme: 'light',
        width: '100%',
        pageable: true,
        pageSize: 100,
        pagerMode: "advanced",
        pageSizeOptions: ['100', '250', '500', '1000'],
        pagerPosition: 'bottom',
        ready: function () {
            //$("#treeGrid").jqxTreeGrid('selectRow', 2);
        },
        filterable: true,
        columns: [{
            text: 'Kode Rekening',
            dataField: 'kodeRekening',
            width: '20%',
            align: 'center'
        }, {
            text: 'Nama Rekening',
            dataField: 'namaRekening',
            width: '40%',
            align: 'center'
        }, {
            text: 'Level',
            dataField: 'levelRekening',
            width: '15%',
            align: 'center',
            cellsAlign: 'center',
        }, {
            text: 'Saldo Normal',
            dataField: 'saldoNormal',
            width: '15%',
            align: 'center',
            cellsAlign: 'center',
            // row - row's key.
            // column - column's data field.
            // value - cell's value.
            // rowData - rendered row's object.
            cellsRenderer: function (row, column, value, rowData) {
                return value;
            }
        }, {
            text: 'Status Aktif',
            dataField: 'statusAktif',
            width: '10%',
            align: 'center',
            cellsAlign: 'center',
            cellsRenderer: function (row, column, value, rowData) {
                if (value == 1) {
                    return "<i class=\"fa fa-check\"></i>"
                } else {
                    return "<i class=\"fa fa-times\"></i>"
                }
            }
        }]

    });

    $('#treeGrid').on('rowClick', function (event) {
        var clicks = $(this).data('clicks');
        if (clicks) {
            // odd clicks
            var args = event.args;
            var row = args.row;
            $("#treeGrid").jqxTreeGrid('collapseRow', row.idRekening);
        } else {
            var args = event.args;
            var row = args.row;
            $("#treeGrid").jqxTreeGrid('expandRow', row.idRekening);
        }
        $(this).data("clicks", !clicks);

    });

    $('#expandAllBtn').click(function () {
        $("#treeGrid").jqxTreeGrid('expandAll');
    });

    $('#collapseAllBtn').click(function () {
        $("#treeGrid").jqxTreeGrid('collapseAll');
    });

    $('#treeGrid').on('rowSelect',
        function (event) {
            var args = event.args;
            rekeningSelected = args.row;
            // console.log(args.row);
            createdBy = args.row.createdBy;
            createdDate = args.row.createdDate;
        });

    $('#treeGrid').on('rowDoubleClick',
        function (event) {
            // event args.
            var args = event.args;
            patchFormRekening(args.row);
            $('#formRekeningDialog').modal('show');
        });

    $('#treeGrid').on('filter',
        function (event) {
            $('#expandAllBtn').click();
        });
});

function patchFormRekening(data) {
    if (data.idRekening !== null) $('#txtIdRekening').val(data.idRekening);
    if (data.kodeRekening !== null) $('#txtKodeRekening').val(data.kodeRekening);
    if (data.idParent !== null) $('#txtParent').val(data.idParent);
    if (data.parent !== null) {
        $('#txtKodeRekeningParent').val(data.parent.kodeRekening);
        $('.form-parent').show();
    } else {
        $('.form-parent').hide();
    }
    if (data.isSummary !== null) $('#txtIsSummary').val(data.isSummary);
    if (data.levelRekening !== null) $('#txtLevelRekening').val(data.levelRekening);
    if (data.namaRekening !== null) $('#txtNamaRekening').val(data.namaRekening);
    if (data.saldoNormal !== null) $('#txtSaldoNormal').val(data.saldoNormal);
    if (data.statusNeracaAnggaran !== null) $('#txtStatusNeracaAnggaran').val(data.statusNeracaAnggaran);
    if (data.tipeRekening !== null) $('#txtTipeRekening').val(data.tipeRekening);
    data.statusAktif === "1" ? $("#statusAktifYa").prop("checked", true) : $("#statusAktifTidak").prop("checked", true);
}

$('#addRekeningRoot').on('click', function () {
    clearTxtRekening();
    $('#txtLevelRekening').val(1);
    $('#formRekeningDialog').modal('show');
});

$('#addRekeningBranch').on('click', function () {
    if (typeof rekeningSelected === undefined || typeof rekeningSelected === "undefined") {
        showWarning("Please choose a root for the branch");
        return;
    }

    clearTxtRekening();

    $("[name='kodeRekParent']").hide();
    $('#txtKodeRekening').attr('readonly', false);

    $('#txtParent').val(rekeningSelected.idRekening);
    $('#txtKodeRekening').val(rekeningSelected.kodeRekening);
    $('#txtLevelRekening').val(rekeningSelected.levelRekening + 1);

    if (rekeningSelected.levelRekening === 6) {
        showWarning("Sorry, can't add branches to this line");
        return;
    } else {
        $('#formRekeningDialog').modal('show');
    }
});

$("#simpanRekening").on('click', function (event) {

    var obj = {
        "idRekening": document.getElementById("txtIdRekening").value,
        "kodeRekening": document.getElementById("txtKodeRekening").value,
        "idParent": document.getElementById("txtParent").value,
        "isSummary": document.getElementById("txtIsSummary").value,
        "levelRekening": document.getElementById("txtLevelRekening").value,
        "namaRekening": document.getElementById("txtNamaRekening").value,
        "saldoNormal": document.getElementById("txtSaldoNormal").value,
        "statusNeracaAnggaran": document.getElementById("txtStatusNeracaAnggaran").value,
        "tipeRekening": document.getElementById("txtTipeRekening").value,
        "statusAktif": $('input[name=txtStatusAktif]:checked', '#formRekening').val(),
        "createdBy": createdBy,
        "createdDate": createdDate
    };
    // console.log(obj, "obj for save");
    if (
        obj.isSummary === "" ||
        obj.namaRekening === "" ||
        obj.saldoNormal === "" ||
        obj.statusNeracaAnggaran === "" ||
        obj.tipeRekening === "" ||
        /^\d+$/.test($("#txtKodeRekening").val()) !== true
    ) {
        showWarning("Some of the inputs are invalid/mandatory.");
    } else {
        serviceSaveRekening(obj);
    }
    event.preventDefault();
});

function serviceSaveRekening(data) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        url: _baseUrl + "/api/akunting/parameter/rekening/",
        success: function (response) {
            // console.log(response, "RESPONSE SAVE");
        },
        statusCode: {
            201: function () {
                refresh();
                showSuccess();
                clearTxtRekening();
                $('#formRekeningDialog').modal('toggle');
            },
            400: function () {
                showWarning('Inputan tidak boleh kosong');
            },
            500: function (response) {
                if (response.responseJSON.message.includes("UNIQUE_KODE_REK"))
                    showWarning("Kode Rekening sudah terdaftar")
            }
        }
    });
}

function levelOptionsPressed(data) {
    var filtertype = 'numericfilter';
// create a new group of filters.
    var filtergroup = new $.jqx.filter();
    var filter_or_operator = 1;
    var filtervalue = data;
    var filtercondition = 'equal';
    var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
    filtergroup.addfilter(filter_or_operator, filter);
// add the filters.
    $("#treeGrid").jqxTreeGrid('addFilter', 'levelRekening', filtergroup);
// apply the filters.
    $("#treeGrid").jqxTreeGrid('applyFilters');
}

function refresh() {
    $("#treeGrid").jqxTreeGrid('refresh');
    $("#treeGrid").jqxTreeGrid('updateBoundData');
    rekeningSelected = null;
    clearTxtRekening();
}

function clearTxtRekening() {
    $('#txtKodeRekening').removeClass('is-invalid');
    $('#txtKodeRekening').removeClass('is-valid');
    $('#txtIdRekening').val('');
    $('#txtKodeRekening').val('');
    $('#txtParent').val('0');
    $('#txtKodeRekeningParent').val('');
    $('#txtIsSummary').val('');
    $('#txtLevelRekening').val('1');
    $('#txtNamaRekening').val('');
    $('#txtSaldoNormal').val('');
    $('#txtStatusNeracaAnggaran').val('');
    $('#txtTipeRekening').val('');
    $("#statusAktifYa").prop("checked", true);
}