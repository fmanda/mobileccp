<?php
	require_once '../src/models/BaseModel.php';

	class ModelCustomer extends BaseModel{

		public static function getFields(){
			return array(
				"ShipID","ShipName","ShipAddress","ShipCity","ShipPhone","ShipCity",
				"PartnerID","PartnerName","PriceLevel","IsActive","AreaNo","AreaName"
			);
		}

		public static function getPrimaryKey(){
			return "ShipID";
		}

		public static function retrieve($id){
			$sql = "select a.ShipId, a.[Ship Name] as ShipName, b.PartnerId, b.PartnerName,
					a.[Ship Address] as ShipAddress, a.[Ship City] as ShipCity, a.[Ship Phone] as ShipPhone, a.[Ship HP] as ShipHP,
					a.PriceLevel, a.IsActive, c.AreaNo, c.AreaName
					from IntacsDataUpgrade.dbo.CustomerDelivery a
					inner join IntacsDataUpgrade.dbo.[Partner] b on a.CustomerId = b.PartnerId
					inner join IntacsDataUpgrade.dbo.Area c on b.areano = c.AreaNo
					where a.ShipID = '" . $id . "'";						
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveByArea($areano){
			$sql = "select a.ShipId, a.[Ship Name] as ShipName, b.PartnerId, b.PartnerName,
					a.[Ship Address] as ShipAddress, a.[Ship City] as ShipCity, a.[Ship Phone] as ShipPhone, a.[Ship HP] as ShipHP,
					a.PriceLevel, a.IsActive, c.AreaNo, c.AreaName
					from IntacsDataUpgrade.dbo.CustomerDelivery a
					inner join IntacsDataUpgrade.dbo.[Partner] b on a.CustomerId = b.PartnerId
					inner join IntacsDataUpgrade.dbo.Area c on b.areano = c.AreaNo
					where c.AreaNo = '" . $areano . "'";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}