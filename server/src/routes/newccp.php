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
              . " and b.datetr is not null"
              . " and (lower(c.[Ship Name]) like '%". strtolower($filtertxt) . "%'"
              . " or lower(c.PartnerName) like '%". strtolower($filtertxt) . "%'"
              . " or lower(d.EmpName) like '%". strtolower($filtertxt) . "%')";

    $str = "select TOP 1000 
            a.idno, a.notr, isnull(b.datetr, a.datetr) as datetr, a.dabin, a.description, a.entity, 
            d.EmpName as salesman, c.[Ship Name] as shipname, c.partnername, h.[Ship Address] as shipaddress,
            b.remark, e.ccpschname, b.flagdikunjungi, g.ccptypename, f.markname, isnull(b.lat,0) as lat, isnull(b.lng, 0) as lng, b.uid
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


$app->get('/visitplan/{salid}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');

    
    $filter = " where a.salid = '" . $salid . "'" 
            . " and cast(a.datetr as date) between cast(getdate() as date) and cast(getdate()+30 as date)";

    $str = "select cast(a.datetr as date) as plandate, b.idno, b.shipid
            from newccp a
            inner join NEWCCPDet b on a.idno = b.IDNo " .$filter;


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




$app->get('/visitimage/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $str = "SELECT encode(img1, 'base64')  as img1 FROM visitimage where visit_id = " . $id ;
    $obj =  DB::openQuery($str);
    $img = $obj[0]->img1;


    echo '<img crossorigin=""  src="data:image/jpeg;base64,'.$img.'"/>';

    return $response->withHeader("Content-Type", "image/jpeg");
		// return $response->withHeader('Content-Type', 'image/jpeg');
    // return $response->withHeader('Content-Type', 'text/html');
	}catch(Exception $e){
    $msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
			->withHeader('Content-Type', 'text/html');
	}
});



$app->get('/visitimageurl/{id}', function ($request, $response, $args) {
	try{
    $id = $request->getAttribute('id');
    $str = "SELECT imgpath1, imgpath2 FROM visitimage where visit_id = " . $id . "  order by imgpath1 desc";
    $obj =  DB::openQuery($str);

    // if($obj[0] == null) $response->withHeader("Content-Type", "text/html");

    $json = json_encode($obj[0]);

    //PR bikinkan config
    // $config = parse_ini_file("../src/config.ini");
  	// $directory =  $config["upload_directory"];
    // $directory = $directory . DIRECTORY_SEPARATOR; //. $year;

    $response->getBody()->write($json);

    return $response->withHeader("Content-Type", "text/html");
		// return $response->withHeader('Content-Type', 'image/jpeg');
    // return $response->withHeader('Content-Type', 'text/html');
	}catch(Exception $e){
    $msg = $e->getMessage();
    $response->getBody()->write($msg);
		return $response->withStatus(500)
        ->withHeader('Content-Type', 'text/html');
  }
});

$app->post('/uploadimg', function ($request, $response) {
  $uploadedFiles = $request->getUploadedFiles();

  if (isset($uploadedFiles['file'])) {
      $uploadedFile = $uploadedFiles['file'];
      
      if ($uploadedFile->getError() === UPLOAD_ERR_OK) {
          // $directory = 'C:\Sharedprojects\mobileccp\server\uploads';
          //$directory = __DIR__ . '/uploads';  
          // $filename = moveUploadedFile($directory, $uploadedFile);

          $filename = processFile($uploadedFile);

          $response->getBody()->write($filename);
          return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
      }
  }

  return $response->withStatus(500)
    ->withHeader('Content-Type', 'text/html');
  
});


// function moveUploadedFile($directory, $uploadedFile)
// {
//     $filename = $uploadedFile->getClientFilename();
//     $filePath = $directory . DIRECTORY_SEPARATOR . $filename;


//     $uploadedFile->moveTo($filePath);
//     return $filename; 
// }


function processFile($uploadedFile) {
    // $ext = '.jpg';

    $config = parse_ini_file("../src/config.ini");
  	$directory =  $config["upload_directory"];

  	$directory = $directory . DIRECTORY_SEPARATOR; //. $year;

  	if (!file_exists($directory)) {
  		mkdir($directory, 0777, true);
  	}

    $filename = $uploadedFile->getClientFilename(); //  . $ext;


    // $filename = str_replace('tmp', '', $filename);     
    // $filename = str_replace('..', '.', $filename); 
    $filename = $directory . DIRECTORY_SEPARATOR . $filename;
    // if (strpos($filename, 'tmp') === 0) {
    //   $filename = substr($filename, strlen('tmp'));
    // }

    $uploadedFile->moveTo($filename);
    return $filename;

}




$app->get('/checkconfig', function (Request $request, Response $response, $args) {
  $config = parse_ini_file("../src/config.ini");
  $directory =  $config["upload_directory"];

  $response->getBody()->write($directory);    
  return $response;
});


$app->get('/image/{filename}', function (Request $request, Response $response, $args) {
  $filename = $args['filename'];

  $config = parse_ini_file("../src/config.ini");
  $directory =  $config["upload_directory"];
  $directory = $directory . DIRECTORY_SEPARATOR;

  $imagePath = $directory . $filename . ".jpg"; // Adjust the path as needed

  // Check if the file exists
  if (!file_exists($imagePath)) {
    $response->getBody()->write('Image not found' . $imagePath);
    return $response->withStatus(500)->withHeader('Content-Type', 'text/html');
  }

  // Set the response headers
  $response = $response->withHeader('Content-Type', 'image/jpeg');

  // Read the file content
  $fileContent = file_get_contents($imagePath);
  $response->getBody()->write($fileContent);
  
  return $response;
});