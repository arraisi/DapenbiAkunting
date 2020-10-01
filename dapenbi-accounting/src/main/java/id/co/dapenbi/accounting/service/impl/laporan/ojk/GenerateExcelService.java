package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.entity.laporan.ojk.*;
import id.co.dapenbi.core.util.DateUtil;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class GenerateExcelService {

    public void writeOjkLan(List<OjkLan> dataList, String excelFilePath) {
        ZipSecureFile.setMinInflateRatio(0);
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        workbook.setCompressTempFiles(true);

        CreationHelper createHelper = workbook.getCreationHelper();
        SXSSFSheet sheet = workbook.createSheet("Laporan Aset Neto (LAN)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
//        headerFont.setBold(true);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] header = {"Uraian", "Persentase Investasi", "Program Pensiun", "Manfaat Lain", "Gabungan"};
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerCellStyle);
        }

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        int rowNum = 1;
        for (OjkLan data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getPersentaseInvestasi(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getProgramPensiun(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getGabungan(), 4, workbook.createCellStyle(), workbook.createDataFormat());
        }

        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOjkLpan(List<OjkLpan> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Perubahan Aset Neto (LPAN)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Program Pensiun", "Manfaat Lain", "Gabungan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkLpan data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getProgramPensiun(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getGabungan(), 3, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkNrc(List<OjkNrc> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Neraca (NRC)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Program Pensiun", "Manfaat Lain", "Gabungan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkNrc data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getProgramPensiun(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getGabungan(), 3, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkLphu(List<OjkLphu> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Perhitungan Hasil Usaha (LPHU)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Program Pensiun", "Manfaat Lain", "Gabungan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkLphu data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getProgramPensiun(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getGabungan(), 3, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkLak(List<OjkLak> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Arus Kas (LAK)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Program Pensiun", "Manfaat Lain", "Gabungan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkLak data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getProgramPensiun(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getGabungan(), 3, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPst(List<OjkPst> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Kepesertaan (PST)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Program Pensiun", "Manfaat Lain", "Gabungan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPst data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getProgramPensiun(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getGabungan(), 3, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkKup(List<OjkKup> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan KUP");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Nilai"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkKup data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getNilai(), 1, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkKup1(List<OjkKup1> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan KUP1");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nomor Baris", "Uraian", "Nilai"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1, number = 1;
        for (OjkKup1 data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, number, 0);
            createCell(row, data.getUraian(), 1);
            createCell(row, data.getNilai(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            number++;
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkKup2(List<OjkKup2> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan KUP2");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Nilai"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkKup2 data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getNilai(), 1, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRekinv(List<OjkRekinv> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rekap Investasi (REKINV)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt",
                "Nov", "Des"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRekinv data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getTotJan(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotFeb(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotMar(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotApr(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotMay(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotJun(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotJul(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotAug(), 8, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotSep(), 9, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotOct(), 10, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotNov(), 11, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotDes(), 12, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRoi(List<OjkRoi> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan ROI");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Bunga / Bagi Hasil", "Deviden", "Sewa", "Laba/Rugi Pelepasan", "Lainnya",
                "Hasil Investasi yang Belum Terealisasi", "Beban Investasi", "Hasil Investasi Bersih",
                "Rata-rata Investasi", "ROID"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRoi data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getTotalBunga(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalDeviden(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalSewa(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalLaba(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalLainnya(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalInvBelum(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalBebanInv(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalHasilInv(), 8, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalRata2Inv(), 9, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalRoi(), 10, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRoiml(List<OjkRoiml> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan ROIML");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Bunga / Bagi Hasil", "Deviden", "Sewa", "Laba/Rugi Pelepasan", "Lainnya",
                "Hasil Investasi yang Belum Terealisasi", "Beban Investasi", "Hasil Investasi Bersih",
                "Rata-rata Investasi", "ROID"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRoiml data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getTotalBunga(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalDeviden(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalSewa(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalLaba(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalLainnya(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalInvBelum(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalBebanInv(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalHasilInv(), 8, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalRata2Inv(), 9, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTotalRoi(), 10, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkAlm(List<OjkAlm> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan ALM");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Tahun 1 Rp", "Tahun 5 Rp", "Tahun 5 Non Rp", "Tahun 10 Rp",
                "Tahun 10 Non Rp", "Tahun 11 Rp", "Tahun 11 Non Rp"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkAlm data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getTahun1Rp(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahun5Rp(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahun5NonRp(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahun10Rp(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahun10NonRp(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahun11Rp(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahun11NonRp(), 7, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkSbn(List<OjkSbn> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Investasi SBN");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Jenis Investasi", "Saldo", "Nama Jenis Investasi", "Seri Efek",
                "Jenis Kepemilikan", "Rating", "Saldo", "Nama Jenis Investasi", "Manajer Investasi", "Nilai Wajar",
                "% SBN dalam Reksadana", "Saldo", "Nama Jenis Invetasi", "Manajer Investasi", "Jenis Kepemilikan",
                "Emiten penerima Dana/Project", "Saldo", "Nama Jenis Investasi", "Seri Efek", "Jenis Kepemilikan", "Rating",
                "Saldo", "Nama Jenis Investasi", "Saldo"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkSbn data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getSbnNama(), 0);
            createCell(row, data.getSbnSaldo(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getObligasiJnsInvestasi(), 2);
            createCell(row, data.getObligasiSeriEfek(), 3);
            createCell(row, data.getObligasiJnsKepemilikan(), 4);
            createCell(row, data.getObligasiRating(), 5);
            createCell(row, data.getObligasiSaldo(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getReksadanaJnsInvestasi(), 7);
            createCell(row, data.getReksadanaMngInvestasi(), 8);
            createCell(row, data.getReksadanaNilaiWajar(), 9);
            createCell(row, data.getReksadanaPersenSbn(), 10);
            createCell(row, data.getReksadanaSaldo(), 11, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getReksadanatJnsInvestasi(), 12);
            createCell(row, data.getReksadanatMngInvestasi(), 13);
            createCell(row, data.getReksadanatJnsKepemilikan(), 14);
            createCell(row, data.getReksadanatEmiten(), 15);
            createCell(row, data.getReksadanatSaldo(), 16, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getEfekJnsInvestasi(), 17);
            createCell(row, data.getEfekSeriEfek(), 18);
            createCell(row, data.getEfekJnsKepemilikan(), 19);
            createCell(row, data.getEfekRating(), 20);
            createCell(row, data.getEfekSaldo(), 21, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getLainJnsInvestasi(), 22);
            createCell(row, data.getLainSaldo(), 23, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkInsp(List<OjkInsp> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Investasi Pada Satu Pihak (INSP)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Pihak", "jenis", "Jumlah", "Persentase Terhadap Total",
                "Batasan Dalam Arahan Investasi", "Batasan Investasi Sesuai Ketentuan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkInsp data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaPihak(), 0);
            createCell(row, data.getJenis(), 1);
            createCell(row, data.getJumlah(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersentaseFormatted(), 3);
            createCell(row, data.getBatasanArahan(), 4);
            createCell(row, data.getBatasanSesuai(), 5);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRasio(List<OjkRasio> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rasio Keuangan (Rasio)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Uraian", "Program Pensiun", "Manfaat Lain", "Total"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRasio data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getUraian(), 0);
            createCell(row, data.getProgramPensiun(), 1);
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getTotal(), 3, workbook.createCellStyle(), workbook.createDataFormat());
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPmi(List<OjkPmi> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Pengungkapan Manajer Investasi (PMI)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Manajer Investasi", "Nomor Kontrak", "Tanggal Kontrak", "Masa Perjanjian",
                "Jenis Investasi", "Jumlah Dana Kelolaan (Rp)", "Tingkat Hasil Investasi Bersih (Rp)",
                "Jumlah Biaya Pengelolaan yang dibebankan (Rp)", "Jumlah Biaya Pengelolaan yang dibebankan (Rp) Formatted",
                "Terafiliasi dengan Dana Pensiun (Ya/Tidak)"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPmi data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getManagerInvestasi(), 0);
            createCell(row, data.getNoKontrak(), 1);
            createCell(row, data.getTglKontrak(), 2);
            createCell(row, data.getMasaPerjanjian(), 3);
            createCell(row, data.getJnsInvestasi(), 4);
            createCell(row, data.getJumlahDana(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTingkatHasil(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getJumlahBiaya(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getJumlahBiayaFormatted(), 8);
            createCell(row, data.getTerafiliasi(), 9);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkTab(List<OjkTab> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Tabungan (TAB)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Bank", "Tanggal Perolehan", "Nila Nominal", "Tingkat Bunga/Nisbah",
                "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkTab data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaBank(), 0);
            createCell(row, data.getTglPerolehan(), 1);
            createCell(row, data.getNilaiNominal(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTingkatBunga(), 3);
            createCell(row, data.getManfaatLain(), 4);
            createCell(row, data.getKeterangan(), 5);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkDoc(List<OjkDoc> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Deposit on Call (DOC)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Bank", "Tanggal Perolehan", "Nilai Nominal", "Nilai Nominal Formatted",
                "Jangka Waktu", "Tingkat Bunga/Nisbah", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkDoc data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaBank(), 0);
            createCell(row, data.getTglPerolehan(), 1);
            createCell(row, data.getNilaiNominal(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiNominalFormatted(), 3);
            createCell(row, data.getJangkaWaktu(), 4);
            createCell(row, data.getTingkatBungaFormatted(), 5);
            createCell(row, data.getManfaatLain(), 6);
            createCell(row, data.getKeterangan(), 7);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkDpjka(List<OjkDpjka> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Deposito Berjangka (DPJKA)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Bank", "Tanggal Perolehan", "Nilai Nominal", "Jangka Waktu",
                "Tingkat Bunga/Nisbah", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkDpjka data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaBank(), 0);
            createCell(row, data.getTglPerolehan(), 1);
            createCell(row, data.getNilaiNominalFormatted(), 2);
            createCell(row, data.getJangkaWaktu(), 3);
            createCell(row, data.getTingkatBungaFormatted(), 4);
            createCell(row, data.getManfaatLain(), 5);
            createCell(row, data.getKeterangan(), 6);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkSrdp(List<OjkSrdp> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan SRDP");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Bank", "Tanggal Perolehan", "Nilai Nominal", "Jangka Waktu",
                "Tingkat Bunga/Nisbah", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkSrdp data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaBank(), 0);
            createCell(row, data.getTglPerolehan(), 1);
            createCell(row, data.getNilaiNominal(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getJangkaWaktu(), 3);
            createCell(row, data.getTingkatBunga(), 4);
            createCell(row, data.getManfaatLain(), 5);
            createCell(row, data.getKeterangan(), 6);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkSbi(List<OjkSbi> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan SBI");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode Surat Berharga", "Tanggal Perolehan", "Nilai Nominal", "Kupon(%)",
                "Tanggal Jatuh Tempo", "Nilai Perolehan", "Nilai Wajar", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkSbi data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKodeSurga(), 0);
            createCell(row, data.getTglPerolehan(), 1);
            createCell(row, data.getNilaiNominal(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenKupon(), 3);
            createCell(row, data.getTglJatpo(), 4);
            createCell(row, data.getNilaiPerolehan(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 6);
            createCell(row, data.getKeterangan(), 7);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRsbn(List<OjkRsbn> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan RSBN");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode Surat Berharga", "Tanggal Perolehan", "Nilai Nominal", "Kupon(%)",
                "Tanggal Jatuh Tempo", "Nilai Perolehan", "Nilai Wajar", "Program Pensiun/Manfaat Lain",
                "Metode Pencatatan", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRsbn data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKodeSurga(), 0);
            createCell(row, data.getTglPerolehan(), 1);
            createCell(row, data.getNilaiNominal(), 2, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenKupon(), 3);
            createCell(row, data.getTglJatpo(), 4);
            createCell(row, data.getNilaiPerolehan(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 6);
            createCell(row, data.getMetodePencatatan(), 7);
            createCell(row, data.getKeterangan(), 8);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkShm(List<OjkShm> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Rincian Saham (SHM)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode Saham", "Nama Emiten/Penerbit", "Tanggal Perolehan", "Jumlah Saham",
                "Kupon Selinv (%)", "Nilai Perolehan", "Nilai Pasar", "Sektor Ekonomi", "Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkShm data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKodeSaham(), 0);
            createCell(row, data.getNamaEmiten(), 1);
            createCell(row, data.getTglPerolehan(), 2);
            createCell(row, data.getJumlahSaham(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenSelinv(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiPerolehan(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiWajar(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getSektorEkonomi(), 7);
            createCell(row, data.getManfaatLain(), 8);
            createCell(row, data.getKeterangan(), 9);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkDire(List<OjkDire> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan DIRE");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode", "Nama Produk", "Manajer Investasi", "Tanggal Perolehan", "Jumlah Unit",
                "Nilai Perolehan", "Nilai Wajar", "Nilai", "%", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkDire data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKode(), 0);
            createCell(row, data.getNamaProduk(), 1);
            createCell(row, data.getManajerInvestasi(), 2);
            createCell(row, data.getTglPerolehan(), 3);
            createCell(row, data.getJumlahUnit(), 4);
            createCell(row, data.getNilaiPerolehan(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiWajar(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiSelinv(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenSelinv(), 8);
            createCell(row, data.getManfaatLain(), 9);
            createCell(row, data.getKeterangan(), 10);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkDnfra(List<OjkDnfra> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan DNFRA");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode", "Nama Produk", "Manajer Investasi", "Tanggal Perolehan", "Jumlah Unit",
                "Nilai Perolehan", "Nilai Aktifa Bersih", "Nilai", "%", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkDnfra data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKode(), 0);
            createCell(row, data.getNamaProduk(), 1);
            createCell(row, data.getManajerInvestasi(), 2);
            createCell(row, data.getTglPerolehan(), 3);
            createCell(row, data.getJumlahUnit(), 4);
            createCell(row, data.getNilaiPerolehan(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiAktiva(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiSelinv(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenSelinv(), 8);
            createCell(row, data.getManfaatLain(), 9);
            createCell(row, data.getKeterangan(), 10);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkKokb(List<OjkKokb> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian KOKB");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Emiten", "Nama Pembeli", "Tanggal Perolehan", "Jangka Waktu", "Nilai Perolehan",
                "Nilai Wajar", "Nilai", "%", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkKokb data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaEmiten(), 0);
            createCell(row, data.getNamaPembeli(), 1);
            createCell(row, data.getTglPerolehan(), 2);
            createCell(row, data.getJangkaWaktu(), 3);
            createCell(row, data.getNilaiPerolehan(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiWajar(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiSelinv(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenSelinv(), 7);
            createCell(row, data.getManfaatLain(), 8);
            createCell(row, data.getKeterangan(), 9);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRepo(List<OjkRepo> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian REPO");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Counterparty", "Jenis Jaminan (SBN/SBI/OBL)", "Tanggal Perolehan", "Nilai Jaminan",
                "Awal", "AKhir", "Jangka Waktu", "Kategori(KSEI/BIS4)", "Nilai Perolehan", "Nominal", "%",
                "Amortized Cost", "Nilai Jual", "Nilai", "%", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRepo data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getCounterparty(), 0);
            createCell(row, data.getJenisJaminan(), 1);
            createCell(row, data.getTglPerolehan(), 2);
            createCell(row, data.getNilaiJaminan(), 3, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPeringkatAwal(), 4);
            createCell(row, data.getPeringkatAkhir(), 5);
            createCell(row, data.getJangkaWaktu(), 6);
            createCell(row, data.getKategori(), 7);
            createCell(row, data.getNilaiPerolehan(), 8, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getMarginNominal(), 9, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenMargin(), 10);
            createCell(row, data.getAmortizedCost(), 11, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiJual(), 12, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiSelinv(), 13, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenSelinv(), 14);
            createCell(row, data.getManfaatLain(), 15);
            createCell(row, data.getKeterangan(), 16);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPnyl(List<OjkPnyl> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian PNYL");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Perusahaan", "Nama", "Jabatan", "Kategori Penyertaan", "Tanggal Perolehan",
                "Tanggal", "% Kepemilikan", "Total", "% Kepemilikan", "Total", "Tanggal", "% Kepemilikan", "Total",
                "Nilai", "%", "Sektor Ekonomi", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPnyl data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaPerusahaan(), 0);
            createCell(row, data.getNamaPerwakilan(), 1);
            createCell(row, data.getJabatanPerwakilan(), 2);
            createCell(row, data.getKategoriPenyertaan(), 3);
            createCell(row, data.getTglPerolehan(), 4);
            createCell(row, data.getTglPenempatanFormatted(), 5);
            createCell(row, data.getPersenPenempatanFormatted(), 6);
            createCell(row, data.getTotalPenempatanFormatted(), 7);
            createCell(row, data.getPersenPerolehanFormatted(), 8);
            createCell(row, data.getNilaiPerolehanFormatted(), 9);
            createCell(row, data.getTglWajarFormatted(), 10);
            createCell(row, data.getPersenWajarFormatted(), 11);
            createCell(row, data.getNilaiWajarFormatted(), 12);
            createCell(row, data.getNilaiSelinvFormatted(), 13);
            createCell(row, data.getPersenSelinvFormatted(), 14);
            createCell(row, data.getSektorEkonomi(), 15);
            createCell(row, data.getManfaatLain(), 16);
            createCell(row, data.getKeterangan(), 17);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkProp(List<OjkProp> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian PROP");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Objek", "Alamat", "Lokasi (Kota, Provinsi)", "Luas", "Jenis Bukti Kepemilikan",
                "Nomor Surat Kepemilikan", "Tanggal Perolehan", "Nilai Perolehan", "Akumulasi Penyusutan", "Nilai Buku",
                "Nilai Appraisal/Nilai Wajar", "Tahun Appraisal", "Nama Penilai Publik/Independen", "Nama KJPP", "Nilai",
                "%", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkProp data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisObjek(), 0);
            createCell(row, data.getAlamat(), 1);
            createCell(row, data.getLokasi(), 2);
            createCell(row, data.getLuas(), 3);
            createCell(row, data.getJenisKepemilikan(), 4);
            createCell(row, data.getNoKepemilikan(), 5);
            createCell(row, data.getTglPerolehan(), 6);
            createCell(row, data.getNilaiPerolehan(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiPerolehan(), 8, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getAkumulasiPenyusutan(), 9, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiBuku(), 10, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiWajar(), 11, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTahunAppraisal(), 12);
            createCell(row, data.getNamaPenilai(), 13);
            createCell(row, data.getNamaKjpp(), 14);
            createCell(row, data.getNilaiSelinvFormatted(), 15);
            createCell(row, data.getPersenSelinvFormatted(), 16);
            createCell(row, data.getManfaatLain(), 17);
            createCell(row, data.getKeterangan(), 18);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkKasb(List<OjkKasb> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Kas dan Bank (KASB)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kas/Nama Bank", "No. Rekening", "Nominal", "Program Pensiun/Manfaat Lain",
                "Keterangan (Tujuan Penggunaan)"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkKasb data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaRekening(), 0);
            createCell(row, data.getKodeRekening(), 1);
            createCell(row, data.getNominalFormatted(), 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPiut(List<OjkPiut> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Piutang Iuran (PIUT)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Pemberi Kerja (Pendiri/Mitra)", "Usia Piutang <= 3 Bulan",
                "Usia Piutang > 3 Bulan", "Total", "Usia Piutang <= 3 Bulan", "Usia Piutang > 3 Bulan", "Total",
                "Usia Piutang <= 3 Bulan", "Usia Piutang > 3 Bulan", "Total", "Piutang Iuran Sukarela Peserta",
                "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPiut data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaPemker(), 0);
            createCell(row, data.getUspiu1Pemker(), 1);
            createCell(row, data.getUspiu3Pemker(), 2);
            createCell(row, "", 3);
            createCell(row, data.getUspiu1Peserta(), 4);
            createCell(row, data.getUspiu3Peserta(), 5);
            createCell(row, "", 6);
            createCell(row, data.getUspiu1Tambahan(), 7);
            createCell(row, data.getUspiu3Tambahan(), 8);
            createCell(row, "", 9);
            createCell(row, data.getIuranSukarela(), 10);
            createCell(row, data.getManfaatLain(), 11);
            createCell(row, data.getKeterangan(), 12);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPiub(List<OjkPiub> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian PIUB");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Pemberi Kerja (Pendiri/Mitra Pendiri)", "Piutang Bunga Iuran Peserta",
                "Piutang Bunga Iuran Pemberi Kerja", "Piutang Bunga Iuran Tambahan", "Piutang Bunga Keterlambatan Iuran",
                "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPiub data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaPemker(), 0);
            createCell(row, data.getBungaPesertaFormatted(), 1);
            createCell(row, data.getBungaPemkerFormatted(), 2);
            createCell(row, data.getBungaTambahanFormatted(), 3);
            createCell(row, "", 4);
            createCell(row, data.getManfaatLain(), 5);
            createCell(row, data.getKeterangan(), 6);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkBbmk(List<OjkBbmk> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Beban Dibayar Dimuka (BBMK)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Beban", "Jumlah", "Program Pensiun / Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkBbmk data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisBeban(), 0);
            createCell(row, data.getJumlah(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkRksd(List<OjkRksd> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan RKSD");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode", "Nama Produk", "Jenis Reksadana", "Manajer Investasi", "Tanggal Perolehan",
                "Jumlah Unit", "Nilai Perolehan", "Nilai Wajar", "Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkRksd data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKode(), 0);
            createCell(row, data.getNamaProduk(), 1);
            createCell(row, data.getJenisReksadana(), 2);
            createCell(row, data.getManajerInvestasi(), 3);
            createCell(row, data.getTglPerolehan(), 4);
            createCell(row, data.getJumlahUnit(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiPerolehan(), 6, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiWajar(), 7, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 8);
            createCell(row, data.getKeterangan(), 9);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkMtn(List<OjkMtn> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Laporan Rincian MTN (MTN)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Kode", "Nama Produk", "Nama Penerbit", "Tanggal Perolehan", "Nilai Nominal",
                "Kupon (%)", "Tanggal Jatuh Tempo", "Peringkat Awal", "Peringkat Akhir", "Nilai Perolehan",
                "Nilai Wajar", "Sektor Ekonomi", "Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkMtn data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getKode(), 0);
            createCell(row, data.getNamaProduk(), 1);
            createCell(row, data.getNamaPenerbit(), 2);
            createCell(row, data.getTglPerolehan(), 3);
            createCell(row, data.getNilaiNominal(), 4, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getPersenKupon(), 5, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getTglJatpo(), 6);
            createCell(row, data.getPeringkatAwal(), 7);
            createCell(row, data.getPeringkatAkhir(), 8);
            createCell(row, data.getNilaiPerolehan(), 9, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getNilaiWajar(), 10, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getSektorEkonomi(), 11);
            createCell(row, data.getManfaatLain(), 12);
            createCell(row, data.getKeterangan(), 13);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPiui(List<OjkPiui> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Piutang Investasi (PIUI)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Piutang Investasi", "Nominal", "Program Pensiun / Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPiui data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPiutang(), 0);
            createCell(row, data.getNominal(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkTnbg(List<OjkTnbg> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian TNBG");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Tanah dan/atau Bangunan", "Nomor Sertifikat", "Alamat Lokasi",
                "Tanggal Perolehan", "Nilai Perolehan", "Akumulasi Penyusutan", "Nilai Buku",
                "Nilai Perolehan Pada Tanggal Laporan", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkTnbg data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaTanah(), 0);
            createCell(row, data.getNoSertifikat(), 1);
            createCell(row, data.getAlamatLokasi(), 2);
            createCell(row, data.getTglPerolehanFormatted(), 3);
            createCell(row, data.getNilaiPerolehanFormatted(), 4);
            createCell(row, data.getNilaiPenyusutanFormatted(), 5);
            createCell(row, data.getNilaiBukuFormatted(), 6);
            createCell(row, data.getNilaiPerolehanLapFormatted(), 7);
            createCell(row, data.getKeterangan(), 8);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkKndr(List<OjkKndr> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Kendaraan (KNDR)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"No. Plat Kendaraan", "Jenis Kendaraan", "Tanggal Perolehan", "Nilai Perolehan",
                "Akumulasi Penyusutan", "Nilai Buku", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkKndr data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNoKendaraan(), 0);
            createCell(row, data.getJenisKendaraan(), 1);
            createCell(row, data.getTglPerolehanFormatted(), 2);
            createCell(row, data.getNilaiPerolehanFormatted(), 3);
            createCell(row, data.getNilaiPenyusutanFormatted(), 4);
            createCell(row, data.getNilaiBukuFormatted(), 5);
            createCell(row, data.getKeterangan(), 6);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPkom(List<OjkPkom> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Peralatan Komputer (PKOM)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Peralatan Komputer", "Tanggal Perolehan", "Nilai Perolehan",
                "Akumulasi Penyusutan", "Nilai Buku", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPkom data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPeralatan(), 0);
            createCell(row, data.getTglPerolehanFormatted(), 1);
            createCell(row, data.getNilaiPerolehanFormatted(), 2);
            createCell(row, data.getNilaiPenyusutanFormatted(), 3);
            createCell(row, data.getNilaiBukuFormatted(), 4);
            createCell(row, data.getKeterangan(), 5);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOOjkPkan(List<OjkPkan> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Peralatan Kantor (PKAN)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Peralatan Kantor", "Tanggal Perolehan", "Nilai Perolehan",
                "Akumulasi Penyusutan", "Nilai Buku", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPkan data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPeralatan(), 0);
            createCell(row, data.getTglPerolehanFormatted(), 1);
            createCell(row, data.getNilaiPerolehanFormatted(), 2);
            createCell(row, data.getNilaiPenyusutanFormatted(), 3);
            createCell(row, data.getNilaiBukuFormatted(), 4);
            createCell(row, data.getKeterangan(), 5);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkAsol(List<OjkAsol> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian ASOL");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Aset Operasional Lain", "Tanggal Perolehan", "Nilai Perolehan",
                "Akumulasi Penyusutan", "Nilai Buku", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkAsol data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisAset(), 0);
            createCell(row, data.getTglPerolehanFormatted(), 1);
            createCell(row, data.getNilaiPerolehanFormatted(), 2);
            createCell(row, data.getNilaiPenyusutanFormatted(), 3);
            createCell(row, data.getNilaiBukuFormatted(), 4);
            createCell(row, data.getKeterangan(), 5);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkAsln(List<OjkAsln> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Aset Lain (ASLN)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Aset Lain", "Tanggal Perolehan", "Nilai Aset", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkAsln data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisAset(), 0);
            createCell(row, data.getTglPerolehanFormatted(), 1);
            createCell(row, data.getNilaiAsetFormatted(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkUmpj(List<OjkUmpj> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian UMPJ");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"<= 1 tahun", "> 1 tahun", "Total", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkUmpj data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJumlah1Formatted(), 0);
            createCell(row, data.getJumlah2Formatted(), 1);
            createCell(row, "", 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkUmps(List<OjkUmps> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian UMPS");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"<= 1 tahun", "> 1 tahun", "Total", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkUmps data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJumlah1Formatted(), 0);
            createCell(row, data.getJumlah2Formatted(), 1);
            createCell(row, "", 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkUtin(List<OjkUtin> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian UTIN");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Investasi", "Nilai", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkUtin data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisInvestasi(), 0);
            createCell(row, data.getNilaiFormatted(), 1);
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPpin(List<OjkPpin> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian PPIN");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Investasi", "Selisih Nilai Investasi", "Peningkatan/Penurunan",
                "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPpin data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisInvestasi(), 0);
            createCell(row, data.getSelisihInvestasiFormatted(), 1);
            createCell(row, data.getPeningkatanPenurunanFormatted(), 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkIujt(List<OjkIujt> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian IUJT");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Pendiri/Mitra Pendiri", "PhDP", "Iuran Normal Peserta (%)",
                "Iuran Normal Peserta (Jumlah)", "Iuran Normal Pemberi Kerja (%)", "Iuran Normal Pemberi Kerja (Jumlah)",
                "Iuran Sukarela Peserta", "Iuran Tambahan", "Iuran Normal Peserta", "Iuran Normal Pemberi Kerja",
                "Iuran Tambahan", "Iuran Sukarela Peserta", "Iuran Normal Peserta", "Iuran Normal Pemberi Kerja",
                "Iuran Normal Tamabahan", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkIujt data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaPendiri(), 0);
            createCell(row, data.getPhdp(), 1);
            createCell(row, data.getPersenIuranPesertaFormatted(), 2);
            createCell(row, data.getNilaiIuranPesertaFormatted(), 3);
            createCell(row, data.getPersenIuranPemkerFormatted(), 4);
            createCell(row, data.getNilaiIuranPemkerFormatted(), 5);
            createCell(row, data.getIuranSukarelaFormatted(), 6);
            createCell(row, data.getIuranTambahanFormatted(), 7);
            createCell(row, data.getIuranPesertaTerimaFormatted(), 8);
            createCell(row, data.getIuranPemkerTerimaFormatted(), 9);
            createCell(row, data.getIuranTambahanTerimaFormatted(), 10);
            createCell(row, data.getIuranSukarelaTerimaFormatted(), 11);
            createCell(row, data.getIuranPesertaKurlebFormatted(), 12);
            createCell(row, data.getIuranPemkerKurlebFormatted(), 13);
            createCell(row, data.getIuranTambahanKurlebFormatted(), 14);
            createCell(row, data.getManfaatLain(), 15);
            createCell(row, data.getKeterangan(), 16);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPdin(List<OjkPdin> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian PDIN");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Pendapatan Investasi Lain", "Jumlah", "Program Pensiun/Manfaat Lain",
                "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPdin data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPendapatan(), 0);
            createCell(row, data.getJumlahFormatted(), 1);
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPdpl(List<OjkPdpl> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian PDPL");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Nama Dana Pensiun yang Mengalihkan", "Jumlah", "Rincian Manfaat Lain",
                "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPdpl data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getNamaDapen(), 0);
            createCell(row, data.getJumlahFormatted(), 1);
            createCell(row, "", 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkBinv(List<OjkBinv> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Beban Investasi (BINV)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Beban", "Jumlah", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkBinv data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisBeban(), 0);
            createCell(row, data.getJumlahFormatted(), 1);
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkBopr(List<OjkBopr> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Beban Operasional (BOPR)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Beban", "Jumlah", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkBopr data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisBeban(), 0);
            createCell(row, data.getJumlahFormatted(), 1);
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkBipr(List<OjkBipr> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian BIPR");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Beban", "Jumlah", "Program Pensiun/Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkBipr data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisBeban(), 0);
            createCell(row, data.getJumlahFormatted(), 1);
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPihi(List<OjkPihi> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Piutang Hasil Investasi (PIHI)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Piutang Hasil Investasi", "Nominal", "Program Pensiun / Manfaat Lain",
                "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPihi data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPiutang(), 0);
            createCell(row, data.getNominal(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPill(List<OjkPill> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Piutang Lain-lain (PILL)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Piutang Lain-lain", "Nominal", "Program Pensiun / Manfaat Lain",
                "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPill data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPiutang(), 0);
            createCell(row, data.getNominal(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPddm(List<OjkPddm> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Pendapatan Diterima Di Muka (PDDM)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Rincian Pendapatan Diterima Di Muka", "Pihak", "Nilai",
                "Program Pensiun / Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPddm data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, "", 0);
            createCell(row, data.getPihak(), 1);
            createCell(row, data.getNilaiFormatted(), 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkBmhb(List<OjkBmhb> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Beban Yang Masih Harus Dibayar (BMHB)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Rincian Beban Yang Harus Dibayar", "Pihak", "Nilai",
                "Program Pensiun / Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkBmhb data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getRincianBeban(), 0);
            createCell(row, data.getPihak(), 1);
            createCell(row, data.getNilaiFormatted(), 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkUtln(List<OjkUtln> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Utang Lain (UTLN)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Utang Lain", "Pihak", "Nilai", "Program Pensiun / Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkUtln data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisUtang(), 0);
            createCell(row, data.getPihak(), 1);
            createCell(row, data.getNilaiFormatted(), 2);
            createCell(row, data.getManfaatLain(), 3);
            createCell(row, data.getKeterangan(), 4);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    public void writeOjkPph(List<OjkPph> dataList, String excelFilePath) throws IOException {
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(excelFilePath);
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(fis));
        workbook.setCompressTempFiles(true);
        fis.close();

        SXSSFSheet sheet = workbook.createSheet("Rincian Pajak Penghasilan (PPH)");
        sheet.setRandomAccessWindowSize(100);

        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"Jenis Pajak Penghasilan", "Jumlah", "Program Pensiun / Manfaat Lain", "Keterangan"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (OjkPph data : dataList) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, data.getJenisPajak(), 0);
            createCell(row, data.getJumlah(), 1, workbook.createCellStyle(), workbook.createDataFormat());
            createCell(row, data.getManfaatLain(), 2);
            createCell(row, data.getKeterangan(), 3);
        }

        FileOutputStream fileOut = new FileOutputStream(excelFilePath);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    private void createCell(Row row, String val, int col) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val);
        cell.setCellType(CellType.STRING);
    }

    private void createCell(Row row, Integer value, int col) {
        if (value == null) {
            createCell(row, "", col);
        } else {
            Cell cell = row.createCell(col);
            cell.setCellValue(value);
            cell.setCellType(CellType.NUMERIC);
        }
    }

    private void createCell(Row row, Long value, int col) {
        if (value == null) {
            createCell(row, "", col);
        } else {
            Cell cell = row.createCell(col);
            cell.setCellValue(value);
            cell.setCellType(CellType.NUMERIC);
        }
    }

    private void createCell(Row row, Float value, int col) {
        if (value == null) {
            createCell(row, "", col);
        } else {
            Cell cell = row.createCell(col);
            cell.setCellValue(value);
            cell.setCellType(CellType.NUMERIC);
        }
    }

    private void createCell(Row row, BigDecimal value, int col, CellStyle style, DataFormat format) {
        style.setDataFormat(format.getFormat("#,##0.00"));
        if (value == null) {
            createCell(row, "", col);
        } else {
            Cell cell = row.createCell(col);
            cell.setCellValue(value.doubleValue());
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(style);
        }
    }

    private void createCell(Row row, Date value, int col) {
        if (value == null) {
            createCell(row, "", col);
        } else {
            String date = DateUtil.format(value, "dd-MM-yyyy");
            createCell(row, date, col);
        }
    }
}
