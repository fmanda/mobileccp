<?php
	require_once '../src/models/BaseModel.php';

	class ModelVisitRoute extends BaseModel{
		

		public static function getPrimaryKey(){
			return "id";
		}

		public static function getFields(){
			return array(
				"id", "dabin", "routename"
			);
		}

		//override
		public static function retrieve($id){
			$obj = parent::retrieve($id);
			if (isset($obj)) $obj->items = ModelVisitRouteItem::retrieveList("visitroute_id = '{$obj->id}'");
			return $obj;
		}

		//override
		public static function retrieveList($filter=''){
			$sql = 'select * from '.static::getTableName().' where 1=1 ';
			if ($filter<>''){
				$sql = $sql .' and '. $filter;
			}
			$objs = DB::openQuery($sql);

			foreach($objs as $obj){
				if (isset($obj)) $obj->items = ModelVisitRouteItem::retrieveList("visitroute_id = '{$obj->id}'");
			}

			return $objs;
		}

		//override
		public static function deleteFromDB($id){
			try{
				$obj = parent::retrieve($id);
				$str = static::generateSQLDelete("id=". $id); 
				$str = $str . ModelVisitRouteItem::generateSQLDelete( "visitroute_id = '{$id}'" );
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
				$sql = ModelVisitRouteItem::generateSQLDelete("visitroute_id = ". $db->quote($obj->id));
				$db->prepare($sql)->execute();
			
				foreach($obj->items as $item){
					ModelVisitRouteItem::saveObjToDB($item, $db);
				}
				$db->commit();
				$db = null;

			} catch (Exception $e) {
				$db->rollback();
				throw $e;
			}
		}


		//override to always insert using merge
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

			$sql = "MERGE INTO visitroute AS target
					USING (VALUES (
						'{$obj->id}', 
						'{$obj->dabin}', 
						'{$obj->routename}'
					)) AS source (id, dabin, routename)
					ON target.id = source.id

					WHEN MATCHED THEN
						UPDATE SET 
							target.dabin = source.dabin,
							target.routename = source.routename

					WHEN NOT MATCHED BY TARGET THEN
						INSERT (id, dabin, routename)
						VALUES ('{$obj->id}', '{$obj->dabin}', '{$obj->routename}');
					;";

			return $sql;

		}

		

		public static function saveToDBBatch($objs){
			foreach($objs as $obj){
				static::saveToDB($obj);
			}
		}

	}

	class ModelVisitRouteItem extends BaseModel{

		public static function getFields(){
			return array(
				"visitroute_id","partnerid", "planmark_id"
			);
		}
	}
