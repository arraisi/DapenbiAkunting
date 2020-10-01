package id.co.dapenbi.accounting.enums;

public enum NamaLaporan {
    LAN("LAN", "Laporan Aset Neto (LAN)"),
    LPAN("LPAN", "Laporan Perubahan Aset Neto (LPAN)"),
    NRC("NRC", "Laporan Neraca (NRC)"),
    LPHU("LPHU", "Laporan Perhitungan Hasil Usaha (LPHU)"),
    LAK("LAK", "Laporan Arus Kas (LAK)"),
    PST("PST", "Laporan Kepesertaan (PST)"),
    KUP("KUP", "Laporan Kekayaan Untuk Pendanaan (KUP)"),
    KUP1("KUP1", "Laporan Kekayaan Untuk Pendanaan (KUP1)"),
    KUP2("KUP2", "Laporan Kekayaan Untuk Pendanaan (KUP2)"),
    REKINV("REKINV", "Rekap Investasi (REKINV)"),
    ROI("ROI", "Laporan Hasil Investasi Return on Investment (ROI)"),
    ROIML("ROIML", "Laporan Hasil Investasi Manfaat Lain (ROIML)"),
    ALM("ALM", "Laporan Aset dan Liabilities (ALM)"),
    INSP("INSP", "Investasi Pada Satu Pihak (INSP)"),
    SBN("SBN", "Rincian Pemenuhan Ketentuan Mengenai Investasi SBN (SBN)"),
    RASIO("RASIO", "Rasio Keuangan (Rasio)"),
    PMI("PMI", "Pengungkapan Manajer Investasi (PMI)"),
    TAB("TAB", "Rincian Tabungan (TAB)"),
    DOC("DOC", "Rincian Deposit on Call (DOC)"),
    DPJKA("DPJKA", "Rincian Deposito Berjangka (DPJKA)"),
    SRDP("SRDP", "Laporan Sertifikat Rincian Deposito (SRDP)"),
    SBI("SBI", "Laporan Surat Berharga Indonesia (SBI)"),
    RSBN("RSBN", "Laporan Rincian Surat Berharga Negera (RSBN)"),
    SHM("SHM", "Laporan Rincian Saham (SHM)"),
    OBLI("OBLI", "Laporan Rincian Obligasi (OBLI)"),
    SUKUK("SUKUK", "Laporan Rincian Sukuk (SUKUK)"),
    OBSUD("OBSUD", "Laporan Rincian Obligasi/Suku Daerah (OBSUD)"),
    DIRE("DIRE", "Rincian Dana Investasi Real Estate Berbentuk Kontrak Investasi Kolektif (DIRE)"),
    DNFRA("DNFRA", "Rincian Dana Investasi Infrastruktur Berbentuk Kontrak Investasi Kolektif (DNFRA)"),
    KOKB("KOKB", "Rincian Kontrak Opsi dan Kontrak Berjangka Efek Yang Tercatat di Bursa Efek Indonesia (KOKB)"),
    REPO("REPO", "Rincian REPO (REPO)"),
    PNYL("PNYL", "Rincian Penyertaan Langsung (PNYL)"),
    PROP("PROP", "Rincian Tanah, Bangunan, Tanah dan Bangunan di Indonesia (PROP)"),
    KASB("KASB", "Rincian Kas dan Bank (KASB)"),
    PIUT("PIUT", "Rincian Piutang Iuran (PIUT)"),
    PIUB("PIUB", "Rincian Piutang Bunga Keterlambatan Iuran (PIUB)"),
    BBMK("BBMK", "Rincian Beban Dibayar Dimuka (BBMK)"),
    RKSD("RKSD", "Laporan Rincian Reksadana (RKSD)"),
    MTN("MTN", "Laporan Rincian MTN (MTN)"),
    PIUI("PIUI", "Rincian Piutang Investasi (PIUI)"),
    PIHI("PIHI", "Rincian Piutang Hasil Investasi (PIHI)"),
    PILL("PILL", "Rincian Piutang Lain-lain (PILL)"),
    TNBG("TNBG", "Rincian Tanah dan Bangunan (TNBG)"),
    KNDR("KNDR", "Rincian Kendaraan (KNDR)"),
    PKOM("PKOM", "Rincian Peralatan Komputer (PKOM)"),
    PKAN("PKAN", "Rincian Peralatan Kantor (PKAN)"),
    ASOL("ASOL", "Rincian Aset Operasional Lainnya (ASOL)"),
    ASLN("ASLN", "Rincian Aset Lain (ASLN)"),
    UMPJ("UMPJ", "Rincian Utang Manfaat Pensiun Jatuh Tempo (UMPJ)"),
    UMPS("UMPS", "Rincian Utang Manfaat Pensiun Sukarela Jatuh Tempo (UMPS)"),
    UTIN("UTIN", "Rincian Utang Investasi (UTIN)"),
    PDDM("PDDM", "Rincian Pendapatan Diterima Di Muka (PDDM)"),
    BMHB("BMHB", "Rincian Beban Yang Masih Harus Dibayar (BMHB)"),
    UTLN("UTLN", "Rincian Utang Lain (UTLN)"),
    PPIN("PPIN", "Rincian Peningkatan/Penurunan Investasi (PPIN)"),
    IUJT("IUJT", "Rincian Iuran Jatuh Tempo (IUJT)"),
    PDIN("PDIN", "Rincian Pendapatan Diluar Investasi (PDIN)"),
    PDPL("PDPL", "Rincian Pengalihan dari Dana Pensiun Lain (PDPL)"),
    BINV("BINV", "Rincian Beban Investasi (BINV)"),
    BOPR("BOPR", "Rincian Beban Operasional (BOPR)"),
    BIPR("BIPR", "Rincian Beban Diluar Investasi dan Operasional (BIPR)"),
    PPH("PPH", "Rincian Pajak Penghasilan (PPH)"),
    PKPL("PKPL", "Rincian Pengalihan Ke Dana Pensiun Lain (PKPL)");

    private final String value;
    private final String text;

    NamaLaporan(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
