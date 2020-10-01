package id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan.RekeningIndividualDao;
import id.co.dapenbi.accounting.dto.dri.DRIDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RekeningIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RekeningIndividualService {

    @Autowired
    private RekeningIndividualDao dao;

    public DataTablesResponse<RekeningIndividualDTO> datatables(DataTablesRequest<RekeningIndividualDTO> params, String search) {
        List<RekeningIndividualDTO> data = dao.datatable(params, search);
        Long rowCount = dao.datatable(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<WarkatDTO> getData(List<String> idList) {
        return dao.getData(idList);
    }

    public byte[] exportPdf(Map<String, Object> params, List<RekeningIndividualDTO.Header> data) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream("/jasper/laporan_rekening_individual.jasper");
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_rekening_individual_subreport.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("rekeningIndividualSubreport", jasperReportSR);
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

    public byte[] exportExcel(Map<String, Object> params, List<RekeningIndividualDTO.Header> data) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream("/jasper/laporan_rekening_individual.jasper");
            InputStream jasperStreamSR = this.getClass().getResourceAsStream("/jasper/laporan_rekening_individual_subreport.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperReport jasperReportSR = (JasperReport) JRLoader.loadObject(jasperStreamSR);

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("rekeningIndividualSubreport", jasperReportSR);
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

    public List<RekeningIndividualDTO.Header> findRekeningByRange(String startDate, String endDate, String kodeRekening) throws ParseException {
        return dao.findRekeningByRange(startDate, endDate, kodeRekening);
    }

    public List<RekeningIndividualDTO.Header> findExportData(String startDate, String endDate, String kodeRekening) throws ParseException {
//        final List<RekeningIndividualDTO.Header> _rekening =

//        for (int i = 0; i < _rekening.size(); i++) {
//            final List<RekeningIndividualDTO> rekeningDetails = calculateSaldoDetails(_rekening.get(i).getDetails(), _rekening.get(i).getSaldoAwal(), 0);
//            _rekening.get(i).setDetails(rekeningDetails);
//
//            final int lastIndexDetails = rekeningDetails.size() - 1;
//            if (lastIndexDetails >= 0)
//                _rekening.get(i).setSaldoAkhir(rekeningDetails.get(lastIndexDetails).getSaldo());
//        }
        return dao.findRekeningByRange(startDate, endDate, kodeRekening);
    }

    private List<RekeningIndividualDTO> calculateSaldoDetails(List<RekeningIndividualDTO> details, BigDecimal currentSaldo, Integer index) {
        if (index == details.size()) {
            return details;
        } else {
            if (details.get(index).getSaldoNormal().equalsIgnoreCase("D")) {
                final BigDecimal saldo = currentSaldo.add(details.get(index).getJumlahDebit()).subtract(details.get(index).getJumlahKredit());
                details.get(index).setSaldo(saldo);
            } else {
                final BigDecimal saldo = currentSaldo.subtract(details.get(index).getJumlahDebit()).add(details.get(index).getJumlahKredit());
                details.get(index).setSaldo(saldo);
            }
            return calculateSaldoDetails(details, details.get(index).getSaldo(), index + 1);
        }
    }
}
