package id.co.dapenbi.accounting.service.impl.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanKeuanganDao;
import id.co.dapenbi.accounting.dto.dri.DRIDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RekeningIndividualDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuanganDTO;
import id.co.dapenbi.accounting.dto.parameter.ArusKasDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.repository.parameter.TahunBukuRepository;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class LaporanKeuanganService {

    @Autowired
    private LaporanKeuanganDao laporanKeuanganDao;

    @Autowired
    private TahunBukuRepository tahunBukuRepository;

    public DataTablesResponse<LaporanKeuanganDTO.LaporanKeuangan> datatables(DataTablesRequest<LaporanKeuanganDTO.LaporanKeuangan> params, String search) {
        List<LaporanKeuanganDTO.LaporanKeuangan> data = laporanKeuanganDao.datatables(params, search);
        Long rowCount = laporanKeuanganDao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public byte[] exportPdf(Map<String, Object> parameter, String jasperName, List<Object> dataList) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            parameter.put("logoLocation", logoImage);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(dataList));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportPdfPajakPenghasilanBadan(Map<String, Object> params, List<LaporanKeuanganDTO.PajakPenghasilanBadan> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_pajak_penghasilan_badan.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("logoLocation", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
//            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcelPajakPenghasilanBadan(Map<String, Object> params, List<LaporanKeuanganDTO.PajakPenghasilanBadan> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_pajak_penghasilan_badan.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("logoLocation", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

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
//            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportPdfNeraca(Map<String, Object> parameter, String jasperName, String idLaporanHeader, String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        List<LaporanKeuanganDTO.Neraca> neraca = findTotalNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);
        parameter.put("totalSaldoBerjalanAset", neraca.get(4).getTotalSaldoBerjalanAset());
        parameter.put("totalSaldoSebelumAset", neraca.get(4).getTotalSaldoSebelumAset());
        parameter.put("totalSaldoBerjalanLiabilitas", neraca.get(4).getTotalSaldoBerjalanLiabilitas());
        parameter.put("totalSaldoSebelumLiabilitas", neraca.get(4).getTotalSaldoSebelumLiabilitas());
//        parameter.put("tanggalLaporan", neraca.get(0).getOdd().get(0).getTanggalLaporan());

        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR1 = this.getClass().getResourceAsStream("/jasper/laporan_keuangan_neraca_subreport1.jasper");
            InputStream jasperStreamSR2 = this.getClass().getResourceAsStream("/jasper/laporan_keuangan_neraca_subreport2.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR1 = (JasperReport) JRLoader.loadObject(jasperStreamSR1);
            JasperReport jasperReportSR2 = (JasperReport) JRLoader.loadObject(jasperStreamSR2);

            BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            parameter.put("subreportLocation1", jasperReportSR1);
            parameter.put("subreportLocation2", jasperReportSR2);
            parameter.put("logoLocation", logoImage);
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(neraca)
            );

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcel(Map<String, Object> parameter, String jasperName, List<Object> dataList) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            parameter.put("logoLocation", logoImage);

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

    public byte[] exportExcelNeraca(Map<String, Object> parameter, String jasperName, String idLaporanHeader, String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        List<LaporanKeuanganDTO.Neraca> neraca = findTotalNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);
        parameter.put("totalSaldoBerjalanAset", neraca.get(4).getTotalSaldoBerjalanAset());
        parameter.put("totalSaldoSebelumAset", neraca.get(4).getTotalSaldoSebelumAset());
        parameter.put("totalSaldoBerjalanLiabilitas", neraca.get(4).getTotalSaldoBerjalanLiabilitas());
        parameter.put("totalSaldoSebelumLiabilitas", neraca.get(4).getTotalSaldoBerjalanLiabilitas());
        parameter.put("tanggalLaporan", neraca.get(0).getOdd().get(0).getTanggalLaporan());

        try {
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            InputStream jasperStreamSR1 = this.getClass().getResourceAsStream("/jasper/laporan_keuangan_neraca_subreport1.jasper");
            InputStream jasperStreamSR2 = this.getClass().getResourceAsStream("/jasper/laporan_keuangan_neraca_subreport2.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR1 = (JasperReport) JRLoader.loadObject(jasperStreamSR1);
            JasperReport jasperReportSR2 = (JasperReport) JRLoader.loadObject(jasperStreamSR2);

            BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            parameter.put("subreportLocation1", jasperReportSR1);
            parameter.put("subreportLocation2", jasperReportSR2);
            parameter.put("logoLocation", logoImage);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    new JRBeanCollectionDataSource(neraca));

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

    public List<LaporanKeuanganDTO.PajakPenghasilanBadan> findPajakPenghasilanBadan(String kodePeriode, String kodeDRI) {
        return laporanKeuanganDao.findPajakPenghasilanBadan(kodePeriode, kodeDRI);
    }

    public List<LaporanKeuanganDTO.Neraca> findTotalNeraca(String idLaporanHeader, String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        List<LaporanKeuanganDTO.LaporanKeuangan> asetInvestasiList = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 1, 23);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetInvestasiTotal = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 24, 24);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetSelisihInvestasiList = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 25, 25);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetLancarList = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 26, 36);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetLancarTotal = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 37, 37);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetOperasional = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 38, 44);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetOperasionalTotal = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 45, 45);
        List<LaporanKeuanganDTO.LaporanKeuangan> asetLain = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 46, 46);
        List<LaporanKeuanganDTO.LaporanKeuangan> liabilitasList = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 47, 54);
        List<LaporanKeuanganDTO.LaporanKeuangan> liabilitasTotal = laporanKeuanganDao.findNeraca(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI, 55, 55);

        BigDecimal totalSaldoBerjalanAset = BigDecimal.ZERO;
        BigDecimal totalSaldoSebelumAset = BigDecimal.ZERO;
        BigDecimal totalSaldoBerjalanLiabilitas = BigDecimal.ZERO;
        BigDecimal totalSaldoSebelumLiabilitas = BigDecimal.ZERO;

        /* Aset Investasi */
        Optional<BigDecimal> investasiBerjalanOptional = asetInvestasiList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoBerjalan).reduce((p, q) -> p.add(q));
        if (investasiBerjalanOptional.isPresent())
            totalSaldoBerjalanAset = investasiBerjalanOptional.get();

        Optional<BigDecimal> investasiSebelumOptional = asetInvestasiList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoSebelum).reduce((p, q) -> p.add(q));
        if (investasiSebelumOptional.isPresent())
            totalSaldoSebelumAset = investasiSebelumOptional.get();

        /* Aset Selisih Investasi */
        Optional<BigDecimal> selisihInvestasiBerjalanOptinal = asetSelisihInvestasiList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoBerjalan).reduce((p, q) -> p.add(q));
        if (selisihInvestasiBerjalanOptinal.isPresent())
            totalSaldoBerjalanAset = totalSaldoBerjalanAset.add(selisihInvestasiBerjalanOptinal.get());

        Optional<BigDecimal> selisihInvestasiSebelumOptional = asetSelisihInvestasiList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoSebelum).reduce((p, q) -> p.add(q));
        if (selisihInvestasiSebelumOptional.isPresent())
            totalSaldoSebelumAset = totalSaldoSebelumAset.add(selisihInvestasiSebelumOptional.get());

        /* Aset Lancar Luar Investasi */
        Optional<BigDecimal> asetLancarBerjalanOptional = asetLancarList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoBerjalan).reduce((p, q) -> p.add(q));
        if (asetLancarBerjalanOptional.isPresent())
            totalSaldoBerjalanAset = totalSaldoBerjalanAset.add(asetLancarBerjalanOptional.get());

        Optional<BigDecimal> asetLancarSebelumOptional = asetLancarList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoSebelum).reduce((p, q) -> p.add(q));
        if (asetLancarSebelumOptional.isPresent())
            totalSaldoSebelumAset = totalSaldoSebelumAset.add(asetLancarSebelumOptional.get());

        /* Aset Operasional */
        Optional<BigDecimal> asetOperasionalBerjalanOptional = asetOperasional.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoBerjalan).reduce((p, q) -> p.add(q));
        if (asetOperasionalBerjalanOptional.isPresent())
            totalSaldoBerjalanAset = totalSaldoBerjalanAset.add(asetOperasionalBerjalanOptional.get());

        Optional<BigDecimal> asetOperasionalSebelumOptional = asetOperasional.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoSebelum).reduce((p, q) -> p.add(q));
        if (asetOperasionalSebelumOptional.isPresent())
            totalSaldoSebelumAset = totalSaldoSebelumAset.add(asetOperasionalSebelumOptional.get());

        /* Aset Lainnya */
        Optional<BigDecimal> asetLainBerjalanOptional = asetLain.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoBerjalan).reduce((p, q) -> p.add(q));
        if (asetLainBerjalanOptional.isPresent())
            totalSaldoBerjalanAset = totalSaldoBerjalanAset.add(asetLainBerjalanOptional.get());

        Optional<BigDecimal> asetLainSebelumOptional = asetLain.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoSebelum).reduce((p, q) -> p.add(q));
        if (asetLainSebelumOptional.isPresent())
            totalSaldoSebelumAset = totalSaldoSebelumAset.add(asetLainSebelumOptional.get());

        /* Liabilitas */
        Optional<BigDecimal> liabilitasBerjalanOptional = liabilitasList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoBerjalan).reduce((p, q) -> p.add(q));
        if (liabilitasBerjalanOptional.isPresent())
            totalSaldoBerjalanLiabilitas = liabilitasBerjalanOptional.get();

        Optional<BigDecimal> liabilitasSebelumOptional = liabilitasList.stream().map(LaporanKeuanganDTO.LaporanKeuangan::getSaldoSebelum).reduce((p, q) -> p.add(q));
        if (liabilitasSebelumOptional.isPresent())
            totalSaldoSebelumLiabilitas = liabilitasSebelumOptional.get();


        List<LaporanKeuanganDTO.Neraca> list = new ArrayList<>();

        LaporanKeuanganDTO.Neraca neraca0 = new LaporanKeuanganDTO.Neraca();
        neraca0.setOdd(asetInvestasiList);
        neraca0.setOddSubreport(1);
        neraca0.setJudulTotalSubreportOdd(asetInvestasiTotal.get(0).getJudul());
        neraca0.setTotalBerjalanSubreportOdd(asetInvestasiTotal.get(0).getSaldoBerjalan());
        neraca0.setTotalSebelumSubreportOdd(asetInvestasiTotal.get(0).getSaldoSebelum());
        neraca0.setEven(liabilitasList);
        neraca0.setEvenSubreport(1);
        neraca0.setJudulTotalSubreportEven(liabilitasTotal.get(0).getJudul());
        neraca0.setTotalBerjalanSubreportEven(liabilitasTotal.get(0).getSaldoBerjalan());
        neraca0.setTotalSebelumSubreportEven(liabilitasTotal.get(0).getSaldoSebelum());
        list.add(neraca0);

        LaporanKeuanganDTO.Neraca neraca1 = new LaporanKeuanganDTO.Neraca();
        neraca1.setOdd(asetSelisihInvestasiList);
        neraca1.setOddSubreport(2);
        neraca1.setEvenSubreport(2);
        list.add(neraca1);

        LaporanKeuanganDTO.Neraca neraca2 = new LaporanKeuanganDTO.Neraca();
        neraca2.setOdd(asetLancarList);
        neraca2.setOddSubreport(1);
        neraca2.setJudulTotalSubreportOdd(asetLancarTotal.get(0).getJudul());
        neraca2.setTotalBerjalanSubreportOdd(asetLancarTotal.get(0).getSaldoBerjalan());
        neraca2.setTotalSebelumSubreportOdd(asetLancarTotal.get(0).getSaldoSebelum());
        neraca2.setEvenSubreport(2);
        list.add(neraca2);

        LaporanKeuanganDTO.Neraca neraca3 = new LaporanKeuanganDTO.Neraca();
        neraca3.setOdd(asetOperasional);
        neraca3.setOddSubreport(1);
        neraca3.setJudulTotalSubreportOdd(asetOperasionalTotal.get(0).getJudul());
        neraca3.setTotalBerjalanSubreportOdd(asetOperasionalTotal.get(0).getSaldoBerjalan());
        neraca3.setTotalSebelumSubreportOdd(asetOperasionalTotal.get(0).getSaldoSebelum());
        neraca3.setEvenSubreport(2);
        list.add(neraca3);

        LaporanKeuanganDTO.Neraca neraca4 = new LaporanKeuanganDTO.Neraca();
        neraca4.setOdd(asetLain);
        neraca4.setTotalSaldoBerjalanAset(totalSaldoBerjalanAset);
        neraca4.setTotalSaldoSebelumAset(totalSaldoSebelumAset);
        neraca4.setTotalSaldoBerjalanLiabilitas(totalSaldoBerjalanLiabilitas);
        neraca4.setTotalSaldoSebelumLiabilitas(totalSaldoSebelumLiabilitas);
        neraca4.setOddSubreport(2);
        neraca4.setEvenSubreport(2);
        list.add(neraca4);

        return list;
    }

    public List<String> findTanggalLaporan(String idLaporanHeader, String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        List<String> tanggalLaporan = laporanKeuanganDao.findTanggalLaporan(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);
        return tanggalLaporan;
    }

    public List<LaporanKeuanganDTO.LaporanKeuangan> listPerhitunganAngsuranPPH(String kodeDRI, String kodePeriode, String tahun) {
        Optional<TahunBuku> tahunBukuOptional = tahunBukuRepository.findByTahun(tahun);

        return laporanKeuanganDao.listPerhitunganAngsuranPPH(kodeDRI, kodePeriode, tahunBukuOptional.isPresent() ? tahunBukuOptional.get().getKodeTahunBuku() : "00");
    }
}
