select p.*
from transporte.tb_parada p
    join (select GeomFromEWKT(concat('SRID=4326;POINT(', ?, ' ', ?, ')')) as geo) point on true
where exists (select perc.id
		from transporte.tb_percurso perc
			join transporte.tb_percurso_parada pp on pp.id_percurso = perc.id
		where pp.id_parada = p.id
			and exists (select pp2.id_parada
					from transporte.tb_percurso_parada pp2
					join transporte.tb_area_parada ap on ap.id_parada = pp2.id_parada
					where pp2.id_percurso = perc.id))
order by ST_Distance_Sphere(point.geo, p.geo)
limit ?