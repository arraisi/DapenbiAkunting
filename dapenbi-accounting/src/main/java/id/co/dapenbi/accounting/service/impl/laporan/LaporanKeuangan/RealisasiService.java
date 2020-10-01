package id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan.JurnalIndividualDao;
import id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan.RealisasiDao;
import id.co.dapenbi.accounting.dto.anggaran.AnggaranDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RealisasiDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class RealisasiService {

    @Autowired
    private RealisasiDao dao;

    public List<RealisasiDTO> findRealisasi(String jenisRealisasi, String date) {
        return dao.findRealisasi(jenisRealisasi, date);
    }

    public RealisasiDTO.Summary findSummeryRealisasi(
            String periode,
            String periodeMin1,
            Integer idRekening,
            String jenisRealisasi,
            String tipeRekening
    ) {
        return dao.findSummeryRealisasi(periode, periodeMin1, idRekening, jenisRealisasi, tipeRekening);
    }

    public byte[] exportPdf(
            Map<String, Object> params,
            List<RealisasiDTO> data,
            String jenisLaporan,
            String periode,
            String periodeMin1,
            Integer idRekening,
            String tipeRekening) {
        try {
            ClassPathResource jasperStream;
            RealisasiDTO.Summary summeryRealisasi = findSummeryRealisasi(periode, periodeMin1, idRekening, jenisLaporan, tipeRekening);
            if (jenisLaporan.equalsIgnoreCase("PENGELUARAN")) {
                jasperStream = new ClassPathResource("/jasper/laporan_realisasi_anggaran_pengeluaran.jrxml");
            } else {
                jasperStream = new ClassPathResource("/jasper/laporan_realisasi_anggaran_pendapatan.jrxml");
            }
            params.put("totalAnggaran", summeryRealisasi.getTotalAnggaran());
            params.put("totalNilaiAt", summeryRealisasi.getTotalNilaiAT());
            params.put("totalRealisasiBulanIni", summeryRealisasi.getTotalRealisasiBulanIni());
            params.put("totalRealisasiBulanLalu", summeryRealisasi.getTotalRealisasiBulanLalu());
            params.put("totalRealisasiSum", summeryRealisasi.getTotalRealisasiSum());
            params.put("totalPercentage", summeryRealisasi.getTotalPercentage());
            params.put("tipeRekening", tipeRekening);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("logo", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcel(
            Map<String, Object> params,
            List<RealisasiDTO> data,
            String jenisLaporan,
            String periode,
            String periodeMin1,
            Integer idRekening,
            String tipeRekening) {
        try {
            ClassPathResource jasperStream;
            RealisasiDTO.Summary summeryRealisasi = findSummeryRealisasi(periode, periodeMin1, idRekening, jenisLaporan, tipeRekening);
            if (jenisLaporan.equalsIgnoreCase("PENGELUARAN")) {
                jasperStream = new ClassPathResource("/jasper/laporan_realisasi_anggaran_pengeluaran.jrxml");
            } else {
                jasperStream = new ClassPathResource("/jasper/laporan_realisasi_anggaran_pendapatan.jrxml");
            }
            params.put("totalAnggaran", summeryRealisasi.getTotalAnggaran());
            params.put("totalNilaiAt", summeryRealisasi.getTotalNilaiAT());
            params.put("totalRealisasiBulanIni", summeryRealisasi.getTotalRealisasiBulanIni());
            params.put("totalRealisasiBulanLalu", summeryRealisasi.getTotalRealisasiBulanLalu());
            params.put("totalRealisasiSum", summeryRealisasi.getTotalRealisasiSum());
            params.put("totalPercentage", summeryRealisasi.getTotalPercentage());
            params.put("tipeRekening", tipeRekening);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("logo", image);
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
            log.error(e.getMessage());
            return null;
        }
    }

    public DataTablesResponse<RealisasiDTO> datatables(DataTablesRequest<RealisasiDTO> params, String search) {
        List<RealisasiDTO> data = dao.datatable(params, search);
        Long rowCount = dao.datatable(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }
}
