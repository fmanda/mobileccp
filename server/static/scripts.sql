alter view v_mobile_salesman as
select a.EmpId as salid, a.EmpName as salname, b.areano, b.areaname, a.entity
from IntacsDataUpgrade.dbo.Employee a
inner join IntacsDataUpgrade.dbo.Area b on a.EmpId = b.SalId
where a.PSales =  1
and a.NotActive = 0

alter view v_mobile_employee as
select empid, empname, a.entity, b.EntityName
from IntacsDataUpgrade.dbo.Employee a
inner join IntacsDataUpgrade.dbo.Entity b on a.Entity = b.Entity
where a.PSales = 0
and a.NotActive = 0

alter view v_mobile_customer as
select a.shipid, a.[Ship Name] as shipname, a.[Ship Address] as shipaddress, a.[Ship City] as shipcity, 
a.[Ship Phone] as shipphone, a.[Ship HP] as shiphp, b.partnerid, b.partnername,
a.pricelevel, a.isactive, c.areano, c.areaname, b.npsn, e.CClass2Name as jenjang
from IntacsDataUpgrade.dbo.CustomerDelivery a
inner join IntacsDataUpgrade.dbo.[Partner] b on a.CustomerId = b.PartnerId
inner join IntacsDataUpgrade.dbo.Area c on b.areano = c.AreaNo
left join IntacsDataUpgrade.dbo.PartnerClass d on d.PartnerId = b.PartnerId
left join IntacsDataUpgrade.dbo.CClass2 e on e.CClass2 = d.cclass2


alter function fn_mobile_pricelevel(
	@areano varchar(30)
)returns @mytable table(
	invid int,
	partno varchar(30),
	pricelevel int,
	pricelevelname varchar(200),
	price Float
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



alter function fn_mobile_inventory(
	@areano varchar(30)
)returns @mytable table(
	invid int,
	partno varchar(30),
	invname varchar(200),
	description varchar(max),
	invgrp varchar(100),
	pclass8name varchar(100)
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


alter function fn_mobile_inventory_bypartno(
	@partno varchar(30)
)returns @mytable table(
	invid int,
	partno varchar(30),
	invname varchar(200),
	description varchar(max),
	invgrp varchar(100),
	pclass8name varchar(100)
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

alter procedure sp_mobile_getccp(
	@salid varchar(30),
	@ccpdate date	
)as 
begin
	SET NOCOUNT ON 
	--declare @ccpdate date = '2024-1-1'
	declare @trxno varchar(10) = (SELECT FORMAT(@ccpdate, 'yyMMdd'))
	--declare @salid varchar(30) = '0220222572'
	declare @entity varchar(10) 
	declare @areano varchar(10)
	declare @idno int = 0

	select @entity = entity, @areano = areano 
	from v_mobile_salesman where salid = @salid 

	if  (@areano is null)
	begin
		THROW 50001, 'Salesman tidak punya mapping Dabin', 1;		
	end

	select @idno = idno from newccp 
	where dabin = @areano and salid = @salid and cast(datetr as date) = cast(@ccpdate as date)

	if (@idno = 0)
	begin
		print @areano;
		declare @notr varchar(30) = rtrim(@areano) + '/' + @trxno

		insert into NewCCP(Notr, DateTr, Dabin, Description, Entity, SalID, Status, Operator)
		values(@notr, @ccpdate, @areano, 'Auto Insert By System', @entity, @salid, 0, @salid)
		set @idno = (SELECT SCOPE_IDENTITY())
	end

	select idno, notr, datetr, dabin, description, entity, salid, status, operator from NEWCCP where idno = @IDNO
end




create table mobile_salesorder(
	id uniqueidentifier,
	orderno varchar(30),
	orderdate date,
	entity varchar(30),
	shipid int,
	salid int,
	dpp float, 
	ppn float,
	amt float,
	latitude float,
	longitude float
)


create table mobile_salesorderitem(
	id int identity(1,1),
	salesorder_id uniqueidentifier,
	partno varchar(30),
	uom varchar(30),
	qty int, 
	price float,
	discount float,
	dpp float,
	ppn float,
	amt float
)

//generate dummy ccp
insert into NEWCCP(notr,  DateTR, Dabin, Description, Entity, SalID, Status, Operator)
select top 1 rtrim(a.dabin) + '/' + FORMAT(getdate(), 'yyMMdd') as notr, 
cast(getdate() as date) as datetr, a.Dabin, a.Description, a.Entity, a.SalID, 0 as status, a.Operator
from newccp a
inner join NEWCCPDet b on a.idno = b.IDNo
where a.salid = '0220222570'
and a.DateTR ='2023-2-4'

declare @id int = (SELECT SCOPE_IDENTITY())

insert into NEWCCPDet(idno, shipid, PlanQty, ar, car, CCPSCH, CCPType, mark, CreateDate, 
SOQty, DOQty, RetQty, Coll, Lat, Lng, DateTr, uid)
select @id as idno, shipid, PlanQty, b.ar, b.car, b.CCPSCH, b.CCPType, b.Mark,
null as createdate, b.SOQty, b.DOQty, b.RetQty, b.Coll, b.Lat, b.Lng, b.DateTr, b.uid
from newccp a
inner join NEWCCPDet b on a.idno = b.IDNo
where a.salid = '0220222570'
and a.DateTR ='2023-2-4'


alter view v_mobile_ar_remain as 
select a.invno, cast(a.invdate as date) as invdate, a.amount, a.settle, a.remain, b.[Ship Name] as shipname,
c.PartnerName as partnername, a.shipid, a.salid
from IntacsDataUpgrade.dbo.arinv a
inner join IntacsDataUpgrade.dbo.CustomerDelivery b on a.ShipId = b.shipid
inner join IntacsDataUpgrade.dbo.[Partner] c on a.PartnerID = c.PartnerId
where  isnull(a.remain, 0)>0

