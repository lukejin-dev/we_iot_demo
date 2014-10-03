from common import *

# data to transfer
data = [{"GPS":{
				"GPS_TIME":"2014-09-05 13:57:51",
				"LATITUDE":31.02647,
     			"SPEED":0.147,
     			"LONTITUDE":121.432101767,
     			"HEIGHT":62.9
     			},
			"DATA":{
				"SENSOR_TYPE": 0,
				"SENSOR_ID":"af18d284-3c8c-5442-ef9e-217e8ec77853",
				"SENSOR_VALUE":"[be ef be ef]"
				},
			"BOARD_TIME":"2014-09-05 13:58:00",
			"DEVICE_ID":"71178bf0-54e4-4e10-a2e0-6103f70621e8"}]

result = []
item = {}
item["Submit Time"] = data[0]["GPS"]["GPS_TIME"]
item["BOARD_TIME"] = data[0]["BOARD_TIME"]
item["GPS"] = "("+str(data[0]["GPS"]["LATITUDE"])+","+str(data[0]["GPS"]["LONTITUDE"])+")"
item["SENSOR_ID"] = data[0]["DATA"]["SENSOR_ID"]
item["SENSOR_VALUE"] = data[0]["DATA"]["SENSOR_VALUE"]
item["DEVICE_ID"] = data[0]["DEVICE_ID"]
result.append(item)

# call the datastore_upsert() to insert data
datastore_upsert(resource_id, result, api_key)
