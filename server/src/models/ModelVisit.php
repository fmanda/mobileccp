<?php
	require_once '../src/models/BaseModel.php';

	class ModelVisit extends BaseModel{
		public static function always_insert() { return true; }

		public static function getFields(){
			return array(
				"id", "visitno","visitdate", "project_code",
				"salesman_id","customer_id", 
				"latitude", "longitude"
			);
		}

	
		public static function saveToDB($obj){
			$db = new DB();
			$db = $db->connect();
			$db->beginTransaction();
			try { 
				
				static::saveObjToDB($obj, $db);
				
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
