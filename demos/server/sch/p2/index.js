Highcharts.setOptions({global: {useUTC: false}});
var currentStatus="Ready";
var REC_Status="Ready";
var realtimeCharts={}
var lastUpdateTime=0;
var AJAX_BASE_URL="http://p3demo.sinaapp.com/p2_portal.php?";    
if(window.location.href.indexOf("69.10.52.93")<0)
    AJAX_BASE_URL="/p2_portal.php?";
var currentClientID="";
var replayData=[];
var replayLength=0;
realtimeCharts.humidity = new Highcharts.Chart({         
            chart: {
                type: 'spline',
                renderTo:"humidity-div",
                zoomType:"x"
            },
            title: {
                text: 'Humidity Live Data'
            },
            xAxis: {               
                type: "category"         
            },                 
            series: [{
                name: 'Humidity',
                data: []
            }]
        });
realtimeCharts.temperature = new Highcharts.Chart({         
            chart: {
                type: 'spline', 
                renderTo:"temperature-div",
                zoomType:"x"
            },
            title: {
                text: 'Temperature Live Data'
            },
            xAxis: {
                 type: "category"
            },                          
            series: [{
                name: 'Target Temperature',
                data: []
            },{
                name: 'Ambient Temperature',
                data: []
            }]
        });
realtimeCharts.accelerator = new Highcharts.Chart({         
            chart: {
                type: 'spline', 
                renderTo:"accelerator-div",
                zoomType:"x"
            },
            title: {
                text: 'Accelerator Live Data'
            },
            xAxis: {
                 type: "category"     
               // type: 'datetime'              
            },   
            series: [{
                name: 'X-axis',
                data: []
            },{
                name: 'Y-axis',
                data: []
            },{
                name: 'Z-axis',
                data: []
            }]
        });
realtimeCharts.magnetometer = new Highcharts.Chart({         
            chart: {
                type: 'spline',  
                renderTo:"magnetometer-div",
                 zoomType:"x"
            },
            title: {
                text: 'Magnetometer Live Data'
            },
            xAxis: {
                type: "category"
            },     
            series: [{
                name: 'X-axis',
                data: []
            },{
                name: 'Y-axis',
                data: []
            },{
                name: 'Z-axis',
                data: []
            }]
        });

function initAllCharts(blank){    
    var nums=10;
    if(blank)
        nums=0;
    for (var chart in realtimeCharts){
        for(var idx in realtimeCharts[chart]["series"]){
            var fakedata=[];
            for(var tt=0;tt<nums;tt++)
                fakedata.push(["N/A",0]);
            realtimeCharts[chart]["series"][idx].setData (fakedata,false, false, false);        
        }
        realtimeCharts[chart].redraw();
    }
}

function addOneItem(dataItem,timeLable,NoShift){
    var shiftSymbol=true;
    if(NoShift)
        shiftSymbol=false;

    if(dataItem.humidity!==undefined){ 
        var curSeries = realtimeCharts.humidity.series;
        curSeries[0].addPoint([timeLable,parseFloat(dataItem.humidity)],false,shiftSymbol,true);
        $("#tar_1").text(parseInt(dataItem.humidity));
        
    }
    if(dataItem.target!==undefined){ 
        var curSeries = realtimeCharts.temperature.series;               
        curSeries[0].addPoint([timeLable,parseFloat(dataItem.target)],false,shiftSymbol,true);
        curSeries[1].addPoint([timeLable,parseFloat(dataItem.ambient)],false,shiftSymbol,true);
        $("#tar_2").text(parseInt(dataItem.target));
        $("#tar_3").text(parseInt(dataItem.ambient));
       
    }
    if(dataItem.a_x!==undefined){ 
        var curSeries = realtimeCharts.accelerator.series;
        curSeries[0].addPoint([timeLable,parseFloat(dataItem.a_x)],false,shiftSymbol,true);
        curSeries[1].addPoint([timeLable,parseFloat(dataItem.a_y)],false,shiftSymbol,true);
        curSeries[2].addPoint([timeLable,parseFloat(dataItem.a_z)],false,shiftSymbol,true);
        
    }
    if(dataItem.m_x!==undefined){ 
        var curSeries = realtimeCharts.magnetometer.series;
        curSeries[0].addPoint([timeLable,parseFloat(dataItem.m_x)],false,shiftSymbol,true);
        curSeries[1].addPoint([timeLable,parseFloat(dataItem.m_y)],false,shiftSymbol,true);
        curSeries[2].addPoint([timeLable,parseFloat(dataItem.m_z)],false,shiftSymbol,true);               
    }  
}
function parseData(data){
    var timeLable=Highcharts.dateFormat('%H:%M:%S',new Date(1000*data.time||0));
    
    if(!data.content){
        console.log("No Content.");
        console.log(data);
        return;  
    }       
    else if(typeof(data.content.length)=="undefined"){ //single Object
        addOneItem(data.content,timeLable);
    }        
    else if(typeof(data.content.length)=="number"){ //object Array
        for(var idx in data.content){            
                addOneItem(data.content[idx],timeLable);                
        }
    }
}

function redrawAllCharts(){
    for (var chart in realtimeCharts)
        realtimeCharts[chart].redraw();        
}

function clientSelectChange(){
    if($("#history-radio")[0].checked){
        getTimeListAndShow();
    }
    else if($("#clip-radio")[0].checked){
        getClipList();
    }
}
function refreshStatus(){
    REC_Status=="Ready";
    $("#record-div").hide();
    REC_start=undefined;
    REC_end=undefined;
    if(currentStatus=="Ready"){
        $("#replay-status-panel").slideUp();
        $("#control-field-set").removeAttr("disabled"); 
        $("#control-ok-button").removeClass("btn-danger").addClass("btn-success").text("Start");     
      //  initAllCharts();
    }
    else if(currentStatus=="Realtime_Running"){
        $("#replay-status-panel").slideUp();
        $("#control-field-set").attr("disabled",true);
        $("#control-ok-button").removeClass("btn-success").addClass("btn-danger").text("Stop");     
        initAllCharts();
    }
    else if(currentStatus=="History_Running"){
        $("#replay-status-panel").slideDown();
        $("#control-field-set").attr("disabled",true);
        $("#control-ok-button").removeClass("btn-success").addClass("btn-danger").text("Stop");     
        initAllCharts();
    }
   else if(currentStatus=="Clip_Running"){
        $("#replay-status-panel").slideUp();
        $("#control-field-set").attr("disabled",true);
        $("#control-ok-button").removeClass("btn-success").addClass("btn-danger").text("Stop");     
        initAllCharts(true);
    }
    else {
        alert("undefined status:"+currentStatus);
    }
}

function okButtonHander(){
   
    if(currentStatus=="Ready"){
         currentClientID=$("#client-select").val();
         console.log("currentClientID",currentClientID);
         if(!currentClientID){
            alert("Error: Invalid ClientID.");
            return;
         }           
        if($("#realtime-radio")[0].checked) {  
            currentStatus="Realtime_Running";
            lastUpdateTime=0;
            refreshStatus();
            realtimeThread();
        }     
        else if(($("#history-radio")[0].checked) ){
            var s=Date.parse($("#start-time-input").val().replace(/-/g,"/"));
            var c=parseInt($("#clip-length-input").val())
            if(isNaN(s)||isNaN(c)){
                alert("Please check your input.");
                return;
            }
            s=Math.floor(s/1000)-1;
            c=Math.max(c,3);
            c=Math.min(c,10);            
            currentStatus="History_Running";
            refreshStatus();  
            $("#replay-progress-bar").attr("aria-valuenow","0").css("width","0%");
            $("#replay-progress-text").text("0%");

            $.get(
            AJAX_BASE_URL+"action=read&"+"clientid="+currentClientID+"&time_after="+s+"&time_before="+(s+c*60),
            function(data) {
                console.log("History data length:",data.length);
                if(!data||data.length==0){
                     alert("No data found in that range.");
                     currentStatus="Ready";
                     refreshStatus();  
                     return;
                }
                replayData=data;
                replayLength=replayData.length;
                replayThread();
            });            
        }
        else if(($("#clip-radio")[0].checked) ){
            var clip=$("#clip-select").val();
            if(!clip)
                return;
            currentStatus="Clip_Running";
            refreshStatus();  
            clip=[Number(clip.split("|")[0])-1,Number(clip.split("|")[1])+1];
        $.get(

            AJAX_BASE_URL+"action=read&"+"clientid="+currentClientID+"&time_after="+clip[0]+"&time_before="+clip[1],
            function(data) {
                console.log("clip data length:",data.length);
                if(!data||data.length==0){
                     alert("No clip data found in that range.");
                     currentStatus="Ready";
                     refreshStatus();  
                     return;
                }
                clipDataTimeStart=data[data.length-1].time;
                clipData=data;
                clipLength=clipData.length;                
                clipThread();
            });  

        }
    }
    else{
        currentStatus="Ready";
    }
}
function realtimeThread(){    
    $.get(
    AJAX_BASE_URL+"action=read&time_after="+lastUpdateTime+"&limit=10"+"&clientid="+currentClientID,
    function(data) {
        if(currentStatus!="Realtime_Running"){
            console.log("Realtime Exit.");
            refreshStatus();
            return;
        }
        window.setTimeout(realtimeThread,2000);
        if(!data||!data[0]||!data[0]["time"]){
            console.log("Empty Data Package.")
            return;
        }            
        lastUpdateTime=parseInt(data[0]["time"]);
        if(REC_Status=="Start"){
            REC_start=parseInt(data[0]["time"]);
            REC_Status="Recording";
        }       
        REC_end=parseInt(data[0]["time"]);
        for(var idx=data.length-1;idx>=0;idx--){
            data[idx]["time"]=parseInt(data[idx]["time"]);
            parseData(data[idx]);
        }
        redrawAllCharts();       
    }
);
}
function replayThread(){
     if(currentStatus!="History_Running"){
            console.log("Replay Exit.");
            refreshStatus();
            return;
        }
        if(!replayData||replayData.length==0){           
            alert("Replay Finished."); 
            currentStatus="Ready";
            refreshStatus();           
            return;
        }
        var d=replayData.pop();
        d["time"]=parseInt(d["time"]);
        if(REC_Status=="Start"){
            REC_start=d["time"];
            REC_Status="Recording";
        }       
        REC_end=d["time"];
        parseData(d);
        redrawAllCharts(); 
        var p=(1-(replayData.length/replayLength))*100;
        p=p.toFixed(1);
        $("#replay-progress-bar").attr("aria-valuenow",p+"").css("width",p+"%");
        $("#replay-progress-text").text(p+"%"); 
        $("#replay-current-time").text(Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',(new Date(d.time*1000))))  ;         
        window.setTimeout(replayThread,1000);
}
function clipThread(){
   if(currentStatus!="Clip_Running"){
            console.log("Clip Exit.");
            refreshStatus();
            return;
        }
        if(typeof(clipData)=="undefined"||!clipData||clipData.length==0){           
            //alert("Replay Finished."); 
            currentStatus="Ready";
            refreshStatus();           
            return;
        }
        var nums=Math.max(1,Math.round(clipLength/6));
        for(var count=0;count<nums&&clipData.length>0;count++){
            var d=clipData.pop();
            d["time"]=parseInt(d["time"]);
            var timeLable=Highcharts.dateFormat('%M:%S',new Date(1000*(d.time-clipDataTimeStart)||0));    
                if(!d.content){
                    console.log("No Content.");
                    console.log(d);
                   // return;  
                }       
                else if(typeof(d.content.length)=="undefined"){ //single Object
                    addOneItem(d.content,timeLable,true);
                }        
                else if(typeof(d.content.length)=="number"){ //object Array
                    for(var idx in d.content){            
                            addOneItem(d.content[idx],timeLable,true);                
                    }
                }
        }       
        redrawAllCharts();        
        window.setTimeout(clipThread,500);
}
function getTimeListAndShow(){
    $("#time-list-ul").empty();
    var clientid=$("#client-select").val();
    if(!clientid)
        return;
    $.get(
    AJAX_BASE_URL+"action=time_list&"+"clientid="+clientid,
    function(data) {
        $("#time-list-ul").empty();
        if(data.length<1)
            return;
        console.log("Get Time List.");
        var pRes=[];
        curStart=data[0];
        curEnd=data[0];
        for(var idx=0;idx<data.length;idx++){
           data[idx]=parseInt(data[idx]);
           if(data[idx]-curEnd<200){
                    curEnd=data[idx];
            }
            else{
                if(curEnd-curStart>5)
                    pRes.push([curStart,curEnd]);
                curStart=data[idx];
                curEnd=data[idx];
            }   
        } 
        if(curStart!=curEnd){
            if(curEnd-curStart>5)
                    pRes.push([curStart,curEnd]);
        }
        if(pRes.length==0){
            $("#time-list-ul").append("<li class='list-group-item'> - No Available Active History </li>"); 
        }
        for(var idx=0;idx<pRes.length;idx++){
            var str=Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',(new Date(pRes[idx][0]*1000)))+
                    " ~ "+
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',(new Date(pRes[idx][1]*1000)))+
                    " | "+
                    (pRes[idx][1]-pRes[idx][0])+" seconds.";
            ;
            $("#time-list-ul").append("<li class='list-group-item'>"+str+"</li>"); 
        }
         
    });

}
function getClipList(){
    var clientid=$("#client-select").val();
    if(!clientid)
        return;
    $.get(
    AJAX_BASE_URL+"action=read_clip&"+"clientid="+clientid,
    function(data) {
        $("#clip-select").empty();
        console.log("Get Clip List.");
       // console.log(data);
       if(data.length==0){
         $("#clip-select").append("<option value=''> - No Available Clip. </option>"); 
       }
        for(var idx=0;idx<data.length;idx++)
        {
            if(!data[idx]["start"])
                continue;
            var str="[ "+data[idx]["name"]+" ] - Time : "+Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',(new Date(data[idx]["start"]*1000)))+"  - Length : "+(data[idx]["end"]-data[idx]["start"])+" seconds";
            $("#clip-select").append("<option value='"+data[idx]["start"]+"|"+data[idx]["end"]+"'>"+str+"</option>"); 
        } 
    });
}
function getClientidList(){
    $.get(
    AJAX_BASE_URL+"action=client_list",
    function(data) {
        console.log("Get Client List.");
        var now=new Date();
        now=(now.valueOf()/1000);
       // console.log(data);
        $("#client-select").empty();
        if(data.length==0){
          $("#client-select").append("<option value=''> - No Client Available . Try Refresh Page.</option>");   
        }
        for(var idx=0;idx<data.length;idx++)
        {
            if(!data[idx]["time"])
                continue;
            if(Math.abs(parseInt(data[idx]["time"])-now)<10)
                var str=data[idx]["clientid"]+" - Active Now";
            else
                var str=data[idx]["clientid"]+" - Last Active : "+Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',(new Date(parseInt(data[idx]["time"])*1000)));
            $("#client-select").append("<option value='"+data[idx]["clientid"]+"'>"+str+"</option>"); 
        } 
    }
);
}

$(document).keydown(function(event){ 
   //  console.log(event.keyCode);
if(event.keyCode==82){
    if(currentStatus=="Realtime_Running"||currentStatus=="History_Running"){
        if(REC_Status=="Ready"){
            console.log("REC_START");
            REC_start=undefined;
            REC_end=undefined;
            $("#record-div").show();
            REC_Status="Start";
            return;

        }
        else if(REC_Status=="Recording"){
             $("#record-div").hide();
             REC_Status="Ready";
             console.log("REC_END");
             if(!REC_start||!REC_end){
                alert("REC Stopped : No Vaild Clip Data,0");
                 REC_start=undefined;
                 REC_end=undefined;
             }
             else{
                if(confirm("Save the Clip ( "+(REC_end-REC_start)+" seconds ) ?")){
                    var cname;
                    while(!cname){
                        cname=prompt("Input a name for this clip:");
                    }
                    $.ajax({
                          type: 'POST',
                          url:AJAX_BASE_URL+"action=write_clip",
                          data: {"clientid":$("#client-select").val(),"name":cname,"start":REC_start,"end":REC_end},
                          success:function(data){console.log("REC_SAVE:",data)},
                    });
                }
                
             }
             return;
        }
    }
     console.log("REC_CLEAR");
     REC_Status="Ready";
     $("#record-div").hide();
     REC_start=undefined;
     REC_end=undefined;
     //alert("REC Stopped : No Vaild Clip Data,1");
} 
}); 

getClientidList();
refreshStatus();
initAllCharts();