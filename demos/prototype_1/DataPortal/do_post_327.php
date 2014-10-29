<?php
  if(!empty($_POST["date"])&&!empty($_POST["json_content"]))
  {      
      $con = mysql_connect(SAE_MYSQL_HOST_M.':'.SAE_MYSQL_PORT, SAE_MYSQL_USER, SAE_MYSQL_PASS);
      if (!$con)
      {
          die(json_encode(array('Error' =>'Could not connect:' . mysql_error())));
      }
      mysql_query('SET NAMES UTF8');
      mysql_select_db(SAE_MYSQL_DB,$con);
      $json_content=addslashes($_POST["json_content"]);
      $date=$_POST["date"];    
            
      $res=mysql_query("REPLACE INTO proto1(json_date,json_content) VALUES('$date', '$json_content')");
      echo " - Result: ";
      echo var_dump($res); 
      mysql_close($con);
      
  }
  else
  {
    die(json_encode(array('Error' =>'params required.')));
  }
?>