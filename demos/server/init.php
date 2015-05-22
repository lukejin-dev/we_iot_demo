<?php 
$DB_USER_NAME="root";
$DB_USER_PASS="db_pass1234";

 $con = mysql_connect("127.0.0.1:3306", $DB_USER_NAME, $DB_USER_PASS);
  if (!$con)
  {
      die(json_encode(array('Error' =>'Could not connect:' . mysql_error())));
  }
  mysql_query('SET NAMES UTF8');

  $sql = "CREATE DATABASE IF NOT EXISTS protoDB;";   
  $result = mysql_query($sql,$con); 
  echo "Create Database : ";
  var_dump($result);
  echo "<br/>";

  mysql_select_db("protoDB",$con);

    $sql="CREATE TABLE IF NOT EXISTS p2table(id INT PRIMARY KEY AUTO_INCREMENT,clientid varchar(50) NOT NULL,content TEXT,time INT NOT NULL)";
    $result = mysql_query($sql,$con);
    echo "Create Table For P2 : ";
    var_dump($result);
    echo "<br/>";

     $sql="CREATE TABLE IF NOT EXISTS p2clips(id INT PRIMARY KEY AUTO_INCREMENT,clientid varchar(50) NOT NULL,name varchar(80) NOT NULL,start INT NOT NULL,end INT NOT NULL)";
     $result = mysql_query($sql,$con);
    echo "Create Table For P2 Clips : ";
    var_dump($result);
    echo "<br/>";


  $sql = "CREATE TABLE IF NOT EXISTS p3table(id INTEGER PRIMARY KEY AUTO_INCREMENT,clientid varchar(50) NOT NULL,rssi INT,time INT NOT NULL,stationid varchar(50) NOT NULL)";   
  $result = mysql_query($sql,$con); 
  echo "Create Table For P3 : ";
  var_dump($result);
  echo "<br/>";



?>