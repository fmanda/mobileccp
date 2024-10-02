<?php

	class DB{
		public function connect(){
			$config = parse_ini_file("../src/config.ini");
			$dbhost = $config["server"];
			$dbname = $config['database'];
			$dbuser = $config['user'];
			// $port = $config['port'];
			$dbpassword = $config['password'];
			$connect_str = "sqlsrv:Server=$dbhost;Database=$dbname";
			$conn = new PDO($connect_str, $dbuser, $dbpassword);
			$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			return $conn;
		}

		public function showConfig(){
			print_r(parse_ini_file("../src/config.ini"));
		}

		public static function openQuery($sql){
			try{
				$db = new DB();
				$db = $db->connect();
				$stmt = $db->query($sql);
				$rows = $stmt->fetchAll(PDO::FETCH_OBJ);
				$db = null;
				return $rows;
			}catch(PDOException $e){
				echo '{"error":{"text": '. $e->getMessage() . '; sql : '.$sql.'}}' ;
				throw $e;
			}
		}

		public static function executeSQL($sql){
			$db = new DB();
			$db = $db->connect();
			$db->beginTransaction();
			try {
				$int = $db->prepare($sql)->execute();
				$db->commit();
			} catch (Exception $e) {
				$db->rollback();
				throw $e;
			}
		}

		public static function GUID_MYSQL(){
			return sprintf(
				'%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
				mt_rand(0, 0xffff), mt_rand(0, 0xffff), // 32 bits for "time_low"
				mt_rand(0, 0xffff), // 16 bits for "time_mid"
				mt_rand(0, 0x0fff) | 0x4000, // 16 bits for "time_hi_and_version", four most significant bits are 0100
				mt_rand(0, 0x3fff) | 0x8000, // 16 bits for "clk_seq_hi_res", two most significant bits are 10
				mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff) // 48 bits for "node"
			);
		}


		public static function GUID(){
			$sql = "select newid() as id";
			$data = DB::openQuery($sql);
			return $data[0]->id;
		}

		public static function paginateSQL($sql, $limit = 10, $page = 1) {
		    if ( $limit == 0 ) {
		        return $sql;
		    } else {
		        $sql = $sql . " LIMIT " . ( ( $page - 1 ) * $limit ) . ", $limit";
				return $sql;
		    }
		}

		public static function getRecordCount($sql){
			$sql = "select count(*) as total from (" . $sql . ") as t";
			$data = DB::openQuery($sql);
			return $data[0]->total;
		}

		public static function paginateQuery($sql, $limit = 10, $page = 1){

			$obj = new stdClass();
			$obj->totalrecord = static::getRecordCount($sql);

			$sql = static::paginateSQL($sql, $limit, $page);
			$obj->data = DB::openQuery($sql);

			return $obj;
		}


	}
