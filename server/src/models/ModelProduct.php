<?php
	require_once '../src/models/BaseModel.php';

	class ModelProduct extends BaseModel{
		public static function always_insert() { return true; }

		public static function getTableName(){
			return 'product';
		}

		public static function getPrimaryKey(){
			return "sku";
		}

		public static function getFields(){
			return array(
				"sku",
				"principal",
				"merk",
				"nama",
				"uom_1",
				"uom_2",
				"uom_3",
				"trad_uom_1",
				"trad_uom_2",
				"trad_uom_3",
				"konv_1",
				"konv_2",
				"konv_3",
				"sellprice_1",
				"sellprice_2",
				"sellprice_3",
				"trad_sellprice_1",
				"trad_sellprice_2",
				"trad_sellprice_3",
				"last_updated"
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
