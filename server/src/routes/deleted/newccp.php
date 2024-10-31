<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;



require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelNewCCP.php';
require_once '../src/models/ModelRoute.php';

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
            a.idno, a.notr, isnull(b.datetr, a.datetr) as datetr, a.dabin, a.description, a.entity, 
            d.EmpName as salesman, c.[Ship Name] as shipname, c.partnername, h.[Ship Address] as shipaddress,
            b.remark, e.ccpschname, b.flagdikunjungi, g.ccptypename, f.markname,  isnull(b.lat,0) as lat, isnull(b.lng,0) as lng, b.uid
            from NEWCCP a
            inner join NEWCCPDet b on a.IDNo = b.IDNo
            inner join CustPartnerShip c on b.ShipID = c.ShipId
            left join SalesComboView d on a.SalID = d.EmpId
            left join CCPSch e on b.CCPSCH = e.CCPSch
            left join NewCCPMark f on b.Mark = f.Mark
            left join NewCCPType g on b.CCPType = g.CCPType
            inner join IntacsDataUpgrade.dbo.CustomerDelivery h on b.ShipID = h.ShipId"
            . $filter .
            "order by isnull(b.datetr, a.datetr) desc ";            
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
		ModelNewCCPDet::saveToDBBatch($obj);
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





