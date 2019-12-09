select p.*
from transporte.tb_parada p
	join (select GeomFromEWKT(concat('SRID=4326;POINT(', ?, ' ', ?, ')')) as geo) point on true
where ST_Distance_Sphere(p.geo, point.geo) <= ?
order by ST_Distance_Sphere(p.geo, point.geo)