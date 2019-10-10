package com.garanti.test.customerproject.constants;

public enum FileType {
    CUSTOMER("MUSTERI.TXT"),
    PHONE("TELEFON.TXT"),
    MAIL("MAIL.TXT");

    private String file;

    FileType(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return this.file;
    }
}
