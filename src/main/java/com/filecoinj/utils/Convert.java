package com.filecoinj.utils;

import java.math.BigDecimal;

/**
 * 单位转换
 *
 */
public class Convert {

    public static BigDecimal fromAtto(String number, Convert.Unit unit) {
        return fromAtto(new BigDecimal(number), unit);
    }

    public static BigDecimal fromAtto(BigDecimal number, Convert.Unit unit) {
        return number.divide(unit.getAttoFactor());
    }

    public static BigDecimal toAtto(String number, Convert.Unit unit) {
        return toAtto(new BigDecimal(number), unit);
    }

    public static BigDecimal toAtto(BigDecimal number, Convert.Unit unit) {
        return number.multiply(unit.getAttoFactor());
    }

    public static enum Unit {
        ATTO("atto", 0),
        FIL("fil", 18),
        ;

        private String name;
        private BigDecimal attoFactor;

        private Unit(String name, int factor) {
            this.name = name;
            this.attoFactor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getAttoFactor() {
            return attoFactor;
        }

        public String toString() {
            return this.name;
        }

        public static Convert.Unit fromString(String name) {
            if (name != null) {
                Convert.Unit[] var1 = values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    Convert.Unit unit = var1[var3];
                    if (name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }

            return valueOf(name);
        }
    }

}
