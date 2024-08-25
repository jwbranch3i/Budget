package com.example;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
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
    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> getRightAlignedCellFactory(StringConverter<Double> converter) {
        return column -> {
            TableCell<T, Double> cell = new TextFieldTableCell<>(converter);
            cell.setStyle("-fx-alignment: CENTER-RIGHT;");
            return cell;
        };
    }
}