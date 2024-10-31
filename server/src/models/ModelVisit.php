<?php
	require_once '../src/models/BaseModel.php';

	class ModelVisit extends BaseModel{
		

		public static function getPrimaryKey(){
			return "id";
		}

		public static function getFields(){
			return array(
				"id", "salid", "shipid", "visitno", "visitdate", "visitmark_id",
				"notes", "lat", "lng", "visitplan"
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
			$sql = "MERGE INTO visit AS target
					USING (VALUES (
						'{$obj->id}', 
						'{$obj->salid}', 
						'{$obj->shipid}', 
						'{$obj->visitno}', 
						'{$obj->visitdate}', 
						{$obj->visitmark_id}, 
						'{$obj->notes}', 
						{$obj->lat}, 
						{$obj->lng}, 
						'{$obj->visitplan}'
					)) AS source (id, salid, shipid, visitno, visitdate, visitmark_id, notes, lat, lng, visitplan)
					ON target.id = source.id

					WHEN MATCHED THEN
						UPDATE SET 
							target.salid = source.salid,
							target.shipid = source.shipid,
							target.visitno = source.visitno,
							target.visitdate = source.visitdate,
							target.visitmark_id = source.visitmark_id,
							target.notes = source.notes,
							target.lat = source.lat,
							target.lng = source.lng,
							target.visitplan = source.visitplan

					WHEN NOT MATCHED BY TARGET THEN
						INSERT (id, salid, shipid, visitno, visitdate, visitmark_id, notes, lat, lng, visitplan)
						VALUES ('{$obj->id}', '{$obj->salid}', '{$obj->shipid}', '{$obj->visitno}', '{$obj->visitdate}', {$obj->visitmark_id}, '{$obj->notes}', {$obj->lat}, {$obj->lng} , '{$obj->visitplan}');
					;";

			return $sql;

		}

		public static function saveToDBBatch($objs){
			foreach($objs as $obj){
				static::saveToDB($obj);
			}
		}

	}
