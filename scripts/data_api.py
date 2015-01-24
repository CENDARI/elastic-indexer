#!/usr/bin/env python
import urllib2
import urllib
import json
import pprint
import sys

# Use the json module to dump a dictionary to a string for posting.
#data_string = urllib.quote(json.dumps({'id': 'data-explorer'}))

# Make the HTTP request.
url = 'http://localhost:42042/v1/resources/'+sys.argv[1]
print "Reading url "+url
request = urllib2.Request(url)
request.add_header('Authorization', sys.argv[2])
response = urllib2.urlopen(request)
assert response.code == 200

# Use the json module to load CKAN's response into a dictionary.
response_dict = json.loads(response.read())
pprint.pprint(response_dict)

