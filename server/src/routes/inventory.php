<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelInventory.php';

$app->get('/inventoryarea/{areano}', function ($request, $response) {
	try{
		$areano = $request->getAttribute('areano');
		$data = ModelInventory::retrieveByArea($areano);
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


$app->get('/priceleveldetailarea/{areano}', function ($request, $response) {
	try{
		$areano = $request->getAttribute('areano');
		$data = ModelPriceLevelDetail::retrieveByArea($areano);
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