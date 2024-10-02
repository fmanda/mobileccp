<?php
	require_once '../src/models/BaseModel.php';

	class ModelUsers extends BaseModel{
		public static function always_insert() { return false; }
		public static function is_primary_int(){ return true; }


		public static function getTableName(){
			return 'users';
		}
		public static function getFields(){
			return array(
				"username", "userpassword", "project_code"
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

		public static function deleteFromDB($id){
			try{
				$obj = parent::retrieve($id);
				$str = static::generateSQLDelete("id=". $id);
				DB::executeSQL($str);
			} catch (Exception $e) {
				$db->rollback();
				throw $e;
			}
		}

		public static function retrieveLogin($username, $password){
			$obj = DB::openQuery("select a.*, b.project_name from ".static::getTableName() . " a "
				." left join project b on a.project_code = b.project_code "
				." where username = '" . $username . "'"
				." and userpassword = '" . $password . "'"
			);
			if (isset($obj[0])) return $obj[0];
		}
	}
