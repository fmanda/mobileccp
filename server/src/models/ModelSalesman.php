<?php
	require_once '../src/models/BaseModel.php';

	class ModelSalesman extends BaseModel{

		public static function getFields(){
			return array(
				"SalID","SalName","AreaNo","AreaName","Entity"
			);
		}

		public static function getPrimaryKey(){
			return "SalID";
		}

		public static function retrieve($id){
			$sql = "select * from v_mobile_salesman
					where SalID = '" . $id . "'";			
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveList($filter=''){
			$sql = "select * from v_mobile_salesman";			
			$objs = DB::openQuery($sql);			
			return $objs;
		
		}


		public static function retrieveLogin($username, $password){
			$sql = "select * from v_mobile_salesman"
				." where salid = '" . $username . "'";
			
			$obj = DB::openQuery($sql);
			if (isset($obj[0])) return $obj[0];
		}
	}