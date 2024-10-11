<?php
	require_once '../src/models/BaseModel.php';

	class ModelCustomer extends BaseModel{

		public static function getFields(){
			return array(
				"ShipID","ShipName","ShipAddress","ShipCity","ShipPhone","ShipHP",
				"PartnerID","PartnerName","PriceLevel","IsActive","AreaNo","AreaName",
				"NPSN", "Jenjang"
			);
		}

		public static function getPrimaryKey(){
			return "ShipID";
		}

		public static function retrieve($id){
			$sql = "select *
					from v_mobile_customer
					where ShipID = '" . $id . "'";						
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveByArea($areano){
			$sql = "select *
					from v_mobile_customer
					where AreaNo = '" . $areano . "'";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}

		//temporary limit data, next buatke paging
		public static function retrieveByEntity($areano, $filtert){
			$sql = "select distinct top 2000 a.*
					from v_mobile_customer a
					inner join IntacsDataUpgrade.dbo.area b on a.areano = b.AreaNo
					where b.Entity = '". $areano . "' 
					and (a.shipname like '%". $filtert . "%' 			
					or a.partnername like '%". $filtert . "%' )";
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}