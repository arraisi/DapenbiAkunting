package id.co.dapenbi.accounting.entity.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ACC_THNBUKU")
public class TahunBuku {

    @Id
    @Column(name = "KODE_THNBUKU")
    private String kodeTahunBuku;

    @Column(name = "NAMA_THNBUKU")
    private String namaTahunBuku;

    @Column(name = "TAHUN")
    private String tahun;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    public TahunBuku(String kodeTahunBuku) {
        this.kodeTahunBuku = kodeTahunBuku;
    }
}
