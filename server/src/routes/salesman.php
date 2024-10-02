<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

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


