<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use \Firebase\JWT\JWT;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelProduct.php';

$app->get('/product', function ($request, $response) {
  	try{
		$data = ModelProduct::retrieveList();
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

$app->get('/product/{sku}', function ($request, $response, $args) {
	try{
		$id = $request->getAttribute('sku');
		$data = ModelProduct::retrieve($id);
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

$app->post('/product', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelProduct::saveToDB($obj);
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



$app->post('/batchproduct', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelProduct::saveToDBBatch($obj);
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


$app->get('/product_delete/{id}', function ($request, $response) {  //if hosting not allowed del
  	try{
    	$data = ModelProduct::deleteFromDB($request->getAttribute('id'));
		return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
	}catch(Exception $e){
		$msg = $e->getMessage();
		$response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}
});


$app->get('/searchproduct/{project_code}[/{filtertxt}]', function ($request, $response) {
	try{
	  	$filtertxt = $request->getAttribute('filtertxt');
		$project = $request->getAttribute('project_code');

		$sql = "select distinct a.*
				from product a
				inner join project_principal b on a.principal = b.principal 
				where b.project_code = '" . $project . "'"
				. " and (sku like '%". $filtertxt . "%' "
				. " or nama like '%". $filtertxt . "%' "
				. " or merk like '%". $filtertxt . "%') "
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


  
$app->get('/productbyproject/{projectcode}', function ($request, $response) {
	try{
	  	$filtertxt = $request->getAttribute('projectcode');
  
		$sql = "select distinct a.*
				from product a
				inner join project_principal b on a.principal = b.principal 
				where b.project_code = '" . $filtertxt . "'";
					
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