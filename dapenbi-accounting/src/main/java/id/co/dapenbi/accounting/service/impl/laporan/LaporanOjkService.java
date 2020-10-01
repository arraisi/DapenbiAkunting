package id.co.dapenbi.accounting.service.impl.laporan;

import id.co.dapenbi.accounting.dto.laporan.LaporanOjkDTO;
import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.*;
import id.co.dapenbi.accounting.enums.NamaLaporan;
import id.co.dapenbi.accounting.service.impl.laporan.ojk.*;
import id.co.dapenbi.core.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class LaporanOjkService {

    @Autowired
    private LapOjkLpanService lapOjkLpanService;

    @Autowired
    private LapOjkNrcService lapOjkNrcService;

    @Autowired
    private LapOjkLphuService lapOjkLphuService;

    @Autowired
    private LapOjkLakService lapOjkLakService;

    @Autowired
    private LapOjkPstService lapOjkPstService;

    @Autowired
    private LapOjkKupService lapOjkKupService;

    @Autowired
    private LapOjkKup1Service lapOjkKup1Service;

    @Autowired
    private LapOjkKup2Service lapOjkKup2Service;

    @Autowired
    private LapOjkRekinvService lapOjkRekinvService;

    @Autowired
    private LapOjkRoiService lapOjkRoiService;

    @Autowired
    private LapOjkAlmService lapOjkAlmService;

    @Autowired
    private LapOjkRoimlService lapOjkRoimlService;

    @Autowired
    private LapOjkInspService lapOjkInspService;

    @Autowired
    private LapOjkSbnService lapOjkSbnService;

    @Autowired
    private LapOjkRasioService lapOjkRasioService;

    @Autowired
    private LapOjkPmiService lapOjkPmiService;

    @Autowired
    private LapOjkTabService lapOjkTabService;

    @Autowired
    private LapOjkDocService lapOjkDocService;

    @Autowired
    private LapOjkDpjkaService lapOjkDpjkaService;

    @Autowired
    private LapOjkSrdpService lapOjkSrdpService;

    @Autowired
    private LapOjkSbiService lapOjkSbiService;

    @Autowired
    private LapOjkRsbnService lapOjkRsbnService;

    @Autowired
    private LapOjkShmService lapOjkShmService;

    @Autowired
    private LapOjkObliService lapOjkObliService;

    @Autowired
    private LapOjkSukukService lapOjkSukukService;

    @Autowired
    private LapOjkObsudService lapOjkObsudService;

    @Autowired
    private LapOjkDireService lapOjkDireService;

    @Autowired
    private LapOjkDnfraService lapOjkDnfraService;

    @Autowired
    private LapOjkKokbService lapOjkKokbService;

    @Autowired
    private LapOjkRepoService lapOjkRepoService;

    @Autowired
    private LapOjkPnylService lapOjkPnylService;

    @Autowired
    private LapOjkPropService lapOjkPropService;

    @Autowired
    private LapOjkKasbService lapOjkKasbService;

    @Autowired
    private LapOjkPiutService lapOjkPiutService;

    @Autowired
    private LapOjkPiubService lapOjkPiubService;

    @Autowired
    private LapOjkBbmkService lapOjkBbmkService;

    @Autowired
    private LapOjkRksdService lapOjkRksdService;

    @Autowired
    private LapOjkMtnService lapOjkMtnService;

    @Autowired
    private LapOjkPiuiService lapOjkPiuiService;

    @Autowired
    private LapOjkPihiService lapOjkPihiService;

    @Autowired
    private LapOjkPillService lapOjkPillService;

    @Autowired
    private LapOjkTnbgService lapOjkTnbgService;

    @Autowired
    private LapOjkKndrService lapOjkKndrService;

    @Autowired
    private LapOjkPkomService lapOjkPkomService;

    @Autowired
    private LapOjkPkanService lapOjkPkanService;

    @Autowired
    private LapOjkAsolService lapOjkAsolService;

    @Autowired
    private LapOjkAslnService lapOjkAslnService;

    @Autowired
    private LapOjkUmpjService lapOjkUmpjService;

    @Autowired
    private LapOjkUmpsService lapOjkUmpsService;

    @Autowired
    private LapOjkUtinService lapOjkUtinService;

    @Autowired
    private LapOjkPddmService lapOjkPddmService;

    @Autowired
    private LapOjkBmhbService lapOjkBmhbService;

    @Autowired
    private LapOjkUtlnService lapOjkUtlnService;

    @Autowired
    private LapOjkPpinService lapOjkPpinService;

    @Autowired
    private LapOjkIujtService lapOjkIujtService;

    @Autowired
    private LapOjkPdinService lapOjkPdinService;

    @Autowired
    private LapOjkPdplService lapOjkPdplService;

    @Autowired
    private LapOjkBinvService lapOjkBinvService;

    @Autowired
    private LapOjkBoprService lapOjkBoprService;

    @Autowired
    private LapOjkBiprService lapOjkBiprService;

    @Autowired
    private LapOjkPphService lapOjkPphService;

    @Autowired
    private LapOjkPkplService lapOjkPkplService;

    @Autowired
    private LapOjkLanService lapOjkLanService;

    @Autowired
    private GenerateExcelService excelService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public byte[] exportPdf(Map<String, Object> parameter, String namaLaporan, List<Object> dataList) {
        try {
            final JRBeanCollectionDataSource jrBeanCollectionDataSource;
            if (namaLaporan.equals(NamaLaporan.LAN.getValue())) {
                final List<OjkLan> exportData = lapOjkLanService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.NRC.getValue())) {
                final List<OjkNrc> exportData = lapOjkNrcService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.LPHU.getValue())) {
                final List<OjkLphu> exportData = lapOjkLphuService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.KUP.getValue())) {
                final List<OjkKup> exportData = lapOjkKupService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.REKINV.getValue())) {
                final List<OjkRekinv> exportData = lapOjkRekinvService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.BBMK.getValue())) {
                final List<OjkBbmk> exportData = lapOjkBbmkService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PIUI.getValue())) {
                final List<OjkPiui> exportData = lapOjkPiuiService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PIHI.getValue())) {
                final List<OjkPihi> exportData = lapOjkPihiService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PILL.getValue())) {
                final List<OjkPill> exportData = lapOjkPillService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PDDM.getValue())) {
                final List<OjkPddm> exportData = lapOjkPddmService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.BMHB.getValue())) {
                final List<OjkBmhb> exportData = lapOjkBmhbService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.UTLN.getValue())) {
                final List<OjkUtln> exportData = lapOjkUtlnService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.BOPR.getValue())) {
                final List<OjkBopr> exportData = lapOjkBoprService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PPH.getValue())) {
                final List<OjkPph> exportData = lapOjkPphService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else {
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(dataList);
            }

            String jasperName = getJasper(namaLaporan);
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    jrBeanCollectionDataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcel(Map<String, Object> parameter, String namaLaporan, List<Object> dataList) {
        try {

            final JRBeanCollectionDataSource jrBeanCollectionDataSource;
            if (namaLaporan.equals(NamaLaporan.LAN.getValue())) {
                final List<OjkLan> exportData = lapOjkLanService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.NRC.getValue())) {
                final List<OjkNrc> exportData = lapOjkNrcService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.LPHU.getValue())) {
                final List<OjkLphu> exportData = lapOjkLphuService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.KUP.getValue())) {
                final List<OjkKup> exportData = lapOjkKupService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.REKINV.getValue())) {
                final List<OjkRekinv> exportData = lapOjkRekinvService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.BBMK.getValue())) {
                final List<OjkBbmk> exportData = lapOjkBbmkService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PIUI.getValue())) {
                final List<OjkPiui> exportData = lapOjkPiuiService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PIHI.getValue())) {
                final List<OjkPihi> exportData = lapOjkPihiService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PILL.getValue())) {
                final List<OjkPill> exportData = lapOjkPillService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PDDM.getValue())) {
                final List<OjkPddm> exportData = lapOjkPddmService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.BMHB.getValue())) {
                final List<OjkBmhb> exportData = lapOjkBmhbService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.UTLN.getValue())) {
                final List<OjkUtln> exportData = lapOjkUtlnService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.BOPR.getValue())) {
                final List<OjkBopr> exportData = lapOjkBoprService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else if (namaLaporan.equals(NamaLaporan.PPH.getValue())) {
                final List<OjkPph> exportData = lapOjkPphService.findAll();
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);
            } else {
                jrBeanCollectionDataSource = new JRBeanCollectionDataSource(dataList);
            }

            String jasperName = getJasper(namaLaporan);
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    jrBeanCollectionDataSource);

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


    public String getJasper(String namaLap) {
        String jasper = "";
        if (namaLap.equals(NamaLaporan.LAN.getValue()))
            jasper = "/jasper/laporan_aset_neto.jasper";
        else if (namaLap.equals(NamaLaporan.LPAN.getValue()))
            jasper = "/jasper/laporan_perubahan_aset_neto.jasper";
        else if (namaLap.equals(NamaLaporan.NRC.getValue()))
            jasper = "/jasper/laporan_neraca.jasper";
        else if (namaLap.equals(NamaLaporan.LPHU.getValue()))
            jasper = "/jasper/laporan_perhitungan_hasil_usaha.jasper";
        else if (namaLap.equals(NamaLaporan.LAK.getValue()))
            jasper = "/jasper/laporan_arus_kas.jasper";
        else if (namaLap.equals(NamaLaporan.PST.getValue()))
            jasper = "/jasper/laporan_kepesertaan.jasper";
        else if (namaLap.equals(NamaLaporan.KUP.getValue()))
            jasper = "/jasper/laporan_kekayaan_pendanaan.jasper";
        else if (namaLap.equals(NamaLaporan.KUP1.getValue()))
            jasper = "/jasper/laporan_kekayaan_pendanaan_1.jasper";
        else if (namaLap.equals(NamaLaporan.KUP2.getValue()))
            jasper = "/jasper/laporan_kekayaan_pendanaan_2.jasper";
        else if (namaLap.equals(NamaLaporan.REKINV.getValue()))
            jasper = "/jasper/laporan_rekinv.jasper";
        else if (namaLap.equals(NamaLaporan.ROI.getValue()))
            jasper = "/jasper/laporan_hasil_return_on_investment.jasper";
        else if (namaLap.equals(NamaLaporan.ROIML.getValue()))
            jasper = "/jasper/laporan_hasil_investasi_manfaat_lain.jasper";
        else if (namaLap.equals(NamaLaporan.ALM.getValue()))
            jasper = "/jasper/laporan_alm.jasper";
        else if (namaLap.equals(NamaLaporan.INSP.getValue()))
            jasper = "/jasper/laporan_rincian_investasi_pada_satu_pihak.jasper";
        else if (namaLap.equals(NamaLaporan.SBN.getValue()))
            jasper = "/jasper/laporan_rincian_pemenuhan_ketentuan_mengenai_investasi.jasper";
        else if (namaLap.equals(NamaLaporan.RASIO.getValue()))
            jasper = "/jasper/laporan_rasio.jasper";
        else if (namaLap.equals(NamaLaporan.PMI.getValue()))
            jasper = "/jasper/laporan_pmi.jasper";
        else if (namaLap.equals(NamaLaporan.TAB.getValue()))
            jasper = "/jasper/laporan_tab.jasper";
        else if (namaLap.equals(NamaLaporan.DOC.getValue()))
            jasper = "/jasper/laporan_doc.jasper";
        else if (namaLap.equals(NamaLaporan.DPJKA.getValue()))
            jasper = "/jasper/laporan_dpjka.jasper";
        else if (namaLap.equals(NamaLaporan.SRDP.getValue()))
            jasper = "/jasper/laporan_rincian_sertifikat_deposito.jasper";
        else if (namaLap.equals(NamaLaporan.SBI.getValue()))
            jasper = "/jasper/laporan_rincian_surat_berharga_indonesia.jasper";
        else if (namaLap.equals(NamaLaporan.RSBN.getValue()))
            jasper = "/jasper/laporan_rincian_surat_berharga_negara.jasper";
        else if (namaLap.equals(NamaLaporan.SHM.getValue()))
            jasper = "/jasper/laporan_rincian_saham.jasper";
        else if (namaLap.equals(NamaLaporan.OBLI.getValue()))
            jasper = "/jasper/laporan_rincian_obligasi.jasper";
        else if (namaLap.equals(NamaLaporan.SUKUK.getValue()))
            jasper = "/jasper/laporan_rincian_sukuk.jasper";
        else if (namaLap.equals(NamaLaporan.OBSUD.getValue()))
            jasper = "/jasper/laporan_rincian_obligasi_sukuk_daerah.jasper";
        else if (namaLap.equals(NamaLaporan.RKSD.getValue()))
            jasper = "/jasper/laporan_rincian_reksadana.jasper";
        else if (namaLap.equals(NamaLaporan.MTN.getValue()))
            jasper = "/jasper/laporan_rincian_MTN.jasper";
        else if (namaLap.equals(NamaLaporan.DIRE.getValue()))
            jasper = "/jasper/laporan_dire.jasper";
        else if (namaLap.equals(NamaLaporan.DNFRA.getValue()))
            jasper = "/jasper/laporan_dnfra.jasper";
        else if (namaLap.equals(NamaLaporan.KOKB.getValue()))
            jasper = "/jasper/laporan_kokb.jasper";
        else if (namaLap.equals(NamaLaporan.REPO.getValue()))
            jasper = "/jasper/laporan_repo.jasper";
        else if (namaLap.equals(NamaLaporan.PNYL.getValue()))
            jasper = "/jasper/laporan_pnyl.jasper";
        else if (namaLap.equals(NamaLaporan.PROP.getValue()))
            jasper = "/jasper/laporan_prop.jasper";
        else if (namaLap.equals(NamaLaporan.KASB.getValue()))
            jasper = "/jasper/laporan_kasb.jasper";
        else if (namaLap.equals(NamaLaporan.PIUT.getValue()))
            jasper = "/jasper/laporan_piut.jasper";
        else if (namaLap.equals(NamaLaporan.PIUB.getValue()))
            jasper = "/jasper/laporan_piub.jasper";
        else if (namaLap.equals(NamaLaporan.BBMK.getValue()))
            jasper = "/jasper/laporan_bbmk.jasper";
        else if (namaLap.equals(NamaLaporan.PIUI.getValue()))
            jasper = "/jasper/laporan_piui.jasper";
        else if (namaLap.equals(NamaLaporan.PIHI.getValue()))
            jasper = "/jasper/laporan_pihi.jasper";
        else if (namaLap.equals(NamaLaporan.PILL.getValue()))
            jasper = "/jasper/laporan_pill.jasper";
        else if (namaLap.equals(NamaLaporan.TNBG.getValue()))
            jasper = "/jasper/laporan_tnbg.jasper";
        else if (namaLap.equals(NamaLaporan.KNDR.getValue()))
            jasper = "/jasper/laporan_kndr.jasper";
        else if (namaLap.equals(NamaLaporan.PKOM.getValue()))
            jasper = "/jasper/laporan_pkom.jasper";
        else if (namaLap.equals(NamaLaporan.PKAN.getValue()))
            jasper = "/jasper/laporan_pkan.jasper";
        else if (namaLap.equals(NamaLaporan.ASOL.getValue()))
            jasper = "/jasper/laporan_asol.jasper";
        else if (namaLap.equals(NamaLaporan.ASLN.getValue()))
            jasper = "/jasper/laporan_asln.jasper";
        else if (namaLap.equals(NamaLaporan.UMPS.getValue()))
            jasper = "/jasper/laporan_umps.jasper";
        else if (namaLap.equals(NamaLaporan.UTIN.getValue()))
            jasper = "/jasper/laporan_utin.jasper";
        else if (namaLap.equals(NamaLaporan.PDDM.getValue()))
            jasper = "/jasper/laporan_pddm.jasper";
        else if (namaLap.equals(NamaLaporan.BMHB.getValue()))
            jasper = "/jasper/laporan_bmhb.jasper";
        else if (namaLap.equals(NamaLaporan.UTLN.getValue()))
            jasper = "/jasper/laporan_utln.jasper";
        else if (namaLap.equals(NamaLaporan.PPIN.getValue()))
            jasper = "/jasper/laporan_ppin.jasper";
        else if (namaLap.equals(NamaLaporan.IUJT.getValue()))
            jasper = "/jasper/laporan_iujt.jasper";
        else if (namaLap.equals(NamaLaporan.PDIN.getValue()))
            jasper = "/jasper/laporan_pdin.jasper";
        else if (namaLap.equals(NamaLaporan.PDPL.getValue()))
            jasper = "/jasper/laporan_pdpl.jasper";
        else if (namaLap.equals(NamaLaporan.BOPR.getValue()))
            jasper = "/jasper/laporan_bopr.jasper";
        else if (namaLap.equals(NamaLaporan.BIPR.getValue()))
            jasper = "/jasper/laporan_bipr.jasper";
        else if (namaLap.equals(NamaLaporan.PPH.getValue()))
            jasper = "/jasper/laporan_pph.jasper";
        else if (namaLap.equals(NamaLaporan.PKPL.getValue()))
            jasper = "/jasper/laporan_pkpl.jasper";
        return jasper;
    }

    public TableLapOjk<?> getData(String namaLap, String periode, String tahunBuku) {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Column 1", "uraian"));

        if (namaLap.equals(NamaLaporan.LAN.getValue()))
            return lapOjkLanService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.LPAN.getValue()))
            return lapOjkLpanService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.NRC.getValue()))
            return lapOjkNrcService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.LPHU.getValue()))
            return lapOjkLphuService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.LAK.getValue()))
            return lapOjkLakService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PST.getValue()))
            return lapOjkPstService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.KUP.getValue()))
            return lapOjkKupService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.KUP1.getValue()))
            return lapOjkKup1Service.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.KUP2.getValue()))
            return lapOjkKup2Service.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.REKINV.getValue()))
            return lapOjkRekinvService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.ROI.getValue()))
            return lapOjkRoiService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.ROIML.getValue()))
            return lapOjkRoimlService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.ALM.getValue()))
            return lapOjkAlmService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.INSP.getValue()))
            return lapOjkInspService.getDatatable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.SBN.getValue()))
            return lapOjkSbnService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.RASIO.getValue()))
            return lapOjkRasioService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PMI.getValue()))
            return lapOjkPmiService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.TAB.getValue()))
            return lapOjkTabService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.DOC.getValue()))
            return lapOjkDocService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.DPJKA.getValue()))
            return lapOjkDpjkaService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.SRDP.getValue()))
            return lapOjkSrdpService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.SBI.getValue()))
            return lapOjkSbiService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.RSBN.getValue()))
            return lapOjkRsbnService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.SHM.getValue()))
            return lapOjkShmService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.OBLI.getValue()))
            return lapOjkObliService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.SUKUK.getValue()))
            return lapOjkSukukService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.OBSUD.getValue()))
            return lapOjkObsudService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.DIRE.getValue()))
            return lapOjkDireService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.DNFRA.getValue()))
            return lapOjkDnfraService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.KOKB.getValue()))
            return lapOjkKokbService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.REPO.getValue()))
            return lapOjkRepoService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PNYL.getValue()))
            return lapOjkPnylService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PROP.getValue()))
            return lapOjkPropService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.KASB.getValue()))
            return lapOjkKasbService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PIUT.getValue()))
            return lapOjkPiutService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PIUB.getValue()))
            return lapOjkPiubService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.BBMK.getValue()))
            return lapOjkBbmkService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PIUI.getValue()))
            return lapOjkPiuiService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PIHI.getValue()))
            return lapOjkPihiService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PILL.getValue()))
            return lapOjkPillService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.TNBG.getValue()))
            return lapOjkTnbgService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.KNDR.getValue()))
            return lapOjkKndrService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PKOM.getValue()))
            return lapOjkPkomService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PKAN.getValue()))
            return lapOjkPkanService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.ASOL.getValue()))
            return lapOjkAsolService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.ASLN.getValue()))
            return lapOjkAslnService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.UMPJ.getValue()))
            return lapOjkUmpjService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.RKSD.getValue()))
            return lapOjkRksdService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.MTN.getValue()))
            return lapOjkMtnService.getDataTable(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.UMPS.getValue()))
            return lapOjkUmpsService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.UTIN.getValue()))
            return lapOjkUtinService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PDDM.getValue()))
            return lapOjkPddmService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.BMHB.getValue()))
            return lapOjkBmhbService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.UTLN.getValue()))
            return lapOjkUtlnService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PPIN.getValue()))
            return lapOjkPpinService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.IUJT.getValue()))
            return lapOjkIujtService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PDIN.getValue()))
            return lapOjkPdinService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PDPL.getValue()))
            return lapOjkPdplService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.BINV.getValue()))
            return lapOjkBinvService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.BOPR.getValue()))
            return lapOjkBoprService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.BIPR.getValue()))
            return lapOjkBiprService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PPH.getValue()))
            return lapOjkPphService.getDatatables(periode, tahunBuku);
        else if (namaLap.equals(NamaLaporan.PKPL.getValue()))
            return lapOjkPkplService.getDatatables(periode, tahunBuku);
        else return new TableLapOjk<>(
                    new ArrayList<>(),
                    columns
            );
    }

    public int proses(String namaLap, String kodePeriode, String kodeTahunBuku, Principal principal) throws Exception {
        List<LaporanOjkDTO.Manual> manualList = getUraianManual(namaLap);

        if (namaLap.equals(NamaLaporan.PPH.getValue()))
            return lapOjkPphService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.BBMK.getValue()))
            return lapOjkBbmkService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.PIUI.getValue()))
            return lapOjkPiuiService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.PIHI.getValue()))
            return lapOjkPihiService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.PILL.getValue()))
            return lapOjkPillService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.PDDM.getValue()))
            return lapOjkPddmService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.BMHB.getValue()))
            return lapOjkBmhbService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.UTLN.getValue()))
            return lapOjkUtlnService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.LAN.getValue()))
            return lapOjkLanService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.LPAN.getValue()))
            return lapOjkLpanService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.NRC.getValue()))
            return lapOjkNrcService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.LPHU.getValue()))
            return lapOjkLphuService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.KUP.getValue()))
            return lapOjkKupService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.REKINV.getValue()))
            return lapOjkRekinvService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.BOPR.getValue()))
            return lapOjkBoprService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.LAK.getValue()))
            return lapOjkLakService.proses(kodePeriode, kodeTahunBuku, principal);
        else if (namaLap.equals(NamaLaporan.PST.getValue()))
            return lapOjkPstService.proses(manualList, kodeTahunBuku, kodePeriode, principal);
        return 0;
    }

    public byte[] generateExcel(String kodePeriode, String kodeTahunBuku) throws IOException {
        String excelFilePath = fileStorageService.fileStoragePath() + File.separator + "laporan-ojk-tmp.xlsx";

        List<OjkLan> ojkLans = lapOjkLanService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkLan(ojkLans, excelFilePath);

        List<OjkLpan> ojkLpans = lapOjkLpanService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkLpan(ojkLpans, excelFilePath);

        List<OjkNrc> ojkNrcs = lapOjkNrcService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkNrc(ojkNrcs, excelFilePath);

        List<OjkLphu> ojkLphus = lapOjkLphuService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkLphu(ojkLphus, excelFilePath);

        List<OjkLak> ojkLaks = lapOjkLakService.findAll();
        excelService.writeOjkLak(ojkLaks, excelFilePath);

        List<OjkPst> ojkPsts = lapOjkPstService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPst(ojkPsts, excelFilePath);

        List<OjkKup> ojkKups = lapOjkKupService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkKup(ojkKups, excelFilePath);

        List<OjkKup1> ojkKup1s = lapOjkKup1Service.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkKup1(ojkKup1s, excelFilePath);

        List<OjkKup2> ojkKup2s = lapOjkKup2Service.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkKup2(ojkKup2s, excelFilePath);

        List<OjkRekinv> ojkRekinvs = lapOjkRekinvService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRekinv(ojkRekinvs, excelFilePath);

        List<OjkRoi> ojkRois = lapOjkRoiService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRoi(ojkRois, excelFilePath);

        List<OjkRoiml> ojkRoimls = lapOjkRoimlService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRoiml(ojkRoimls, excelFilePath);

        List<OjkAlm> ojkAlms = lapOjkAlmService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkAlm(ojkAlms, excelFilePath);

        List<OjkInsp> ojkInsps = lapOjkInspService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkInsp(ojkInsps, excelFilePath);

        List<OjkRasio> ojkRasios = lapOjkRasioService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRasio(ojkRasios, excelFilePath);

        List<OjkPmi> ojkPmis = lapOjkPmiService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPmi(ojkPmis, excelFilePath);

        List<OjkTab> ojkTabs = lapOjkTabService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkTab(ojkTabs, excelFilePath);

        List<OjkDoc> ojkDocs = lapOjkDocService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkDoc(ojkDocs, excelFilePath);

        List<OjkDpjka> ojkDpjkas = lapOjkDpjkaService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkDpjka(ojkDpjkas, excelFilePath);

        List<OjkSrdp> ojkSrdps = lapOjkSrdpService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkSrdp(ojkSrdps, excelFilePath);

        List<OjkSbi> ojkSbis = lapOjkSbiService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkSbi(ojkSbis, excelFilePath);

        List<OjkRsbn> ojkRsbns = lapOjkRsbnService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRsbn(ojkRsbns, excelFilePath);

        List<OjkShm> ojkShms = lapOjkShmService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkShm(ojkShms, excelFilePath);

        List<OjkDire> ojkDires = lapOjkDireService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkDire(ojkDires, excelFilePath);

        List<OjkDnfra> ojkDnfras = lapOjkDnfraService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkDnfra(ojkDnfras, excelFilePath);

        List<OjkKokb> ojkKokbs = lapOjkKokbService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkKokb(ojkKokbs, excelFilePath);

        List<OjkRepo> ojkRepos = lapOjkRepoService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRepo(ojkRepos, excelFilePath);

        List<OjkPnyl> ojkPnyls = lapOjkPnylService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPnyl(ojkPnyls, excelFilePath);

        List<OjkProp> ojkProps = lapOjkPropService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkProp(ojkProps, excelFilePath);

        List<OjkKasb> ojkKasbs = lapOjkKasbService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkKasb(ojkKasbs, excelFilePath);

        List<OjkPiut> ojkPiuts = lapOjkPiutService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPiut(ojkPiuts, excelFilePath);

        List<OjkPiub> ojkPiubs = lapOjkPiubService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPiub(ojkPiubs, excelFilePath);

        List<OjkBbmk> ojkBbmks = lapOjkBbmkService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkBbmk(ojkBbmks, excelFilePath);

        List<OjkRksd> ojkRksds = lapOjkRksdService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkRksd(ojkRksds, excelFilePath);

        List<OjkMtn> ojkMtns = lapOjkMtnService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkMtn(ojkMtns, excelFilePath);

        List<OjkPiui> ojkPiuis = lapOjkPiuiService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPiui(ojkPiuis, excelFilePath);

        List<OjkPihi> ojkPihis = lapOjkPihiService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPihi(ojkPihis, excelFilePath);

        List<OjkPill> ojkPills = lapOjkPillService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPill(ojkPills, excelFilePath);

        List<OjkTnbg> ojkTnbgs = lapOjkTnbgService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkTnbg(ojkTnbgs, excelFilePath);

        List<OjkKndr> ojkKndrs = lapOjkKndrService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkKndr(ojkKndrs, excelFilePath);

        List<OjkPkom> ojkPkoms = lapOjkPkomService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPkom(ojkPkoms, excelFilePath);

        List<OjkPkan> ojkPkans = lapOjkPkanService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOOjkPkan(ojkPkans, excelFilePath);

        List<OjkAsol> ojkAsols = lapOjkAsolService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkAsol(ojkAsols, excelFilePath);

        List<OjkAsln> ojkAslns = lapOjkAslnService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkAsln(ojkAslns, excelFilePath);

        List<OjkUmpj> ojkUmpjs = lapOjkUmpjService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkUmpj(ojkUmpjs, excelFilePath);

        List<OjkUmps> ojkUmps = lapOjkUmpsService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkUmps(ojkUmps, excelFilePath);

        List<OjkUtin> ojkUtins = lapOjkUtinService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkUtin(ojkUtins, excelFilePath);

        List<OjkPddm> ojkPddms = lapOjkPddmService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPddm(ojkPddms, excelFilePath);

        List<OjkBmhb> ojkBmhbs = lapOjkBmhbService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkBmhb(ojkBmhbs, excelFilePath);

        List<OjkUtln> ojkUtlns = lapOjkUtlnService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkUtln(ojkUtlns, excelFilePath);

        List<OjkPpin> ojkPpins = lapOjkPpinService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPpin(ojkPpins, excelFilePath);

        List<OjkIujt> ojkIujts = lapOjkIujtService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkIujt(ojkIujts, excelFilePath);

        List<OjkPdin> ojkPdins = lapOjkPdinService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPdin(ojkPdins, excelFilePath);

        List<OjkPdpl> ojkPdpls = lapOjkPdplService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPdpl(ojkPdpls, excelFilePath);

        List<OjkBinv> ojkBinvs = lapOjkBinvService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkBinv(ojkBinvs, excelFilePath);

        List<OjkBopr> ojkBoprs = lapOjkBoprService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkBopr(ojkBoprs, excelFilePath);

        List<OjkBipr> ojkBiprs = lapOjkBiprService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkBipr(ojkBiprs, excelFilePath);

        List<OjkPph> ojkPphs = lapOjkPphService.findAll(kodePeriode, kodeTahunBuku);
        excelService.writeOjkPph(ojkPphs, excelFilePath);

        File file = new File(excelFilePath);
        byte[] bytes = FileUtils.readFileToByteArray(file);
        file.delete();

        return bytes;
    }

    public List<LaporanOjkDTO.Manual> getUraianManual(String namaLap) throws IncorrectResultSizeDataAccessException {
        String namaLaporan = "";
        if (namaLap.equals(NamaLaporan.PST.getValue())){
            System.out.println("masuk pst");
            namaLaporan = "OJK_PST";
        }

        //language=Oracle
        String finalQuery = "SELECT \n" +
                "   ML.*\n" +
                "FROM MST_LOOKUP ML \n" +
                "WHERE ML.JENIS_LOOKUP = :namaLap \n" +
                "ORDER BY ML.KODE_LOOKUP ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("namaLap", namaLaporan);
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<LaporanOjkDTO.Manual>() {
            @Override
            public LaporanOjkDTO.Manual mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanOjkDTO.Manual manual = new LaporanOjkDTO.Manual();
                manual.setUrutan(resultSet.getString("KODE_LOOKUP"));
                manual.setUraian(resultSet.getString("NAMA_LOOKUP"));
                return manual;
            }
        });
    }
}
