#!/usr/bin/env python
import urllib2
import urllib
import json
import pprint

# Use the json module to dump a dictionary to a string for posting.
#data_string = urllib.quote(json.dumps({'id': 'data-explorer'}))

# Make the HTTP request.
response = urllib2.urlopen('http://134.76.21.222/ckan/api/3/action/package_list')
assert response.code == 200

# Use the json module to load CKAN's response into a dictionary.
response_dict = json.loads(response.read())

# Check the contents of the response.
assert response_dict['success'] is True
packages = response_dict['result']

for package in packages:
    data_string = urllib.quote(json.dumps({'id': package}))
    response = urllib2.urlopen('http://134.76.21.222/ckan/api/3/action/package_show',data_string)
    assert response.code == 200
    response_dict = json.loads(response.read())
    assert response_dict['success'] is True
    result = response_dict['result']
    for res in result['resources']:
        pprint.pprint(res)
