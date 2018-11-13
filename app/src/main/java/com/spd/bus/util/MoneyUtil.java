package com.spd.bus.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * 金额处理类
 */
public class MoneyUtil {

    private static DecimalFormat doubleDF = new DecimalFormat("#0.00");
    private static DecimalFormatSymbols dfs = new DecimalFormatSymbols();

    public static String formatDouble2Str4Money(double d) {
        /*
        Resolve system languages, such as Germany, France, the Netherlands, Portugal, and so on,
        where decimal points formatted with decimal format become commas
         */
        dfs.setDecimalSeparator('.');
        doubleDF.setDecimalFormatSymbols(dfs);
        return doubleDF.format(d);
    }

    public static String fen2yuan(long fen) {
        return formatDouble2Str4Money(fen / 100.00);
    }

    public static Double fenTrans2Yuan(Long fen) {
        return Double.parseDouble(fen2yuan(fen));
    }
    public static String toCent(String dollar) {
        String cent = "";
        long cents = 0;

        if (TextUtils.isEmpty(dollar)) {
            cents = 0;
        } else {
            int index = dollar.indexOf(".");
            if (index >= 0) {
                int gap = dollar.length() - index - 1;
                if (gap == 0) {
                    cent = dollar + "00";
                } else if (gap == 1) {
                    cent = dollar.replace(".", "") + "0";
                } else if (gap == 2) {
                    cent = dollar.replace(".", "");
                } else {
                    cent = dollar.substring(0, index + 3).replace(".", "");
                }
            } else {
                cent = dollar + "00";
            }
            cents = NumberUtil.parseLong(cent);
        }

        return String.format(Locale.US, "%012d", cents);
    }
    /**
     * 将元为单位的转换为分 （乘100）
     *
     * @param amount
     * @return
     */
    public static long yuan2fen(double amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).longValue();
    }

}

