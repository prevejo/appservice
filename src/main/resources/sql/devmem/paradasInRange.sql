select p.*
from transporte.tb_parada p
	join (select ST_GeomFromText(concat('POINT(', ?, ' ', ?, ')'), 4326) as geo) point on true
where ST_DistanceSphere(p.geo, point.geo) <= ?
order by ST_DistanceSphere(p.geo, point.geo)