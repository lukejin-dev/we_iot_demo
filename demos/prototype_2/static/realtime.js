Highcharts.setOptions({global: {useUTC: false}});
var realtimeSeries={};
fakeInitData=[["N/A",0],["N/A",0],["N/A",0],["N/A",0],["N/A",0],["N/A",0],["N/A",0],["N/A",0],["N/A",0]];
var lastUpdateTime={"magnetometer":0,"accelerator":0,"temperature":0,"humidity":0,"DB":0}



 $('#humidity-div').highcharts({         
            chart: {
                type: 'spline',      
                events: {
                    load: function() {                          
                      realtimeSeries["humidity"] = this.series;                   
                    }
                }
            },
            title: {
                text: 'Humidity Live data'
            },
            xAxis: {               
                type: "category"         
            },   
                
            series: [{
                name: 'Humidity',
                data: fakeInitData
            }]
        });


  $('#temperature-div').highcharts({         
            chart: {
                type: 'spline',            
                events: {
                    load: function() {                          
                      realtimeSeries["temperature"] = this.series;                   
                    }
                }
            },
            title: {
                text: 'Temperature Live data'
            },
            xAxis: {
                 type: "category"     
              //  type: 'datetime' ,

            },    
                      
            series: [{
                name: 'Target Temperature',
                data: fakeInitData
            },{
                name: 'Ambient Temperature',
                data: fakeInitData
            }]
        });


   $('#accelerator-div').highcharts({         
            chart: {
                type: 'spline',                   
                events: {
                    load: function() {                          
                      realtimeSeries["accelerator"] = this.series;                   
                    }
                }
            },
            title: {
                text: 'Accelerator Live data'
            },
            xAxis: {
                 type: "category"     
               // type: 'datetime'              
            },       
                 
            series: [{
                name: 'x-axis',
                data: fakeInitData
            },{
                name: 'y-axis',
                data: fakeInitData
            },{
                name: 'z-axis',
                data: fakeInitData
            }]
        });


    $('#magnetometer-div').highcharts({         
            chart: {
                type: 'spline',                  
                events: {
                    load: function() {                          
                      realtimeSeries["magnetometer"] = this.series;                   
                    }
                }
            },
            title: {
                text: 'Magnetometer Live data'
            },
            xAxis: {
                type: "category"     
              //  type: 'datetime'              
            },     
            series: [{
                name: 'x-axis',
                data: fakeInitData
            },{
                name: 'y-axis',
                data: fakeInitData
            },{
                name: 'z-axis',
                data: fakeInitData
            }]
        });


function getRealtimeData(){    
    $.get(
    "/api/p2/read?time_after="+lastUpdateTime["DB"]+"&limit=10",
    function(data) {
        window.setTimeout(getRealtimeData,2000);
        if(!data){
            console.log("Empty Data Package.")
            console.log(data);
        }            
        lastUpdateTime["DB"]=data[0]["time"]-1;
        for(var idx=data.length-1;idx>=0;idx--){
            var timeLable=(new Date(1000*data[idx].time||0)).toLocaleTimeString()
            if(data[idx].content.humidity&&data[idx].time>lastUpdateTime.humidity){
                lastUpdateTime.humidity=data[idx].time;
                realtimeSeries["humidity"][0].addPoint([timeLable,data[idx].content.humidity],true,true);
            }
            else if(data[idx].content.target&&data[idx].time>lastUpdateTime.temperature){
                lastUpdateTime.temperature=data[idx].time;
                realtimeSeries["temperature"][0].addPoint([timeLable,data[idx].content.target],true,true);
                realtimeSeries["temperature"][1].addPoint([timeLable,data[idx].content.ambient],true,true);
            }
            else if(data[idx].content.a_x&&data[idx].time>lastUpdateTime.accelerator){
                lastUpdateTime.accelerator=data[idx].time;
                realtimeSeries["accelerator"][0].addPoint([timeLable,data[idx].content.a_x],true,true);
                realtimeSeries["accelerator"][1].addPoint([timeLable,data[idx].content.a_y],true,true);
                realtimeSeries["accelerator"][2].addPoint([timeLable,data[idx].content.a_z],true,true);
            }
            else if(data[idx].content.m_x&&data[idx].time>lastUpdateTime.magnetometer){
                lastUpdateTime.magnetometer=data[idx].time;
                realtimeSeries["magnetometer"][0].addPoint([timeLable,data[idx].content.m_x],true,true);
                realtimeSeries["magnetometer"][1].addPoint([timeLable,data[idx].content.m_y],true,true);
                realtimeSeries["magnetometer"][2].addPoint([timeLable,data[idx].content.m_z],true,true);
            }            
        }
    }
);
}
getRealtimeData();