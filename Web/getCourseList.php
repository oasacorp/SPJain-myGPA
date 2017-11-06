<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
require 'connection.php';
getCourseList();
}

function getCourseList()
{global $connect;
$data = json_decode(file_get_contents('php://input'), true);
$srch =$data["batch_id"];
 
$query = "SELECT course_list.course_id,course_list.name,course_list.credit,course_batch.ol,course_batch.rel,course_batch.type,course_batch.term FROM course_list JOIN course_batch ON course_list.course_id = course_batch.course_id WHERE course_batch.batch_id=".$srch.";";
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
echo json_encode(array("courselist"=>$temp_array));
mysqli_close($connect);
}

?>