<?php 
$DB_USER_NAME="root";
$DB_USER_PASS="db_pass1234";

 $con = mysql_connect("127.0.0.1:3306", $DB_USER_NAME, $DB_USER_PASS);
  if (!$con)
  {
      die(json_encode(array('Error' =>'Could not connect:' . mysql_error())));
  }
  mysql_query('SET NAMES UTF8');

  mysql_select_db("protoDB",$con);

    $sql="truncate table p2table;";
    $result = mysql_query($sql,$con);
    echo "Delete Table For P2 : ";
    var_dump($result);
    echo "<br/>";

     $sql="truncate table p2clips;";
     $result = mysql_query($sql,$con);
    echo "delete Table For P2 Clips : ";
    var_dump($result);
    echo "<br/>";


  $sql = "truncate table p3table;";   
  $result = mysql_query($sql,$con); 
  echo "delete Table For P3 : ";
  var_dump($result);
  echo "<br/>";



?>