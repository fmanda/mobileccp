<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;
use \Firebase\JWT\JWT;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelSalesman.php';
require_once '../src/models/ModelEmployee.php';

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


$app->get('/salesman/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelSalesman::retrieve($id);
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
		$config = parse_ini_file("../src/config.ini");
		$username = $request->getAttribute('username');
		$password = $request->getAttribute('password');
		$data = ModelSalesman::retrieveLogin($username, $password);

		if(!$data) {
			$response->getBody()->write('These credentials do not match our records username');
			return $response->withStatus(401)
					->withHeader('Content-Type', 'text/html');
		}
		$token = array(
			'id' =>  $data->salid,
			'username' => $data->salid
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


//temporary, nanti buat admin

$app->post('/login', function (Request $request, Response $response, array $args) {
	$config = parse_ini_file("../src/config.ini");
  
	$json = $request->getBody();
	$obj = json_decode($json);

	
	$user = ModelEmployee::retrieveLogin($obj->username, $obj->password);
	if(!$user) {
	  $response->getBody()->write('These credentials do not match our records username');
	  return $response->withStatus(401)
			  ->withHeader('Content-Type', 'text/html');
	}
	$token = array(
		'id' =>  $user->empid,
		'username' => $user->empid
	);
	$token = JWT::encode($token,  $config["secret"], "HS256");
  
	$result = new stdClass();
	$result->token = $token;
	$result->user = $user;
	$result = json_encode($result);
  
	$response->getBody()->write($result);
	return $response->withHeader('Content-Type', 'multipart/form-data');
	// return $this->response->withJson(['status' => 'success','data'=>$user, 'token' => $token]);
  });
  

