package pl.kosim.spis.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public enum Media {
    GAZ,
    WODA_ZIMNA,
    WODA_CIEPLA,
    ENERGIA,
    CIEPLO,
    ZAOK;

    private DecimalFormat df = new DecimalFormat();

    static {
        GAZ.df.setMinimumFractionDigits(3);
        GAZ.df.setMaximumFractionDigits(3);
        GAZ.df.setGroupingUsed(false);
        CIEPLO.df.setMinimumFractionDigits(3);
        CIEPLO.df.setMaximumFractionDigits(3);
        CIEPLO.df.setGroupingUsed(false);
        ENERGIA.df.setMinimumFractionDigits(0);
        ENERGIA.df.setMaximumFractionDigits(0);
        ENERGIA.df.setGroupingUsed(false);
        WODA_CIEPLA.df.setMinimumFractionDigits(0);
        WODA_CIEPLA.df.setMaximumFractionDigits(0);
        WODA_CIEPLA.df.setGroupingUsed(false);
        WODA_ZIMNA.df.setMinimumFractionDigits(1);
        WODA_ZIMNA.df.setMaximumFractionDigits(1);
        WODA_ZIMNA.df.setGroupingUsed(false);
        /* ZAOK jest uzywany na stronie podsumowania spisu */
        ZAOK.df.setMinimumFractionDigits(0);
        ZAOK.df.setMaximumFractionDigits(0);
    }

    public String format( BigDecimal bd ) {
        return df.format( bd );
    }
}
