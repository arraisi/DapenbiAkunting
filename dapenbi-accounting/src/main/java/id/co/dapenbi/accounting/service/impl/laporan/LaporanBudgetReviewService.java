package id.co.dapenbi.accounting.service.impl.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanBudgetReviewDao;
import id.co.dapenbi.accounting.dao.parameter.RekeningDao;
import id.co.dapenbi.accounting.dto.laporan.LaporanBudgetReviewDto;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LaporanBudgetReviewService {

    @Autowired
    private LaporanBudgetReviewDao dao;

    @Autowired
    private RekeningDao rekeningDao;

    public DataTablesResponse<LaporanBudgetReviewDto.Response> datatables(DataTablesRequest<LaporanBudgetReviewDto.Request> params) {
        List<LaporanBudgetReviewDto.Response> baseList = dao.datatables(params);
        List<LaporanBudgetReviewDto.Response> list = new ArrayList<>();

        /*to look changed rekening induk*/
        String idRekeningParentBeforeLvl3 = "0";
        String idRekeningParentBeforeLvl2 = "0";

        /* store value to sum on rekening lvl 3*/
        BigDecimal anggaranTahunanLvl3 = BigDecimal.ZERO;
        BigDecimal realisasiLvl3 = BigDecimal.ZERO;
        BigDecimal saldoAnggaranTahunanLvl3 = BigDecimal.ZERO;

        /* store value to sum on rekening lvl 3*/
        BigDecimal anggaranTahunanLvl2 = BigDecimal.ZERO;
        BigDecimal realisasiLvl2 = BigDecimal.ZERO;
        BigDecimal saldoAnggaranTahunanLvl2 = BigDecimal.ZERO;

        BigDecimal anggaranTahunanTotal = BigDecimal.ZERO;
        BigDecimal realisasiTotal = BigDecimal.ZERO;
        BigDecimal saldoAnggaranTahunanTotal = BigDecimal.ZERO;

        for(int i=0; i<baseList.size(); i++){
            if(i>0){
                if(!idRekeningParentBeforeLvl3.equals(baseList.get(i).getIdRekeningParentLvl3())){
                    Rekening dataRekeningParentLvl3 = rekeningDao.findById(idRekeningParentBeforeLvl3);
                    LaporanBudgetReviewDto.Response addLvl3 = new LaporanBudgetReviewDto.Response();
                    addLvl3.setNoMataAnggaran(dataRekeningParentLvl3.getKodeRekening());
                    addLvl3.setNamaMataAnggaran(dataRekeningParentLvl3.getNamaRekening());
                    addLvl3.setAnggaranTahunan(anggaranTahunanLvl3);
                    addLvl3.setRealisasi(realisasiLvl3);
                    if (anggaranTahunanLvl3.equals(new BigDecimal(0))){
                        addLvl3.setPersen(0.f);
                    } else {
                        addLvl3.setPersen(((realisasiLvl3.divide(anggaranTahunanLvl3, 3, RoundingMode.CEILING) ).multiply(new BigDecimal("100"))).floatValue());
                    }
                    addLvl3.setSaldoAnggaranTahunan(saldoAnggaranTahunanLvl3);
                    addLvl3.setLevelRekening(3);

                    list.add(addLvl3);
                    anggaranTahunanLvl3 = BigDecimal.ZERO;
                    realisasiLvl3 = BigDecimal.ZERO;
                    saldoAnggaranTahunanLvl3 = BigDecimal.ZERO;

                    anggaranTahunanLvl2 = anggaranTahunanLvl2.add(addLvl3.getAnggaranTahunan());
                    realisasiLvl2 = realisasiLvl2.add(addLvl3.getRealisasi());
                    saldoAnggaranTahunanLvl2 = saldoAnggaranTahunanLvl2.add(addLvl3.getSaldoAnggaranTahunan());

                    if(!idRekeningParentBeforeLvl2.equals(baseList.get(i).getIdRekeningParentLvl2())){
                        Rekening dataRekeningParentLvl2 = rekeningDao.findById(idRekeningParentBeforeLvl2);
                        LaporanBudgetReviewDto.Response addLvl2 = new LaporanBudgetReviewDto.Response();
                        addLvl2.setNoMataAnggaran(dataRekeningParentLvl2.getKodeRekening());
                        addLvl2.setNamaMataAnggaran(dataRekeningParentLvl2.getNamaRekening());
                        addLvl2.setAnggaranTahunan(anggaranTahunanLvl2);
                        addLvl2.setRealisasi(realisasiLvl2);
                        if (anggaranTahunanLvl2.equals(new BigDecimal(0))){
                            addLvl2.setPersen(0.f);
                        } else {
                            addLvl2.setPersen(((realisasiLvl2.divide(anggaranTahunanLvl2, 3, RoundingMode.CEILING)).multiply(new BigDecimal("100"))).floatValue());
                        }
                        addLvl2.setSaldoAnggaranTahunan(saldoAnggaranTahunanLvl2);
                        addLvl2.setLevelRekening(2);

                        list.add(addLvl2);
                        anggaranTahunanLvl2 = BigDecimal.ZERO;
                        realisasiLvl2 = BigDecimal.ZERO;
                        saldoAnggaranTahunanLvl2 = BigDecimal.ZERO;

                        anggaranTahunanTotal = anggaranTahunanTotal.add(addLvl2.getAnggaranTahunan());
                        realisasiTotal = realisasiTotal.add(addLvl2.getRealisasi());
                        saldoAnggaranTahunanTotal = saldoAnggaranTahunanTotal.add(addLvl2.getSaldoAnggaranTahunan());
                    }
                }
            }
            baseList.get(i).setLevelRekening(6);
            list.add(baseList.get(i));

            anggaranTahunanLvl3 = anggaranTahunanLvl3.add(baseList.get(i).getAnggaranTahunan());
            realisasiLvl3 = realisasiLvl3.add(baseList.get(i).getRealisasi());
            saldoAnggaranTahunanLvl3 = saldoAnggaranTahunanLvl3.add(baseList.get(i).getSaldoAnggaranTahunan());

            idRekeningParentBeforeLvl3 = baseList.get(i).getIdRekeningParentLvl3();
            idRekeningParentBeforeLvl2 = baseList.get(i).getIdRekeningParentLvl2();

            if(i == (baseList.size()-1)){
                Rekening dataRekeningParentLvl3 = rekeningDao.findById(idRekeningParentBeforeLvl3);
                LaporanBudgetReviewDto.Response addLvl3 = new LaporanBudgetReviewDto.Response();
                addLvl3.setNoMataAnggaran(dataRekeningParentLvl3.getKodeRekening());
                addLvl3.setNamaMataAnggaran(dataRekeningParentLvl3.getNamaRekening());
                addLvl3.setAnggaranTahunan(anggaranTahunanLvl3);
                addLvl3.setRealisasi(realisasiLvl3);
                if (anggaranTahunanLvl3.equals(new BigDecimal(0))){
                    addLvl3.setPersen(0.f);
                } else {
                    addLvl3.setPersen(((realisasiLvl3.divide(anggaranTahunanLvl3, 3, RoundingMode.CEILING) ).multiply(new BigDecimal("100"))).floatValue());
                }
                addLvl3.setSaldoAnggaranTahunan(saldoAnggaranTahunanLvl3);
                addLvl3.setLevelRekening(3);

                list.add(addLvl3);

                anggaranTahunanLvl2 = anggaranTahunanLvl2.add(addLvl3.getAnggaranTahunan());
                realisasiLvl2 = realisasiLvl2.add(addLvl3.getRealisasi());
                saldoAnggaranTahunanLvl2 = saldoAnggaranTahunanLvl2.add(addLvl3.getSaldoAnggaranTahunan());

                Rekening dataRekeningParentLvl2 = rekeningDao.findById(idRekeningParentBeforeLvl2);
                LaporanBudgetReviewDto.Response addLvl2 = new LaporanBudgetReviewDto.Response();
                addLvl2.setNoMataAnggaran(dataRekeningParentLvl2.getKodeRekening());
                addLvl2.setNamaMataAnggaran(dataRekeningParentLvl2.getNamaRekening());
                addLvl2.setAnggaranTahunan(anggaranTahunanLvl2);
                addLvl2.setRealisasi(realisasiLvl2);
                if (anggaranTahunanLvl2.equals(new BigDecimal(0))){
                    addLvl2.setPersen(0.f);
                } else {
                    addLvl2.setPersen(((realisasiLvl2.divide(anggaranTahunanLvl2, 3, RoundingMode.CEILING) ).multiply(new BigDecimal("100"))).floatValue());
                }
                addLvl2.setSaldoAnggaranTahunan(saldoAnggaranTahunanLvl2);
                addLvl2.setLevelRekening(2);

                list.add(addLvl2);

                anggaranTahunanTotal = anggaranTahunanTotal.add(addLvl2.getAnggaranTahunan());
                realisasiTotal = realisasiTotal.add(addLvl2.getRealisasi());
                saldoAnggaranTahunanTotal = saldoAnggaranTahunanTotal.add(addLvl2.getSaldoAnggaranTahunan());

                LaporanBudgetReviewDto.Response addTotal = new LaporanBudgetReviewDto.Response();
                addTotal.setNoMataAnggaran("");
                addTotal.setNamaMataAnggaran("JUMLAH TOTAL BUDGET REVIEW");
                addTotal.setAnggaranTahunan(anggaranTahunanTotal);
                addTotal.setRealisasi(realisasiTotal);
                if (anggaranTahunanLvl3.equals(new BigDecimal(0))){
                    addLvl3.setPersen(0.f);
                } else {
                    addTotal.setPersen(((realisasiTotal.divide(anggaranTahunanTotal, 3, RoundingMode.CEILING) ).multiply(new BigDecimal("100"))).floatValue());
                }
                addTotal.setSaldoAnggaranTahunan(saldoAnggaranTahunanTotal);
                addTotal.setLevelRekening(0);

                list.add(addTotal);
            }
        }

        Long rowCount = (long) list.size();

        return new DataTablesResponse<>(list, params.getDraw(), rowCount, rowCount);
    }

    public byte[] exportPdf(Map<String, Object> parameter, String jasperName, List<Object> dataList) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(dataList));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcel(Map<String, Object> parameter, String jasperName, List<Object> dataList) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(dataList));

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

    public List<String> findListBudgetReviewValid(String kodeTahunBuku, String triwulan) {
        return dao.findListBudgetReviewValid(kodeTahunBuku, triwulan);
    }
}
