package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.MonitoringTransaksiDao;
import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitoringTransaksiService {

    @Autowired
    private MonitoringTransaksiDao dao;

    public DataTablesResponse<Warkat> datatables(DataTablesRequest<Warkat> params, String search) {
        List<Warkat> data = dao.list(params, search);
        Long rowCount = dao.listCount(search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<Warkat> warkatList(Warkat warkat) {
        return dao.list(warkat);
    }

    public List<MSTLookUp> findAllStatusData() {
        return dao.findAllStatusData();
    }
}
