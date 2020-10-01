package id.co.dapenbi.accounting.service.impl.sse;

import id.co.dapenbi.accounting.dao.sse.SubSequenceEventDao;
import id.co.dapenbi.accounting.dao.transaksi.WarkatDao;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.dto.transaksi.ValidasiWarkatDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubSequenceEventService {

    @Autowired
    private SubSequenceEventDao dao;

    @Autowired
    private WarkatService warkatService;

    @Autowired
    private WarkatDao warkatDao;

    @Transactional
    public String validasi(WarkatDTO warkatDTO, String aktivitas) {

        final String checkSaldoAnggaran = checkSaldoAnggaran(warkatDTO.getWarkatJurnals(), "1", warkatDTO.getTglTransaksi());
        if (!checkSaldoAnggaran.equals("OK")) {
            return checkSaldoAnggaran;
        }

        try {
            warkatService.updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String validasiPreApproval(WarkatDTO warkatDTO, String aktivitas) {
        final String checkSaldoAnggaran = checkSaldoAnggaran(warkatDTO.getWarkatJurnals(), "2", warkatDTO.getTglTransaksi());
        if (!checkSaldoAnggaran.equals("OK")) {
            return checkSaldoAnggaran;
        }

        try {
            warkatService.updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String validasiFinalApproval(WarkatDTO warkatDTO, String aktivitas) {
        final String checkSaldoAnggaran = checkSaldoAnggaran(warkatDTO.getWarkatJurnals(), "3", warkatDTO.getTglTransaksi());
        if (!checkSaldoAnggaran.equals("OK")) {
            return checkSaldoAnggaran;
        }

        List<ValidasiWarkatDTO> listAccSaldoDRISatu;
        List<ValidasiWarkatDTO> listAccSaldoDRIDua;
        List<ValidasiWarkatDTO> listAccSaldoDRITiga;
        try {
            listAccSaldoDRISatu = dao.findSaldoByDRI(dao.findListIdRekeningByNoWarkat(warkatDTO.getNoWarkat()), "1", warkatDTO.getTglTransaksi());
            listAccSaldoDRIDua = dao.findSaldoByDRI(dao.findListIdRekeningByNoWarkat(warkatDTO.getNoWarkat()), "2", warkatDTO.getTglTransaksi());
            listAccSaldoDRITiga = dao.findSaldoByDRI(dao.findListIdRekeningByNoWarkat(warkatDTO.getNoWarkat()), "3", warkatDTO.getTglTransaksi());
        } catch (IncorrectResultSizeDataAccessException e) {
            return "Data saldo current tidak ditemukan / Data Saldo duplikat";
        }

        final List<ValidasiWarkatDTO> calculatedSaldoDRISatu = calculateValidasiSaldo(warkatDTO.getWarkatJurnals(), listAccSaldoDRISatu);
        final List<ValidasiWarkatDTO> calculatedSaldoDRIDua = calculateValidasiSaldo(warkatDTO.getWarkatJurnals(), listAccSaldoDRIDua);
        final List<ValidasiWarkatDTO> calculatedSaldoDRITiga = calculateValidasiSaldo(warkatDTO.getWarkatJurnals(), listAccSaldoDRITiga);

        SaldoDTO saldoSNK;
        try {
            saldoSNK = dao.findSaldoSNKDRITiga(warkatDTO.getTglTransaksi());
            calculatedSaldoDRITiga.forEach(saldo -> {
            /*  - jika lawannya debit
                - saldoSNK.getSaldoDebet().add(warkatJurnal.getJumlahDebit);
                - jika lawannya kredit
                - saldoSNK.getSaldoKredit().add(warkatJurnal.getJumlahKredit);
                - hitung saldo akhir dan saldo anggarannya
                - done tahap 1 
            */
                calculatedSaldoSNKA(warkatDTO, saldoSNK, saldo);
            });

        } catch (IncorrectResultSizeDataAccessException e) {
            return "Data Saldo SNKA duplikat";
        }

        calculatedSaldoDRISatu.forEach(validasiWarkatDTO -> {
            dao.updateAccSaldo(validasiWarkatDTO, "1");
        });
        calculatedSaldoDRIDua.forEach(validasiWarkatDTO -> {
            dao.updateAccSaldo(validasiWarkatDTO, "2");
        });
        calculatedSaldoDRITiga.forEach(validasiWarkatDTO -> {
            dao.updateAccSaldo(validasiWarkatDTO, "3");
        });


        // LOOPING UPDATE SALDO AWAL SNKA/KAS AMBIL DARI SALDO AKHIR DRI 3-NYA
        final ValidasiWarkatDTO kas = calculatedSaldoDRITiga.stream()
                .filter(value -> value.getTipeRekening().equals("KAS"))
                .findAny()
                .orElse(null);

        dao.updateAccSaldoSNKA(saldoSNK);
        dao.updateAccSaldoKas(kas);

        try {
            warkatService.updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void calculatedSaldoSNKA(WarkatDTO warkatDTO, SaldoDTO saldoSNK, ValidasiWarkatDTO saldo) {
        if (saldo.getTipeRekening().equals("BIAYA") || saldo.getTipeRekening().equals("PENDAPATAN")) {

            final WarkatJurnal warkatJurnal = warkatDTO.getWarkatJurnals().stream()
                    .filter(value -> saldo.getKodeRekekning().equals(value.getIdRekening().getKodeRekening()))
                    .findAny()
                    .orElse(null);

            if (saldo.getTipeRekening().equals("D")) {
                final BigDecimal saldoDebetSNKA = saldoSNK.getSaldoDebet().add(warkatJurnal.getJumlahDebit());
                final BigDecimal saldoAkhir = saldo.getSaldoAwal().add(saldo.getSaldoDebet()).subtract(saldo.getSaldoKredit());
                BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                        .subtract(saldo.getSaldoDebet())
                        .add(saldo.getSaldoKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
                saldo.setSaldoAnggaran(saldoAnggaran);

                saldoSNK.setSaldoDebet(saldoDebetSNKA);
                saldoSNK.setSaldoAkhir(saldoAkhir);
            } else {
                final BigDecimal saldoKreditSNKA = saldoSNK.getSaldoKredit().add(warkatJurnal.getJumlahKredit());
                final BigDecimal saldoAkhir = saldo.getSaldoAwal().subtract(saldo.getSaldoDebet()).add(saldo.getSaldoKredit());
                BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                        .subtract(saldo.getSaldoDebet())
                        .add(saldo.getSaldoKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
                saldo.setSaldoAnggaran(saldoAnggaran);

                saldoSNK.setSaldoDebet(saldoKreditSNKA);
                saldoSNK.setSaldoAkhir(saldoAkhir);
            }
        }
    }

    public List<ValidasiWarkatDTO> calculateValidasiSaldo(List<WarkatJurnal> warkatJurnals, List<ValidasiWarkatDTO> listSaldo) {

        List<ValidasiWarkatDTO> saldoCalculateds = new ArrayList<>();

        for (WarkatJurnal warkatJurnal : warkatJurnals) {

            ValidasiWarkatDTO saldo = listSaldo.stream()
                    .filter(value -> warkatJurnal.getIdRekening().getKodeRekening().equals(value.getKodeRekekning()))
                    .findAny()
                    .orElse(null);

            if ((saldo.getTipeRekening().equals("BIAYA") || saldo.getTipeRekening().equals("PENDAPATAN")) && saldo.getKodeDRI().equals("3")) {

                final BigDecimal debet = saldo.getSaldoDebet().add(warkatJurnal.getJumlahDebit());
                saldo.setSaldoDebet(debet);

                final BigDecimal kredit = saldo.getSaldoKredit().add(warkatJurnal.getJumlahDebit());
                saldo.setSaldoKredit(kredit);

                BigDecimal saldoAkhir; // SALDO_AKHIR = SALDO_AWAL + SALDO_DEBIT - SALDO_KREDIT
                if (saldo.getSaldoNormal().equals("D")) {
                    saldoAkhir = saldo.getSaldoAwal().add(saldo.getSaldoDebet()).subtract(saldo.getSaldoKredit());
                } else {
                    saldoAkhir = saldo.getSaldoAwal().subtract(saldo.getSaldoDebet()).add(saldo.getSaldoKredit());
                }

                BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                        .subtract(saldo.getSaldoDebet())
                        .add(saldo.getSaldoKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
                saldo.setSaldoAnggaran(saldoAnggaran);

                saldo.setSaldoAkhir(saldoAkhir);
                saldo.setSaldoAnggaran(saldoAnggaran);

                saldoCalculateds.add(saldo);
            } else {
                final ValidasiWarkatDTO calculatedSaldoValidasi = getCalculatedSaldoValidasi(warkatJurnal, saldo);
                saldoCalculateds.add(calculatedSaldoValidasi);
            }
        }

        return saldoCalculateds;
    }

    private ValidasiWarkatDTO getCalculatedSaldoValidasi(WarkatJurnal warkatJurnal, ValidasiWarkatDTO saldo) {
        ValidasiWarkatDTO validasiWarkatDTO = new ValidasiWarkatDTO();
        BigDecimal saldoDebit = saldo.getSaldoDebet().add(warkatJurnal.getJumlahDebit()); // SALDO_DEBIT += NOMINAL_DEBIT
        BigDecimal saldoKredit = saldo.getSaldoKredit().add(warkatJurnal.getJumlahKredit()); // SALDO_KREDIT += NOMINAL_KREDIT

        BigDecimal saldoAkhir; // SALDO_AKHIR = SALDO_AWAL + SALDO_DEBIT - SALDO_KREDIT
        if (saldo.getSaldoNormal().equals("D")) {
            saldoAkhir = saldo.getSaldoAwal().add(saldoDebit).subtract(saldoKredit);
        } else {
            saldoAkhir = saldo.getSaldoAwal().subtract(saldoDebit).add(saldoKredit);
        }

        if (saldo.getTipeRekening().equals("PENDAPATAN")) {

            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .add(warkatJurnal.getJumlahDebit())
                    .subtract(warkatJurnal.getJumlahKredit()); // SALDO_ANGGARAN += JUMLAH_DEBIT - JUMLAH_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);

        } else if (saldo.getTipeRekening().equals("BIAYA")) { // biaya, aset opr, kombinasi false

            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .subtract(warkatJurnal.getJumlahDebit())
                    .add(warkatJurnal.getJumlahKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);

        } else if (saldo.getTipeRekening().equals("ASET_OPR")) { // biaya, aset opr, kombinasi false

            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .subtract(warkatJurnal.getJumlahDebit())
                    .add(warkatJurnal.getJumlahKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);

        }

        return saldo;
    }

    @Transactional
    public String reject(WarkatDTO warkatDTO, String aktivitas) {
        try {
            warkatService.updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String checkSaldoAnggaran(List<WarkatJurnal> _saldo, String kodeDRI, Timestamp tglTransaksi) {
        if (_saldo.isEmpty()) return "No Content";
        for (int i = 0; i < _saldo.size(); i++) {
            // cek jumlah debit tidak boleh > saldo anggaran
            final SaldoDTO accSaldo = dao.findSaldoByKodeRekeningAndDRI(_saldo.get(i).getIdRekening().getKodeRekening(), kodeDRI, tglTransaksi);
            if (_saldo.get(i).getJumlahDebit().compareTo(accSaldo.getSaldoAnggaran()) > 0) {
                return "Saldo anggaran " + _saldo.get(i).getIdRekening().getKodeRekening() + " - " + _saldo.get(i).getIdRekening().getNamaRekening() + " tidak mencukupi.";
            }
        }
        return "OK";
    }
}
