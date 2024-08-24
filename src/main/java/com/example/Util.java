package com.example;

import javafx.util.StringConverter;
import java.text.NumberFormat;
import java.util.Locale;

public class Util {
    public static StringConverter<Double> getCurrencyConverter() {
        return new StringConverter<Double>() {
            private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

            {
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
            }

            @Override
            public String toString(Double object) {
                return object == null ? "" : numberFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return string == null || string.isEmpty() ? null : numberFormat.parse(string).doubleValue();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}