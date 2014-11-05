# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import json
import sqlite3
from flask import Flask,redirect
from flask.ext.restful import reqparse, abort, Api, Resource
from flask.ext.restful.utils import cors
from flask.ext.restful.representations.json import output_json
output_json.func_globals['settings'] = {'ensure_ascii': True, 'encoding': 'utf8'}

app = Flask(__name__)
api = Api(app)

#args parser for prototype2
p2_parser = reqparse.RequestParser()
p2_parser.add_argument('clientid', type=str)
p2_parser.add_argument('content', type=str)
p2_parser.add_argument('time_after', type=int)
p2_parser.add_argument('time_before', type=int)
p2_parser.add_argument("limit",type=int)

#action handler for prototype2
class p2Handler(Resource):
    def get(self, action):
        db = sqlite3.connect("./RT_Server.db")
        cu = db.cursor() 
        if action == "read_latest":            
            cu.execute("SELECT id,clientid,time,content FROM realtime ORDER BY time DESC LIMIT 10")
            res=[]
            for item in cu.fetchall():
                res.append({"id":item[0],"clientid":item[1],"time":item[2],"content":json.loads(item[3])})
            cu.close()
            db.close()
            return res
        elif action == "read":
            args = p2_parser.parse_args()
            time_before=args["time_before"]
            time_after=args["time_after"]
            limit=args["limit"]
            if not time_before:
                time_before=9999999999  
            if not time_after:
                time_after=0
            if not limit:
                limit=1000
            if not args["clientid"]:
                cu.execute("SELECT id,clientid,time,content FROM realtime WHERE time>? AND time<? ORDER BY time DESC LIMIT ?",(time_after,time_before,limit))
            else:
                cu.execute("SELECT id,clientid,time,content FROM realtime WHERE time>? AND time<? AND clientid=? ORDER BY time DESC LIMIT ?",(time_after,time_before,args["clientid"],limit))
            res=[]
            for item in cu.fetchall():
                res.append({"id":item[0],"clientid":item[1],"time":item[2],"content":json.loads(item[3])})
            cu.close()
            db.close()
            return res
        cu.close()
        db.close()
        return {'error':'action undefined'}, 400


    def delete(self, action):
        return {'error':'method not supprot.'}, 405

    def options(self, action):
        return {'Allow' : 'POST,GET' }, 200, \
            { 'Access-Control-Allow-Origin': '*', \
                  'Access-Control-Allow-Methods' : 'POST,GET' }

    def post(self, action):
        if action == "write":
            args = p2_parser.parse_args()  
            if not (args["clientid"] and args["content"]):
                return {'error':"params missing."},401
            print "\n -ClientID: "+args["clientid"]
            print " -Content: "+args["content"]
            json_obj=json.loads(args["content"])
            db = sqlite3.connect("./RT_Server.db")
            cu = db.cursor()  
            cu.execute("CREATE TABLE IF NOT EXISTS realtime(id INTEGER PRIMARY KEY AUTOINCREMENT,clientid varchar(50) NOT NULL,content NTEXT,time INTEGER NOT NULL)")
            cu.execute("insert into realtime(clientid,content,time) values(?,?,strftime('%s', 'now'))",(args["clientid"],json.dumps(json_obj)))
            db.commit() 
            cu.close()            
            db.close()
            return {"ok":"1"},201
        if action == "runsql":
            args = p2_parser.parse_args()  
            db = sqlite3.connect("./RT_Server.db")
            cu = db.cursor()  
            cu.execute(args["content"])
            res=cu.fetchall()
            db.commit() 
            cu.close()            
            db.close()
            return res
        return {'error': 'action undefined.'}, 400


## Actually setup the Api resource routing here
api.decorators=[cors.crossdomain(origin='*', methods=['POST', 'OPTIONS'], headers=['X-Requested-With', 'Content-Type', 'Origin', 'Accept'])]
api.add_resource(p2Handler, '/api/p2/<string:action>')

#static file for front end
@app.route("/")
def index():
    return redirect("/static/index.html")
    
if __name__ == '__main__':
    app.run(host='0.0.0.0',port=83, threaded=True)
