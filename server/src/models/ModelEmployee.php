<?php
	require_once '../src/models/BaseModel.php';

	class ModelEmployee extends BaseModel{

		public static function getFields(){
			return array(
				"empid","empname","entity", "entityname"
			);
		}

		public static function getPrimaryKey(){
			return "empid";
		}

		public static function retrieve($id){
			$sql = "select * from v_mobile_employee
					where empid = '" . $id . "'";			
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveList($filter=''){
			$sql = "select * from v_mobile_employee";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}


		public static function retrieveLogin($username, $password){
			$obj = DB::openQuery("select * from v_mobile_employee"
				." where empid = '" . $username . "'"
			);
			if (isset($obj[0])) return $obj[0];
		}
	}