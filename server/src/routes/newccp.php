<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelNewCCP.php';

$app->get('/newccp/{entity}/{dt1}/{dt2}[/{filtertxt}]', function ($request, $response) {
  try{
    $entity = $request->getAttribute('entity');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    $filtertxt = $request->getAttribute('filtertxt');

    $filter = " where a.entity = '" . $entity . "'" 
              . " and cast(a.datetr as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" 
              . " and (lower(c.[Ship Name]) like '%". strtolower($filtertxt) . "%'"
              . " or lower(c.PartnerName) like '%". strtolower($filtertxt) . "%'"
              . " or lower(d.EmpName) like '%". strtolower($filtertxt) . "%')";

    $str = "select TOP 1000 
            a.idno, a.NoTR, a.DateTR, a.Dabin, a.Description, a.Entity, 
            d.EmpName as Salesman, c.[Ship Name] as ShipName, c.PartnerName, 
            b.Remark, e.CCPSchName, b.FlagDikunjungi, g.CCPTypeName, f.MarkName,  b.lat, b.Lng
            from NEWCCP a
            inner join NEWCCPDet b on a.IDNo = b.IDNo
            inner join CustPartnerShip c on b.ShipID = c.ShipId
            inner join SalesComboView d on a.SalID = d.EmpId
            left join CCPSch e on b.CCPSCH = e.CCPSch
            left join NewCCPMark f on b.Mark = f.Mark
            left join NewCCPType g on b.CCPType = g.CCPType"
            . $filter .
            "order by a.DateTR desc ";            
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


$app->post('/batchnewccp', function ($request, $response) {
	$json = $request->getBody();
	$obj = json_decode($json);
	try{
		ModelNewccp::saveToDBBatch($obj);
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

$app->get('/newccp/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $data = ModelNewccp::retrieve($id);
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



$app->get('/newccpdownload/{salid}/{dt1}/{dt2}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    
    $filter = " salid = '" . $salid . "'" 
              . " and cast(datetr as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" ;

    $data = ModelNewccp::retrieveList($filter);

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



$app->get('/ccpmark', function ($request, $response) {
  try{
    $entity = $request->getAttribute('entity');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    $filtertxt = $request->getAttribute('filtertxt');


    $str = "select mark, markname from NewCCPMark where markname not like 'PID%'";            
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



$app->get('/ccpsch', function ($request, $response) {
  try{
    $entity = $request->getAttribute('entity');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    $filtertxt = $request->getAttribute('filtertxt');


    $str = "select ccpsch, ccpschname from CCPSch where CCPSchName not like 'PID%'";            
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

