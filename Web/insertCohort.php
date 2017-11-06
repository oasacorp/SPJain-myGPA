<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
require 'connection.php';
insertCohort();
}

function insertCohort()
{
global $connect;
$name =$_POST["name"];
$query="INSERT into cohortlist(name) values('$name');";

mysqli_query($connect, $query) or die (mysqli_error($connect));
mysqli_close($connect);

}

?>