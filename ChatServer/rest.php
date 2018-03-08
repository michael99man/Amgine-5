<?php
error_reporting( E_ALL );

// get the HTTP method, path and body of the request
$method = $_SERVER['REQUEST_METHOD'];
$request = explode('/', trim($_SERVER['PATH_INFO'],'/'));
$input = json_decode(file_get_contents('php://input'),true);

// connect to the mysql database
$link = mysqli_connect('localhost', 'restServer', 'restAPIpass', 'ChatData');
mysqli_set_charset($link,'utf8');

// retrieve the table and key from the path
$table = preg_replace('/[^a-z0-9_]+/i','',array_shift($request));
$key = array_shift($request)+0;

//format is http://michaelman.net/api.php/{$table}/{$id}

//don't analyze JSON input for get (there is none)
if($method != 'GET'){
    // escape the columns and values from the input object
    $columns = preg_replace('/[^a-z0-9_]+/i','',array_keys($input));
    $values = array_values($input);

    // build the SET part of the SQL command
    $set = '';
    for ($i=0;$i<count($columns);$i++) {
        $set.=($i>0?',':'').'`'.$columns[$i].'`=';
        $set.=($values[$i]===null?'NULL':'"'.mysqli_real_escape_string($link,$values[$i][0]).'"');
    }
} else {
    $set = "";
    $and = false;
    foreach($_GET as $k => $value){
        if($and){
            $set .= " AND {$k}='{$value}'";
        } else {
            $op = "=";
            if(strpos($k, 'TIMESTAMPDIFF') !== false){
                $op = "<";
            }
            $set .= " WHERE {$k}{$op}'{$value}'";
        }
        $and = true;
    }
    if($table == "messages"){
        $set .= " ORDER BY Date DESC";
    } else if($table == "users"){
        $set .= " ORDER BY date_joined ASC";
    }
}

// create SQL based on HTTP method
switch ($method) {
    case 'GET':
        $sql = "select * from `$table`".$set; break;
    case 'PUT':
        $sql = "update `$table` set $set where id=$key"; break;
        //POST autoincrements (puts at the end of table)
    case 'POST':
        $sql = "insert into `$table` set $set"; echo $sql; break;
    case 'DELETE':
        $sql = "delete `$table` where id=$key"; break;
}

//TESTING: echo $sql . "<br>";

// excecute SQL statement
$result = mysqli_query($link,$sql);

// die if SQL statement failed
if (!$result) {
    http_response_code(404);
    die(mysqli_error($link));
}

// print results, insert id or affected row count
if ($method == 'GET') {
    if (!$key) echo '[';
    for ($i=0;$i<mysqli_num_rows($result);$i++) {
        echo ($i>0?',':'').json_encode(mysqli_fetch_object($result));
    }
    if (!$key) echo ']';
} elseif ($method == 'POST') {
    echo mysqli_insert_id($link);
} else {
    echo mysqli_affected_rows($link);
}

// close mysql connection
mysqli_close($link);

?>