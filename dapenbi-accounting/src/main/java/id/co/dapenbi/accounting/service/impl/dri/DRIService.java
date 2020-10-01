package id.co.dapenbi.accounting.service.impl.dri;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.dri.DRIDao;
import id.co.dapenbi.accounting.dao.transaksi.InformasiSaldoDao;
import id.co.dapenbi.accounting.dto.dri.DRIDTO;
import id.co.dapenbi.accounting.repository.parameter.PengaturanSistemRepository;
import id.co.dapenbi.accounting.repository.parameter.RekeningRepository;
import id.co.dapenbi.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class DRIService {

    @Autowired
    private DRIDao driDao;

    @Autowired
    private RekeningRepository rekeningRepository;

    @Autowired
    private PengaturanSistemRepository pengaturanSistemRepository;

    @Autowired
    private InformasiSaldoDao informasiSaldoDao;

    public DataTablesResponse<DRIDTO.DRIDebitKredit> sementara(DataTablesRequest<DRIDTO.DRISementara> params, String search) {
//        List<DRIDTO.DRISementara> data = driDao.sementara(params, search);
//        Long rowCount = driDao.sementara(search, params.getValue());

        DRIDTO.DRISementara driSementara = params.getValue();
//        log.info("Datatables request: {}", params);
        driSementara.setSaldoNormal("D");
        params.setValue(driSementara);
        List<DRIDTO.DRISementara> rawDataDebit = driDao.sementara(params, search);

        List<DRIDTO.DRISementara> dataDebit = new ArrayList<>();
        int idx = 0;
        int lvl = 0;
        BigDecimal saldo = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (int i=0; i<rawDataDebit.size(); i++) {
            DRIDTO.DRISementara value = new DRIDTO.DRISementara();
            if (rawDataDebit.get(i).getLevelRekening() == 6) {
                BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir();
                totalDebit = totalDebit.add(saldoAkhir);
                saldo = saldo.add(saldoAkhir == null ? BigDecimal.ZERO : saldoAkhir);
            }

            if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataDebit.add(idx, value);
                idx++;
            }

            lvl = rawDataDebit.get(i).getLevelRekening();
            dataDebit.add(idx, rawDataDebit.get(i));
            idx++;

            if ((rawDataDebit.size() - i == 1) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataDebit.add(idx, value);
            }
        }

        if ((params.getLength() != -1) && (dataDebit.size() > params.getLength()))
            dataDebit = dataDebit.subList(0, params.getLength().intValue()-1);

//        log.info("Index: {}, Size: {}", idx, rawDataDebit.size());

        driSementara.setSaldoNormal("K");
        params.setValue(driSementara);
        List<DRIDTO.DRISementara> rawDataKredit = driDao.sementara(params, search);

        List<DRIDTO.DRISementara> dataKredit = new ArrayList<>();
        idx = 0;
        lvl = 0;
        saldo = BigDecimal.ZERO;
        BigDecimal totalKredit = BigDecimal.ZERO;
        for (int i=0; i<rawDataKredit.size(); i++) {
            DRIDTO.DRISementara value = new DRIDTO.DRISementara();
            if (rawDataKredit.get(i).getLevelRekening() == 6) {
                BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir();
                totalKredit = totalKredit.add(saldoAkhir);
                saldo = saldo.add(saldoAkhir == null ? BigDecimal.ZERO : saldoAkhir);
            }

            if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataKredit.add(idx, value);
                idx++;
            }
            lvl = rawDataKredit.get(i).getLevelRekening();
            dataKredit.add(idx, rawDataKredit.get(i));
            idx++;

            if ((rawDataKredit.size() - i == 1) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataKredit.add(idx, value);
            }
        }

        if ((params.getLength() != -1) && (dataKredit.size() > params.getLength()))
            dataKredit = dataKredit.subList(0, params.getLength().intValue()-1);

        List<DRIDTO.DRIDebitKredit> listDebitKredit = new ArrayList<>();
        if (dataDebit.size() > dataKredit.size()) {
            int diff = dataDebit.size() - dataKredit.size();
            for (int i=0; i<diff; i++) {
                DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                dataKredit.add(blankObject);
            }
        } else if (dataKredit.size() > dataDebit.size()) {
            int diff = dataKredit.size() - dataDebit.size();
            for (int i=0; i<diff; i++) {
                DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                dataDebit.add(blankObject);
            }
        }

//        BigDecimal totalDebit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalAset");
//        BigDecimal totalKredit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalKewajiban");

        for (int i=0; i<dataDebit.size(); i++) {
            DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
            debitKredit.setIdRekeningDebit(dataDebit.get(i).getIdRekening());
            debitKredit.setIdRekeningKredit(dataKredit.get(i).getIdRekening());
            debitKredit.setKodeRekeningDebit(dataDebit.get(i).getKodeRekening());
            debitKredit.setKodeRekeningKredit(dataKredit.get(i).getKodeRekening());
            debitKredit.setLevelRekeningDebit(dataDebit.get(i).getLevelRekening());
            debitKredit.setLevelRekeningKredit(dataKredit.get(i).getLevelRekening());
            debitKredit.setNamaRekeningDebit(dataDebit.get(i).getNamaRekening());
            debitKredit.setNamaRekeningKredit(dataKredit.get(i).getNamaRekening());
            debitKredit.setSaldoAkhirDebit(dataDebit.get(i).getSaldoAkhir());
            debitKredit.setSaldoAkhirKredit(dataKredit.get(i).getSaldoAkhir());
            debitKredit.setCetakTebalDebit(dataDebit.get(i).getCetakTebal());
            debitKredit.setCetakTebalKredit(dataKredit.get(i).getCetakTebal());
            listDebitKredit.add(debitKredit);
        }

        DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
        debitKredit.setNamaRekeningDebit("TOTAL DEBET");
        debitKredit.setNamaRekeningKredit("TOTAL KREDIT");
        debitKredit.setCetakTebalDebit("1");
        debitKredit.setSaldoAkhirDebit(totalDebit);
        debitKredit.setSaldoAkhirKredit(totalKredit);
        debitKredit.setCetakTebalKredit("1");
        listDebitKredit.add(debitKredit);

        Long rowCount = (long) listDebitKredit.size();
        return new DataTablesResponse<>(listDebitKredit, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<DRIDTO.DRIDebitKredit> dri(DataTablesRequest<DRIDTO.DRI> params, String search) {
//        List<DRIDTO.DRI> data = driDao.dri(params, search);
//        Long rowCount = driDao.dri(params.getValue(), search);

        DRIDTO.DRI dri = params.getValue();
        dri.setSaldoNormal("D");
        params.setValue(dri);
        List<DRIDTO.DRI> rawDataDebit = driDao.dri(params, search);

        List<DRIDTO.DRI> dataDebit = new ArrayList<>();
        int idx = 0;
        int lvl = 0;
        BigDecimal saldo = BigDecimal.ZERO;
        for (int i=0; i<rawDataDebit.size(); i++) {
            DRIDTO.DRI value = new DRIDTO.DRI();
            if (rawDataDebit.get(i).getLevelRekening() == 6) {
                saldo = saldo.add(rawDataDebit.get(i).getSaldoAkhir());
            }

            if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataDebit.add(idx, value);
                idx++;
            }
            lvl = rawDataDebit.get(i).getLevelRekening();
            dataDebit.add(idx, rawDataDebit.get(i));
            idx++;

            if ((rawDataDebit.size() - i == 1) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataDebit.add(idx, value);
            }
        }

        dri.setSaldoNormal("K");
        params.setValue(dri);
        List<DRIDTO.DRI> rawDataKredit = driDao.dri(params, search);

        List<DRIDTO.DRI> dataKredit = new ArrayList<>();
        idx = 0;
        lvl = 0;
        saldo = BigDecimal.ZERO;
        for (int i=0; i<rawDataKredit.size(); i++) {
            DRIDTO.DRI value = new DRIDTO.DRI();
            if (rawDataKredit.get(i).getLevelRekening() == 6) {
                saldo = saldo.add(rawDataKredit.get(i).getSaldoAkhir());
            }

            if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataKredit.add(idx, value);
                idx++;
            }
            lvl = rawDataKredit.get(i).getLevelRekening();
            dataKredit.add(idx, rawDataKredit.get(i));
            idx++;

            if ((rawDataKredit.size() - i == 1) && (lvl == 6)) {
                value.setKodeRekening("");
                value.setNamaRekening("Total Saldo");
                value.setSaldoAkhir(saldo);
                value.setCetakTebal("1");
                saldo = BigDecimal.ZERO;
                dataKredit.add(idx, value);
            }
        }

        List<DRIDTO.DRIDebitKredit> listDebitKredit = new ArrayList<>();

        if (dataDebit.size() > dataKredit.size()) {
            int diff = dataDebit.size() - dataKredit.size();
            for (int i=0; i<diff; i++) {
                DRIDTO.DRI blankObject = new DRIDTO.DRI();
                dataKredit.add(blankObject);
            }
        } else if (dataKredit.size() > dataDebit.size()) {
            int diff = dataKredit.size() - dataDebit.size();
            for (int i=0; i<diff; i++) {
                DRIDTO.DRI blankObject = new DRIDTO.DRI();
                dataDebit.add(blankObject);
            }
        }

        BigDecimal totalDebit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO", params.getValue().getKodeDRI(), params.getValue().getTglPeriode()).get("totalAset");
        BigDecimal totalKredit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO", params.getValue().getKodeDRI(), params.getValue().getTglPeriode()).get("totalKewajiban");

        for (int i=0; i<dataDebit.size(); i++) {
            DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
            debitKredit.setIdRekeningDebit(dataDebit.get(i).getIdRekening());
            debitKredit.setIdRekeningKredit(dataKredit.get(i).getIdRekening());
            debitKredit.setKodeRekeningDebit(dataDebit.get(i).getKodeRekening());
            debitKredit.setKodeRekeningKredit(dataKredit.get(i).getKodeRekening());
            debitKredit.setLevelRekeningDebit(dataDebit.get(i).getLevelRekening());
            debitKredit.setLevelRekeningKredit(dataKredit.get(i).getLevelRekening());
            debitKredit.setNamaRekeningDebit(dataDebit.get(i).getNamaRekening());
            debitKredit.setNamaRekeningKredit(dataKredit.get(i).getNamaRekening());
            debitKredit.setSaldoAkhirDebit(dataDebit.get(i).getSaldoAkhir());
            debitKredit.setSaldoAkhirKredit(dataKredit.get(i).getSaldoAkhir());
            debitKredit.setCetakTebalDebit(dataDebit.get(i).getCetakTebal());
            debitKredit.setCetakTebalKredit(dataKredit.get(i).getCetakTebal());
            listDebitKredit.add(debitKredit);
        }

        DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
        debitKredit.setNamaRekeningDebit("TOTAL DEBET");
        debitKredit.setNamaRekeningKredit("TOTAL KREDIT");
        debitKredit.setCetakTebalDebit("1");
        debitKredit.setSaldoAkhirDebit(totalDebit);
        debitKredit.setSaldoAkhirKredit(totalKredit);
        debitKredit.setCetakTebalKredit("1");
        listDebitKredit.add(debitKredit);

        Long rowCount = (long) listDebitKredit.size();

        return new DataTablesResponse<>(listDebitKredit, params.getDraw(), rowCount, rowCount);
    }

    public byte[] exportPdfSementara(Map<String, Object> parameter, String jasperName, List<Object> dataList) {
        try {
            List<DRIDTO.ExportDataSementara> res = new ArrayList<>();
            List<DRIDTO.DRISementara> rawDataDebit = driDao.exportSementara("D");
            List<DRIDTO.DRISementara> rawDataKredit = driDao.exportSementara("K");
            List<DRIDTO.DRISementara> dataDebit = new ArrayList<>();
            List<DRIDTO.DRISementara> dataKredit = new ArrayList<>();

            int idx = 0;
            int lvl = 0;
            BigDecimal saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataDebit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataDebit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataDebit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                    idx++;
                }
                lvl = rawDataDebit.get(i).getLevelRekening();
                dataDebit.add(idx, rawDataDebit.get(i));
                idx++;

                if (i == rawDataDebit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                }
            }

            idx = 0;
            lvl = 0;
            saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataKredit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataKredit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataKredit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                    idx++;
                }
                lvl = rawDataKredit.get(i).getLevelRekening();
                dataKredit.add(idx, rawDataKredit.get(i));
                idx++;

                if (i == rawDataKredit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                }
            }

            DRIDTO.ExportDataSementara driSementara = new DRIDTO.ExportDataSementara();
            driSementara.setOddPage(dataDebit);
            driSementara.setEvenPage(dataKredit);
            driSementara.setTotalSaldoDebit(informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalAset"));
            driSementara.setTotalSaldoKredit(informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalKewajiban"));

            if (driSementara.getOddPage().size() > driSementara.getEvenPage().size()) {
                int diff = driSementara.getOddPage().size() - driSementara.getEvenPage().size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    driSementara.getEvenPage().add(blankObject);
                }
            } else if (driSementara.getEvenPage().size() > driSementara.getOddPage().size()) {
                int diff = driSementara.getEvenPage().size() - driSementara.getOddPage().size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    driSementara.getOddPage().add(blankObject);
                }
            }

            int size = driSementara.getOddPage().size();
            final int pageCount = 13;
            int chunk = 0;
            for(int i = 0; i < (int) Math.ceil(size / (pageCount * 1.0)); i++) {
//                DRIDTO.ExportData exportData = new DRIDTO.ExportData();
//                List<Object> oddPage;
//                List<Object> evenPage;
                DRIDTO.ExportDataSementara exportData = new DRIDTO.ExportDataSementara();
                List<DRIDTO.DRISementara> oddPage;
                List<DRIDTO.DRISementara> evenPage;

                if (i == 0) {
                    int initCount = 12;
                    oddPage = subListValidSementara(driSementara.getOddPage(), chunk, chunk + initCount);
//                    oddPage = driSementara.getOddPage().subList(chunk, chunk + initCount);
//                    chunk+=initCount;
                    evenPage = subListValidSementara(driSementara.getEvenPage(), chunk, chunk + initCount);
//                    evenPage = driSementara.getEvenPage().subList(chunk, chunk + initCount);
                    chunk+=initCount;
                    exportData.setOddPage(oddPage);
                    exportData.setEvenPage(evenPage);
                    res.add(exportData);
                } else {
                    oddPage = subListValidSementara(driSementara.getOddPage(), chunk, chunk + pageCount);
//                    oddPage = driSementara.getOddPage().subList(chunk, chunk + pageCount);
//                    chunk+=pageCount;
                    evenPage = subListValidSementara(driSementara.getEvenPage(), chunk, chunk + pageCount);
//                    evenPage = driSementara.getEvenPage().subList(chunk, chunk + pageCount);
                    chunk+=pageCount;

                    exportData.setOddPage(oddPage);
                    exportData.setEvenPage(evenPage);
                    res.add(exportData);
                }
            }

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_dri_sementara_detail.jasper");

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            parameter.put("driDetailLocation", jasperReportSR);
            parameter.put("totalSaldoDebit", driSementara.getTotalSaldoDebit());
            parameter.put("totalSaldoKredit", driSementara.getTotalSaldoKredit());
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("logoLocation", image);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(res));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcelSementara(Map<String, Object> parameter, String jasperName, List<Object> dataList) {
        try {
            List<DRIDTO.ExportDataSementara> res = new ArrayList<>();
            List<DRIDTO.DRISementara> rawDataDebit = driDao.exportSementara("D");
            List<DRIDTO.DRISementara> rawDataKredit = driDao.exportSementara("K");
            List<DRIDTO.DRISementara> dataDebit = new ArrayList<>();
            List<DRIDTO.DRISementara> dataKredit = new ArrayList<>();

            int idx = 0;
            int lvl = 0;
            BigDecimal saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataDebit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataDebit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataDebit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                    idx++;
                }
                lvl = rawDataDebit.get(i).getLevelRekening();
                dataDebit.add(idx, rawDataDebit.get(i));
                idx++;

                if ((rawDataDebit.size() - i == 1) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                }
            }

            idx = 0;
            lvl = 0;
            saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataKredit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataKredit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataKredit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                    idx++;
                }
                lvl = rawDataKredit.get(i).getLevelRekening();
                dataKredit.add(idx, rawDataKredit.get(i));
                idx++;

                if ((rawDataKredit.size() - i == 1) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                }
            }

            DRIDTO.ExportDataSementara driSementara = new DRIDTO.ExportDataSementara();
            driSementara.setOddPage(dataDebit);
            driSementara.setEvenPage(dataKredit);
            driSementara.setTotalSaldoDebit(informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalAset"));
            driSementara.setTotalSaldoKredit(informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalKewajiban"));

            if (driSementara.getOddPage().size() > driSementara.getEvenPage().size()) {
                int diff = driSementara.getOddPage().size() - driSementara.getEvenPage().size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    driSementara.getEvenPage().add(blankObject);
                }
            } else if (driSementara.getEvenPage().size() > driSementara.getOddPage().size()) {
                int diff = driSementara.getEvenPage().size() - driSementara.getOddPage().size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    driSementara.getOddPage().add(blankObject);
                }
            }

            int size = driSementara.getOddPage().size();
            final int pageCount = 13;
            int chunk = 0;
            for(int i = 0; i < (int) Math.ceil(size / (pageCount * 1.0)); i++) {
//                DRIDTO.ExportData exportData = new DRIDTO.ExportData();
//                List<Object> oddPage;
//                List<Object> evenPage;
                DRIDTO.ExportDataSementara exportData = new DRIDTO.ExportDataSementara();
                List<DRIDTO.DRISementara> oddPage;
                List<DRIDTO.DRISementara> evenPage;

                if (i == 0) {
                    int initCount = 12;
                    oddPage = subListValidSementara(driSementara.getOddPage(), chunk, chunk + initCount);
//                    oddPage = driSementara.getOddPage().subList(chunk, chunk + pageCount);
//                    chunk+=initCount;
                    evenPage = subListValidSementara(driSementara.getEvenPage(), chunk, chunk + initCount);
//                    evenPage = driSementara.getEvenPage().subList(chunk, chunk + pageCount);
                    chunk+=initCount;
                    exportData.setOddPage(oddPage);
                    exportData.setEvenPage(evenPage);
                    res.add(exportData);
                } else {
                    oddPage = subListValidSementara(driSementara.getOddPage(), chunk, chunk + pageCount);
//                    oddPage = driSementara.getOddPage().subList(chunk, chunk + pageCount);
//                    chunk+=pageCount;
                    evenPage = subListValidSementara(driSementara.getEvenPage(), chunk, chunk + pageCount);
//                    evenPage = driSementara.getEvenPage().subList(chunk, chunk + pageCount);
                    chunk+=pageCount;

                    exportData.setOddPage(oddPage);
                    exportData.setEvenPage(evenPage);
                    res.add(exportData);
                }
            }

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_dri_sementara_detail.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            parameter.put("driDetailLocation", jasperReportSR);
            parameter.put("totalSaldoDebit", driSementara.getTotalSaldoDebit());
            parameter.put("totalSaldoKredit", driSementara.getTotalSaldoKredit());
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("logoLocation", logoImage);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(res));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {
                Exporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                exporter.setConfiguration(configuration);
                exporter.exportReport();

                return byteArrayOutputStream.toByteArray();
            }
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<DRIDTO.DRISementara> subListValidSementara(List<DRIDTO.DRISementara> data, Integer from, Integer to) {
        if ((data.size() < from) && (data.size() < to)) {
            return new ArrayList<>();
        } else if (data.size() < to) {
            return data.subList(from, data.size());
        } else {
            return data.subList(from, to);
        }
    }

    public byte[] exportPdfDRI(Map<String, Object> parameter, String jasperName) {
        try {
            List<DRIDTO.DRI> rawDataDebit = driDao.exportDRI("D", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString());
            List<DRIDTO.DRI> rawDataKredit = driDao.exportDRI("K", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString());
            List<DRIDTO.DRI> dataDebit = new ArrayList<>();
            List<DRIDTO.DRI> dataKredit = new ArrayList<>();

            int idx = 0;
            int lvl = 0;
            BigDecimal saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataDebit.size(); i++) {
                DRIDTO.DRI value = new DRIDTO.DRI();
                if (rawDataDebit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataDebit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                    idx++;
                }
                lvl = rawDataDebit.get(i).getLevelRekening();
                dataDebit.add(idx, rawDataDebit.get(i));
                idx++;

                if (i == rawDataDebit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                }
            }

            idx = 0;
            lvl = 0;
            saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataKredit.size(); i++) {
                DRIDTO.DRI value = new DRIDTO.DRI();
                if (rawDataKredit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataKredit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                    idx++;
                }
                lvl = rawDataKredit.get(i).getLevelRekening();
                dataKredit.add(idx, rawDataKredit.get(i));
                idx++;

                if (i == rawDataKredit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                }
            }

            BigDecimal totalDebit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString()).get("totalAset");
            BigDecimal totalKredit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString()).get("totalKewajiban");

            if (dataDebit.size() > dataKredit.size()) {
                int diff = dataDebit.size() - dataKredit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRI blankObject = new DRIDTO.DRI();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataKredit.add(blankObject);
                }
            } else if (dataKredit.size() > dataDebit.size()) {
                int diff = dataKredit.size() - dataDebit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRI blankObject = new DRIDTO.DRI();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataDebit.add(blankObject);
                }
            }

            List<DRIDTO.DRIDebitKredit> listDebitKredit = new ArrayList<>();
            for (int i=0; i<dataDebit.size(); i++) {
                DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
                debitKredit.setIdRekeningDebit(dataDebit.get(i).getIdRekening());
                debitKredit.setIdRekeningKredit(dataKredit.get(i).getIdRekening());
                debitKredit.setKodeRekeningDebit(dataDebit.get(i).getKodeRekening());
                debitKredit.setKodeRekeningKredit(dataKredit.get(i).getKodeRekening());
                debitKredit.setLevelRekeningDebit(dataDebit.get(i).getLevelRekening());
                debitKredit.setLevelRekeningKredit(dataKredit.get(i).getLevelRekening());
                debitKredit.setNamaRekeningDebit(dataDebit.get(i).getNamaRekening());
                debitKredit.setNamaRekeningKredit(dataKredit.get(i).getNamaRekening());
                debitKredit.setSaldoAkhirDebit(dataDebit.get(i).getSaldoAkhir());
                debitKredit.setSaldoAkhirKredit(dataKredit.get(i).getSaldoAkhir());
                debitKredit.setCetakTebalDebit(dataDebit.get(i).getCetakTebal());
                debitKredit.setCetakTebalKredit(dataKredit.get(i).getCetakTebal());
                listDebitKredit.add(debitKredit);
            }

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_dri_sementara_detail.jasper");

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            parameter.put("driDetailLocation", jasperReportSR);
            parameter.put("totalSaldoDebit", totalDebit);
            parameter.put("totalSaldoKredit", totalKredit);
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("logoLocation", image);
//            parameter.put("periode", DateUtil.stringToDate(parameter.get("periode").toString()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(listDebitKredit));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcelDRI(Map<String, Object> parameter, String jasperName) {
        try {
            List<DRIDTO.DRI> rawDataDebit = driDao.exportDRI("D", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString());
            List<DRIDTO.DRI> rawDataKredit = driDao.exportDRI("K", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString());
            List<DRIDTO.DRI> dataDebit = new ArrayList<>();
            List<DRIDTO.DRI> dataKredit = new ArrayList<>();

            int idx = 0;
            int lvl = 0;
            BigDecimal saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataDebit.size(); i++) {
                DRIDTO.DRI value = new DRIDTO.DRI();
                if (rawDataDebit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataDebit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                    idx++;
                }
                lvl = rawDataDebit.get(i).getLevelRekening();
                dataDebit.add(idx, rawDataDebit.get(i));
                idx++;

                if (i == rawDataDebit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                }
            }

            idx = 0;
            lvl = 0;
            saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataKredit.size(); i++) {
                DRIDTO.DRI value = new DRIDTO.DRI();
                if (rawDataKredit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataKredit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                    idx++;
                }
                lvl = rawDataKredit.get(i).getLevelRekening();
                dataKredit.add(idx, rawDataKredit.get(i));
                idx++;

                if (i == rawDataKredit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                }
            }

            BigDecimal totalDebit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString()).get("totalAset");
            BigDecimal totalKredit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO", parameter.get("kodeDRI").toString(), parameter.get("datePeriode").toString()).get("totalKewajiban");

            if (dataDebit.size() > dataKredit.size()) {
                int diff = dataDebit.size() - dataKredit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRI blankObject = new DRIDTO.DRI();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataKredit.add(blankObject);
                }
            } else if (dataKredit.size() > dataDebit.size()) {
                int diff = dataKredit.size() - dataDebit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRI blankObject = new DRIDTO.DRI();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataDebit.add(blankObject);
                }
            }

            List<DRIDTO.DRIDebitKredit> listDebitKredit = new ArrayList<>();
            for (int i=0; i<dataDebit.size(); i++) {
                DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
                debitKredit.setIdRekeningDebit(dataDebit.get(i).getIdRekening());
                debitKredit.setIdRekeningKredit(dataKredit.get(i).getIdRekening());
                debitKredit.setKodeRekeningDebit(dataDebit.get(i).getKodeRekening());
                debitKredit.setKodeRekeningKredit(dataKredit.get(i).getKodeRekening());
                debitKredit.setLevelRekeningDebit(dataDebit.get(i).getLevelRekening());
                debitKredit.setLevelRekeningKredit(dataKredit.get(i).getLevelRekening());
                debitKredit.setNamaRekeningDebit(dataDebit.get(i).getNamaRekening());
                debitKredit.setNamaRekeningKredit(dataKredit.get(i).getNamaRekening());
                debitKredit.setSaldoAkhirDebit(dataDebit.get(i).getSaldoAkhir());
                debitKredit.setSaldoAkhirKredit(dataKredit.get(i).getSaldoAkhir());
                debitKredit.setCetakTebalDebit(dataDebit.get(i).getCetakTebal());
                debitKredit.setCetakTebalKredit(dataKredit.get(i).getCetakTebal());
                listDebitKredit.add(debitKredit);
            }

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_dri_sementara_detail.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            parameter.put("driDetailLocation", jasperReportSR);
            parameter.put("totalSaldoDebit", totalDebit);
            parameter.put("totalSaldoKredit", totalKredit);
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("logoLocation", image);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(listDebitKredit));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {
                Exporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                exporter.setConfiguration(configuration);
                exporter.exportReport();

                return byteArrayOutputStream.toByteArray();
            }
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<DRIDTO.DRI> subListValidDRI(List<DRIDTO.DRI> data, Integer from, Integer to) {
        if ((data.size() < from) && (data.size() < to)) {
            return new ArrayList<>();
        } else if (data.size() < to) {
            return data.subList(from, data.size());
        } else {
            return data.subList(from, to);
        }
    }

    public byte[] exportPDFSementara(Map<String, Object> parameter, String jasperName) {
        try {
            List<DRIDTO.DRISementara> rawDataDebit = driDao.exportSementara("D");
            List<DRIDTO.DRISementara> rawDataKredit = driDao.exportSementara("K");
            List<DRIDTO.DRISementara> dataDebit = new ArrayList<>();
            List<DRIDTO.DRISementara> dataKredit = new ArrayList<>();

            int idx = 0;
            int lvl = 0;
            BigDecimal saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataDebit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataDebit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataDebit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                    idx++;
                }
                lvl = rawDataDebit.get(i).getLevelRekening();
                dataDebit.add(idx, rawDataDebit.get(i));
                idx++;

                if (i == rawDataDebit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                }
            }

            idx = 0;
            lvl = 0;
            saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataKredit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataKredit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataKredit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                    idx++;
                }
                lvl = rawDataKredit.get(i).getLevelRekening();
                dataKredit.add(idx, rawDataKredit.get(i));
                idx++;

                if (i == rawDataKredit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                }
            }

            BigDecimal totalDebit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalAset");
            BigDecimal totalKredit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalKewajiban");

            if (dataDebit.size() > dataKredit.size()) {
                int diff = dataDebit.size() - dataKredit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataKredit.add(blankObject);
                }
            } else if (dataKredit.size() > dataDebit.size()) {
                int diff = dataKredit.size() - dataDebit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataDebit.add(blankObject);
                }
            }

            List<DRIDTO.DRIDebitKredit> listDebitKredit = new ArrayList<>();
            for (int i=0; i<dataDebit.size(); i++) {
                DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
                debitKredit.setIdRekeningDebit(dataDebit.get(i).getIdRekening());
                debitKredit.setIdRekeningKredit(dataKredit.get(i).getIdRekening());
                debitKredit.setKodeRekeningDebit(dataDebit.get(i).getKodeRekening());
                debitKredit.setKodeRekeningKredit(dataKredit.get(i).getKodeRekening());
                debitKredit.setLevelRekeningDebit(dataDebit.get(i).getLevelRekening());
                debitKredit.setLevelRekeningKredit(dataKredit.get(i).getLevelRekening());
                debitKredit.setNamaRekeningDebit(dataDebit.get(i).getNamaRekening());
                debitKredit.setNamaRekeningKredit(dataKredit.get(i).getNamaRekening());
                debitKredit.setSaldoAkhirDebit(dataDebit.get(i).getSaldoAkhir());
                debitKredit.setSaldoAkhirKredit(dataKredit.get(i).getSaldoAkhir());
                debitKredit.setCetakTebalDebit(dataDebit.get(i).getCetakTebal());
                debitKredit.setCetakTebalKredit(dataKredit.get(i).getCetakTebal());
                listDebitKredit.add(debitKredit);
            }

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_dri_sementara_detail.jasper");

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            parameter.put("driDetailLocation", jasperReportSR);
            parameter.put("totalSaldoDebit", totalDebit);
            parameter.put("totalSaldoKredit", totalKredit);
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("logoLocation", image);
            parameter.put("jenisDRI", "DRI Sementara");

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(listDebitKredit));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportEXCELSementara(Map<String, Object> parameter, String jasperName) {
        try {
            List<DRIDTO.DRISementara> rawDataDebit = driDao.exportSementara("D");
            List<DRIDTO.DRISementara> rawDataKredit = driDao.exportSementara("K");
            List<DRIDTO.DRISementara> dataDebit = new ArrayList<>();
            List<DRIDTO.DRISementara> dataKredit = new ArrayList<>();

            int idx = 0;
            int lvl = 0;
            BigDecimal saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataDebit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataDebit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataDebit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataDebit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataDebit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                    idx++;
                }
                lvl = rawDataDebit.get(i).getLevelRekening();
                dataDebit.add(idx, rawDataDebit.get(i));
                idx++;

                if (i == rawDataDebit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataDebit.add(idx, value);
                }
            }

            idx = 0;
            lvl = 0;
            saldo = BigDecimal.ZERO;
            for (int i=0; i<rawDataKredit.size(); i++) {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                if (rawDataKredit.get(i).getLevelRekening() == 6) {
                    BigDecimal saldoAkhir = rawDataKredit.get(i).getSaldoAkhir() == null ? BigDecimal.ZERO : rawDataKredit.get(i).getSaldoAkhir();
                    saldo = saldo.add(saldoAkhir);
                }

                if ((rawDataKredit.get(i).getLevelRekening() != 6) && (lvl == 6)) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                    idx++;
                }
                lvl = rawDataKredit.get(i).getLevelRekening();
                dataKredit.add(idx, rawDataKredit.get(i));
                idx++;

                if (i == rawDataKredit.size()-1) {
                    value.setKodeRekening("");
                    value.setNamaRekening("Total Saldo");
                    value.setSaldoAkhir(saldo);
                    value.setCetakTebal("1");
                    saldo = BigDecimal.ZERO;
                    dataKredit.add(idx, value);
                }
            }

            BigDecimal totalDebit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalAset");
            BigDecimal totalKredit = informasiSaldoDao.totalAsetKewajibanSaldoWarkat("ACC_SALDO_FA", "", "").get("totalKewajiban");

            if (dataDebit.size() > dataKredit.size()) {
                int diff = dataDebit.size() - dataKredit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataKredit.add(blankObject);
                }
            } else if (dataKredit.size() > dataDebit.size()) {
                int diff = dataKredit.size() - dataDebit.size();
                for (int i=0; i<diff; i++) {
                    DRIDTO.DRISementara blankObject = new DRIDTO.DRISementara();
                    blankObject.setKodeRekening("");
                    blankObject.setNamaRekening("");
                    blankObject.setSaldoAkhir(null);
                    blankObject.setCetakTebal("");
                    dataDebit.add(blankObject);
                }
            }

            List<DRIDTO.DRIDebitKredit> listDebitKredit = new ArrayList<>();
            for (int i=0; i<dataDebit.size(); i++) {
                DRIDTO.DRIDebitKredit debitKredit = new DRIDTO.DRIDebitKredit();
                debitKredit.setIdRekeningDebit(dataDebit.get(i).getIdRekening());
                debitKredit.setIdRekeningKredit(dataKredit.get(i).getIdRekening());
                debitKredit.setKodeRekeningDebit(dataDebit.get(i).getKodeRekening());
                debitKredit.setKodeRekeningKredit(dataKredit.get(i).getKodeRekening());
                debitKredit.setLevelRekeningDebit(dataDebit.get(i).getLevelRekening());
                debitKredit.setLevelRekeningKredit(dataKredit.get(i).getLevelRekening());
                debitKredit.setNamaRekeningDebit(dataDebit.get(i).getNamaRekening());
                debitKredit.setNamaRekeningKredit(dataKredit.get(i).getNamaRekening());
                debitKredit.setSaldoAkhirDebit(dataDebit.get(i).getSaldoAkhir());
                debitKredit.setSaldoAkhirKredit(dataKredit.get(i).getSaldoAkhir());
                debitKredit.setCetakTebalDebit(dataDebit.get(i).getCetakTebal());
                debitKredit.setCetakTebalKredit(dataKredit.get(i).getCetakTebal());
                listDebitKredit.add(debitKredit);
            }

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_dri_sementara_detail.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            parameter.put("driDetailLocation", jasperReportSR);
            parameter.put("totalSaldoDebit", totalDebit);
            parameter.put("totalSaldoKredit", totalKredit);
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("logoLocation", logoImage);
            parameter.put("jenisDRI", "DRI Sementara");

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(listDebitKredit));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {
                Exporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                exporter.setConfiguration(configuration);
                exporter.exportReport();

                return byteArrayOutputStream.toByteArray();
            }
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
