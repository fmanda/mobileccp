<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';

$app->get('/salesdashboardweek', function ($request, $response) {
  try{
    $str = "select cast(a.dodate as date) as orderdate, count(a.idno) as jml, floor(sum(a.amount)) as amount
            from IntacsDataUpgrade.dbo.Saldel a
            where a.Entity = '110101'
            and cast(a.DODate as date) between '2023-9-22' and '2023-9-30'
            group by cast(a.dodate as date) ";

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

$app->get('/salesdashboardmonth', function ($request, $response) {
  try{
    $str = "select cast(a.dodate as date) as orderdate, count(a.idno) as jml, floor(sum(a.amount)) as amount
            from IntacsDataUpgrade.dbo.Saldel a
            where a.Entity = '110101'
            and cast(a.DODate as date) between '2023-9-1' and '2023-9-30'
            group by cast(a.dodate as date) ";

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

$app->get('/salesdashboardsalesman', function ($request, $response) {
  try{
    $str = "select b.EmpName as salname, floor(sum(a.amount)) as amount
            from IntacsDataUpgrade.dbo.Saldel a
            inner join IntacsDataUpgrade.dbo.Employee b on a.SalId = b.EmpId
            where a.Entity = '110101'
            and cast(a.DODate as date) between '2023-9-22' and '2023-9-30'
            group by b.EmpName ";

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

