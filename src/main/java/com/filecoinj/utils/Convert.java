package com.filecoinj.utils;

import java.math.BigDecimal;

/**
 * 单位转换
 * 注意：以下Default所指为filcoin默认单位
 */
public class Convert {

    public static BigDecimal fromDefault(String number, Convert.Unit unit) {
        return fromDefault(new BigDecimal(number), unit);
    }

    public static BigDecimal fromDefault(BigDecimal number, Convert.Unit unit) {
        return number.divide(unit.getWeiFactor());
    }

    public static BigDecimal toDefault(String number, Convert.Unit unit) {
        return toDefault(new BigDecimal(number), unit);
    }

    public static BigDecimal toDefault(BigDecimal number, Convert.Unit unit) {
        return number.multiply(unit.getWeiFactor());
    }

    public static enum Unit {
        DEFAULT("default", 0),
        ETHER("fil", 18),
        ;

        private String name;
        private BigDecimal defaultFactor;

        private Unit(String name, int factor) {
            this.name = name;
            this.defaultFactor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getWeiFactor() {
            return this.defaultFactor;
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
