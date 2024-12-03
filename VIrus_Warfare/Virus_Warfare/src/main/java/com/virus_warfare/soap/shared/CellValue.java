package com.virus_warfare.soap.shared;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CellValue")
@XmlEnum
public enum CellValue {
    @XmlEnumValue("X")
    X,
    @XmlEnumValue("O")
    O,
    @XmlEnumValue("ZX")
    ZX,
    @XmlEnumValue("ZO")
    ZO,
    @XmlEnumValue("EMPTY")
    EMPTY;

    public String value() {
        return name();
    }

    public static CellValue fromValue(String v) {
        return valueOf(v);
    }
}
