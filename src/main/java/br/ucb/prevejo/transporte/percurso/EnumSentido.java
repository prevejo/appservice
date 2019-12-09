package br.ucb.prevejo.transporte.percurso;

public enum EnumSentido {
    IDA,
    VOLTA,
    CIRCULAR;

    public static EnumSentido valueByNumeral(String integer) {
        if ("0".equals(integer)) {
            return IDA;
        }

        if ("1".equals(integer)) {
            return VOLTA;
        }

        return null;
    }

}
