<?php
	require_once '../src/models/BaseModel.php';

	class ModelInventory extends BaseModel{
		public static function getFields(){
			return array(
				"InvID","PartNo","InvName","Description", "InvGrp", "PClass8"
			);
		}

		public static function getPrimaryKey(){
			return "InvID";
		}

		public static function retrieve($id){
			$sql = "select distinct a.InvId, a.partno, a.InvName, a.[desc] as Description,
					a.InvGrp, P8.PClass8Name
					from IntacsDataUpgrade.dbo.Inventory a
					inner join IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
					inner join IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
					left join IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
					where a.partno = '" . $id . "'";
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveByArea($area){
			$sql = "select distinct a.InvId, a.partno, a.InvName, a.[desc] as Description,
					a.InvGrp, P8.PClass8Name
					from IntacsDataUpgrade.dbo.Inventory a
					inner join IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
					inner join IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
					inner join (
						select distinct z.PriceLevel
						from IntacsDataUpgrade.dbo.[Partner] x 
						inner join IntacsDataUpgrade.dbo.Area y on x.areano = y.AreaNo
						inner join IntacsDataUpgrade.dbo.PriceLevel z on x.PriceLevel = z.PriceLevel
						where y.AreaNo = '" . $area . "'
					) as PL on c.PriceLevel = PL.PriceLevel
					left join IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
					where a.Active = 1 and a.partno not like 'x%'";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}

	class ModelPriceLevelDetail extends BaseModel{
		public static function getFields(){
			return array(
				"InvID","PartNo","PriceLevel","PriceLevelName", "Price"
			);
		}
		public static function retrieveByArea($area){
			$sql = "select distinct a.InvId, a.partno, c.PriceLevel, PriceLevelName, b.Price
					from IntacsDataUpgrade.dbo.Inventory a
					inner join IntacsDataUpgrade.dbo.PriceLevelDetail b on a.InvId = b.InvID
					inner join IntacsDataUpgrade.dbo.PriceLevel c on b.PriceLevel = c.PriceLevel
					inner join (
						select distinct z.PriceLevel
						from IntacsDataUpgrade.dbo.[Partner] x 
						inner join IntacsDataUpgrade.dbo.Area y on x.areano = y.AreaNo
						inner join IntacsDataUpgrade.dbo.PriceLevel z on x.PriceLevel = z.PriceLevel
						where y.AreaNo = '" . $area . "'
					) as PL on c.PriceLevel = PL.PriceLevel
					left join IntacsDataUpgrade.dbo.PClass8 P8 on a.pclass8 = P8.Pclass8
					where a.Active = 1 and a.partno not like 'x%'";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}