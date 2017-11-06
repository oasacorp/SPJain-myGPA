<?php

define('hostname','web308.webfaction.com');
define('user','spjadmin');
define('password','1123581321');
define('databaseName','spjainmstr');

$connect=mysqli_connect(hostname, user,password,databaseName);
if (!$connect)
{
    die('Could not connect: ' . mysql_error());
}
else
{
    echo "Connected";

}
?>

