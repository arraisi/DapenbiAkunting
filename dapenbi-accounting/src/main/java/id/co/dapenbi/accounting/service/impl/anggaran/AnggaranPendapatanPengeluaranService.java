package id.co.dapenbi.accounting.service.impl.anggaran;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.anggaran.AnggaranPendapatanPengeluaranDao;
import id.co.dapenbi.accounting.dao.laporan.LaporanBudgetReviewDao;
import id.co.dapenbi.accounting.dao.parameter.RekeningDao;
import id.co.dapenbi.accounting.dto.anggaran.AnggaranPendapatanPengeluaranDto;
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
public class AnggaranPendapatanPengeluaranService {

    @Autowired
    private AnggaranPendapatanPengeluaranDao dao;

    @Autowired
    private RekeningDao rekeningDao;

    public DataTablesResponse<AnggaranPendapatanPengeluaranDto.Response> datatables(DataTablesRequest<AnggaranPendapatanPengeluaranDto.Request> params) {
        List<AnggaranPendapatanPengeluaranDto.Pendapatan> listPendapatan = dao.datatablesPendapatan(params);
        List<AnggaranPendapatanPengeluaranDto.Pengeluaran> listPengeluaran = dao.datatablesPengeluaran(params);

        int lengthListPengeluaran = listPengeluaran.size();
        int lengthListPendapatan = listPendapatan.size();
        int sizeMin = 0;
        int sizeDiff = 0;
        int status = 0; //status 0 pendapatan lebih kecil, status 1 pengeluaran lebih kecil

        if (lengthListPendapatan < lengthListPengeluaran) {
            sizeMin = lengthListPendapatan;
            sizeDiff = lengthListPengeluaran - lengthListPendapatan;
            status = 0;
        }
        else{
            sizeMin = lengthListPengeluaran;
            sizeDiff = lengthListPendapatan - lengthListPengeluaran;
            status = 1;
        }

        List<AnggaranPendapatanPengeluaranDto.Response> list = new ArrayList<>();

        BigDecimal jumlahPendapatan = BigDecimal.ZERO;
        BigDecimal jumlahPengeluaran = BigDecimal.ZERO;

        for(int i=0; i<sizeMin; i++) {
            AnggaranPendapatanPengeluaranDto.Response addValue = new AnggaranPendapatanPengeluaranDto.Response();
            addValue.setSandiMaPendapatan(listPendapatan.get(i).getSandiMaPendapatan());
            addValue.setPendapatan(listPendapatan.get(i).getPendapatan());
            addValue.setJumlahPendapatan(listPendapatan.get(i).getJumlahPendapatan());
            addValue.setSandiMaPengeluaran(listPengeluaran.get(i).getSandiMaPengeluaran());
            addValue.setPengeluaran(listPengeluaran.get(i).getPengeluaran());
            addValue.setJumlahPengeluaran(listPengeluaran.get(i).getJumlahPengeluaran());

            list.add(addValue);

            jumlahPendapatan = jumlahPendapatan.add(addValue.getJumlahPendapatan());
            jumlahPengeluaran = jumlahPengeluaran.add(addValue.getJumlahPengeluaran());
        }

        if (status == 0){
            for(int i=0; i<sizeDiff; i++) {
                AnggaranPendapatanPengeluaranDto.Response addValue = new AnggaranPendapatanPengeluaranDto.Response();
                addValue.setSandiMaPengeluaran(listPengeluaran.get(i).getSandiMaPengeluaran());
                addValue.setPengeluaran(listPengeluaran.get(i).getPengeluaran());
                addValue.setJumlahPengeluaran(listPengeluaran.get(i).getJumlahPengeluaran());

                list.add(addValue);

                jumlahPengeluaran = jumlahPengeluaran.add(addValue.getJumlahPengeluaran());
            }
        } else {
            for(int i=0; i<sizeDiff; i++) {
                AnggaranPendapatanPengeluaranDto.Response addValue = new AnggaranPendapatanPengeluaranDto.Response();
                addValue.setSandiMaPendapatan(listPendapatan.get(i).getSandiMaPendapatan());
                addValue.setPendapatan(listPendapatan.get(i).getPendapatan());
                addValue.setJumlahPendapatan(listPendapatan.get(i).getJumlahPendapatan());

                list.add(addValue);

                jumlahPendapatan = jumlahPendapatan.add(addValue.getJumlahPendapatan());
            }
        }

        AnggaranPendapatanPengeluaranDto.Response subJumlah = new AnggaranPendapatanPengeluaranDto.Response();
        subJumlah.setJumlahPendapatan(null);
        subJumlah.setPengeluaran("Sub Jumlah Pengeluaran");
        subJumlah.setJumlahPengeluaran(jumlahPengeluaran);
        list.add(subJumlah);

        AnggaranPendapatanPengeluaranDto.Response defisit = new AnggaranPendapatanPengeluaranDto.Response();
        defisit.setJumlahPendapatan(null);
        defisit.setPengeluaran("SURPLUS/ (DEFISIT) TERMASUK IURAN");
        defisit.setJumlahPengeluaran(jumlahPendapatan.subtract(jumlahPengeluaran));
        list.add(defisit);

        AnggaranPendapatanPengeluaranDto.Response total = new AnggaranPendapatanPengeluaranDto.Response();

        total.setPendapatan("Jumlah");
        total.setJumlahPendapatan(jumlahPendapatan);
        total.setPengeluaran("Jumlah");
        total.setJumlahPengeluaran(jumlahPendapatan);
        list.add(total);

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
}
