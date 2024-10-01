<?php
	require_once '../src/models/BaseModel.php';

	class ModelSalesOrder extends BaseModel{
		public static function always_insert() { return true; }

		public static function getFields(){
			return array(
				"id", "orderno", "orderdate", "project_code",
				"customer_id","salesman_id", 
				"dpp", "ppn", "amt", "latitude", "longitude"
			);
		}

		//override
		public static function retrieve($id){
			$obj = parent::retrieve($id);
			if (isset($obj)) $obj->items = ModelSalesOrderItem::retrieveList('salesorder_id = "'. $obj->id .'"');
			//additional property :
			// foreach($obj->items as $item){
			// 	if (isset($item->material_id)) $item->material =  ModelMaterial::retrieve($item->material_id);
			// }
			return $obj;
		}

		public static function retrieveList($filter=''){
			$sql = 'select * from '.static::getTableName().' where 1=1 ';
			if ($filter<>''){
				$sql = $sql .' and '. $filter;
			}
			$objs = DB::openQuery($sql);

			foreach($objs as $obj){
				if (isset($obj)) $obj->items = ModelSalesOrderItem::retrieveList('salesorder_id = "'. $obj->id .'"');
			}

			return $objs;
		}

		public static function deleteFromDB($id){
			try{
				$obj = parent::retrieve($id);
				$str = static::generateSQLDelete("id=". $id);
				$str = $str . ModelSalesOrderItem::generateSQLDelete('header_id = '. $id);
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
					$sql = ModelSalesOrderItem::generateSQLDelete('salesorder_id = '. $db->quote($obj->id));
					$db->prepare($sql)->execute();
				// }
				static::saveObjToDB($obj, $db);
				foreach($obj->items as $item){
					// $item->salesorder_id = $obj->id;  //object already have
					ModelSalesOrderItem::saveObjToDB($item, $db);
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

	class ModelSalesOrderItem extends BaseModel{
		public static function getFields(){
			return array(
				"salesorder_id","sku", "uom", "qty",  "unitprice",
				"discount", "dpp", "ppn", "amt"
			);
		}
	}
