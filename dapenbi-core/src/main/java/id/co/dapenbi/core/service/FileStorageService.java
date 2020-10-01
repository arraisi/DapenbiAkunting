package id.co.dapenbi.core.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import id.co.dapenbi.core.exception.FileStorageException;
import id.co.dapenbi.core.property.FileStorageProperties;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }
    }

    public String storeFile(MultipartFile file, String kodeRekening, String tahunAnggaran) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (kodeRekening != null) {
            fileName = new StringBuilder()
                    .append(kodeRekening)
                    .append("-")
                    .append(tahunAnggaran)
                    .append(".xlsx").toString();
        }

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.delete(targetLocation);
            Files.write(targetLocation, file.getBytes(), StandardOpenOption.CREATE_NEW);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Create New Excel SATKER
     */
    public String createExcelMataAnggaranSatker(String kodeRekening, String tahunAnggaran, String idSatker) throws Exception {
        String fileName = new StringBuilder("satker-")
                .append(kodeRekening)
                .append("-")
                .append(tahunAnggaran)
                .append("-")
                .append(idSatker)
                .append(".xlsx").toString();

        File file = new File(this.fileStorageLocation.resolve(fileName).toString());
        if (!file.exists() && !kodeRekening.isEmpty()) {
            // Directory path where the xls file will be created
            String destinationFilePath = this.fileStorageLocation.resolve(fileName).toString();
            logger.info("DESTINATION FILE PATH {}", destinationFilePath);

            // Create object of FileOutputStream
            FileOutputStream fout = new FileOutputStream(destinationFilePath);

            // Build the Excel File
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Workbook workBook = new XSSFWorkbook();


            // Create the spreadsheet
            Sheet sheet = workBook.createSheet("Sheet 1");

            // Create the first row
            Row row = sheet.createRow(0);

            // Create the cells and write to the file
            Cell cell = row.createCell(0);

            // Write Hello
//            cell = row.createCell(0);
//            cell.setCellValue(("Testing"));

            // Write World
//            cell = row.createCell(1);
//            cell.setCellValue(("Excel"));

            workBook.write(outputStream);

            outputStream.writeTo(fout);
            outputStream.close();
            fout.close();
            return destinationFilePath;
        } else {
            return this.fileStorageLocation.resolve(fileName).toString();
        }
    }

    /**
     * Create Excel New
     */
    public String createExcelMataAnggaran(String kodeRekening, String tahunAnggaran, String version) throws Exception {
        String fileName = new StringBuilder()
                .append(kodeRekening)
                .append("-")
                .append(tahunAnggaran)
                .append("-")
                .append(version)
                .append(".xlsx").toString();

        File file = new File(this.fileStorageLocation.resolve(fileName).toString());
        if (!file.exists() && !kodeRekening.isEmpty()) {
            // Directory path where the xls file will be created
            String destinationFilePath = this.fileStorageLocation.resolve(fileName).toString();
            logger.info("DESTINATION FILE PATH {}", destinationFilePath);

            // Create object of FileOutputStream
            FileOutputStream fout = new FileOutputStream(destinationFilePath);

            // Build the Excel File
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Workbook workBook = new XSSFWorkbook();


            // Create the spreadsheet
            Sheet sheet = workBook.createSheet("Sheet 1");

            // Create the first row
            Row row = sheet.createRow(0);

            // Create the cells and write to the file
            Cell cell = row.createCell(0);

            // Write Hello
//            cell = row.createCell(0);
//            cell.setCellValue(("Testing"));

            // Write World
//            cell = row.createCell(1);
//            cell.setCellValue(("Excel"));

            workBook.write(outputStream);

            outputStream.writeTo(fout);
            outputStream.close();
            fout.close();
            return destinationFilePath;
        } else {
            return this.fileStorageLocation.resolve(fileName).toString();
        }
    }

    public String fileStoragePath() {
        return fileStorageLocation.toString();
    }
}
