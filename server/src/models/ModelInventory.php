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
			$sql = "select * from fn_mobile_inventory_bypartno('". $id."')";
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveByArea($area){
			$sql = "select * from fn_mobile_inventory('". $area . "')";			
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
			$sql = "select * from fn_mobile_pricelevel('". $area . "')";		
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}