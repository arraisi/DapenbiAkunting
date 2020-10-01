package id.co.dapenbi.accounting.entity.parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_JNS_TRANSAKSI")
@ToString(exclude = {"transaksiJurnals"})
public class Transaksi {

    @Id
    @Column(name = "KODE_TRANSAKSI")
    private String kodeTransaksi;

    @Column(name = "NAMA_TRANSAKSI")
    private String namaTransaksi;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @Column(name = "JENIS_WARKAT")
    private String jenisWarkat;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @JsonIgnore
    @OneToMany(mappedBy = "kodeTransaksi")
    private List<TransaksiJurnal> transaksiJurnals = new ArrayList<>();
}
