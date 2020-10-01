package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.dao.transaksi.BukaSistemDao;
import id.co.dapenbi.accounting.dto.StatusPemakai;
import id.co.dapenbi.accounting.dto.transaksi.BukaSistemDto;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.transaksi.BukaSistem;
import id.co.dapenbi.accounting.repository.transaksi.BukaSistemRepository;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@Slf4j
public class BukaSistemService {

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private BukaSistemDao dao;

    @Autowired
    private BukaSistemRepository repository;

    public List<StatusPemakai> getStatusPemakai() {
        return dao.findStatusPemakai();
    }

    public Date getMaxTglSaldo() {
        return dao.getMaxTglSaldo();
    }

    public BukaSistemDto aktivaPasiva(String tglSaldo) {
        return dao.aktivaPasiva(tglSaldo);
    }

    public BukaSistemDto getAktivaPasiva(String tglSaldo) {
        return dao.getAktivaPasivaSaldo(tglSaldo);
    }

    public Integer getStatusPemakai(String username) {
        List<Integer> idOrg = dao.getIdOrg(username);
        if (idOrg.size() > 0) return idOrg.get(0);
        else return 0;
    }

    @Transactional
    public BukaSistem save(BukaSistemDto dto, String username) throws ParseException {
        BukaSistem sistem = new BukaSistem();
        sistem.setNoBukaSistem(dto.getNoBukaSistem());
        sistem.setTglBuka(DateUtil.stringToDate(dto.getTglTransaksiSekarang()));
        sistem.setNilaiAktiva(dto.getNilaiAktiva());
        sistem.setNilaiPasiva(dto.getNilaiPasiva());
        sistem.setStatusPemakaian(dto.getStatusPemakai());
        sistem.setCreatedBy(username);
        sistem.setCreatedDate(new Date());
        return repository.save(sistem);
    }

    public void saveUpdateParam(String username) {
        List<Integer> idParams = dao.findIdParamCurrentDate();
        System.out.println(idParams);
        if (idParams.size() > 0) dao.updateParam(idParams.get(0));
        else dao.saveParam(UUID.randomUUID().getMostSignificantBits(), username);
    }

    public void delete(Long noBukaSistem) {
        repository.deleteById(noBukaSistem);
    }

    public BukaSistem findById(Long noBukaSistem) {
        Optional<BukaSistem> bukaSistem = repository.findById(noBukaSistem);
        if (bukaSistem.isPresent()) return bukaSistem.get();
        return new BukaSistem();
    }

    public List<BukaSistem> findAll() {
        return repository.findAll();
    }

    public Boolean libur() {
        return dao.getHariLibur().size() > 0;
    }

    public Date getTglBuka() {
        return dao.getTglBuka();
    }

    public Date getTglBukaMin1() {
        return dao.getTglBukaMin1();
    }

    public Long latestId() {
        List<BukaSistem> list = repository.latest();
        Long lastId = new Long(list.isEmpty() ? 0 : list.get(0).getNoBukaSistem());
//        log.info("{}", list);
        return lastId;
    }

    public Optional<Integer> getIdOrgByNip(String nip) {
        return dao.getIdOrgByNip(nip);
    }

    public Boolean cekTglOpen(String tglTransaksiSekarang) throws ParseException {       // CHECK STATUS OPEN
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        String txtTglTransaksiTerakhir = sdformat.format(findTglTransaksiTerakhir());

        Date tglTransaksiTerakhir = sdformat.parse(txtTglTransaksiTerakhir);
        Date tglTransaksi = sdformat.parse(tglTransaksiSekarang);

        System.out.println("The date 1 is: " + sdformat.format(tglTransaksiTerakhir));
        System.out.println("The date 2 is: " + sdformat.format(tglTransaksi));

        if (tglTransaksi.compareTo(tglTransaksiTerakhir) > 0) {
            System.out.println("Date 1 occurs after Date 2");
//            return new ResponseEntity<>("Transaksi sebelumnya belum ditutup.", HttpStatus.ACCEPTED);
            return true;
        }
        return false;
    }

    private Timestamp findTglTransaksiTerakhir() {
        return dao.findTglTransaksiTerakhir();
    }

    public String findLastDRI(String tglTransaksi) {
        return dao.findLastDRI(tglTransaksi);
    }

    public int bukaSistem(BukaSistemDto sistemDto) {
        try {
            dao.resetSaldoCurrent();
            dao.resetSaldoPA();
            dao.resetSaldoFA();
            pengaturanSistemService.setStatusOpen();
            pengaturanSistemService.setTahunBukuAndPeriode(sistemDto.getKodeTahunBuku(), sistemDto.getKodePeriode());
            pengaturanSistemService.setTanggalTransaksi(sistemDto.getTglTransaksiSekarang());
            return 1;
        } catch (Exception e){
            log.error(e.getMessage());
            return 0;
        }
    }
}
