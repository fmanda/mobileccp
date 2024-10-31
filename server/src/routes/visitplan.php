<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelVisitPlan.php';


$app->post('/batchvisitplan', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelVisitpLan::saveToDBBatch($obj);
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

$app->get('/visitplan/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelVisitpLan::retrieve($id);
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


$app->get('/visitplandownload/{salid}/{dt1}/{dt2}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    
    $filter = " salid = '" . $salid . "'" 
              . " and cast(datetr as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" ;

    $data = ModelVisitpLan::retrieveList($filter);

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



$app->get('/visitplanbysal/{salid}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');

    $filter = " salid = '" . $salid . "'" 
            . " and cast(datetr as date) between cast(getdate() as date) and cast(getdate()+30 as date)";

    $data = ModelVisitpLan::retrieveList($filter);

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
