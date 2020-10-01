package id.co.dapenbi.accounting.entity.laporan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_LAPORAN_HDR")
public class LaporanHeader {

    @Id
    @SequenceGenerator(sequenceName = "ACC_LAPORAN_HDR_SEQ", allocationSize = 1, name = "laporanHdrGenerator")
    @GeneratedValue(generator = "laporanHdrGenerator")
    @Column(name = "ID_LAPORAN_HDR")
    private Integer idLaporanHdr;

    @Column(name = "NAMA_LAPORAN")
    private String namaLaporan;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "NAMA_TABEL")
    private String namaTabel;

    @Column(name = "URUTAN")
    private Integer urutan;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @JsonIgnore
    public LaporanHeader(Integer idLaporanHdr, String namaLaporan, String keterangan) {
        this.setIdLaporanHdr(idLaporanHdr);
        this.setNamaLaporan(namaLaporan);
        this.setKeterangan(keterangan);
    }
}
