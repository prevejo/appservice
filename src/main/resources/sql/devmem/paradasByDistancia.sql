select p.*
from transporte.tb_parada p
    join (select ST_GeomFromText(concat('POINT(', ?, ' ', ?, ')'), 4326) as geo) point on true
where exists (select perc.id
		from transporte.tb_percurso perc
			join transporte.tb_percurso_parada pp on pp.id_percurso = perc.id
		where pp.id_parada = p.id
			and exists (select pp2.id_parada
					from transporte.tb_percurso_parada pp2
					join transporte.tb_area_parada ap on ap.id_parada = pp2.id_parada
					where pp2.id_percurso = perc.id))
order by ST_DistanceSphere(point.geo, p.geo)
limit ?