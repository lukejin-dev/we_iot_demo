import time,calendar,json,urllib,urllib2
from myoauthlib import oauth_connection

## Withings - oAuth 1.0
WT_CONFIGURE = {
    "TYPE":"WITHINGS",
    "CONSUMER_KEY" : "9c7465cffb438c3b0e276114b5565372d2ce71f775cf5b5822498d9275df",
    "CONSUMER_SECRET" : "429665c2c6ceb7e656de5032ce414e8293db24de50f14b0ceb43366cc00",
    "OAUTH_TOKEN" : "fd7939258ed6d869879fe01e7721d6c9458b07939457f45c208135dbbcd6e",
    "OAUTH_TOKEN_SECRET" : "f990e6e65712cde1c3e2c85c96e4f98eeb274edb0cd33fb77d41e2d2cd7b",
    "DEFAULT_PARAMS" : {"userid" : "4859718"}
}

## Fitbit - oAuth 1.0 userid=2VRHY3
FB_CONFIGURE = {
    "TYPE":"FITBIT",
    "CONSUMER_KEY" : "c897a4b04b754e5fb28c0a7c53fff41e",
    "CONSUMER_SECRET" : "8a1401a65ce042bc89908472754c44e4",
    "OAUTH_TOKEN" : "7eebc02685d9ee0bc683495bf1e8201c",
    "OAUTH_TOKEN_SECRET" : "78b7dbe804c15003ee50473e5cb68b60",
    "DEFAULT_PARAMS" : {}
}

## jawbone - oAuth 2.0
JB_CONFIGURE = {
    "TYPE" : "JAWBONE",
    "AUTH_KEY" : "b6_3pfGGwEhDbJDRzS8cJtpj72FdmizXtZ5jj96JKpYfRCU6KwEHQ6blI-dRctXd8EvaJSumcI0GoYT-V9UbpVECdgRlo_GULMgGZS0EumxrKbZFiOmnmAPChBPDZ5JP"
}

def get_day_bound(date):
    TIME_ZONE_OFFSET=-28800
    time_struct=time.strptime(date+" 00:00:00","%Y-%m-%d %H:%M:%S")
    start=int(calendar.timegm(time_struct)+TIME_ZONE_OFFSET)
    return {"start":start,"end":start+86400,"date":time.strftime("%Y-%m-%d",time_struct),"date_jawbone":time.strftime("%Y%m%d",time_struct)}

def withings(day):
    result={}   
    wt_con=oauth_connection(WT_CONFIGURE)
    
    wt_con.url="https://wbsapi.withings.net/v2/measure"
    wt_con.params={"action":"getactivity","date":day["date"]}
    json_obj = json.loads(wt_con.get())
    if(json_obj["body"].has_key("steps")):
        result["steps"]=json_obj["body"]["steps"]#Number of steps for the day.
        result["elevation"]=json_obj["body"]["elevation"]#Distance travelled for the day (in meters).
        result["distance"]=json_obj["body"]["distance"]#Distance travelled for the day (in meters).
        result["calories"]=json_obj["body"]["calories"]#Calories burned in the day (in kcal).
        result["soft"]=json_obj["body"]["soft"]#Duration of soft activities (in seconds).
        result["moderate"]=json_obj["body"]["moderate"]#Duration of moderate activities (in seconds).
        result["intense"]=json_obj["body"]["intense"]#Duration of intense activities (in seconds).
        
        
    wt_con.url="https://wbsapi.withings.net/v2/sleep"
    wt_con.params={"action":"get","startdate":str(day["start"]-43200),"enddate":str(day["end"]-43200)}#43200 = 12 hours. 
    json_obj = json.loads(wt_con.get())
    if(json_obj["body"].has_key("series")):
        result["sleep_series"]=json_obj["body"]["series"]#Serie of sleep data  0-awake  1-light sleep  2-deep sleep 3-REM sleep
        result["awake"]=0;
        result["light"]=0;
        result["deep"]=0;
        result["rem"]=0;
        for idx in range(len(result["sleep_series"])):
            if(result["sleep_series"][idx]["state"]==0):
                result["awake"]+=(result["sleep_series"][idx]["enddate"]-result["sleep_series"][idx]["startdate"])
            elif(result["sleep_series"][idx]["state"]==1):
                result["light"]+=(result["sleep_series"][idx]["enddate"]-result["sleep_series"][idx]["startdate"])
            elif(result["sleep_series"][idx]["state"]==2):
                result["deep"]+=(result["sleep_series"][idx]["enddate"]-result["sleep_series"][idx]["startdate"])
            elif(result["sleep_series"][idx]["state"]==3):
                result["rem"]+=(result["sleep_series"][idx]["enddate"]-result["sleep_series"][idx]["startdate"])
                

    wt_con.url="https://wbsapi.withings.net/measure"
    wt_con.params={"action":"getmeas","startdate":str(day["start"]),"enddate":str(day["end"]),"category":"1"}
    json_obj = json.loads(wt_con.get())
    for grp in json_obj["body"]["measuregrps"]:
        for item in grp["measures"]:
            if(item["type"]==1 and not result.has_key("weight")):
                result["weight"]=item["value"]
            elif(item["type"]==5 and not result.has_key("fat_free_mass")):
                result["fat_free_mass"]=item["value"]
            elif(item["type"]==6 and not result.has_key("fat_ratio")):
                result["fat_ratio"]=item["value"]
            elif(item["type"]==8 and not result.has_key("fat_mass_weight")):
                result["fat_mass_weight"]=item["value"]
    wt_con.url="https://wbsapi.withings.net/v2/measure"
    wt_con.params={"action":"getintradayactivity","startdate":str(day["start"]),"enddate":str(day["end"])}
    json_obj = json.loads(wt_con.get())
    print json_obj
    if(json_obj["body"].has_key("series")):
        result["activityseries"]=json_obj["body"]["series"]
    return result

def fitbit(day):
    result={}
    fb_con=oauth_connection(FB_CONFIGURE)
    
    fb_con.url="https://api.fitbit.com/1/user/-/sleep/date/"+day["date"]+".json"
    json_obj = json.loads(fb_con.get())
    result["totalMinutesAsleep"] = json_obj["summary"]["totalMinutesAsleep"]
    result["totalTimeInBed"] = json_obj["summary"]["totalTimeInBed"]
    result["totalSleepRecords"] = json_obj["summary"]["totalSleepRecords"]
    
    fb_con.url="https://api.fitbit.com/1/user/-/activities/date/"+day["date"]+".json"
    json_obj = json.loads(fb_con.get())
    result["activities"]=json_obj["activities"]
    result["steps"] = json_obj["summary"]["steps"]
    result["floors"] = json_obj["summary"]["floors"]
    for item in json_obj["summary"]["distances"]:
        if(item["activity"]=="total"):
            result["distance"]=item["distance"]
            break
    result["elevation"] = json_obj["summary"]["elevation"]
    result["caloriesOut"] = json_obj["summary"]["caloriesOut"]
    result["caloriesBMR"] = json_obj["summary"]["caloriesBMR"]
    result["lightlyActiveMinutes"] = json_obj["summary"]["lightlyActiveMinutes"]
    result["fairlyActiveMinutes"] = json_obj["summary"]["fairlyActiveMinutes"]
    result["sedentaryMinutes"] = json_obj["summary"]["sedentaryMinutes"]
    result["veryActiveMinutes"] = json_obj["summary"]["veryActiveMinutes"]
    
    fb_con.url="https://api.fitbit.com/1/user/-/body/date/"+day["date"]+".json"
    json_obj = json.loads(fb_con.get())
    if(json_obj.has_key("body")):
        result["weight"]=json_obj["body"]["weight"]
        result["bmi"]=json_obj["body"]["bmi"]
        result["fat"]=json_obj["body"]["fat"]
    return result
    
def jawbone(day):
    result={}
    jb_con=oauth_connection(JB_CONFIGURE)
    
    jb_con.url="https://jawbone.com/nudge/api/users/@me/sleeps?date="+day["date_jawbone"]
    json_obj = json.loads(jb_con.get())
    if(int(json_obj["data"]["size"])>0):
        result["sleep_duration"]=json_obj["data"]["items"][0]["details"]["duration"]#Total time for this sleep event, in seconds.
        result["sleep_quality"]=json_obj["data"]["items"][0]["details"]["quality"]#Sleep quality for the night.
        result["awake_time"]=json_obj["data"]["items"][0]["details"]["awake_time"]#Epoch timestamp when the user awoke.
        result["asleep_time"]=json_obj["data"]["items"][0]["details"]["asleep_time"]#Epoch timestamp when the user fell asleep.
        result["awakenings"]=json_obj["data"]["items"][0]["details"]["awakenings"]#Number of times the user awoke during sleep period.
        result["rem"]=json_obj["data"]["items"][0]["details"]["rem"]#REM sleep duration in seconds. NOTE: not in use at this time.
        result["light"]=json_obj["data"]["items"][0]["details"]["light"]#Total light sleep time, in seconds.
        result["deep"]=json_obj["data"]["items"][0]["details"]["deep"]#Total deep sleep time, in seconds.
        result["awake"]=json_obj["data"]["items"][0]["details"]["awake"]#Total time spent awake, in seconds.
 
        
    jb_con.url="https://jawbone.com/nudge/api/users/@me/moves?date="+day["date_jawbone"]
    json_obj = json.loads(jb_con.get())
    if(int(json_obj["data"]["size"])>0):
         result["distance"]=json_obj["data"]["items"][0]["details"]["distance"]#Distance travelled, in meters.
         result["steps"]=json_obj["data"]["items"][0]["details"]["steps"]#Number of steps taken.
         result["calories"]=json_obj["data"]["items"][0]["details"]["calories"]#Total calories burned
         result["bmr_day"]=json_obj["data"]["items"][0]["details"]["bmr_day"]#Estimated basal metabolic rate for entire day, in calories.
         result["wo_time"]=json_obj["data"]["items"][0]["details"]["wo_time"]#Total time spent in workouts, in seconds.
         result["active_time"]=json_obj["data"]["items"][0]["details"]["active_time"]#Total active time for move, in seconds.
         result["longest_active"]=json_obj["data"]["items"][0]["details"]["longest_active"]#Longest consecutive active period, in seconds.
         result["inactive_time"]=json_obj["data"]["items"][0]["details"]["inactive_time"]#Total inactive time for move, in seconds.
         result["longest_idle"]=json_obj["data"]["items"][0]["details"]["longest_idle"]#Longest consecutive inactive period, in seconds.
         result["hourly_totals"]=json_obj["data"]["items"][0]["details"]["hourly_totals"]
##    jb_con.url="https://jawbone.com/nudge/api/users/@me/body_events?date="+day["date_jawbone"]
##    json_obj = json.loads(jb_con.get())
##    print json_obj
##    if(int(json_obj["data"]["size"])>0):
##        result["weight"]=json_obj["data"]["items"][0]["weight"]#Body weight in kilograms.
##        result["body_fat"]=json_obj["data"]["items"][0]["body_fat"]#Body fat percentage.
##        result["bmi"]=json_obj["data"]["items"][0]["body_fat"]#Body mass index.
    return result

def fetch_day(date):
    day=get_day_bound(date)
    json_obj={}    
    json_obj["fitbit"]=fitbit(day)
    print " - Fibit Finished."
    json_obj["withings"]=withings(day)
    print " - Withings Finished."   
    json_obj["jawbone"]=jawbone(day)
    print " - jawbone Finished."
    return json_obj

def fetch_and_post(date):
    print "== Fetch and Post: "+date+" =="
    day=get_day_bound(date)
    json_obj=fetch_day(date)
    json_content=json.dumps(json_obj)
    req=urllib2.Request("http://p1demo.sinaapp.com/do_post_327.php",urllib.urlencode({"date":day["date"],"json_content":json_content}))   
    print " - Server Response: " + urllib2.urlopen(req).read()

def batch_fetch_and_post(enddate,day_num):
    time_struct=time.strptime(enddate+" 00:00:00","%Y-%m-%d %H:%M:%S")
    start=calendar.timegm(time_struct)
    for i in range(day_num):        
        fetch_and_post(time.strftime("%Y-%m-%d",time.gmtime(start-86400*i)))
        time.sleep(80)# Fitbit API Rate Limit: 150/H

    
    
