var tableBudgetReview;

jQuery(document).ready(function () {
    tableBudgetReview = $("#tblBudgetReview").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/transaksi/budget-review/datatables',
            type: "POST",
            data: function (d) {
                return JSON.stringify(d);
            }
        },
        lengthMenu: [10, 25, 50, 100],
        serverSide: true,
        processing: true,
        language: {
            'loadingRecords': '&nbsp;',
            'processing': '<div class="spinner"></div>'
        },
        columns: [
            {
                defaultContent: "",
                title: 'No.',
                data: 'noBudgetReview',
                className: 'text-center',
                width: '50px',
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                defaultContent: "",
                title: 'No. Budget Review',
                data: 'noBudgetReview',
                render: function (data, type, row, meta) {
                    // console.log(data, type, row, meta);
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Tahun Buku',
                data: 'kodeThnBuku',
                render: function (data, type, row, meta) {
                    return data.kodeTahunBuku;
                }
            },
            {
                defaultContent: "",
                title: 'Periode',
                data: 'kodePeriode',
                render: function (data, type, row, meta) {
                    return data.kodePeriode;
                }
            },
            {
                defaultContent: "",
                title: 'Triwulan',
                data: 'triwulan',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Tgl. Input',
                data: 'createdDate',
                render: function (data, type, row, meta) {
                    return data.substr(0, 10);
                }
            },
            {
                defaultContent: "",
                title: 'User Input',
                data: 'createdBy',
                render: function (data, type, row, meta) {
                    return data;
                }
            },
            {
                defaultContent: "",
                title: 'Status Data',
                data: 'statusData',
                render: function (data, type, row, meta) {
                    console.log(data);
                    return data;
                }
            },
            {
                searchable: false,
                sortable: false,
                orderable: false,
                class: "text-center",
                title: "Tindakan",
                width: "100px",
                data: 'statusData',
                render: function (data, type, row, meta) {
                    if (data !== 'VALID')
                        return `<button class='btn btn-sm btn-clean validation-button btn-primary' title='Validasi'><i class='la la-edit'></i> Validasi</button>`;
                    else return '';
                }
            }
        ],
        bFilter: true,
        bLengthChange: true,
        responsive: true
    });

    // BUTTON EDIT
    $('#tblBudgetReview tbody').on('click', '.validation-button', function () {

        var data = tableBudgetReview.row($(this).parents('tr')).data();
        console.log(data);
        $('#txtNoBudgetReview').val(data.noBudgetReview);
        $('#txtCreatedDate').val(data.createdDate.substr(0, 10));
        $('#txtKodeThnBuku').val(data.kodeThnBuku.kodeTahunBuku);
        $('#txtKodePeriode').val(data.kodePeriode.kodePeriode);
        $('#txtTriwulan').val(data.triwulan);
        $('#txtKeterangan').val(data.keterangan);

        // RENDER TABLE BUDGET DETAIL
        var budgetReviewDetails = data.budgetReviewDetails;
        var tbody = document.getElementById('tbody-detail');
        tbody.innerHTML = '';
        if (budgetReviewDetails.length != 0) {
            for (var i = 0; i < Object.keys(budgetReviewDetails).length; i++) {
                const keterangan = budgetReviewDetails[i].keterangan === null ? '-' : budgetReviewDetails[i].keterangan;
                let tr = "<tr>";
                tr += "<td>" + budgetReviewDetails[i].noBudgetReview.noBudgetReview + "</td>" +
                    "<td>" + budgetReviewDetails[i].idRekening.idRekening + "</td>" +
                    "<td>" + budgetReviewDetails[i].anggaranTahunan + "</td>" +
                    "<td>" + budgetReviewDetails[i].realisasi + "</td>" +
                    "<td>" + budgetReviewDetails[i].persen + '%' + "</td>" +
                    "<td>" + budgetReviewDetails[i].saldo + "</td>" +
                    "<td>" + keterangan + "</td>" +
                    "</td></tr>";
                tbody.innerHTML += tr;
            }
        }

        $('#budgetReviewDialog').modal('show');
    });
});

function updateStatusBudgetReview(status) {

    var keterangan = $('#txtKeterangan').val();
    var noBudgetReview = $('#txtNoBudgetReview').val();

    if (status === 'REJECT') {
        console.log(keterangan);
        if (keterangan === '') {
            showWarning("Keterangan tidak boleh kosong");
            return false;
        }
    }

    $.ajax({
        type: "PUT",
        contentType: "application/json",
        url: _baseUrl + `/akunting/transaksi/budget-review/validasi?statusData=${status}&noBudgetReview=${noBudgetReview}&keterangan=${keterangan}`,
        success: function (response) {
            console.log(response, "RESPONSE");
        },
        statusCode: {
            200: function (data) {
                showSuccess();
                tableBudgetReview.ajax.reload();
                $('#budgetReviewDialog').modal('hide');
            },
            204: function (data) {
                showWarning("Terjadi kesalahan | Error code : 204");
                $('#budgetReviewDialog').modal('hide');
            },
            403: function (data) {
                showWarning("Terjadi kesalahan, Mohon cek kesesuaian debit, kredit dengan saldo anggaran dan saldo akhir", 3000);
                $('#warkatDialog').modal('hide');
            },
            500: function (data) {
                showWarning("Terjadi kesalahan, Mohon cek kesesuaian debit, kredit dengan saldo anggaran dan saldo akhir", 3000);
                $('#warkatDialog').modal('hide');
            }
        }
    });
}
