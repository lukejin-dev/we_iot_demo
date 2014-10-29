
function navbarCtr($scope,$location) 
{ 
    $scope.isActive = function (viewLocation) { 
        //console.log($location);
       return viewLocation === $location.path();
    };
}

function queryCtr($scope,$http){
  var today=new Date();
  var a_week_ago=new Date(today.valueOf()-680400000);
  function toDateFormat(m){return m<10?'0'+m:m }
  $scope.enddate=today.getFullYear()+"-"+toDateFormat(today.getMonth()+1)+"-"+toDateFormat(today.getDate());
  $scope.startdate=a_week_ago.getFullYear()+"-"+toDateFormat(a_week_ago.getMonth()+1)+"-"+toDateFormat(a_week_ago.getDate());
  $scope.doQuery=function(){

    if(!$scope.startdate||!$scope.enddate){
      alert("Please input date in following format:2014-10-15");
      return;
    }
   $scope.Msg="Querying...";
   $http.get("http://p1demo.sinaapp.com/portal_main.php?startdate="+$scope.startdate+"&enddate="+$scope.enddate).success(function(data) {
      console.log(data);
      if(data.length>0)
        $scope.ajaxData=data;
      else
        $scope.ajaxData=false;
      $scope.Msg=" # Query Finished: [ "+$scope.startdate+" > "+$scope.enddate+" ]   @  "+data.length+" Records Found.";
  });
}

}

function overviewCtr($scope){

}
function visualizationCtr($scope,$http){
  $scope.currentIntradayChartID=0;
  var today=new Date();
  var a_week_ago=new Date(today.valueOf()-680400000);
  function toDateFormat(m){return m<10?'0'+m:m }
  $scope.enddate=today.getFullYear()+"-"+toDateFormat(today.getMonth()+1)+"-"+toDateFormat(today.getDate());
  $scope.startdate=a_week_ago.getFullYear()+"-"+toDateFormat(a_week_ago.getMonth()+1)+"-"+toDateFormat(a_week_ago.getDate());
  
  $scope.chartConfig= {   
            options:{
                    chart: {
                        type: 'spline',
                        zoomType: 'xy'
                    },                  
              },          
             title: {text:""},
             xAxis: {categories: [], tickInterval : 1},
             yAxis: {min:0,title: {text:"Value"}},             
             series: []
  };

  $scope.chartConfig_intraday_calories={
   options:{
     chart: {
            type: 'column',
            zoomType: 'xy'
        }
      },
        title: {text: 'Calories'},
        xAxis: {
            categories: []
        },
        yAxis: {
            min: 0,
        },   
        series: []
    };

      $scope.chartConfig_intraday_steps={
   options:{
     chart: {
            type: 'column',
            zoomType: 'xy'
        }
      },
        title: {text: 'Steps'},
        xAxis: {
            categories: []
        },
        yAxis: {
            min: 0,
        },   
        series: []
    };

  $scope.switchChart=function(id){
    $scope.currentChartID=id;
    $scope.chartConfig.title.text=id;
    $scope.chartConfig.series=$scope.chartArray[id];
  }
  $scope.renderIntradayChart=function(data){
    if(!data){
    var data=$scope.currentIntradayData;
    }
    if(!data){
      console.log("No Data");
      return;
    }
    var xAxis=[];
    var jb_cal=[];
    var jb_steps=[];
    var wt_steps=[];
    var wt_cal=[];
    var fb_steps=[];
    var fb_cal=[];
    for(var h=8;h<22;h++){
      xAxis.push(h);
      jb_cal.push(0);
      jb_steps.push(0);
      wt_cal.push(0);
      wt_steps.push(0);
      fb_cal.push(0);
      fb_steps.push(0);
    }
    for(hour in data.content.jawbone.hourly_totals){
      var hour_idx=parseInt(hour.slice(-2));
      if(hour_idx>7&&hour_idx<22){
        jb_cal[hour_idx-8]=parseFloat(data.content.jawbone.hourly_totals[hour].calories||0)
        jb_steps[hour_idx-8]=parseInt(data.content.jawbone.hourly_totals[hour].steps||0)
      }      
    }
     $scope.chartConfig_intraday_steps.xAxis.categories=xAxis;
     $scope.chartConfig_intraday_calories.xAxis.categories=xAxis;
     $scope.chartConfig_intraday_steps.series=[{name:"jawbone",data:jb_steps},{name:"Withings",data:wt_steps},{name:"Fitbit",data:fb_steps}];
     $scope.chartConfig_intraday_calories.series=[{name:"jawbone",data:jb_cal},{name:"Withings",data:wt_cal},{name:"Fitbit",data:fb_cal}];
  }
  $scope.doQuery=function(){
      if(!$scope.startdate||!$scope.enddate){
        alert("Please input date in following format:2014-10-15");
        return;
      }
     $scope.Msg="Querying...";
     $http.get("http://p1demo.sinaapp.com/portal_main.php?startdate="+$scope.startdate+"&enddate="+$scope.enddate).success(function(data) {
        console.log(data);
        if(data.length>0)
        {
          $scope.ajaxData=data;
          function makeChartDataObj(d){
            res=[];
            for (var key in d){
              tmp_obj={};
              tmp_obj["name"]=key;
              tmp_obj["data"]=d[key];
              res.push(tmp_obj);
            }
            return res;
          }
          var tmp_steps={"jawbone":[],"Withings":[],"Fitbit":[]};
          var tmp_weight={"Withings":[],"Fitbit":[]};
          var tmp_sleepquality={"jawbone":[]};
          var tmp_asleep={"jawbone":[],"Fitbit":[],"Withings":[]};
          var x_array=[];
          for (var idx in data){
            x_array.unshift(data[idx]["date"]);
            tmp_steps["jawbone"].unshift(data[idx]["content"]["jawbone"]["steps"]||0);
            tmp_steps["Fitbit"].unshift(data[idx]["content"]["fitbit"]["steps"]||0);
            tmp_steps["Withings"].unshift(data[idx]["content"]["withings"]["steps"]||0);

            tmp_weight["Fitbit"].unshift(data[idx]["content"]["fitbit"]["weight"]||0);
            tmp_weight["Withings"].unshift(data[idx]["content"]["withings"]["weight"]/1000||0);

            tmp_sleepquality["jawbone"].unshift(data[idx]["content"]["jawbone"]["sleep_quality"]||0);

            tmp_asleep["jawbone"].unshift((
              (data[idx]["content"]["jawbone"]["rem"]||0)+
              (data[idx]["content"]["jawbone"]["deep"]||0)+
              (data[idx]["content"]["jawbone"]["light"]||0)
              )/3600);
            tmp_asleep["Withings"].unshift((
              (data[idx]["content"]["withings"]["rem"]||0)+
              (data[idx]["content"]["withings"]["deep"]||0)+
              (data[idx]["content"]["withings"]["light"]||0)
              )/3600);
            tmp_asleep["Fitbit"].unshift(data[idx]["content"]["fitbit"]["totalMinutesAsleep"]/60||0);
            
            }
            $scope.chartConfig.xAxis.categories=x_array;
            $scope.chartConfig.xAxis.tickInterval=Math.max(Math.floor(x_array.length/10),1);
            $scope.chartArray={ " Total Steps ":makeChartDataObj(tmp_steps),
                                " Weight ":makeChartDataObj(tmp_weight),
                                "Sleep Quality":makeChartDataObj(tmp_sleepquality),
                                "Total Time Asleep":makeChartDataObj(tmp_asleep),
                              };

          $scope.switchChart(" Total Steps ");  
          $scope.currentIntradayData=$scope.ajaxData[0];    
          $scope.renderIntradayChart($scope.ajaxData[0]);
        }
          
        else
          $scope.ajaxData=false;
        $scope.Msg=" # Query Finished: [ "+$scope.startdate+" > "+$scope.enddate+" ]   @  "+data.length+" Records Found.";
    });
  }
  


}
function referenceCtr($scope){

}