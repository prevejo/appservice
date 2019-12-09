package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.core.ContextProvider;

public enum EnumAreaEntity {

    PARADA {
        @Override
        public AreaEntity buildArea(String data) {
            return ContextProvider.getBean(AreaEntityBuilder.class).buildForParada(data);
        }
    },
    GROUP {
        @Override
        public AreaEntity buildArea(String data) {
            return ContextProvider.getBean(AreaEntityBuilder.class).buildForParadaGroup(data);
        }
    },
    LOCATION {
        @Override
        public AreaEntity buildArea(String data) {
            return ContextProvider.getBean(AreaEntityBuilder.class).buildForLocation(data);
        }
    };

    public abstract AreaEntity buildArea(String data);

}
