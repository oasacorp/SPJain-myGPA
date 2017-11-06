<?php
if($_SERVER["REQUEST_METHOD"]=="POST"){
require 'connection.php';
getUserData();
}

function getUserData()
{global $connect;
$data = json_decode(file_get_contents('php://input'), true);
$srch =$data["userid"];
$query = "SELECT userdata.userid, userdata.course_id, userdata.course_id, userdata.credit, course_list.name FROM userdata JOIN course_list WHERE userdata.id=".$srch.";";
$result=mysqli_query($connect,$query);
$number_of_rows=mysqli_num_rows($result);
$temp_array=array();
$echo query;
if($number_of_rows > 0)
{
while ($row=mysqli_fetch_assoc($result)){
$temp_array[]=$row;
}
}
header('Content-Type: application/json');
echo json_encode(array("courselist"=>$temp_array));
mysqli_close($connect);
}

?>