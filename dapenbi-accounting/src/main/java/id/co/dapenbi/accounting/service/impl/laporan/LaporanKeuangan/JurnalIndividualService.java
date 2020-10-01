package id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan.JurnalIndividualDao;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class JurnalIndividualService {

    @Autowired
    private JurnalIndividualDao dao;

    public DataTablesResponse<JurnalIndividualDTO> datatables(DataTablesRequest<JurnalIndividualDTO> params, String search) {
        List<JurnalIndividualDTO> data = dao.datatable(params, search);
        Long rowCount = dao.datatable(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<JurnalIndividualDTO> findAll() {
        return dao.findAll();
    }

    public List<JurnalIndividualDTO> findAllByDate(JurnalIndividualDTO params) {
        return dao.findAllByDate(params);
    }

    public JurnalIndividualDTO getSummaryValue(String startDate, String endDate, Integer idRekening) {
        return dao.getSummaryValue(startDate, endDate, idRekening);
    }

    public byte[] exportPdf(Map<String, Object> params, List<JurnalIndividualDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_jurnal_individual.jrxml");
            ClassPathResource jasperStreamSR = new ClassPathResource("/jasper/laporan_jurnal_individual_subreport.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            JasperReport jasperReportSR = JasperCompileManager.compileReport(jasperStreamSR.getInputStream());

            params.put("jurnalIndividualDetails", jasperReportSR);
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

    public byte[] exportExcel(Map<String, Object> parameter, List<JurnalIndividualDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_jurnal_individual.jrxml");
            ClassPathResource jasperStreamSR = new ClassPathResource("/jasper/laporan_jurnal_individual_subreport.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            JasperReport jasperReportSR = JasperCompileManager.compileReport(jasperStreamSR.getInputStream());

            parameter.put("jurnalIndividualDetails", jasperReportSR);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
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

    public List<JurnalIndividualDTO.Rekening> findListRekening() {
        return dao.findListRekening();
    }
}
