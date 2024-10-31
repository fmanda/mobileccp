<?php
	require_once '../src/models/BaseModel.php';

	class ModelCustomerDelivery extends BaseModel{

		public static function getPrimaryKey(){
			return "shipid";
		}

		public static function retrieve($id){
			$sql = "select *
					from v_customerdelivery
					where ShipID = '" . $id . "'";						
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveByArea($areano){
			$sql = "select *
					from v_customerdelivery
					where AreaNo = '" . $areano . "'";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}

		//temporary limit data, next buatke paging
		public static function retrieveByEntity($areano, $filtert){
			$sql = "select distinct top 2000 *
					from v_customerdelivery 
					where Entity = '". $areano . "' 
					and (shipname like '%". $filtert . "%' 			
					or partnername like '%". $filtert . "%' )";
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}