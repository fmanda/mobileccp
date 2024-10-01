<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelVisit.php';

$app->get('/visitbyperiod/{projectcode}/{dt1}/{dt2}[/{filtertxt}]', function ($request, $response) {
  try{
    $projectcode = $request->getAttribute('projectcode');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    $filtertxt = $request->getAttribute('filtertxt');

    $filter = " where a.project_code = '" . $projectcode . "'" 
              . " and cast(visitdate as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" 
              . " and (lower(b.nama) like '%". strtolower($filtertxt) . "%'"
              . " or lower(c.nama) like '%". strtolower($filtertxt) . "%')";

    $filterOrder = " where a.project_code = '" . $projectcode . "'" 
              . " and cast(orderdate as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" 
              . " and (lower(b.nama) like '%". strtolower($filtertxt) . "%'"
              . " or lower(c.nama) like '%". strtolower($filtertxt) . "%')";


    $str = "select a.id, a.visitno, a.visitdate, a.latitude, a.longitude, b.nama as customer, c.nama as salesman, b.alamat, b.phone
            from visit a 
            left join customer b on a.customer_id = b.id
            left join salesman c on a.salesman_id = c.id " .$filter.
            " union all
            select a.id, a.orderno, a.orderdate, a.latitude, a.longitude, b.nama as customer, c.nama as salesman, b.alamat, b.phone
            from salesorder a 
            left join customer b on a.customer_id = b.id
            left join salesman c on a.salesman_id = c.id " .$filterOrder;


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


$app->post('/batchvisit', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelVisit::saveToDBBatch($obj);
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

$app->get('/visit/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelVisit::retrieve($id);
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

