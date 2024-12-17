package Lab3_VarB10;

import javax.swing.table.AbstractTableModel;
@SuppressWarnings("serial")
public class GornerTableModel extends AbstractTableModel {
    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;
    public GornerTableModel(Double from, Double to, Double step,
                            Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
    }
    public Double getFrom() {
        return from;
    }
    public Double getTo() {
        return to;
    }
    public Double getStep() {
        return step;
    }
    public int getColumnCount() {
        return 3;
    }
    public int getRowCount() {
// Вычислить количество точек между началом и концом отрезка
// исходя из шага табулирования
        return Double.valueOf(Math.ceil((to-from)/step)).intValue()+1;
    }
    public Object getValueAt(int row, int col) {
        double x = from + step*row;
        switch (col) {
            case 0:
                return x;
            case 1:
                Double result = 0.0;
                for (int i = coefficients.length - 1; i >= 0; i--) {
                    result = result * x + coefficients[i];
                }
                return result;
            case 2:
                Double resultForLimitedSymmetry = 0.0;
                for (int i = coefficients.length - 1; i >= 0; i--) {
                    resultForLimitedSymmetry = resultForLimitedSymmetry * x + coefficients[i];
                }
                return LimitedSymmetry(resultForLimitedSymmetry);
            default:
                return null;
        }
    }

    private boolean LimitedSymmetry(Double value) {
        String strValue = String.valueOf(value);
        int dotIndex = strValue.indexOf('.');

        String beforeDot = strValue.substring(0, dotIndex);
        String afterDot = strValue.substring(dotIndex + 1);

        if (beforeDot.isEmpty() || afterDot.isEmpty()) {
            return false;
        }
        if(afterDot.equals("0"))
        {
            return false;
        }

        return beforeDot.charAt(beforeDot.length() - 1) == afterDot.charAt(0);

    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Значение X";
            case 1:
                return "Значение многочлена";
            case 2:
                return "Ограниченная симметрия";
            default:
                return "";
        }
    }
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
            case 1:
                return Double.class;
            case 2:
                return Boolean.class;
            default:
                return Object.class;
        }
    }
}