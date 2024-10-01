<?php
	require_once '../src/classes/DB.php';

	class BaseModel{
		// public $always_insert = false;
		// public $is_primary_int = false;  
		//change to method because override

		//override this
		public static function always_insert() { return false; }

		//override this
		public static function is_primary_int(){ return false; }

		public static function getFields(){
			return array("id"); //uuid string
		}

		public static function getPrimaryKey(){
			return "id";
		}

		public static function getTableName(){
			$str = get_called_class();
			$str = str_replace("Model","",$str);
			$str = strtolower($str);
			return $str;
		}

		public static function retrieve($id){  //uuid string
			$sql = 'select * from '.static::getTableName().' where '. static::getPrimaryKey() .' = "'.$id.'"';
			$obj = DB::openQuery($sql);
			if (isset($obj[0])) return $obj[0];
		}


		public static function retrieveList($filter=''){
			$sql = 'select * from '.static::getTableName().' where 1=1 ';
			if ($filter<>''){
				$sql = $sql .' and '. $filter;
			}
			$obj = DB::openQuery($sql);
			return $obj;
		}

		public static function generateSQLInsert($obj, $db, $ignore = false){
			$classname = get_called_class();
			try{
				if ($obj == null){
					throw new Exception("[BaseModel] Object is null \n");
				}
				$sql = "";
				$strvalue = "";
				$fields = static::getFields();
				foreach ($fields as $field) {
	            
					if (!isset($obj->{$field})) continue;

					if ($sql<>""){
						$sql = $sql . ",";
						$strvalue = $strvalue . ",";
					}
				    $sql = $sql. $field;
					// if (!isset($obj->{$field}))
					// 	throw new Exception("undeclared property $field on object $classname", 1);
					// $strvalue = $strvalue. "'". $obj->{$field} ."'";
					$strvalue = $strvalue. $db->quote($obj->{$field} );
				}
				
				if ($ignore) {
					$sql = "insert ignore into ". static::getTableName() . "(" . $sql .")";		
				}else{
					$sql = "insert into ". static::getTableName() . "(" . $sql .")";
				}
				$sql = $sql. "values(" . $strvalue . ");";
		
				return $sql;
			}catch(Exception $e){
				throw $e ;
			}
		}

		public static function generateSQLUpdate($obj, $db){
			$strvalue = "";
			$fields = static::getFields();
			$classname = get_called_class();
			foreach ($fields as $field) {
				if ($field == "id") continue;
				if (!isset($obj->{$field})) continue;
				// if (!isset($obj->{$field}))
				// 	throw new Exception("undeclared property $field on object $classname", 1);

				if ($strvalue<>""){
					$strvalue = $strvalue . ",";
				}

				// $strvalue = $strvalue. $field ." = '". $obj->{$field} ."'";
				$strvalue = $strvalue. $field ." = ". $db->quote($obj->{$field});
			}
			$sql = "update ". static::getTableName() . " set " . $strvalue;
			$sql = $sql. " where id = " . $db->quote($obj->id) . ";";
			return $sql;
		}

		public static function generateSQLUpsert($obj, $db){
			$classname = get_called_class();
			try{
				if ($obj == null){
					throw new Exception("[BaseModel] Object is null \n");
				}
				$sql = "";
				$sqlupdate = "";
				$strvalue = "";
				$fields = static::getFields();
				foreach ($fields as $field) {
	            
					if (!isset($obj->{$field})) continue;

					if ($sql<>""){
						$sql = $sql . ",";
						$strvalue = $strvalue . ",";	
					}

					if ($sqlupdate <> ""){
						$sqlupdate = $sqlupdate . ",";
					}
				    $sql = $sql. $field;

					if ($field != static::getPrimaryKey()){
						$sqlupdate = $sqlupdate . $field . " = values(" . $field . ")";
					}
		
					$strvalue = $strvalue. $db->quote($obj->{$field} );
				}
				
				if ($ignore) {
					$sql = "insert into ". static::getTableName() . "(" . $sql .")";		
				}else{
					$sql = "insert into ". static::getTableName() . "(" . $sql .")";
				}
				$sql = $sql. "values(" . $strvalue . ") ";

				if ($sqlupdate <> ""){
					$sql = $sql . " ON DUPLICATE KEY UPDATE " . $sqlupdate;
				}
		
				$sql = $sql . ";";

				return $sql;
			}catch(Exception $e){
				throw $e ;
			}
		}

		public static function generateSQLDelete($filter){
			return "delete from " . static::getTableName() . " where " . $filter .";";
		}

		
		public static function generateSQL($obj, $db){
			if (static::always_insert()){ //defined ID/or different primary 
				// return static::generateSQLInsert($obj, $db, true);
				return static::generateSQLUpsert($obj, $db);
			}else{
				if (static::isNewTransaction($obj)) {

					if (!static::is_primary_int()) {
						$obj->id = DB::GUID();
					} 
					
					return static::generateSQLInsert($obj, $db);
				}else{
					return static::generateSQLUpdate($obj, $db);
				}
			}
		}


		//temp set all to insert ignore
		public static function saveObjToDB($obj, $db){
			try {
				$sql = static::generateSQL($obj, $db);

				// echo $sql;
				$int = $db->prepare($sql)->execute();
			} catch (Exception $e) {
				echo $sql;
				// $db->rollback(); //handle rollback diluar
				throw $e;
			}
		}

		
		public static function isNewTransaction($obj){
			if (isset($obj->id)){
				$do_insert = ($obj->id == "") || ($obj->id == "0");
			}else{
				$do_insert = true;
			}
			return $do_insert;
		}


		// public static function setIDByUID($obj){
		// 	$obj->id = 0; //default insert, sampai ditemukan UID di DB
		// 	// $obj->name = "xxx";

		// 	$hasUID = false;
		// 	$fields = static::getFields();
		// 	foreach ($fields as $field) {
		// 		if ($field == "uid") $hasUID = true;
		// 	}
		// 	if (!$hasUID) return; //hanya id yg dicek

		// 	if (!isset($obj->uid))	return;
		// 	if (($obj->uid == null) || ($obj->uid == '')) return;

		// 	$sql = "select id from ".static::getTableName()." where uid= '" .$obj->uid."'";


		// 	$dbobj = DB::openQuery($sql);
		// 	if (isset($dbobj[0])) {
		// 		if (isset($dbobj[0]->id)){
		// 			$obj->id = $dbobj[0]->id;
		// 		}
		// 	}
		// }

		// public static function generateSQL($obj, $db){
		// 	if (isset($obj->restclient)){
		// 		if ($obj->restclient){
		// 			static::setIDByUID($obj);
		// 		}
		// 	}

		// 	if (static::isNewTransaction($obj)) {
		// 		return static::generateSQLInsert($obj, $db);
		// 	}else{
		// 		return static::generateSQLUpdate($obj, $db);
		// 	}
		// }

		// public static function saveObjToDB($obj, $db){
		// 	// $sql = static::generateSQL($obj);
		// 	try {
		// 		$sql = static::generateSQL($obj, $db);
		// 		$int = $db->prepare($sql)->execute();
		// 		if (static::isNewTransaction($obj)){
		// 			$obj->id = $db->lastInsertId();
		// 		}
		// 	} catch (Exception $e) {
		// 		echo $sql;
		// 		// $db->rollback(); //handle rollback diluar
		// 		throw $e;
		// 	}
		// }


		// public static function getIDFromUID($uid){
		// 	$sql = "select id from ".static::getTableName()." where uid= '" .$uid."'";
		// 	$dbobj = DB::openQuery($sql);
		// 	if (isset($dbobj[0])) {
		// 		if (isset($dbobj[0]->id)){
		// 			return $dbobj[0]->id;
		// 		}
		// 	}
		// }
		//master detail example :
		// public static function saveToDB($obj){
		// 	$db = new DB();
		// 	$db = $db->connect();
		// 	$db->beginTransaction();
		// 	try {
		// 		if (!static::isNewTransaction($obj)){
		// 			$sql = ModelUnits::generateSQLDelete("company_id=". $obj->id);
		// 			$db->prepare($sql)->execute();
		// 		}
		// 		static::saveObjToDB($obj, $db);
		// 		foreach($obj->items as $item){
		// 			$item->company_id = $obj->id;
		// 			ModelUnits::saveObjToDB($item, $db);
		// 		}
		// 		$db->commit();
		// 		$db = null;
		// 	} catch (Exception $e) {
		// 		$db->rollback();
		// 		throw $e;
		// 	}
		// }


	}
