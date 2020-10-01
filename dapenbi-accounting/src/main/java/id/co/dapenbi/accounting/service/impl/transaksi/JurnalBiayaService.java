package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.JurnalBiayaDao;
import id.co.dapenbi.accounting.dto.transaksi.JurnalBiayaDTO;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class JurnalBiayaService {

    @Autowired
    private JurnalBiayaDao dao;

    public DataTablesResponse<JurnalBiayaDTO> datatables(DataTablesRequest<JurnalBiayaDTO> params, String search) {
        List<JurnalBiayaDTO> data = dao.datatable(params, search);
        Long rowCount = dao.datatable(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<WarkatJurnal> findJurnalBiayaDebits() {
        return dao.findJurnalBiayaDebits();
    }
}
