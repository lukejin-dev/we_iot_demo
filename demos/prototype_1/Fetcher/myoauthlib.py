##   A simple oAuth 1.0/2.0 request lib for Fitbit, Withings and jawbone. 
##   Nihiue@gmail.com 10/13/2014

import hmac
from hashlib import sha1
from urllib import quote, urlencode
from base64 import b64encode
from urlparse import urlparse
from random import getrandbits
from time import time
import urllib,urllib2

class oauth_connection:
  
  def __init__(self,configure,url="",params={}):
    self.configure=configure
    self.url=url
    self.params=params

  # For oAuth 1.0
  def sign_request_sha1(self,data,method):    
    pu = urlparse(urlparse(self.url).geturl())
    normUrl = "%s://%s%s" % (
        pu.scheme,
        pu.hostname,
        pu.path,
        )
    names = data.keys()
    names.sort()
    sig = "%s&%s&%s" % (
            method,
            quote(normUrl,''),
            quote("&".join(["%s=%s" % (k,quote(data[k].encode('utf-8'),'')) for k in names]),''),
            )
    key = "%s&%s" % (quote(self.configure["CONSUMER_SECRET"].encode('utf-8'),''),self.configure["OAUTH_TOKEN_SECRET"])
    return b64encode(hmac.new(key,sig,sha1).digest())
  
  # For oAuth 1.0
  def make_request(self,method):
    data = {     
      "oauth_consumer_key":self.configure["CONSUMER_KEY"],
      "oauth_token":self.configure["OAUTH_TOKEN"],
      "oauth_nonce" : b64encode("%0x" % getrandbits(256))[:32],
      "oauth_timestamp" : str(int(time())),   
      "oauth_signature_method" : "HMAC-SHA1",  
      "oauth_version" : "1.0",    
      }
    for key in self.configure["DEFAULT_PARAMS"].keys():
      data[key]=self.configure["DEFAULT_PARAMS"][key]
    for key in self.params.keys():
      data[key]=self.params[key]
    data["oauth_signature"] = self.sign_request_sha1(data,method)  
    return data    

  def get(self):  
    res="Uknown TYPE:"+self.configure["TYPE"]  
    if (self.configure["TYPE"]=="WITHINGS"):
      data=self.make_request("GET")
      req_url="%s?%s"%(self.url,"&".join(["%s=%s" % (key,quote(data[key].encode('utf-8'),'')) for key in data.keys()]))
      res = urllib.urlopen(req_url).read()

    elif(self.configure["TYPE"]=="FITBIT"):
      data=self.make_request("GET")
      names = data.keys()
      names.sort()
      auth_str="OAuth "
      for key in names:
        auth_str=auth_str+(key+'=\"'+quote(data[key])+'\",')
      #print self.url
      req=urllib2.Request(self.url)
      req.add_header("Authorization",auth_str)
      res = urllib2.urlopen(req).read()

    elif(self.configure["TYPE"]=="JAWBONE"):     
      req = urllib2.Request(self.url);
      req.add_header('Authorization', 'Bearer '+self.configure["AUTH_KEY"]);
      res = urllib2.urlopen(req).read()
    #print res
    return res

   
  
    
    
    
