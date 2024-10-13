<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelSalesOrder.php';

$app->get('/salesorderbyperiod/{entity}/{dt1}/{dt2}[/{filtertxt}]', function ($request, $response) {
  try{
    $entity = $request->getAttribute('entity');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    $filtertxt = $request->getAttribute('filtertxt');

    $filter = " where a.entity = '" . $entity . "'" 
              . " and cast(orderdate as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" 
              . " and (lower(b.[Ship Name]) like '%". strtolower($filtertxt) . "%'"
              . " or lower(c.empname) like '%". strtolower($filtertxt) . "%'"
              . " or lower(a.orderno) like '%". strtolower($filtertxt) . "%')";

    $str = "select a.*, c.empname as salesman, b.[Ship Name] as customer, b.[Ship Address] as alamat, b.[Ship Phone] as phone
            from mobile_salesorder a
            left join IntacsDataUpgrade.dbo.CustomerDelivery b on a.shipid = b.ShipId
            left join IntacsDataUpgrade.dbo.Employee c on a.salid = c.empid"
            . $filter;

    $data = DB::openQuery($str);
    
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


$app->post('/batchsalesorder', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelSalesOrder::saveToDBBatch($obj);
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

$app->get('/salesorder/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelSalesOrder::retrieve($id);
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



$app->get('/salesorderdownload/{entity}/{dt1}/{dt2}', function ($request, $response) {
  try{
    $projectcode = $request->getAttribute('entity');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    
    $filter = " entity = '" . $projectcode . "'" 
              . " and cast(orderdate as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" ;

    $data = ModelSalesOrder::retrieveList($filter);

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



$app->get('/arremain/{salid}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');
  
    $str = "select * from v_mobile_ar_remain where salid = '" . $salid . "'"; 
    $data = DB::openQuery($str);
    
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