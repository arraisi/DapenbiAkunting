package id.co.dapenbi.accounting.service.impl.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanPerincianArusKasDao;
import id.co.dapenbi.accounting.dto.laporan.ArusKasBulananDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.ArusKasDTO;
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
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LaporanPerincianArusKasService {

    @Autowired
    private LaporanPerincianArusKasDao dao;

    public DataTablesResponse<ArusKasBulananDTO> datatables(DataTablesRequest<ArusKasBulananDTO> params, String search) {
        List<ArusKasBulananDTO> data = dao.datatables(params, search);
        Long rowCount = dao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<ArusKasDTO> findAllForExport(String tglTransaksi) throws SQLException {
        return dao.findAllForExport(tglTransaksi);
    }

    public byte[] exportPdf(Map<String, Object> params, List<ArusKasDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_perincian_arus_kas.jrxml");
            ClassPathResource jasperStreamDetails = new ClassPathResource("/jasper/laporan_perincian_arus_kas_detail_subreport.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            JasperReport jasperReportDetails = JasperCompileManager.compileReport(jasperStreamDetails.getInputStream());

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("rincianArusKasPenambahan", jasperReportDetails);
            params.put("rincianArusKasPengurangan", jasperReportDetails);

            params.put("logo", image);
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


    public byte[] exportExcel(Map<String, Object> params, List<ArusKasDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_perincian_arus_kas.jrxml");
            ClassPathResource jasperStreamDetails = new ClassPathResource("/jasper/laporan_perincian_arus_kas_detail_subreport.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            JasperReport jasperReportDetails = JasperCompileManager.compileReport(jasperStreamDetails.getInputStream());

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("rincianArusKasPenambahan", jasperReportDetails);
            params.put("rincianArusKasPengeluaran", jasperReportDetails);

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
//            log.error(e.getMessage());
            return null;
        }
    }

    public ArusKasDTO.TotalKas findTotalKas(String tanggal) {
        return dao.findTotalKas(tanggal);
    }
}
