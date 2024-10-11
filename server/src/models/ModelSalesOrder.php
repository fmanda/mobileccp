<?php
	require_once '../src/models/BaseModel.php';

	class ModelSalesOrder extends BaseModel{
		// public static function always_insert() { return true; }

		public static function getPrimaryKey(){
			return "id";
		}

		public static function getTableName(){
			return "mobile_salesorder";
		}

		public static function getFields(){
			return array(
				"id", "orderno", "orderdate", "entity",
				"shipid","salid", 
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
				static::saveObjToDB($obj, $db);

			
				$sql = ModelSalesOrderItem::generateSQLDelete('salesorder_id = '. $db->quote($obj->id));
				$db->prepare($sql)->execute();
			
				
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


		//override
		public static function saveObjToDB($obj, $db){			
			try {				
				$sql = static::generateSQL($obj, $db);
				$int = $db->prepare($sql)->execute();				
			} catch (Exception $e) {
				echo $sql;				
				throw $e;
			}
		}

		//override
		public static function generateSQL($obj, $db){
			$sql = "MERGE INTO mobile_salesorder AS target
					USING (VALUES (
						'{$obj->id}', 
						'{$obj->orderno}', 
						'{$obj->orderdate}', 
						'{$obj->entity}', 
						{$obj->shipid}, 
						{$obj->salid}, 
						{$obj->dpp}, 
						{$obj->ppn}, 
						{$obj->amt}, 
						{$obj->latitude}, 
						{$obj->longitude}
					)) AS source (id, orderno, orderdate, entity, shipid, salid, dpp, ppn, amt, latitude, longitude)
					ON target.id = source.id

					WHEN MATCHED THEN
						UPDATE SET 
							target.orderno = source.orderno,
							target.orderdate = source.orderdate,
							target.entity = source.entity,
							target.shipid = source.shipid,
							target.salid = source.salid,
							target.dpp = source.dpp,
							target.ppn = source.ppn,
							target.amt = source.amt,
							target.latitude = source.latitude,
							target.longitude = source.longitude

					WHEN NOT MATCHED BY TARGET THEN
						INSERT (id, orderno, orderdate, entity, shipid, salid, dpp, ppn, amt, latitude, longitude)
						VALUES ('{$obj->id}', '{$obj->orderno}', '{$obj->orderdate}', '{$obj->entity}', {$obj->shipid}, {$obj->salid}, {$obj->dpp}, {$obj->ppn}, {$obj->amt}, {$obj->latitude}, {$obj->longitude});
					;";

			return $sql;

		}

		

		public static function saveToDBBatch($objs){
			foreach($objs as $obj){
				static::saveToDB($obj);
			}
		}

	}

	class ModelSalesOrderItem extends BaseModel{

		public static function getTableName(){
			return "mobile_salesorderitem";
		}

		public static function getFields(){
			return array(
				"salesorder_id","partno", "uom", "qty",  "price",
				"discount", "dpp", "ppn", "amt"
			);
		}
	}
