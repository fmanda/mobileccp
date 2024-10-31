create view v_salesman as
select a.EmpId as salid, a.EmpName as salname, b.areano, b.areaname, a.entity
from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Employee a
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Area b on a.EmpId = b.SalId
where a.PSales =  1
and a.NotActive = 0

create view v_employee as
select empid, empname, a.entity, b.EntityName
from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Employee a
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Entity b on a.Entity = b.Entity
where a.PSales = 0
and a.NotActive = 0


alter view v_customerdelivery as
select a.shipid, a.[Ship Name] as shipname, a.[Ship Address] as shipaddress, a.[Ship City] as shipcity, 
a.[Ship Phone] as shipphone, a.[Ship HP] as shiphp, b.partnerid, b.partnername,
a.pricelevel, a.isactive, c.areano, c.areaname, c.Entity, b.npsn, e.CClass2Name as jenjang
from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.CustomerDelivery a
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.[Partner] b on a.CustomerId = b.PartnerId
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Area c on b.areano = c.AreaNo
left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PartnerClass d on d.PartnerId = b.PartnerId
left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.CClass2 e on e.CClass2 = d.cclass2


ALTER view [dbo].[v_customer] as
select b.partnerid, b.partnername, c.areano, c.areaname, c.Entity, b.npsn, e.CClass2Name as jenjang
from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.[Partner] b 
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Area c on b.areano = c.AreaNo
left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PartnerClass d on d.PartnerId = b.PartnerId
left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.CClass2 e on e.CClass2 = d.cclass2


create function [dbo].[fn_pricelevel](
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
	select distinct a.InvId, a.partno, c.PriceLevel, c.PriceLevelName, b.Price
	from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Inventory a
	inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
	inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
	inner join (
		select distinct CD.PriceLevel
		from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.[Partner] x 
		INNER JOIN MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.CustomerDelivery CD on cd.CustomerId = x.PartnerId 
		--INNER join IntacsDataUpgrade.dbo.Area y on x.areano = y.AreaNo
		--inner join IntacsDataUpgrade.dbo.PriceLevel z on x.PriceLevel = CD.PriceLevel
		where X.AreaNo = @areano
	) as PL on c.PriceLevel = PL.PriceLevel
	left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
	where a.TOAndroid = 1 and a.partno not like 'x%'

	return;
end



ALTER function [dbo].[fn_inventory](
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
	from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Inventory a
	inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
	inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
	inner join (
		select distinct z.PriceLevel
		from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.[Partner] x 
		inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Area y on x.areano = y.AreaNo
		inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevel z on x.PriceLevel = z.PriceLevel
		where (y.AreaNo = @areano or y.Entity = @areano)
	) as PL on c.PriceLevel = PL.PriceLevel
	left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
	where a.TOAndroid = 1 and a.partno not like 'x%'

	return;
end






create function fn_inventory_bypartno(
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
	from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.Inventory a
	inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
	inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
	left join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
	where a.partno = @partno

	return;
end

create table planmark(
	id int,
	markname varchar(100)
)

create table visitmark(
	id int,
	markname varchar(100)
)



create table salesorder(
	id uniqueidentifier,
	orderno varchar(30),
	orderdate date,
	entity varchar(30),
	shipid int,
	salid int,
	dpp float default 0, 
	ppn float default 0,
	amt float default 0,
	latitude float,
	longitude float,
	discount float default 0,
	refund float default 0
)


create table salesorderitem(
	id int identity(1,1),
	salesorder_id uniqueidentifier,
	partno varchar(30),
	uom varchar(30),
	qty int, 
	price float default 0,
	discount float default 0,
	dpp float default 0,
	ppn float default 0,
	amt float default 0
)


create table visitplan(
	id uniqueidentifier,
	salid varchar(30),
	notr varchar(30),
	datetr date,
	dabin varchar(30),
	entity varchar(10),
	description varchar(max),
	operator varchar(30),
	[status] int,
	ident int identity(1,1),
)


create table visitplanitem(
	id int primary key identity(1,1),
	visitplan_id uniqueidentifier,
	partnerid int,
	planmark_id int,
)


create table visit(
	id uniqueidentifier,
	salid varchar(30),
	shipid int,
	visitno varchar(30),
	visitdate date,
	visitmark_id int,
	notes varchar(max),
	lat float,
	lng float,
	visitplan varchar(30),
	ident int identity(1,1),
)




create table visitroute(
	id uniqueidentifier,
	dabin varchar(30),
	routename varchar(max),
	ident int primary key identity(1,1),
)



create table visitrouteitem(
	id int primary key identity(1,1),
	visitroute_id uniqueidentifier,
	partnerid int
)


create view v_mobile_ar_remain as 
select a.invno, cast(a.invdate as date) as invdate, a.amount, a.settle, a.remain, b.[Ship Name] as shipname,
c.PartnerName as partnername, a.shipid, a.salid
from MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.arinv a
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.CustomerDelivery b on a.ShipId = b.shipid
inner join MOCCA_TSPM_DIS.IntacsDataUpgrade.dbo.[Partner] c on a.PartnerID = c.PartnerId
where  isnull(a.remain, 0)>0





-------------------------


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



--------- pump data



insert into visitrouteitem(visitroute_id, partnerid)
select distinct c.id, d.customerid
from TS_147.Marketing.dbo.RouteCCP a
inner join TS_147.Marketing.dbo.RouteCCPDet b on a.RouteID = b.RouteID
inner join visitroute c on a.RouteName = c.routename and c.dabin = a.Dabin
inner join TS_147.IntacsDataUpgrade.dbo.customerdelivery d on b.ShipID = d.ShipId



insert into visitplan(id, salid, notr, datetr, dabin , Entity, Description, Operator, Status)
select newid(), salid, notr, datetr, dabin , Entity, Description, Operator, Status
from marketing.dbo.newccp a
where a.datetr = '2024-10-29'




insert into visitplanitem(visitplan_id, partnerid, planmark_id)
select c.id, d.partnerid, 0
from marketing.dbo.newccp a
inner join marketing.dbo.NEWCCPDet b on a.IDNo = b.IDNo
inner join visitplan c on a.salid = c.salid and a.datetr = c.datetr
inner join v_customerdelivery d on b.ShipID = d.shipid

