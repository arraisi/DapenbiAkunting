function generateMonthCombobox(control){
    for(var i = 1; i <= 12; i++){
        control.append(generateMonthOption(i));
    }
}

function generateYearCombobox(control){
    var range = 10;
    var currentYear = new Date().getFullYear();

    for(var i = 0; i < range; i++){
        control.append('<option value="">' + (Number(currentYear) - Number(i)) + '</option>');
    }
}

function generateMonthOption(monthNumber){
    switch (monthNumber) {
        case 1 :
            return '<option value="' + monthNumber + '">Januari</option>';
            break;
        case 2:
            return '<option value="' + monthNumber + '">Februari</option>';
            break;
        case 3 :
            return '<option value="' + monthNumber + '">Maret</option>';
            break;
        case 4 :
            return '<option value="' + monthNumber + '">April</option>';
            break;
        case 5 :
            return '<option value="' + monthNumber + '">Mei</option>';
            break;
        case 6 :
            return '<option value="' + monthNumber + '">Juni</option>';
            break;
        case 7 :
            return '<option value="' + monthNumber + '">Juli</option>';
            break;
        case 8 :
            return '<option value="' + monthNumber + '">Agustus</option>';
            break;
        case 9 :
            return '<option value="' + monthNumber + '">September</option>';
            break;
        case 10 :
            return '<option value="' + monthNumber + '">Oktober</option>';
            break;
        case 11 :
            return '<option value="' + monthNumber + '">November</option>';
            break;
        case 12 :
            return '<option value="' + monthNumber + '">Desember</option>';
            break;

    }
}