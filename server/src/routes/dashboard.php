<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';

$app->get('/salesdashboardweek', function ($request, $response) {
  try{
    $str = "select cast(orderdate as date) as orderdate, count(id) as jml, round(sum(amt)) as amount
            from salesorder
            where cast(orderdate as date) between DATE_SUB(now(), INTERVAL 7 DAY) and DATE_ADD(now(), INTERVAL 1 DAY)
            group by cast(orderdate as date)";

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
    $str = "select cast(orderdate as date) as orderdate, count(id) as jml, round(sum(amt)) as amount
            from salesorder
            where cast(orderdate as date) between DATE_SUB(now(), INTERVAL 30 DAY) and DATE_ADD(now(), INTERVAL 1 DAY)
            group by cast(orderdate as date)";

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

$app->get('/salesdashboardkecamatan', function ($request, $response) {
  try{
    $str = "select b.kecamatan, round(sum(amt)) as amount
            from salesorder a
            inner join customer b on a.customer_id = b.id
            where cast(orderdate as date) between DATE_SUB(now(), INTERVAL 30 DAY) and DATE_ADD(now(), INTERVAL 1 DAY)
            group by kecamatan";

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

