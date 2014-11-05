# -*- coding: utf-8 -*-
import sqlite3,sys,json
db = sqlite3.connect("./RT_Server.db")
cu = db.cursor()
cu.execute("CREATE TABLE IF NOT EXISTS realtime(id INTEGER PRIMARY KEY AUTOINCREMENT,clientid varchar(50) NOT NULL,content NTEXT,time INTEGER NOT NULL)")
#cu.execute("insert into realtime(clientid,content,time) values(?,?,strftime('%s', 'now'))",("had",'{"basd":"sda"}'))
db.commit() 
try:
  cu.execute("SELECT clientid,content,time FROM realtime ORDER BY time DESC")
  print cu.fetchall()
except Exception,e:
  print sys.exc_info()    
#