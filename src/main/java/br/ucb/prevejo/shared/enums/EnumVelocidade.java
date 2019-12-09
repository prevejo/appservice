package br.ucb.prevejo.shared.enums;

import br.ucb.prevejo.shared.util.StringUtil;

public enum EnumVelocidade {
    METROS_POR_SEG("m/s"),
    KM_POR_HORA("km/h");

    private String abr;

    private EnumVelocidade(String abr) {
        this.abr = abr;
    }

    public String toStringAbr() {
        return abr;
    }

    public static EnumVelocidade valueByAbr(String abr) {
        if (StringUtil.isEmpty(abr)) {
            return null;
        }

        EnumVelocidade value = null;
        EnumVelocidade[] values = values();

        for (EnumVelocidade v : values()) {
            if (abr.equalsIgnoreCase(v.abr)) {
                value = v;
            }
        }

        return value;
    }

}
