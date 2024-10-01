<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use \Firebase\JWT\JWT;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelSalesman.php';

$app->get('/salesman', function ($request, $response) {
  try{
    $data = ModelSalesman::retrieveList();
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

$app->get('/login_salesman/{username}/{password}', function ($request, $response, $args) {
	try{
    $username = $request->getAttribute('username');
    $password = $request->getAttribute('password');
    $data = ModelSalesman::retrieveLogin($username, $password);

	if(!$data) {
		$response->getBody()->write('These credentials do not match our records username');
		return $response->withStatus(401)
				->withHeader('Content-Type', 'text/html');
	  }
	$token = array(
		'id' =>  $data->id,
		'username' => $data->username
	);
	$token = JWT::encode($token,  $config["secret"], "HS256");

	$data->token = $token;
	
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

$app->post('/salesman', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelSalesman::saveToDB($obj);
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

$app->get('/salesman_delete/{id}', function ($request, $response) {  //if hosting not allowed del
  try{
    $data = ModelSalesman::deleteFromDB($request->getAttribute('id'));
		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
    $msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}
});

$app->post('/batchsalesman', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelSalesman::saveToDBBatch($obj);
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



$app->get('/salesman/{projectcode}', function ($request, $response) {
	try{
	  	$projectcode = $request->getAttribute('projectcode');
	
		$data = ModelSalesman::retrieveList('project_code ="' . $projectcode .'"');
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