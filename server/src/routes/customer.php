<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use \Firebase\JWT\JWT;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelCustomer.php';


$app->get('/customer/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelCustomerDelivery::retrieve($id);
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

$app->get('/customerarea/{areano}', function ($request, $response) {
try{
	$areano = $request->getAttribute('areano');
	$data = ModelCustomerDelivery::retrieveByArea($areano);
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


$app->get('/customerentity/{entity}[/{filtertxt}]', function ($request, $response) {
	try{
		$filtertxt = $request->getAttribute('filtertxt');
		$entity = $request->getAttribute('entity');
		$data = ModelCustomerDelivery::retrieveByEntity($entity, $filtertxt);
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
