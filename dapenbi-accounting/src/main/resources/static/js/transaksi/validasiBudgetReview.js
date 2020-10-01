var tableBudgetReview;
var _currentBudgetReview;
var _budgetReviewDetails;

jQuery(document).ready(function () {
    tableBudgetReview = $("#tblBudgetReview").DataTable({
        ajax: {
            contentType: 'application/json',
            url: _baseUrl + '/akunting/transaksi/budget-review/datatables',
            type: "POST",
            data: function (d) {
                d.budgetReviewDTO = {
                    statusData: 'SUBMIT'
                }
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
                    return moment(data).format('yyyy-MM-DD');
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
                    // console.log(data);
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
        _currentBudgetReview = tableBudgetReview.row($(this).parents('tr')).data();
        findDetails();
        // console.log(_currentBudgetReview);
        $('#txtNoBudgetReview').val(_currentBudgetReview.noBudgetReview);
        $('#txtCreatedDate').val(moment(_currentBudgetReview.createdDate).format('yyyy-MM-DD'));
        $('#txtKodeThnBuku').val(_currentBudgetReview.kodeThnBuku.kodeTahunBuku);
        $('#txtKodePeriode').val(_currentBudgetReview.kodePeriode.kodePeriode);
        $('#txtTriwulan').val(_currentBudgetReview.triwulan);
        $('#txtKeterangan').val(_currentBudgetReview.keterangan);
    });
});

function renderDetailsTbl() {
    // RENDER TABLE BUDGET DETAIL
    var tbody = document.getElementById('tbody-detail');
    tbody.innerHTML = '';
    if (_budgetReviewDetails.length != 0) {
        for (var i = 0; i < Object.keys(_budgetReviewDetails).length; i++) {
            let tr = "<tr>";
            tr += "<td>" + _budgetReviewDetails[i].noBudgetReview.noBudgetReview + "</td>" +
                "<td>" + _budgetReviewDetails[i].idRekening.idRekening + "</td>" +
                "<td>" + _budgetReviewDetails[i].anggaranTahunan + "</td>" +
                "<td>" + _budgetReviewDetails[i].realisasi + "</td>" +
                "<td>" + _budgetReviewDetails[i].persen + '%' + "</td>" +
                "<td>" + _budgetReviewDetails[i].saldo + "</td>" +
                "<td>" + _budgetReviewDetails[i].keterangan + "</td>" +
                "</td></tr>";
            tbody.innerHTML += tr;
        }
    }
}

function findDetails() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: _baseUrl + `/akunting/transaksi/budget-review/details?noBudgetReview=${_currentBudgetReview.noBudgetReview}`,
        success: function (response) {
            _budgetReviewDetails = response;
            // console.log(_budgetReviewDetails)
            renderDetailsTbl();
            $('#budgetReviewDialog').modal('show');
        },
        complete: function (response) {
            if (response.status != 200)
                showSuccess("Data kosong.");
        }
    });
}

function updateStatusBudgetReview(status) {
    _currentBudgetReview.statusData = status;
    _currentBudgetReview.catatanValidasi = $('#txtCatatanValidasi').val();

    if (status === 'REJECT') {
        if (_currentBudgetReview.catatanValidasi === '') {
            showWarning("Keterangan tidak boleh kosong");
            return false;
        }
    }

    $('#catatanTolakDialog').modal('hide');
    $('#budgetReviewDialog').modal('hide');

    $.ajax({
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(_currentBudgetReview),
        url: _baseUrl + `/akunting/transaksi/budget-review/validasi`,
        success: function (response) {
            // console.log(response, "RESPONSE");
        },
        complete: function (resp) {
            // console.log(resp);
            if (resp.status === 200) {
                showSuccess();
                tableBudgetReview.ajax.reload();
            } else {
                swal.fire({
                    title: "Gagal",
                    text: resp.responseText,
                    type: "warning",
                    confirmButtonText: "Tutup."
                });
            }
        }
    });
}
