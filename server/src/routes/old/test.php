<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelTest.php';
//
// $app->get('/test_delete/{id}', function ($request, $response) {  //if hosting not allowed del
//   try{
//     $data = ModelTestHeader::deleteFromDB($request->getAttribute('id'));
// 		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
// 	}catch(Exception $e){
//     $msg = $e->getMessage();
//     $response->getBody()->write($msg);
// 		return $response->withStatus(500)
// 			->withHeader('Content-Type', 'text/html');
// 	}
// });

// $app->get('/test', function ($request, $response) {
//   try{
//
//     $data = DB::openQuery('select * from ttest');
//
//
//     $json = json_encode($data);
//     $response->getBody()->write($json);
//
// 		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
// 	}catch(Exception $e){
//     $msg = $e->getMessage();
//     $response->getBody()->write($msg);
// 		return $response->withStatus(500)
// 			->withHeader('Content-Type', 'text/html');
// 	}
// });

$app->get('/test', function ($request, $response) {
  try{
    $data = ModelTestHeader::retrieveList();
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
//
$app->get('/test/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelTestHeader::retrieve($id);
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
//
$app->post('/test', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelTestHeader::saveToDB($obj);
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


$app->get('/testDate', function ($request, $response) {
	try{
		date_default_timezone_set("UTC");
		// date_default_timezone_set('Asia/Jakarta');
		$date = new \DateTime();
		$exitdate = $date->format('Y-m-d H:i:s');
	
		$response->getBody()->write($exitdate);
	
		return $response;
	}catch(Exception $e){
		$msg = $e->getMessage();
		$response->getBody()->write($msg);
			return $response->withStatus(500)
				->withHeader('Content-Type', 'text/html');
	}
  });