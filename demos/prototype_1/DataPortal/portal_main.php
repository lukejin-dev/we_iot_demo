<?php
  header('Content-type:application/json'); 
  header('Access-Control-Allow-Origin:*');
        
  $con = mysql_connect(SAE_MYSQL_HOST_M.':'.SAE_MYSQL_PORT, SAE_MYSQL_USER, SAE_MYSQL_PASS);
  if (!$con)
  {
      die(json_encode(array('Error' =>'Could not connect:' . mysql_error())));
  }
  mysql_query('SET NAMES UTF8');
  mysql_select_db(SAE_MYSQL_DB,$con);

  $resArray = array();
  if($_GET["action"]=="getdatelist")
  {
      $sql = "SELECT json_date FROM proto1 ORDER BY json_date DESC";
      $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_array($result)) 
      {     
        array_push($resArray,$row["json_date"]);        
      }          
      mysql_free_result($result);      
      mysql_close($con);
      echo json_encode($resArray);
  }
  else if(!empty($_GET["date"]))
  {
      $date=mysql_real_escape_string($_GET["date"]);
      $sql = "SELECT * FROM proto1 WHERE json_date='$date'";
      $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_array($result)) 
      {        
        $rowArray=array();
        $rowArray["date"]=$row["json_date"];
        $rowArray["content"]=json_decode($row["json_content"]);
        array_push($resArray,$rowArray);        
      }          
      mysql_free_result($result);      
      mysql_close($con);
      echo json_encode($resArray);

  }
  else if(!empty($_GET["startdate"])&&!empty($_GET["enddate"]))
  {    
      $startdate=mysql_real_escape_string($_GET["startdate"]);
      $enddate=mysql_real_escape_string($_GET["enddate"]);   
      $sql = "SELECT * FROM proto1 WHERE json_date>='$startdate' AND json_date<='$enddate' ORDER BY json_date DESC";
      $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_array($result)) 
      {        
        $rowArray=array();
        $rowArray["date"]=$row["json_date"];
        $rowArray["content"]=json_decode($row["json_content"]);
        array_push($resArray,$rowArray);        
      }          
      mysql_free_result($result);      
      mysql_close($con);
      echo json_encode($resArray);
  } 

  else
  {
      die(json_encode(array('Error' =>'params required.')));
  }  
    

?>