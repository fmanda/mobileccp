<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\UploadedFileInterface;

require '../vendor/autoload.php';
require_once '../src/classes/DB.php';
require_once '../src/models/ModelVisit.php';


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


$app->get('/visitdownload/{salid}/{dt1}/{dt2}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');
    $dt1 = $request->getAttribute('dt1');
    $dt2 = $request->getAttribute('dt2');
    
    $filter = " salid = '" . $salid . "'" 
              . " and cast(visitdate as date) between '".  $dt1 . "'" 
              . " and '". $dt2 . "'" ;

    $data = ModelVisit::retrieveList($filter);

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



$app->get('/visitbysal/{salid}', function ($request, $response) {
  try{
    $salid = $request->getAttribute('salid');

    $filter = " salid = '" . $salid . "'" 
            . " and cast(visitdate as date) between cast(getdate() as date) and cast(getdate()+30 as date)";

    $data = ModelVisit::retrieveList($filter);

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



$app->get('/planmark', function ($request, $response) {
  try{
    $str = "select * from planmark";            
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



$app->get('/visitmark', function ($request, $response) {
  try{
    $str = "select * from visitmark";            
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

    $json = json_encode($obj[0]);
    $response->getBody()->write($json);

    return $response->withHeader("Content-Type", "text/html");
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
          $filename = processFile($uploadedFile);
          $response->getBody()->write($filename);
          return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
      }
  }

  return $response->withStatus(500)
    ->withHeader('Content-Type', 'text/html');
  
});




function processFile($uploadedFile) {
    // $ext = '.jpg';

    $config = parse_ini_file("../src/config.ini");
  	$directory =  $config["upload_directory"];
  	$directory = $directory . DIRECTORY_SEPARATOR; //. $year;
  	if (!file_exists($directory)) {
  		mkdir($directory, 0777, true);
  	}

    $filename = $uploadedFile->getClientFilename(); //  . $ext;
    $filename = $directory . DIRECTORY_SEPARATOR . $filename;
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

  $imagePath = $directory . $filename . ".jpg"; 

  if (!file_exists($imagePath)) {
    $response->getBody()->write('Image not found' . $imagePath);
    return $response->withStatus(500)->withHeader('Content-Type', 'text/html');
  }

  $response = $response->withHeader('Content-Type', 'image/jpeg');

  $fileContent = file_get_contents($imagePath);
  $response->getBody()->write($fileContent);  
  return $response;
});