Highcharts.setOptions({global: {useUTC: false}});
var AJAX_BASE_URL="http://p3demo.sinaapp.com/p3_portal.php?";
if(window.location.href.indexOf("69.10.52.93")<0)
    AJAX_BASE_URL="/p3_portal.php?";
var clientList=[];
var stationList=[];

function update(){
    $.get(
    AJAX_BASE_URL+"action=read_latest",
    function(data) {        
        window.setTimeout(update,2000);
        var latestUpdate=0;
        var highlightObj={};
        var now=new Date();
        now=now.valueOf()/1000;

        $("#main-data-table tbody td").not(".clientid-row").empty().removeClass("bg-primary");
        data.sort(function(a,b){return (a.clientid>b.clientid)?1:-1;});
        for(var idx=0;idx<data.length;idx++){
            data[idx].time=parseInt(data[idx].time);
            data[idx].rssi=parseInt(data[idx].rssi);
            latestUpdate=Math.max(latestUpdate,parseInt(data[idx].time));
        }
       
        if(now-latestUpdate>120){
            $("#main-table-last-update").text("No Device Connected.");
            $("#chart-container").empty();
            $("#main-data-table tbody").empty();
            $("#main-data-table thead").empty().append("<tr><td><b>Client \\ Station</b></td></tr>");
            clientList=[];
            stationList=[];
            return;  
        }
            
         $("#main-table-last-update").text("Last Update : "+Highcharts.dateFormat('%Y-%m-%d %H:%M:%S',1000*latestUpdate));
        for(var idx=0;idx<data.length;idx++){
            if(!data[idx].stationid||!data[idx].clientid||data[idx].time<latestUpdate-10||data[idx].stationid.indexOf("Unknown_")>-1||data[idx].clientid.indexOf("Unknown_")>-1)
                continue;
            if(stationList.indexOf(data[idx].stationid)<0){
                stationList.push(data[idx].stationid);
                $("#main-data-table thead tr").append("<th>"+data[idx].stationid+"</th>)");
                $("#main-data-table tbody tr").append("<td></td>");    
                $("#chart-container").append("<div class='chart-station' id='chart_station_"+stationList.indexOf(data[idx].stationid)+"'><span>"+data[idx].stationid+"</span></div>");         
                
            }
            if(clientList.indexOf(data[idx].clientid)<0){
                var tds="";
                for(var count=0;count<stationList.length;count++){
                    tds+="<td></td>";
                }
                $("#main-data-table tbody").append("<tr><td class='clientid-row'>"+data[idx].clientid+"</td>"+tds+"</tr>");
                clientList.push(data[idx].clientid);
            }
            var x_idx=stationList.indexOf(data[idx].stationid)+1,y_idx=clientList.indexOf(data[idx].clientid);
            $("#main-data-table tbody tr").eq(y_idx).children().eq(x_idx).text(data[idx].rssi);
            if(typeof(highlightObj[data[idx].clientid])=="undefined"){
                highlightObj[data[idx].clientid]=[data[idx].rssi,x_idx,y_idx];
            }
            else if(highlightObj[data[idx].clientid][0]<data[idx].rssi){                
                    highlightObj[data[idx].clientid]=[data[idx].rssi,x_idx,y_idx];
                 }
        }
        $(".chart-student").remove();
        for(var key in highlightObj){
            if(!highlightObj.hasOwnProperty(key))
                continue;
            $("#chart_station_"+(highlightObj[key][1]-1)).append("<div class='chart-student'>"+clientList[highlightObj[key][2]]+"</div>");
            $("#main-data-table tbody tr").eq(highlightObj[key][2]).children().eq(highlightObj[key][1]).addClass("bg-primary");
        }       
    });

}

update();