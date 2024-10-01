<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use \Firebase\JWT\JWT;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelCustomer.php';

$app->get('/customer', function ($request, $response) {
  try{
    $data = ModelCustomer::retrieveList();
    $json = json_encode($data);
    $response->getBody()->write($json);

		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
    $msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}
});

$app->get('/customer/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelCustomer::retrieve($id);
    $json = json_encode($data);
    $response->getBody()->write($json);
		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
    $msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}
});

$app->post('/customer', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelCustomer::saveToDB($obj);
    $json = json_encode($obj);
    $response->getBody()->write($json);
    return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
		$msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}

});

$app->get('/customer_delete/{id}', function ($request, $response) {  //if hosting not allowed del
  try{
    $data = ModelCustomer::deleteFromDB($request->getAttribute('id'));
		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
    $msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}
});


$app->post('/batchcustomer', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelCustomer::saveToDBBatch($obj);
    $json = json_encode($obj);
    $response->getBody()->write($json);
    return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
		$msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}

});



$app->get('/searchcustomer/{project_code}[/{filtertxt}]', function ($request, $response) {
	try{
	  	$filtertxt = $request->getAttribute('filtertxt');
		$project = $request->getAttribute('project_code');
  
		$sql = "select * from customer where project_code = '". $project . "'"
					. " and (nama like '%". $filtertxt . "%' "
					. " or alamat like '%". $filtertxt . "%' "
					. " or kecamatan like '%". $filtertxt . "%' "
					. " or kelurahan like '%". $filtertxt . "%') "
					. " limit 300";
					
		$data = DB::openQuery($sql);
		$json = json_encode($data);
		$response->getBody()->write($json);

		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	  }catch(Exception $e){
		$msg = $e->getMessage();
		$response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	  }
  });

  
$app->get('/searchnewcustomer/{project_code}[/{filtertxt}]', function ($request, $response) {
	try{
	  	$filtertxt = $request->getAttribute('filtertxt');
		$project = $request->getAttribute('project_code');
  
		$sql = "select * from customer where project_code = '". $project . "'"
					. " and is_new = 1 and (nama like '%". $filtertxt . "%' "
					. " or alamat like '%". $filtertxt . "%' "
					. " or kecamatan like '%". $filtertxt . "%' "
					. " or kelurahan like '%". $filtertxt . "%') "
					. " limit 300";
					
		$data = DB::openQuery($sql);
		$json = json_encode($data);
		$response->getBody()->write($json);

		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	  }catch(Exception $e){
		$msg = $e->getMessage();
		$response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	  }
  });

  $app->get('/customerbyproject/{projectcode}', function ($request, $response) {
	try{
		$filtertxt = $request->getAttribute('projectcode');
	  	$data = ModelCustomer::retrieveList("project_code='" . $filtertxt . "'");
	  	$json = json_encode($data);
	  	$response->getBody()->write($json);
  
			return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
		}catch(Exception $e){
		$msg = $e->getMessage();
		$response->getBody()->write($msg);
			return $response->withStatus(500)
				->withHeader('Content-Type', 'text/html');
		}
  });


// $app->post('/login', function (Request $request, Response $response, array $args) {
//   $config = parse_ini_file("../src/config.ini");

//   $json = $request->getBody();
// 	$obj = json_decode($json);
//   $user = ModelUsers::retrieveLogin($obj->username, $obj->password);
//   if(!$user) {
//     $response->getBody()->write('These credentials do not match our records username');
//     return $response->withStatus(401)
// 			->withHeader('Content-Type', 'text/html');
//   }
//   $token = array(
//       'id' =>  $user->id,
//       'username' => $user->username
//   );
//   $token = JWT::encode($token,  $config["secret"], "HS256");

//   $result = new stdClass();
//   $result->token = $token;
//   $result->user = $user;
//   $result = json_encode($result);

//   $response->getBody()->write($result);
//   return $response->withHeader('Content-Type', 'multipart/form-data');
//   // return $this->response->withJson(['status' => 'success','data'=>$user, 'token' => $token]);
// });
