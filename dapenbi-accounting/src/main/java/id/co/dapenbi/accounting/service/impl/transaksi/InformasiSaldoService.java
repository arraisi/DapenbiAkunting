package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.InformasiSaldoDao;
import id.co.dapenbi.accounting.dto.transaksi.InformasiSaldoDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.SaldoFA;
import id.co.dapenbi.accounting.entity.transaksi.SaldoPA;
import id.co.dapenbi.accounting.repository.transaksi.SaldoFARepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class InformasiSaldoService {

    @Autowired
    private InformasiSaldoDao informasiSaldoDao;

    @Autowired
    private SaldoPARepository saldoPARepository;

    @Autowired
    private SaldoFARepository saldoFARepository;

    public DataTablesResponse<SaldoCurrent> datatablesSaldoCurrent(DataTablesRequest<SaldoCurrent> params, String search) {
        List<SaldoCurrent> data = informasiSaldoDao.datatablesSaldoCurrent(params, search);
        Long rowCount = informasiSaldoDao.datatablesSaldoCurrent(search, params.getValue());
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<Saldo> datatablesSaldo(DataTablesRequest<Saldo> params, String search) {
        List<Saldo> data = informasiSaldoDao.datatablesSaldo(params, search);
        Long rowCount = informasiSaldoDao.datatablesSaldo(search, params.getValue());
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public InformasiSaldoDTO.TotalSaldo totalSaldoCurrent(String param) {
        InformasiSaldoDTO.TotalSaldo totalSaldo = new InformasiSaldoDTO.TotalSaldo();
        if (param.equals("All")) {
            totalSaldo.setSaldoAset(informasiSaldoDao.saldoAsetSaldoCurrent());
            totalSaldo.setSaldoKewajiban(informasiSaldoDao.saldoKewajibanSaldoCurrent());
        } else if (param.equals("PA")) {
            totalSaldo.setSaldoAset(saldoPARepository.totalDebit());
            totalSaldo.setSaldoKewajiban(saldoPARepository.totalKredit());
        } else if (param.equals("FA")) {
            totalSaldo.setSaldoAset(saldoFARepository.totalDebit());
            totalSaldo.setSaldoKewajiban(saldoFARepository.totalKredit());
        }

//        if (param.equals("PA"))
//            totalSaldo.setSaldoAset(saldoPARepository.totalDebit());
//            totalSaldo.setSaldoKewajiban(saldoPARepository.totalKredit());
//        if (param.equals("FA"))
//            totalSaldo.setSaldoAset(saldoFARepository.totalDebit());
//            totalSaldo.setSaldoKewajiban(saldoFARepository.totalKredit());

        return totalSaldo;
    }

    public DataTablesResponse<SaldoCurrent> datatablesSaldoCurrentPA(DataTablesRequest<SaldoCurrent> params, String search) {
        List<SaldoCurrent> data = informasiSaldoDao.datatablesSaldoCurrent(params, search);
        Long rowCount = informasiSaldoDao.datatablesSaldoCurrent(search, params.getValue());
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesOutput<SaldoPA> saldoPADataTablesOutput(DataTablesInput input) {
        return saldoPARepository.findAll(input);
    }

    public DataTablesOutput<SaldoFA> saldoFADataTablesOutput(DataTablesInput input) {
        return saldoFARepository.findAll(input);
    }

    public InformasiSaldoDTO.TotalSaldo totalSaldo(String kodeDri, String tglSaldo) {
        InformasiSaldoDTO.TotalSaldo totalSaldo = new InformasiSaldoDTO.TotalSaldo();
        totalSaldo.setSaldoAset(informasiSaldoDao.saldoAsetSaldo(kodeDri, tglSaldo));
        totalSaldo.setSaldoKewajiban(informasiSaldoDao.saldoKewajibanSaldo(kodeDri, tglSaldo));
        return totalSaldo;
    }

    public Map<String, BigDecimal> totalAsetKewajibanSaldoWarkat(String tableName, String kodeDRI, String tglTransaksi) {
        return informasiSaldoDao.totalAsetKewajibanSaldoWarkat(tableName, kodeDRI, tglTransaksi);
    }
}
