<?php 
  require 'hardcode.php'; 
  $resArray = array();
  if($_GET["action"]=="read_latest")
  {
      $tmpids="-1";  
      $sql = "SELECT MAX(id) FROM p3table GROUP BY clientid,stationid";
      $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_array($result)) 
      { 
        $tmpids=$tmpids.",".$row[0];           
      } 
      $sql = "SELECT * FROM p3table WHERE id IN ($tmpids)";   
       
      $result = mysql_query($sql,$con);  
      while ($row = mysql_fetch_object($result)) 
      {     
        array_push($resArray,$row);        
      }          
      mysql_free_result($result);      
      //mysql_close($con);
      echo json_encode($resArray);
  }
  else if($_GET["action"]=="read")
  {
      if(!empty($_GET["time_before"]))
        $time_before=mysql_real_escape_string($_GET["time_before"]);
      else
        $time_before=9999999999;
      if(!empty($_GET["time_after"]))
        $time_after=mysql_real_escape_string($_GET["time_after"]);
      else
        $time_after=0;
      if(!empty($_GET["limit"]))
        $limit=mysql_real_escape_string($_GET["limit"]);
      else
        $limit=1000;
      if(!empty($_GET["clientid"]))
      {
        $clientid=mysql_real_escape_string($_GET["clientid"]);
        $sql="SELECT id,clientid,stationid,rssi,time FROM p3table WHERE time>$time_after AND time<$time_before AND clientid='$clientid' ORDER BY id DESC LIMIT $limit";
      }
        
      else
      {
        $sql="SELECT id,clientid,stationid,rssi,time FROM p3table WHERE time>$time_after AND time<$time_before ORDER BY id DESC LIMIT $limit";
      }
     
      $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_object($result)) 
      {     
        array_push($resArray,$row);        
      }          
      mysql_free_result($result);      
      //mysql_close($con);
      echo json_encode($resArray);

  }
  else if($_GET["action"]=="write")
  {
      $jsonObj=json_decode($_POST["content"],true);
      $queryCount=0;
      mysql_query("SET AUTOCOMMIT=0"); 
      foreach($jsonObj as $item){
        if(isset($item["rssi"])&&isset($item["clientid"])&&isset($item["stationid"]))
        {
          $rssi=mysql_real_escape_string($item["rssi"]);
          $clientid=mysql_real_escape_string(replaceName($item["clientid"]));
          $stationid=mysql_real_escape_string(replaceName($item["stationid"]));
          $sql="insert into p3table(clientid,stationid,rssi,time) values('$clientid','$stationid',$rssi,unix_timestamp(now()))";
          $result = mysql_query($sql,$con);
          if($result)
            $queryCount++;
        }
      }
      mysql_query("COMMIT");
      echo('{"ok":1,"written":'.$queryCount.'}');
  }
  else if($_GET["action"]=="runsql")
  {
     $sql=$_POST["content"];
     $result = mysql_query($sql,$con);
     var_dump($result);
  }
  else if($_GET["action"]=="createtable")
  {
     $sql="CREATE TABLE IF NOT EXISTS p3table(id INTEGER PRIMARY KEY AUTO_INCREMENT,clientid varchar(50) NOT NULL,rssi INT,time INT NOT NULL,stationid varchar(50) NOT NULL)";
     $result = mysql_query($sql,$con);
     var_dump($result);
  }
  else
  {
      die(json_encode(array('Error' =>'params required.')));
  }  
    

?>