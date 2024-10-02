<?php
	require_once '../src/models/BaseModel.php';

	class ModelNewCCP extends BaseModel{

		public static function getFields(){
			return array(
				"IDNo","NoTR","DateTr","Dabin","Description","Entity","SalID","Status","Operator"
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

		public static function saveToDB($obj){
			$db = new DB();
			$db = $db->connect();
			$db->beginTransaction();
			try { 
				// if (!static::isNewTransaction($obj)){
					// $sql = ModelNewCCPDet::generateSQLDelete('IDNo = '. $db->quote($obj->IDNo));
					// $db->prepare($sql)->execute();
				// }				

				//next : check if salesman already has CCP, then use existing CCP
				static::saveObjToDB($obj, $db);

				foreach($obj->items as $item){
					$item->IDNo = $obj->IDNo;  
					//del 
					$sql = ModelNewCCPDet::generateSQLDelete("IDNo = ". $db->quote($obj->IDNo) . " and ShipID =" . $db->quote($item->ShipID));
					$db->prepare($sql)->execute();
					ModelNewCCPDet::saveObjToDB($item, $db);
				}

				$db->commit();
				$db = null;
			} catch (Exception $e) {
				$db->rollback();
				throw $e;
			}
		}

		public static function saveToDBBatch($objs){
			foreach($objs as $obj){
				static::saveToDB($obj);
			}
		}

	}

	class ModelNewCCPDet extends BaseModel{
		public static function getFields(){
			return array(
				"IDNo", "ShipID", "CCPSCH", 
				"Remark", "CCPType", "Mark", "CreateDate", 
				"SOQty", "DOQty", "RetQty", "Coll",
				"Lat","Lng", "DateTr"
			);
		}
	}
