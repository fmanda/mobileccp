<?php
	require_once '../src/models/BaseModel.php';

	class ModelVisitPlan extends BaseModel{
		

		public static function getPrimaryKey(){
			return "id";
		}

		public static function getFields(){
			return array(
				"id", "salid", "notr", "datetr", "dabin",
				"entity", "operator", "status"
			);
		}

		//override
		public static function retrieve($id){
			$obj = parent::retrieve($id);
			if (isset($obj)) $obj->items = ModelVisitPlanItem::retrieveList("visitplan_id = '{$obj->id}'");
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
				if (isset($obj)) $obj->items = ModelVisitPlanItem::retrieveList("visitplan_id = '{$obj->id}'");
			}

			return $objs;
		}

		//override
		public static function deleteFromDB($id){
			try{
				$obj = parent::retrieve($id);
				$str = static::generateSQLDelete("id=". $id); 
				$str = $str . ModelVisitPlanItem::generateSQLDelete( "visitplan_id = '{$id}'" );
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
				$sql = ModelVisitPlanItem::generateSQLDelete('visitplan_id = '. $db->quote($obj->id));
				$db->prepare($sql)->execute();
			
				foreach($obj->items as $item){
					ModelVisitPlanItem::saveObjToDB($item, $db);
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

			$sql = "MERGE INTO visitplan AS target
					USING (VALUES (
						'{$obj->id}', 
						'{$obj->salid}', 
						'{$obj->notr}', 
						'{$obj->datetr}', 
						'{$obj->dabin}', 
						'{$obj->entity}', 
						'{$obj->operator}', 
						{$obj->status}
					)) AS source (id, salid, notr, datetr, dabin, entity, operator, status)
					ON target.id = source.id

					WHEN MATCHED THEN
						UPDATE SET 
							target.salid = source.salid,
							target.notr = source.notr,
							target.datetr = source.datetr,
							target.dabin = source.dabin,
							target.entity = source.entity,
							target.operator = source.operator,
							target.status = source.status

					WHEN NOT MATCHED BY TARGET THEN
						INSERT (id, salid, notr, datetr, dabin, entity, operator, status)
						VALUES ('{$obj->id}', '{$obj->salid}', '{$obj->notr}', '{$obj->datetr}', '{$obj->dabin}', '{$obj->entity}', '{$obj->operator}', {$obj->status});
					;";

			return $sql;

		}

		

		public static function saveToDBBatch($objs){
			foreach($objs as $obj){
				static::saveToDB($obj);
			}
		}

	}

	class ModelVisitPlanItem extends BaseModel{

		public static function getFields(){
			return array(
				"visitplan_id","partnerid", "planmark_id"
			);
		}
	}
