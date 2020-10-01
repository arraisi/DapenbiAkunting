package id.co.dapenbi.accounting.service.impl.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanSPIDao;
import id.co.dapenbi.accounting.dto.laporan.LaporanSPIDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class LaporanSPIService {

    @Autowired
    private LaporanSPIDao laporanSPIDao;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    public DataTablesResponse<LaporanSPIDTO> datatables(DataTablesRequest<LaporanSPIDTO> params, String search) {
        List<LaporanSPIDTO> data = laporanSPIDao.datatables(params, search);
        Long rowCount = laporanSPIDao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public byte[] exportPDF(String jasperName, Map<String, Object> parameter) {
        try {
            PengaturanSistem pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
            LaporanSPIDTO value = new LaporanSPIDTO();
            value.setIdSPIHDR(Integer.valueOf(parameter.get("kodeSPIHDR").toString()));
            List<LaporanSPIDTO> data = datatables(new DataTablesRequest<>(1L, -1L, 0L, "asc", 2L, value), "").getData();
            Map<String, BigDecimal> total = laporanSPIDao.sumNilaiPerolehanAndNilaiWajarAndNilaiSPIByIdHDR(value.getIdSPIHDR());

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameter.put("logoLocation", image);
            parameter.put("kepalaDivisi", pengaturanSistem.getKdiv());
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("totalNilaiPerolehan", total.get("totalNilaiPerolehan"));
            parameter.put("totalNilaiWajar", total.get("totalNilaiWajar"));
            parameter.put("totalNilaiSPI", total.get("totalNilaiSPI"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, new JRBeanCollectionDataSource(data));
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportEXCEL(String jasperName, Map<String, Object> parameter) {
        try {
            PengaturanSistem pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
            LaporanSPIDTO value = new LaporanSPIDTO();
            value.setIdSPIHDR(Integer.valueOf(parameter.get("kodeSPIHDR").toString()));
            List<LaporanSPIDTO> data = datatables(new DataTablesRequest<>(1L, -1L, 0L, "asc", 2L, value), "").getData();
            Map<String, BigDecimal> total = laporanSPIDao.sumNilaiPerolehanAndNilaiWajarAndNilaiSPIByIdHDR(value.getIdSPIHDR());

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameter.put("logoLocation", image);
            parameter.put("kepalaDivisi", pengaturanSistem.getKdiv());
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            parameter.put("totalNilaiPerolehan", total.get("totalNilaiPerolehan"));
            parameter.put("totalNilaiWajar", total.get("totalNilaiWajar"));
            parameter.put("totalNilaiSPI", total.get("totalNilaiSPI"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, new JRBeanCollectionDataSource(data));
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
