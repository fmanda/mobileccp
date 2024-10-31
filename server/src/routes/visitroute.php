<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelVisitRoute.php';


$app->post('/batchvisitroute', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelVisitRoute::saveToDBBatch($obj);
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

$app->get('/visitroute/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelVisitRoute::retrieve($id);
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




$app->get('/visitroutebydabin/{dabin}', function ($request, $response) {
  try{
    $dabin = $request->getAttribute('dabin');

    $filter = " dabin = '{$dabin}'"; 
    $data = ModelVisitRoute::retrieveList($filter);

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
