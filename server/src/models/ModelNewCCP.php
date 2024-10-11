<?php
	require_once '../src/models/BaseModel.php';

	class ModelNewCCP extends BaseModel{

		public static function getFields(){
			return array(
				"idno","notr","datetr","dabin","description","entity","salid","status","operator"
			);
		}


		public static function getPrimaryKey(){
			return "IDNo";
		}

		//override
		public static function retrieve($id){
			$obj = parent::retrieve($id);
			if (isset($obj)) $obj->items = ModelNewCCPDet::retrieveList("IDNo = ". $obj->IDNo );
			return $obj;
		}

		public static function retrieveList($filter=''){
			$sql = "select * from ".static::getTableName()." where 1 = 1 ";
			if ($filter<>''){
				$sql = $sql ." and ". $filter;
			}
			$objs = DB::openQuery($sql);
			foreach($objs as $obj){
				if (isset($obj)) $obj->items = ModelNewCCPDet::retrieveList("IDNo = ". $obj->IDNo );
			}
			return $objs;
		}

		public static function deleteFromDB($id){
			try{
				$obj = parent::retrieve($id);
				$str = static::generateSQLDelete("IDNo = ". $id);
				$str = $str . ModelNewCCPDet::generateSQLDelete("IDNo = ". $id);
				DB::executeSQL($str);
			} catch (Exception $e) {
				$db->rollback();
				throw $e;
			}
		}

		//updated 
		// public static function saveToDB($obj){
		// 	$tmpitems = $obj->items;
		// 	$obj = static::getOrCreate($obj->salid, $obj->datetr); //save header here via sp
		// 	$obj->items = $tmpitems;
	
		// 	$db = new DB();
		// 	$db = $db->connect();
		// 	$db->beginTransaction();
		// 	try { 
		// 		foreach($obj->items as $item){
		// 			$item->idno = $obj->idno;  
		// 			//del 
		// 			$sql = ModelNewCCPDet::generateSQLDelete("idno = ". $db->quote($obj->idno) . " and shipid =" . $db->quote($item->shipid));
		// 			$db->prepare($sql)->execute();
		// 			ModelNewCCPDet::saveObjToDB($item, $db);
		// 		}

		// 		$db->commit();
		// 		$db = null;
		// 	} catch (Exception $e) {
		// 		$db->rollback();
		// 		throw $e;
		// 	}
		// }

		// public static function saveToDBBatch($objs){
		// 	foreach($objs as $obj){
		// 		static::saveToDB($obj);
		// 	}
		// }


		public static function getOrCreate($salid, $date){
			$sql = "exec sp_mobile_getccp '". $salid . "','" . $date . "' ";
			$obj = DB::openQuery($sql);
			if (isset($obj[0])) return $obj[0];
		}

	}

	class ModelNewCCPDet extends BaseModel{
		public static function getFields(){
			return array(
				"idno", "shipid", "ccpsch", 
				"remark", "ccptype", "mark", "createdate", 
				"soqty", "doqty", "retqty", "coll",
				"lat","lng", "datetr", "uid"
			);
		}

		public static function saveToDBBatch($objs){						
			$db = new DB();
			$db = $db->connect();
			$db->beginTransaction();
			try { 
				foreach($objs as $obj){

					$parent = ModelNewCCP::getOrCreate($obj->salid, $obj->datetr); 

					$obj->idno = $parent->idno;  			
					$sql = ModelNewCCPDet::generateSQLDelete("idno = ". $db->quote($obj->idno) . " and shipid =" . $db->quote($obj->shipid));
					$db->prepare($sql)->execute();
					ModelNewCCPDet::saveObjToDB($obj, $db);
				}

				$db->commit();
				$db = null;
			} catch (Exception $e) {
				$db->rollback();
				throw $e;
			}
		}		


	}
