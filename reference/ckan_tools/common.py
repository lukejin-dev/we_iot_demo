import urllib2
import urllib
import json
import pprint

def fun_exe(resource_dict,fun_name,api_key):
	data_dict = urllib.quote(json.dumps(resource_dict))

	request = urllib2.Request('http://202.121.178.242/api/3/action/' + fun_name)
	request.add_header('Authorization', api_key)

	response = urllib2.urlopen(request, data_dict)
	assert response.code == 200

	response_dict = json.loads(response.read())
	assert response_dict['success'] is True

	fun_result = response_dict['result']

def datastore_create(resource_id,api_key):
	# create a datastore for a resource
	resource_dict = {
    	"resource_id": resource_id,
    	"force": True,
    	#"fields": [{"id": "author", "type": "text"}, {"id": "submitted_on", "type": "timestamp"}, {"id": "PM2_5", "type": "float"}, {"id": "CO", "type": "float"}, {"id": "PM10", "type": "float"}, {"id": "SO2", "type": "float"}, {"id": "O3", "type": "float"}, {"id": "NO2", "type": "float"}],
	"fields": [{"id": "date", "type": "text"}, {"id": "mac_address", "type": "text"}, {"id": "sensor_uuid", "type": "text"}, {"value": "value", "type": "text"}],
    	# 'resource': resource,
    	# 'aliases': ['author', 'submitted_on', 'PM2_5', 'CO', 'PM10', 'SO2', 'O3', 'NO2'],
    	# 'records': records
    	# 'primary_key': ['_id'],
    	# 'indexes': ['id'],
	}
	
	fun_exe(resource_dict,'datastore_create',api_key)

def url_update(resource_id,api_key):
	# update the url of a resource for the datastore
	resource_dict = {
    	'id': resource_id,
    	'url': 'http://202.121.178.242/datastore/dump/' + resource_id,
    	'revision_id': '1.1'
	}

	fun_exe(resource_dict,'resource_update',api_key)

def datastore_upsert(resource_id,data,api_key):
	# insert a new record into the datastore
	resource_dict = {
    	"resource_id": resource_id,
    	"force": True,
    	"records": data,
    	'method': 'insert',
	}

	fun_exe(resource_dict,'datastore_upsert',api_key)

# your resource_id and api_key
resource_id = '88584c0e-4618-41ab-9cc5-25dd8d9ddfa5'
api_key = '268016bf-92cd-48ca-8406-3ad2f1528c1b'
