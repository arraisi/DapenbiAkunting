package id.co.dapenbi.accounting.service.impl.dashboard;

import id.co.dapenbi.accounting.dao.dashboard.HomeDao;
import id.co.dapenbi.accounting.dto.dashboard.HomeDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HomeService {

    @Autowired
    private HomeDao homeDao;

    public List<HomeDTO.HomeChart> listAsetNetto(HomeDTO.HomeParameter params) {
        return homeDao.listAsetNetto(params);
    }

    public List<HomeDTO.HomeChart> listPerubahanAsetNetto(HomeDTO.HomeParameter params) {
        return homeDao.listPerubahanAsetNetto(params);
    }

    public List<List<HomeDTO.HomeChart>> listPerhitunganHasilUsaha(HomeDTO.HomeParameter params) {
        List<List<HomeDTO.HomeChart>> data = new ArrayList<>();
        BigDecimal firstSaldoBerjalan = BigDecimal.ZERO;
        List<HomeDTO.HomeChart> phu = homeDao.listPerhitunganHasilUsaha(params);
        data.add(phu);
        List<HomeDTO.HomeChart> phup = homeDao.listPerhitunganHasilUsahaPendapatan(params);
        if (phup.size() > 1) {
            boolean allSameValue = phup.stream().allMatch(x -> x.getTotalSaldoBerjalan() != null && x.getTotalSaldoBerjalan().equals(firstSaldoBerjalan));
            if (allSameValue) phup = new ArrayList<>();
        }
        data.add(phup);
        List<HomeDTO.HomeChart> phub = homeDao.listPerhitunganHasilUsahaBeban(params);
        if (phub.size() > 1) {
            boolean allSameValue = phub.stream().allMatch(x -> x.getTotalSaldoBerjalan() != null && x.getTotalSaldoBerjalan().equals(firstSaldoBerjalan));
            if (allSameValue) phub = new ArrayList<>();
        }
        data.add(phub);
        return data;
    }

    public List<HomeDTO.HomeChart> listNeraca(HomeDTO.HomeParameter params) {
        return homeDao.listNeraca(params);
    }

    public List<HomeDTO.HomeChart> listArusKas(HomeDTO.HomeParameter params) {
        return homeDao.listArusKas(params);
    }

    public List<List<HomeDTO.HomeChart>> listAnggaranTahunan(HomeDTO.HomeParameter params) {
        List<List<HomeDTO.HomeChart>> data = new ArrayList<>();
        List<HomeDTO.HomeChart> asetop = homeDao.listAnggaranTahunanAsetOperasional(params);
        data.add(asetop);
        List<HomeDTO.HomeChart> asetb = homeDao.listAnggaranTahunanBiaya(params);
        data.add(asetb);
        List<HomeDTO.HomeChart> asetp = homeDao.listAnggaranTahunanPendapatan(params);
        data.add(asetp);
        return data;
    }

    public List<HomeDTO.HomeChart> listROIROA(HomeDTO.HomeParameter params) {
        return homeDao.listROIROA(params);
    }

    public byte[] exportPDF(Map<String, Object> params) {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream("/jasper/laporan_dashboard.jasper");
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            List<Map<String, Object>> data = new ArrayList<>();
            data.add(params);
            params.put("logoLocation", image);
            log.info("Chart: {}, Nama Laporan: {}", params.get("chart"), params.get("namaLaporan"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JREmptyDataSource()
            );
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
