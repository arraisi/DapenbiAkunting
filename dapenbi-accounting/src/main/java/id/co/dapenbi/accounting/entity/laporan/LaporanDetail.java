package id.co.dapenbi.accounting.entity.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_LAPORAN_DTL")
public class LaporanDetail {

    @Id
    @SequenceGenerator(sequenceName = "ACC_LAPORAN_DTL_SEQ", allocationSize = 1, name = "laporanDtlGenerator")
    @GeneratedValue(generator = "laporanDtlGenerator")
    @Column(name = "ID_LAPORAN_DTL")
    private Integer idLaporanDtl;

    //    @ManyToOne
    @Column(name = "ID_LAPORAN_HDR")
    private Integer laporanHeader;

    @Column(name = "JUDUL")
    private String judul;

    @Column(name = "ID_REKENING")
    private Long idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "LEVEL_AKUN")
    private Integer levelAkun;

    @Column(name = "NAMA_TABLE")
    private String namaTable;

    @Column(name = "NAMA_KOLOM")
    private String namaKolom;

    @Column(name = "TAMPILKAN_NILAI")
    private Boolean tampilNilai;

    @Column(name = "CETAK_TEBAL")
    private String cetakTebal;

    @Column(name = "CETAK_MIRING")
    private String cetakMiring;

    @Column(name = "CETAK_GARIS")
    private String cetakGaris;

    @Column(name = "WARNA")
    private String warna;

    @Column(name = "SPI")
    private String spi;

    @Column(name = "PAJAK")
    private Boolean pajak;

    @Column(name = "CETAK_JUDUL")
    private String cetakJudul;

    @Column(name = "RUMUS")
    private String rumus;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "URUTAN")
    private String urutan;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "KODE_RUMUS")
    private String kodeRumus;

    @Column(name = "SALDO_SEBELUM")
    private BigDecimal saldoSebelum;

    @Column(name = "STATUS_RUMUS")
    private String statusRumus;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}
