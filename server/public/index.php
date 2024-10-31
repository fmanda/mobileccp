<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Slim\Factory\AppFactory;
use Slim\Exception\HttpNotFoundException;

use Tuupola\Middleware\CorsMiddleware;


require __DIR__ . '/../vendor/autoload.php';
$config = parse_ini_file("../src/config.ini");

$app = AppFactory::create();

$app->addBodyParsingMiddleware();
$app->addRoutingMiddleware();

$app->setBasePath('/public');

// build https 
// $app->add(new Tuupola\Middleware\CorsMiddleware([
//     "origin" => ["*"],
//     "methods" => ["GET", "POST", "PUT", "PATCH", "DELETE"],
//     "headers.allow" => ["Authorization", "If-Match", "If-Unmodified-Since"],
//     "headers.expose" => [],
//     "credentials" => true,
//     "cache" => 0,
// ]));


// $app->add(new Tuupola\Middleware\JwtAuthentication([
//     "regexp" => "/(.*)/", //default format Bearer <token>
//     "secret" => $config["secret"],
//     "algorithm" => ["HS256"],
//     "rules" => [
//         new Tuupola\Middleware\JwtAuthentication\RequestPathRule([
//             "ignore" => [
//               $app->getBasePath() . "/check",
//               $app->getBasePath() . "/login"
//             ]
//         ]),
//         new Tuupola\Middleware\JwtAuthentication\RequestMethodRule([
//             "ignore" => ["OPTIONS"]
//         ])
//     ]
// ]));

$errorMiddleware = $app->addErrorMiddleware(true, true, true);
$errorMiddleware->setErrorHandler(
    HttpMethodNotAllowedException::class,
    function (Request $request, Throwable $exception, bool $displayErrorDetails) use ($app): Response {
        $response = $app->getResponseFactory()->createResponse();
        $response->getBody()->write("Method Not Allowed");
        return $response->withStatus(405);
    }
);


$app->options('/{routes:.+}', function ($request, $response, $args) {
    return $response;
});

//for deff
$app->add(function ($request, $handler) {
    $response = $handler->handle($request);
    return $response
            ->withHeader('Access-Control-Allow-Origin', '*')
            ->withHeader('Access-Control-Allow-Headers', 'X-Requested-With, Content-Type, Accept, Origin, Authorization')
            ->withHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH')
            ->withHeader("Access-Control-Allow-Credentials", "true");
            // ->withHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');
});


$app->get('/check', function (Request $request, Response $response, $args) {
    $ipAddress = $_SERVER['SERVER_ADDR'];
    $obj = new stdClass();
    $obj->msg = "Server Ready ". $ipAddress;
    $json = json_encode($obj);    
    $response->getBody()->write($json);
    return $response->withHeader('Content-Type', 'application/json;charset=utf-8');
});


require '../src/routes/salesman.php';
require '../src/routes/customer.php';
require '../src/routes/inventory.php';
require '../src/routes/salesorder.php';
require '../src/routes/visitroute.php';
require '../src/routes/visitplan.php';
require '../src/routes/visit.php';
require '../src/routes/dashboard.php';

// $app->map(['GET', 'POST', 'PUT', 'DELETE', 'PATCH'], '/{routes:.+}', function($req, $res) {
//     $handler = $this->notFoundHandler; // handle using the default Slim page not found handler
//     return $handler($req, $res);
// });

// $app->options('/{routes:.+}', function ($request, $response, $args) {
//     return $response;
// });

$app->run();
