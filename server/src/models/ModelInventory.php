<?php
	require_once '../src/models/BaseModel.php';

	class ModelInventory extends BaseModel{
		public static function getFields(){
			return array(
				"invid","partno","invname","description", "invgrp", "pclass8"
			);
		}

		public static function getPrimaryKey(){
			return "invid";
		}

		public static function retrieve($id){
			$sql = "select * from fn_inventory_bypartno('". $id."')";
			$obj = DB::openQuery($sql);			
			if (isset($obj[0])) return $obj[0];		
		}

		public static function retrieveByArea($area){
			$sql = "select * from fn_inventory('". $area . "')";			
			$objs = DB::openQuery($sql);			
			return 
			$objs;
		}
	
	}

	class ModelPriceLevelDetail extends BaseModel{
		public static function getFields(){
			return array(
				"invid","partno","pricelevel","pricelevelname", "price"
			);
		}
		public static function retrieveByArea($area){
			$sql = "select * from fn_pricelevel('". $area . "')";		
			$objs = DB::openQuery($sql);			
			return $objs;
		}
	}