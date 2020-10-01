jQuery(document).ready(function () {
    var data = [];
    setDefaultValue();
    $("#tglTransaksi").val(pengaturanSistem.tglTransaksi.substr(0, 10));
    setDatatables(data);

    $("#btnCheck").click(function(){
        $("input[name='rdTampilRekening']").removeAttr( "disabled" );
        refreshTable("");
    })

    $("input[name='rdTampilRekening']").click(function(){
        var radioValue = $("input[name='rdTampilRekening']:checked").val();
        refreshTable(radioValue);
    });


});

function setDefaultValue() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/check-and-balance/default-value",
        success: function (resp) {
            $("#thnBukuPeriode").val(resp.thnBukuPeriode);
            $("#userInput").val(resp.userInput);
            $("#satuanKerja").val(resp.satuanKerja);
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function refreshTable(status) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: _baseUrl + "/akunting/transaksi/check-and-balance/rekening-datatables?status=" + status,
        success: function (resp) {
            var balance = 0;
            for(i = 0; i < resp.length; i++) {
                if(resp[i].status == 'Balance') balance += 1;
            }

            $("#jmlRekening").val(resp.length);
            $("#totBalance").val(balance);
            $("#totTdkBalance").val(resp.length - balance);

            if(resp.length > 0) {
                $("#tblRekening").DataTable().clear().rows.add(resp).draw();
            } else {
                var data=[];
                $("#tblRekening").DataTable().clear().rows.add(data).draw();
            }
        },
        statusCode: {
            500: function (resp) {
                console.log(resp.responseJSON.message);
            }
        }
    });
}

function setDatatables(data) {
    tableRekening = $("#tblRekening").DataTable({
        lengthMenu: [[5, 10, 25, 50, 100, -1], [5, 10, 25, 50, 100, "All"]],
        serverSide: false,
        processing: true,
        data: data,
        language: {
            'loadingRecords': '&nbsp;',
            'processing': '<div class="spinner"></div>'
        },
        columns: [
            {
                defaultContent: "",
                title: 'No.',
                data: 'nomorRekening',
                className: 'text-center',
                width: '50px',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                defaultContent: "",
                title: 'No. Rekening',
                data: 'nomorRekening'
            },
            {
                defaultContent: "",
                title: 'Nama Rekening',
                data: 'namaRekening'
            },
            {
                defaultContent: "-",
                title: 'Saldo Warkat',
                data: 'saldoWarkat',
                className: 'dt-right',
                render: $.fn.dataTable.render.number( ',', '.', 2 )
            },
            {
                defaultContent: "",
                title: 'Saldo Pre Approval',
                data: 'saldoPreApproval',
                className: 'dt-right',
                render: $.fn.dataTable.render.number( ',', '.', 2 )
            },
            {
                defaultContent: "",
                title: 'Saldo Final Approval',
                data: 'saldoFinalApproval',
                className: 'dt-right',
                render: $.fn.dataTable.render.number( ',', '.', 2 )
            },
            {
                defaultContent: "",
                title: 'Status',
                data: 'status',
                className: 'dt-center'
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });
}