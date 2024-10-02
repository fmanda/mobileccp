<?php
	require_once '../src/models/BaseModel.php';

	class ModelSalesman extends BaseModel{

		public static function getFields(){
			return array(
				"SalID","SalName","AreaNo","AreaName"
			);
		}


		public static function getPrimaryKey(){
			return "SalID";
		}

		public static function retrieve($id){
			$sql = "select a.EmpId as SalID, a.EmpName as Salname, b.AreaNo, b.AreaName
					from IntacsDataUpgrade.dbo.Employee a
					inner join IntacsDataUpgrade.dbo.Area b on a.EmpId = b.SalId
					where a.PSales =  1
					and a.NotActive = 0
					and a.EmpID = '" . $id . "'";			
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveList($filter=''){
			$sql = "select a.EmpId as SalID, a.EmpName as Salname, b.AreaNo, b.AreaName
					from IntacsDataUpgrade.dbo.Employee a
					inner join IntacsDataUpgrade.dbo.Area b on a.EmpId = b.SalId
					where a.PSales =  1
					and a.NotActive = 0";			
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}