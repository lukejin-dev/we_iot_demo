<?php 
  require 'hardcode.php';
  $resArray = array();

  if($_GET["action"]=="read_latest")
  {
     
      $sql = "SELECT * FROM p2table ORDER BY id DESC LIMIT 10";   
      $result = mysql_query($sql,$con);   
      while ($row = mysql_fetch_array($result)) 
      {    
        array_push($resArray,array("time"=>$row["time"],"clientid"=>$row["clientid"],"content"=>json_decode($row["content"])));   
      }      
      mysql_free_result($result);
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
        $sql="SELECT * FROM p2table WHERE time>$time_after AND time<$time_before AND clientid='$clientid' ORDER BY id DESC LIMIT $limit";
      }
        
      else
      {
        $sql="SELECT * FROM p2table WHERE time>$time_after AND time<$time_before ORDER BY id DESC LIMIT $limit";
      }
     
      $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_array($result)) 
      {    
        array_push($resArray,array("time"=>$row["time"],"clientid"=>$row["clientid"],"content"=>json_decode($row["content"])));   
      }          
      mysql_free_result($result);      
      echo json_encode($resArray);
  }
  else if($_GET["action"]=="client_list")
  {
    $sql="SELECT clientid as clientid,MAX(time) as time FROM p2table GROUP BY clientid";
    $result = mysql_query($sql,$con);
      while ($row = mysql_fetch_object($result)) 
      {     
        array_push($resArray,$row);        
      }          
      mysql_free_result($result); 
      echo json_encode($resArray);
  }
  else if($_GET["action"]=="read_clip")
  {
    $clientid=mysql_real_escape_string($_GET["clientid"]);
    $sql="SELECT * FROM p2clips WHERE clientid='$clientid'";
    $result = mysql_query($sql,$con);
    while ($row = mysql_fetch_object($result)) 
    {     
        array_push($resArray,$row);        
    }          
    mysql_free_result($result);  
    echo json_encode($resArray);
  }
  else if($_GET["action"]=="write_clip")
  {
    $clientid=mysql_real_escape_string($_POST["clientid"]);
    $start=mysql_real_escape_string($_POST["start"]);
    $name=mysql_real_escape_string($_POST["name"]);    
    $end=mysql_real_escape_string($_POST["end"]);
    $sql="insert into p2clips(clientid,name,start,end) values('$clientid','$name',$start,$end)";
    $result = mysql_query($sql,$con);
    var_dump($result);
  }
  else if($_GET["action"]=="time_list")
  {
    $clientid=mysql_real_escape_string($_GET["clientid"]);
    $sql="SELECT id,time FROM p2table WHERE clientid='$clientid' ORDER BY id";
    $result = mysql_query($sql,$con);
    while ($row = mysql_fetch_array($result)) 
    {     
        array_push($resArray,$row["time"]);        
    }          
    mysql_free_result($result);      
    echo json_encode($resArray);
  }

  else if($_GET["action"]=="write")
  {      
      if(empty($_POST["content"])||empty($_POST["clientid"]))
      {
         die(json_encode(array('Error' =>'content or clientid required.')));
      }
      $content=addslashes(json_encode(json_decode($_POST["content"])));          
      $clientid=mysql_real_escape_string(replaceName($_POST["clientid"]));;
      $sql="INSERT INTO p2table(clientid,content,time) values('$clientid','$content',unix_timestamp(now()))";
      $result = mysql_query($sql,$con); 
      echo('{"ok":1}');
  }

  else if($_GET["action"]=="runsql")
  {
     $sql=$_POST["content"];
     $result = mysql_query($sql,$con);
     var_dump($result);
  }

  else if($_GET["action"]=="createtable")
  {
     $sql="CREATE TABLE IF NOT EXISTS p2table(id INT PRIMARY KEY AUTO_INCREMENT,clientid varchar(50) NOT NULL,content TEXT,time INT NOT NULL)";
     $result = mysql_query($sql,$con);
     var_dump($result);
     $sql="CREATE TABLE IF NOT EXISTS p2clips(id INT PRIMARY KEY AUTO_INCREMENT,clientid varchar(50) NOT NULL,name varchar(80) NOT NULL,start INT NOT NULL,end INT NOT NULL)";
     $result = mysql_query($sql,$con);
     var_dump($result);
  }

  else
  {
      die(json_encode(array('Error' =>'params required.')));
  }  
    

?>