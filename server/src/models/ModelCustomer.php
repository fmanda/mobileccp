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
	}