
CREATE SCHEMA transporte;

CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();

CREATE SEQUENCE transporte.seq_tb_linha;
CREATE SEQUENCE transporte.seq_tb_percurso;
CREATE SEQUENCE transporte.seq_tb_operador;
CREATE SEQUENCE transporte.seq_tb_operacao;
CREATE SEQUENCE transporte.seq_tb_horario;
CREATE SEQUENCE transporte.seq_tb_parada;
CREATE SEQUENCE transporte.seq_tb_area_integracao;
CREATE SEQUENCE transporte.seq_tb_terminal;


create table transporte.tb_linha (
	id integer NOT NULL DEFAULT transporte.seq_tb_linha.nextval,
	numero character varying(5) NOT NULL,
	descricao character varying(255) NOT NULL,
	tarifa numeric(5,2) NOT NULL,

	CONSTRAINT pk_tb_linha PRIMARY KEY(id)
);


create table transporte.tb_percurso (
	id integer NOT NULL DEFAULT transporte.seq_tb_percurso.nextval,
	id_linha integer NOT NULL,
	sentido character varying(8) NOT NULL,
	origem character varying(255) NOT NULL,
	destino character varying(255) NOT NULL,
	geo geometry(LineString,4326) NOT NULL,

	CONSTRAINT pk_tb_percurso PRIMARY KEY(id),
	CONSTRAINT fk_tb_percurso_to_tb_linha FOREIGN KEY(id_linha) REFERENCES transporte.tb_linha(id)
);


create table transporte.tb_operador (
	id integer NOT NULL DEFAULT transporte.seq_tb_operador.nextval,
	descricao character varying(255) NOT NULL,

	CONSTRAINT pk_tb_operador PRIMARY KEY(id)
);


create table transporte.tb_operacao (
	id integer NOT NULL DEFAULT transporte.seq_tb_operacao.nextval,
	id_operador integer NOT NULL,
	id_percurso integer NOT NULL,
	segunda boolean NOT NULL,
	terca boolean NOT NULL,
	quarta boolean NOT NULL,
	quinta boolean NOT NULL,
	sexta boolean NOT NULL,
	sabado boolean NOT NULL,
	domingo boolean NOT NULL,

	CONSTRAINT pk_tb_operacao PRIMARY KEY(id),
	CONSTRAINT fk_tb_operacao_to_tb_operador FOREIGN KEY(id_operador) REFERENCES transporte.tb_operador(id),
	CONSTRAINT fk_tb_operacao_to_tb_percurso FOREIGN KEY(id_percurso) REFERENCES transporte.tb_percurso(id)
);


create table transporte.tb_horario (
	id integer NOT NULL DEFAULT transporte.seq_tb_horario.nextval,
	id_operacao integer NOT NULL,
	horario time NOT NULL,

	CONSTRAINT pk_tb_horario PRIMARY KEY(id),
	CONSTRAINT fk_tb_horario_to_tb_operacao FOREIGN KEY(id_operacao) REFERENCES transporte.tb_operacao(id)
);


create table transporte.tb_parada (
	id integer NOT NULL DEFAULT transporte.seq_tb_parada.nextval,
	cod character varying(5) NOT NULL,
	geo geometry(Point,4326) NOT NULL,
	geo_via geometry(Point,4326) NOT NULL,

	CONSTRAINT pk_tb_parada PRIMARY KEY(id)
);


create table transporte.tb_percurso_parada (
	id_percurso integer NOT NULL,
	id_parada integer NOT NULL,
	sequencial integer NOT NULL,

	CONSTRAINT pk_tb_percurso_parada PRIMARY KEY(id_percurso, id_parada, sequencial),
	CONSTRAINT fk_tb_percurso_parada_to_tb_percurso FOREIGN KEY(id_percurso) REFERENCES transporte.tb_percurso(id),
	CONSTRAINT fk_tb_percurso_parada_to_tb_parada FOREIGN KEY(id_parada) REFERENCES transporte.tb_parada(id)
);


create table transporte.tb_area_integracao (
	id integer NOT NULL DEFAULT transporte.seq_tb_area_integracao.nextval,
	descricao character varying(255) NOT NULL,
	geo geometry(MultiPolygon,4326) NOT NULL,

	CONSTRAINT pk_tb_area_integracao PRIMARY KEY(id)
);


create table transporte.tb_area_parada (
	id_area_integracao integer NOT NULL,
	id_parada integer NOT NULL,

	CONSTRAINT pk_tb_area_parada PRIMARY KEY(id_area_integracao, id_parada),
	CONSTRAINT fk_tb_area_parada_to_tb_area_integracao FOREIGN KEY(id_area_integracao) REFERENCES transporte.tb_area_integracao(id),
	CONSTRAINT fk_tb_area_parada_to_tb_parada FOREIGN KEY(id_parada) REFERENCES transporte.tb_parada(id)
);


create table transporte.tb_terminal (
	id integer NOT NULL DEFAULT transporte.seq_tb_terminal.nextval,
	id_parada integer NOT NULL,
	descricao character varying(255) NOT NULL,
	cod character varying(5) NOT NULL,
	geo geometry(Polygon,4326) NOT NULL,

	CONSTRAINT pk_tb_terminal PRIMARY KEY(id),
	CONSTRAINT fk_tb_terminal_to_tb_parada FOREIGN KEY(id_parada) REFERENCES transporte.tb_parada(id)
);


create table transporte.tb_localizacao_veiculo (
    num_veiculo character varying(8) NOT NULL,
    num_linha character varying(5),
    ds_sentido character varying(10),
    ds_operadora character varying(20) NOT NULL,
    num_velocidade numeric(5, 2),
    ds_velocidade character varying(4),
    num_direcao numeric(5, 2),
    dt_localizacao timestamp without time zone NOT NULL,
    geo geometry(Point,4326) NOT NULL
);



CREATE SCHEMA comunidade;

CREATE SEQUENCE comunidade.seq_tb_informativo;
CREATE SEQUENCE comunidade.seq_tb_topico;
CREATE SEQUENCE comunidade.seq_tb_comentario;


create table comunidade.tb_informativo (
	id integer NOT NULL DEFAULT comunidade.seq_tb_informativo.nextval,
	titulo character varying(255) NOT NULL,
	resumo character varying(4000) NOT NULL,
	dt_publicacao timestamp NOT NULL,
	endereco character varying(255) NOT NULL,

	CONSTRAINT pk_tb_informativo PRIMARY KEY(id)
);


create table comunidade.tb_topico (
	id integer NOT NULL DEFAULT comunidade.seq_tb_topico.nextval,
	titulo character varying(255) NOT NULL,

	CONSTRAINT pk_tb_topico PRIMARY KEY(id)
);


create table comunidade.tb_comentario (
	id integer NOT NULL DEFAULT comunidade.seq_tb_comentario.nextval,
	id_topico integer NOT NULL,
	assunto character varying(255) NOT NULL,
	comentario character varying(4000) NOT NULL,
	relevancia integer NOT NULL,
	dt_publicacao timestamp NOT NULL,

	CONSTRAINT pk_tb_comentario PRIMARY KEY(id),
	CONSTRAINT fk_tb_comentario_to_tb_topico FOREIGN KEY(id_topico) REFERENCES comunidade.tb_topico(id)
);