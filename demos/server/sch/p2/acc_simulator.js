THE_THRESHOLD=1.85;
THE_RANDOM_BASIS=1.85;
Highcharts.setOptions({global: {useUTC: false}});
var currentStatus="Ready";
var realtimeCharts={}
var lastUpdateTime=0;
var AJAX_BASE_URL="http://p3demo.sinaapp.com/p2_portal.php?";    
if(window.location.href.indexOf("69.10.52.93")<0)
    AJAX_BASE_URL="/p2_portal.php?";

realtimeCharts.accelerator = new Highcharts.Chart({         
            chart: {
                type: 'spline',
                renderTo:"accelerator-div",
                zoomType:"y"
            },
            title: {
                text: 'Accelerator Live Data'
            },
            yAxis:{
                 plotLines: [{
                    color: '#FF883A',
                    width: 3,
                    value: THE_THRESHOLD,
                    id: 'plotline',
                    dashStyle:"LongDash",
                /*    label:{
                        text:"Threshold"
                    }*/
                }] 
            },
            xAxis: {               
                type: "category"   
            },                 
            series: []
        });

function initAllCharts(){    
    for (var chart in realtimeCharts){
        for(var idx=realtimeCharts[chart].series.length-1;idx>-1;idx--)
            realtimeCharts[chart].series[idx].remove(false);
        for(var idx=0;idx<currentClientIDs.length;idx++)
            realtimeCharts[chart].addSeries ({name:currentClientIDs[idx],data:[]}, false,true);
        realtimeCharts[chart].redraw();
    }
}
/*
function addOneItem(dataItem,timeLable,clientid){

        var seriesId=currentClientIDs.indexOf(clientid);
        if(seriesId>-1){
             var curSeries = realtimeCharts.accelerator.series[seriesId];
             if(curSeries.data.length>10)
                curSeries.addPoint([timeLable,parseFloat(dataItem.humidity)],false,true,true);
             else
                curSeries.addPoint([timeLable,parseFloat(dataItem.humidity)],false,false,true);
        }  
}
*/
/*
function parseData(data){
    var timeLable=(data.time||0)*1000;
    
    if(!data.content){
        console.log("No Content.");
        console.log(data);
        return;  
    }       
    else if(typeof(data.content.length)=="undefined"){ //single Object
        addOneItem(data.content,timeLable,data.clientid);
    }        
    else if(typeof(data.content.length)=="number"){ //object Array
        var gap=Math.floor(2000/data.content.length);
        for(var idx in data.content){            
                addOneItem(data.content[idx],timeLable+(1+idx-data.content.length)*gap,data.clientid);                
        }
    }
}
*/
function redrawAllCharts(){
    for (var chart in realtimeCharts)
        realtimeCharts[chart].redraw();     
    var a=realtimeCharts.accelerator.yAxis[0].plotLinesAndBands[0];
    if(a&&a.options.value!=THE_THRESHOLD){
        a.options.value=THE_THRESHOLD;
        a.render();
    }          
}
function refreshStatus(){
    if(currentStatus=="Ready"){
        $("#control-field-set").removeAttr("disabled"); 
        $("#control-ok-button").removeClass("btn-danger").addClass("btn-success").text("Start");     
      //  initAllCharts();
    }
    else if(currentStatus=="Realtime_Running"){
        $("#control-field-set").attr("disabled",true);
        $("#control-ok-button").removeClass("btn-success").addClass("btn-danger").text("Stop");     
        initAllCharts();
    }
    else {
        alert("undefined status:"+currentStatus);
    }
}
function tiggerItemEvent(clientid){
    $("#monitor-item-"+clientid.replace(/\:/g, "_")).addClass("monitor-item-div-active");

}
function okButtonHander(){   
    if(currentStatus=="Ready"){
        currentClientIDs=[];
        $(".client-select-checkbox").each(function(id,obj){
            if(obj.checked&&obj.value)
                currentClientIDs.push(obj.value);
        });
        if(currentClientIDs.length<1){
            alert("Please choose clients.");
            return;
         }    
        $("#monitor-div").empty();
        for (var idx in currentClientIDs){
            $("#monitor-div").append("<a href='javascript:void(0)'><div class='monitor-item-div' id='monitor-item-"+currentClientIDs[idx].replace(/\:/g, "_")+"'><span>"+currentClientIDs[idx]+"</span></div></a>");
        }
        $(".monitor-item-div").click(function(e){
            $(this).removeClass("monitor-item-div-active");
        });
        currentStatus="Realtime_Running";
        lastUpdateTime=0;
        refreshStatus();
        realtimeThread();
    }
    else{
        currentStatus="Ready";
    }
}
nextRaiseTime={};
function realtimeThread(){   
    function simGenerator(){
        now=Math.floor((new Date())/1000);
        var data=[];
        for(var idx in currentClientIDs){
            var tmp=0;
            if(typeof(nextRaiseTime[currentClientIDs[idx]])=="undefined"){
                tmp=0.4+Math.random()*0.2*THE_RANDOM_BASIS;
                nextRaiseTime[currentClientIDs[idx]]=now+8+idx*6;
            }
            else if(now>nextRaiseTime[currentClientIDs[idx]]){
                tmp=THE_RANDOM_BASIS*(0.8+Math.random());   
            }
            else{
                tmp=0.4+Math.random()*0.2*THE_RANDOM_BASIS;
            }
            if(tmp>=THE_RANDOM_BASIS)
                nextRaiseTime[currentClientIDs[idx]]=now+10+Math.random()*12;;
            data.push({time:now,clientid:currentClientIDs[idx],content:{a_x:tmp,a_y:0,a_z:0}});
        }
        return data;
    } 
   
    function updateChart(data) {
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
        if(lastUpdateTime==0){
            lastUpdateTime=data[0]["time"];
            return;
        }
        lastUpdateTime=data[0]["time"];
        var tmpAbsRes={};
        for(var idx in currentClientIDs){
            tmpAbsRes[currentClientIDs[idx]]=0;
        }
        function calcRes(oldValue,obj){
            return Math.max(oldValue,Math.abs(obj.a_x)+Math.abs(obj.a_z)+Math.abs(obj.a_y));
            //return oldValue+Math.max(0,Math.sqrt(obj.a_x*obj.a_x+obj.a_y*obj.a_y+obj.a_z*obj.a_z)-1);
        }
        for(var idx=data.length-1;idx>=0;idx--){   
                if(currentClientIDs.indexOf(data[idx].clientid)<0)
                    continue;
                if(typeof(data[idx].content.a_x)!="undefined"){
                    tmpAbsRes[data[idx].clientid]=calcRes(tmpAbsRes[data[idx].clientid],data[idx].content);      
                 }
                 else if(typeof(data[idx].content.length)=="number"){
                    for(var oidx in data[idx].content){            
                        if(typeof(data[idx].content[oidx].a_x)=="undefined")
                            continue;
                        tmpAbsRes[data[idx].clientid]=calcRes(tmpAbsRes[data[idx].clientid],data[idx].content[oidx]);      
                    }
                 }                
            }
            var timeLable=Highcharts.dateFormat('%H:%M:%S',new Date(1000*lastUpdateTime||0));
            for(var idx in currentClientIDs){
                if(tmpAbsRes[currentClientIDs[idx]]>=THE_THRESHOLD){
                    tiggerItemEvent(currentClientIDs[idx]);
                }
                var curSeries = realtimeCharts.accelerator.series[idx];
                if(curSeries.data.length>15)
                    curSeries.addPoint([timeLable,tmpAbsRes[currentClientIDs[idx]]],false,true,true);
                else
                    curSeries.addPoint([timeLable,tmpAbsRes[currentClientIDs[idx]]],false,false,true);
            }
            redrawAllCharts();         
        };
        updateChart(simGenerator());
}

function getClientidList(){
  
    function addClients(data) {
        console.log("Get Client List.");
       // console.log(data);
        $("#clients-select-div").empty();
        for(var idx=0;idx<data.length;idx++){
            if(!data[idx]["time"])
                continue;
            var str=data[idx]["clientid"]+" - Last Active : "+Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',(new Date(data[idx]["time"]*1000)));
            $("#clients-select-div").append("<div class='checkbox'><label><input class='client-select-checkbox' type='checkbox' value='"+data[idx]["clientid"]+"'>"+str+"</label> </div>"); 
        }
    }
    var now=Math.floor((new Date())/1000);
    addClients([{time:now,clientid:"Student1"},{time:now,clientid:"Student2"},{time:now,clientid:"Student3"}]);
}
function updateThreshold(){
    var th=$("#threshold-input").val();
    if(!th||isNaN(Number(th)))
        alert("Error:Invaild Threshold.");
    THE_THRESHOLD=Number(th);
    var a=realtimeCharts.accelerator.yAxis[0].plotLinesAndBands[0];
    if(a&&a.options.value!=THE_THRESHOLD){
        a.options.value=THE_THRESHOLD;
        a.render();
    }  
}
getClientidList();
refreshStatus();
//initAllCharts();