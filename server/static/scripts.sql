create view v_mobile_salesman as
select a.EmpId as SalID, a.EmpName as Salname, b.AreaNo, b.AreaName
from IntacsDataUpgrade.dbo.Employee a
inner join IntacsDataUpgrade.dbo.Area b on a.EmpId = b.SalId
where a.PSales =  1
and a.NotActive = 0

create view v_mobile_customer as
select a.ShipId, a.[Ship Name], a.[Ship Address], a.[Ship City], a.[Ship Phone], a.[Ship HP],
b.PartnerId, b.PartnerName,
a.PriceLevel, a.IsActive, c.AreaNo, c.AreaName, b.NPSN, e.CClass2Name as Jenjang
from IntacsDataUpgrade.dbo.CustomerDelivery a
inner join IntacsDataUpgrade.dbo.[Partner] b on a.CustomerId = b.PartnerId
inner join IntacsDataUpgrade.dbo.Area c on b.areano = c.AreaNo
left join IntacsDataUpgrade.dbo.PartnerClass d on d.PartnerId = b.PartnerId
left join IntacsDataUpgrade.dbo.CClass2 e on e.CClass2 = d.cclass2



create function fn_mobile_pricelevel(
	@areano varchar(30)
)returns @mytable table(
	InvID int,
	Partno varchar(30),
	PriceLevel int,
	PriceLevelName varchar(200),
	Price Float
)
as
begin
	insert into @mytable
	select distinct a.InvId, a.partno, c.PriceLevel, PriceLevelName, b.Price
	from IntacsDataUpgrade.dbo.Inventory a
	inner join IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
	inner join IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
	inner join (
		select distinct z.PriceLevel
		from IntacsDataUpgrade.dbo.[Partner] x 
		inner join IntacsDataUpgrade.dbo.Area y on x.areano = y.AreaNo
		inner join IntacsDataUpgrade.dbo.PriceLevel z on x.PriceLevel = z.PriceLevel
		where y.AreaNo = @areano
	) as PL on c.PriceLevel = PL.PriceLevel
	left join IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
	where a.Active = 1 and a.partno not like 'x%'

	return;
end



create function fn_mobile_inventory(
	@areano varchar(30)
)returns @mytable table(
	InvID int,
	Partno varchar(30),
	InvName varchar(200),
	Description varchar(max),
	InvGrp varchar(100),
	PClass8Name varchar(100)
)
as
begin
	insert into @mytable
	select distinct a.InvId, a.partno, a.InvName, a.[desc] as Description,
	a.InvGrp, P8.PClass8Name
	from IntacsDataUpgrade.dbo.Inventory a
	inner join IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
	inner join IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
	inner join (
		select distinct z.PriceLevel
		from IntacsDataUpgrade.dbo.[Partner] x 
		inner join IntacsDataUpgrade.dbo.Area y on x.areano = y.AreaNo
		inner join IntacsDataUpgrade.dbo.PriceLevel z on x.PriceLevel = z.PriceLevel
		where y.AreaNo = @areano
	) as PL on c.PriceLevel = PL.PriceLevel
	left join IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
	where a.Active = 1 and a.partno not like 'x%'

	return;
end


create function fn_mobile_inventory_bypartno(
	@partno varchar(30)
)returns @mytable table(
	InvID int,
	Partno varchar(30),
	InvName varchar(200),
	Description varchar(max),
	InvGrp varchar(100),
	PClass8Name varchar(100)
)
as
begin
	insert into @mytable
	select distinct a.InvId, a.partno, a.InvName, a.[desc] as Description,
	a.InvGrp, P8.PClass8Name
	from IntacsDataUpgrade.dbo.Inventory a
	inner join IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
	inner join IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
	left join IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
	where a.partno = @partno

	return;
end