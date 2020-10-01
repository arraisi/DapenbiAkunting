package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.dao.transaksi.CheckAndBalanceDao;
import id.co.dapenbi.accounting.dto.transaksi.CheckAndBalanceDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.core.util.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CheckAndBalanceService {

    @Autowired
    private CheckAndBalanceDao dao;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    public CheckAndBalanceDTO.Value getDefaultValue(String username) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        CheckAndBalanceDTO.Value value = new CheckAndBalanceDTO.Value();
        value.setTglTransaksi(new Date());
        value.setThnBukuPeriode(pengaturanSistem.get().getKodeTahunBuku() + "/" + pengaturanSistem.get().getKodePeriode());
        value.setUserInput(username);
        String satker = SessionUtil.getSession("satker");
        value.setSatuanKerja(satker);
        return value;
    }

    public List<CheckAndBalanceDTO.Rekening> getList() {
        return dao.list();
    }

    public List<CheckAndBalanceDTO.Rekening> getList(String status) {
        List<CheckAndBalanceDTO.Rekening> rekenings = dao.list();
        if (!StringUtils.isNotBlank(status)) return rekenings;

        List<CheckAndBalanceDTO.Rekening> filterRekenings = new ArrayList<>();
        for (CheckAndBalanceDTO.Rekening rekening : rekenings) {
            if (rekening.getStatus().equals(status)) filterRekenings.add(rekening);
        }
        return filterRekenings;
    }

    public String getSatuanKerja(String username) {
        List<String> satuanKerja = dao.getSatuanKerja(username);
        if (satuanKerja.size() > 0) return satuanKerja.get(0);
        else return "";
    }

    private Integer getTotBalance(List<CheckAndBalanceDTO.Rekening> rekenings) {
        Integer balance = 0;
        for (CheckAndBalanceDTO.Rekening rekening : rekenings) {
            if (rekening.getStatus().equals("Balance")) balance += 1;
        }
        return balance;
    }
}
