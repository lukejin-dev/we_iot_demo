<?php
$DB_USER_NAME="root";
$DB_USER_PASS="db_pass1234";

$nameList=array(
    "9621410276f88dac"=>"Ken's Phone",
    "a56e20019cfd6e23"=>"Station_5",
    "bc9538aa26345191"=>"Station_6",
    "7edee74fece40522"=>"Ken's Nexus7",
    "be4b9be098bb6699"=>"Station_2",
    "7d7710ba7d04fab5"=>"Station_1",
    "25fe01607fe80001"=>"Station_3",
    "518c1cfc3ae5c4ee"=>"Station_4",
    "54711d9bc010ead7"=>"Station_7",
    "C3:2E:1C:1C:50:5A"=>"Jawbone",    
    "D0:39:72:BD:6D:6D"=>"ZeroBeacon",
    "E5:CE:83:14:DC:66"=>"Estimote_1",
    "FA:A9:1F:0F:23:A8"=>"Estimote_2",
    "78:A5:04:8C:25:CC"=>"SensorTag_1",
    "78:A5:04:8C:25:F5"=>"SensorTag_2",
    "BC:6A:29:AE:CB:2C"=>"SensorTag_3",
    "78:A5:04:8C:EA:10"=>"SensorTag_4",
    "BC:6A:29:AE:DB:C7"=>"Student_21",
    "BC:6A:29:AE:D6:04"=>"Student_22",
    "BC:6A:29:AE:DA:BA"=>"Student_23"
  /*  "78:A5:04:61:49:6B"=>"Student_1",
    "78:A5:04:61:44:7C"=>"Student_2",
    "78:A5:04:61:4B:46"=>"Student_3",
    "78:A5:04:61:44:60"=>"Student_4",
    "78:A5:04:61:47:97"=>"Student_5",
    "78:A5:04:61:49:03"=>"Student_6",
    "78:A5:04:61:49:5B"=>"Student_7",
    "78:A5:04:61:49:37"=>"Student_8",
    "78:A5:04:61:49:54"=>"Student_9",
    "78:A5:04:61:44:70"=>"Student_10",
    "78:A5:04:61:49:7D"=>"Student_11"*/
    );
function replaceName($mac) {  
  Global $nameList; 
  if(!empty($nameList[$mac]))
    return $nameList[$mac];
  return "Unknown_".$mac;
}
if(!empty($_GET["showhardcode"]))
{
  echo "<table border='1>'<tr><th>Mac</th><th>Name</th></tr>";
  foreach ($nameList as $key => $value) {
    echo("<tr><td>".$key."</td><td>".$value."</td></tr>");
  }
  echo "</table>";
}
else
{
header('Content-type:application/json'); 
header('Access-Control-Allow-Origin:*');
$con = mysql_connect("127.0.0.1:3306", $DB_USER_NAME, $DB_USER_PASS);
  if (!$con)
  {
      die(json_encode(array('Error' =>'Could not connect:' . mysql_error())));
  }
  mysql_query('SET NAMES UTF8');
  mysql_select_db("protoDB",$con);
}
?>
