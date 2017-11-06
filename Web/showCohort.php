<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
include 'connection.php';
showCohort();
}

function showCohort()
{global $connect;
$data = json_decode(file_get_contents('php://input'), true);
$srch =$data["search"];
$query = "Select * FROM cohortlist WHERE (name LIKE '".$srch."');";

$result=mysqli_query($connect,$query);
$number_of_rows=mysqli_num_rows($result);
$temp_array=array();

if($number_of_rows > 0)
{
while ($row=mysqli_fetch_assoc($result)){
$temp_array[]=$row;
}
}
header('Content-Type: application/json');
echo json_encode(array("cohort"=>$temp_array));
mysqli_close($connect);
}

?>