Highcharts.setOptions({global: {useUTC: false}});

POSITION_ARRAY=["Entrance","MediaCenter","Classroom","Library","Cafeteria","Playground"];
/*
8:10 AM to school   0-2
8:10-8:45 Breakfast 2-9 
8:45-9:30 first class 9-18
R 18-21
9:45-10:30 2nd class 21-30
R 30-33
10:45-11:30 3rd class 33-42

11:30 -12:30 launch 42-54
12:30- 14:00 activity 54-72
14:00- 14:45 5th class 72-81
R 81-84
15:00 - 15:45 6th class 84-93
15:45 - 17:00 activity 93-108

"Entrance",  0
"MediaCenter", 1
"Classroom", 2
"Library", 3
"Cafeteria", 4
"Playground" 5
*/
var students=[
[[1,0],[2,4],[7,1],[10,2],[18,3],[21,2],[30,3],[31,1],[32,2],[43,4],[47,3],[58,1],[65,5],[70,2],[82,4],[85,2],[95,3],[105,1]],
[[0,0],[1,1],[3,4],[9,2],[19,1],[21,2],[30,1],[32,2],[42,4],[45,1],[58,1],[65,1],[72,2],[83,1],[85,2],[93,1],[105,3]],
[[2,0],[3,4],[6,1],[8,2],[18,3],[20,2],[31,1],[32,2],[43,4],[47,5],[58,5],[65,0],[72,2],[82,5],[85,2],[93,5],[100,2]],
[[2,0],[4,3],[5,4],[10,2],[18,2],[21,2],[31,1],[32,2],[45,4],[47,2],[58,5],[65,2],[70,2],[83,0],[85,2],[97,3],[107,5]],
[[2,0],[2,4],[7,1],[8,2],[18,5],[21,2],[31,1],[32,2],[47,4],[49,3],[56,3],[65,1],[71,2],[82,5],[85,2],[96,1],[99,5]],
];
function makeFakeData(){   
    var res=[];
    var now=1400716800000;    
    for (var sidx=0;sidx<5;sidx++){
        var tmp={
            name:"Student "+(sidx+1),
            step:"center",
            data:[]
        };
        tmp.data.push({x:now,y:0});
        for(pidx=1;pidx<30;pidx++){
            var lastPos=tmp.data[pidx-1].y;
            var move=Math.random()*3;
            if(move<1){
                tmp.data.push({x:now+pidx*300000,y:Math.max(1,lastPos-1)});
            }
            else if(move<2){
                tmp.data.push({x:now+pidx*300000,y:lastPos});
            }
            else{
                tmp.data.push({x:now+pidx*300000,y:Math.min(POSITION_ARRAY.length-1,lastPos+1)});
            }
        }
        res.push(tmp);
    }   
    console.log(window.JSON.stringify(res));
    return res;
}
function packData(data){
    var res=[];
    var now=1400716800000;  
    for(var sidx=0;sidx<data.length;sidx++)
    {
        
        var source=data[sidx];
        var tmp={
                name:"Student"+(sidx+1),
                step:"center",
                data:[]
            };
        var srcIdx=0;
        var lastPos=null;

        for(pidx=0;pidx<108;pidx++){            
            if(pidx>=source[srcIdx][0]){        
                lastPos=source[srcIdx][1];
                if(srcIdx<source.length-1)
                    srcIdx+=1;
            }
            tmp.data.push({x:now+pidx*300000,y:lastPos});
        }
        res.push(tmp);
    }
    return res;
}
var history_chart=new Highcharts.Chart({         
            chart: {
                type: 'line',
                renderTo:"history-chart-div",
                zoomType:"x"
            },
            title: {
                text: 'In School Location Tracker'
            },
            yAxis:{
               categories:POSITION_ARRAY,
               allowDecimals:false,
              

            },
            xAxis: {               
                type:"datetime",
                plotLines:[{
                    id:"cur",
                    value:0,
                    color: '#FF883A',
                    width: 2,
                    dashStyle:"LongDash"
                }]
            
        },
        plotOptions:{
            line:{

                marker:{enabled:false}
            },
            series:{
                events:{
                  //  click:function(events){console.log(event)}
                }
            }
       },
        tooltip: {
            formatter: function () {
                history_chart.xAxis[0].plotLinesAndBands[0].options.value=this.x;
                history_chart.xAxis[0].plotLinesAndBands[0].render();
                $(".map-sub-div").empty();
                var s = '<b>' + Highcharts.dateFormat('%H:%M:%S',this.x) + '</b>';
// %Y-%m-%d 
                $.each(this.points, function () {
                    s += '<br/>' + this.series.name + ': ' +
                       POSITION_ARRAY[this.y];
                    $("#map-"+POSITION_ARRAY[this.y]).append("<div class='map-item'>"+this.series.name+"</div>");
                });

                return s;
            },
            shared: true
        },
               
        series: packData(students)

        });