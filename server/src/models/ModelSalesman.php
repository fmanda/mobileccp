<?php
	require_once '../src/models/BaseModel.php';

	class ModelSalesman extends BaseModel{
		public static function always_insert() { return true; }

		public static function getTableName(){
			return 'salesman';
		}
		public static function getFields(){
			return array(
				"id", "project_code", "nama", "kode",
				"username", "password"
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

		public static function deleteFromDB($id){
			try{
				$obj = parent::retrieve($id);
				$str = static::generateSQLDelete("id='".  $id ."'");
				DB::executeSQL($str);
			} catch (Exception $e) {
				throw $e;
			}
		}

		public static function retrieveLogin($username, $password){
			$obj = DB::openQuery("select * from ".static::getTableName()
				." where username = '" . $username . "'"
				." and password = '" . $password . "'"
			);
			if (isset($obj[0])) return $obj[0];
		}
	}
