<?php
	require_once '../src/models/BaseModel.php';

	class ModelCustomer extends BaseModel{
		public static function always_insert() { return true; }
		

		public static function getTableName(){
			return 'customer';
		}
		public static function getFields(){
			return array(
				"id",
				"project_code",
				"nama",
				"nik",
				"phone",
				"alamat",
				"kecamatan",
				"kelurahan",
				"last_updated",
				"is_new"
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
