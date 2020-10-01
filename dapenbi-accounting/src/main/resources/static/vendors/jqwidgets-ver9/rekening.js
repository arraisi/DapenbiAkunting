$(document).ready(function () {
    var jsonData = [
        {
            "isSummary": "Y",
            "kodeRekening": "1",
            "namaRekening": "INVESTASI ",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10001,
            "parent": null,
            "level": 1,
            "DT_RowId": "1"
        }, {
            "isSummary": "Y",
            "kodeRekening": "101",
            "namaRekening": "Tabungan pada Bank",
            "saldoNormal": "D",
            "statusNeracaAnggaran": "N",
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": "1234",
            "createdDate": "2020-05-04T01:32:00.000+0000",
            "updatedBy": "1234",
            "updatedDate": "2020-05-19T03:21:42.000+0000",
            "tipeRekening": "N",
            "urutan": null,
            "saldoCurrent": null,
            "key": 10010,
            "parent": "10001",
            "level": 2,
            "DT_RowId": "2"
        }, {
            "isSummary": "Y",
            "kodeRekening": "10101",
            "namaRekening": "Tabungan pada Bank",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10065,
            "parent": "10010",
            "level": 3,
            "DT_RowId": "3"
        }, {
            "isSummary": "Y",
            "kodeRekening": "1010101",
            "namaRekening": "Tabungan",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10140,
            "parent": "10065",
            "level": 4,
            "DT_RowId": "4"
        }, {
            "isSummary": "Y",
            "kodeRekening": "101010101",
            "namaRekening": "Tabungan Swakelola",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10488,
            "parent": "10140",
            "level": 5,
            "DT_RowId": "5"
        }, {
            "isSummary": "N",
            "kodeRekening": "1010101011",
            "namaRekening": "Tabungan Swakelola",
            "saldoNormal": "D",
            "statusNeracaAnggaran": "N",
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": "1234",
            "createdDate": "2020-05-04T01:32:53.000+0000",
            "updatedBy": "1234",
            "updatedDate": "2020-05-04T01:33:53.000+0000",
            "tipeRekening": "N",
            "urutan": null,
            "saldoCurrent": null,
            "key": 11104,
            "parent": "10488",
            "level": 6,
            "DT_RowId": "6"
        }, {
            "isSummary": "N",
            "kodeRekening": "1010101012",
            "namaRekening": "Deposito Berjangka Fund Manager Bahana",
            "saldoNormal": "D",
            "statusNeracaAnggaran": "N",
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": "0047",
            "createdDate": "2020-05-27T05:26:03.000+0000",
            "updatedBy": "0047",
            "updatedDate": "2020-05-27T05:26:37.000+0000",
            "tipeRekening": "N",
            "urutan": null,
            "saldoCurrent": null,
            "key": 1864,
            "parent": "10488",
            "level": 6,
            "DT_RowId": "7"
        }, {
            "isSummary": "Y",
            "kodeRekening": "2",
            "namaRekening": "REKSADANA ",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10002,
            "parent": null,
            "level": 1,
            "DT_RowId": "1"
        }, {
            "isSummary": "Y",
            "kodeRekening": "3",
            "namaRekening": "REKSADANA 1",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10023,
            "parent": null,
            "level": 1,
            "DT_RowId": "1"
        }, {
            "isSummary": "Y",
            "kodeRekening": "4",
            "namaRekening": "REKSADANA 2",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 14002,
            "parent": null,
            "level": 1,
            "DT_RowId": "1"
        }, {
            "isSummary": "Y",
            "kodeRekening": "5",
            "namaRekening": "REKSADANA 3",
            "saldoNormal": "D",
            "statusNeracaAnggaran": null,
            "statusPemilikAnggaran": null,
            "statusAktif": "1",
            "createdBy": null,
            "createdDate": null,
            "updatedBy": null,
            "updatedDate": null,
            "tipeRekening": null,
            "urutan": null,
            "saldoCurrent": null,
            "key": 10322,
            "parent": null,
            "level": 1,
            "DT_RowId": "1"
        },
    ];

    // prepare the data
    var source =
    {
        dataType: "json",
        dataFields: [
            { name: 'key', type: 'number' },
            { name: 'parent', type: 'number' },
            { name: 'kodeRekening', type: 'string' },
            { name: 'namaRekening', type: 'string' },
            { name: 'level', type: 'number' },
            { name: 'saldoNormal', type: 'string' }
        ],
        hierarchy:
        {
            keyDataField: { name: 'key' },
            parentDataField: { name: 'parent' }
        },
        id: 'key',
        localData: jsonData
    };
    var dataAdapter = new $.jqx.dataAdapter(source);
    // create Tree Grid
    $("#treeGrid").jqxTreeGrid(
        {
            width: 850,
            source: dataAdapter,
            pageable: true,
            sortable: true,
            filterable: true,
            filterHeight: 40,
            // enableHover: true,
            ready: function () {
                $("#treeGrid").jqxTreeGrid('expandRow', '10001');
            },
            columns: [
                { text: 'Kode Rekening', dataField: 'kodeRekening', width: 250 },
                { text: 'Nama Rekening', dataField: 'namaRekening', width: 200 },
                { text: 'Level', dataField: 'level', width: 200 },
                { text: 'Saldo Normal', dataField: 'saldoNormal', width: 200 }
            ]
        }
    );
    $("#jqxbutton").jqxButton({
        theme: 'energyblue',
        height: 30
    });
    $('#jqxbutton').click(function () {
        $("#treeGrid").jqxTreeGrid('expandAll');
    });

    $("#jqxbuttonfilter").jqxButton({
        theme: 'energyblue',
        width: 200,
        height: 30
    });
    $('#jqxbuttonfilter').click(function () {
        var filtertype = 'numericfilter';
        // create a new group of filters.
        var filtergroup = new $.jqx.filter();
        var filter_or_operator = 1;
        var filtervalue = 1;
        var filtercondition = 'equal';
        var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
        filtergroup.addfilter(filter_or_operator, filter);
        // add the filters.
        $("#treeGrid").jqxTreeGrid('addFilter', 'level', filtergroup);
        // apply the filters.
        $("#treeGrid").jqxTreeGrid('applyFilters');
    });

    $('#treeGrid').on('rowClick',
        function (event) {
            console.log(event);
            
            // event args.
            var args = event.args;
            // row data.
            var row = args.row;
            // row key.
            var key = args.key;
            // data field
            var dataField = args.dataField;
            // original click event.
            var clickEvent = args.originalEvent;
        });
});