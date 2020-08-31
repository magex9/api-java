import sys, re, requests, json

auth_url = 'http://crm-auth-spring-boot-server-azure.466356a5ffaf4931ba8e.canadacentral.aksapp.io/auth/authenticate'
auth_creds = {'username':'crmadmin','password':'crmadmin'}
auth_resp = requests.post(auth_url, json=auth_creds)
if auth_resp.status_code != 200:
    print("Request status is not valid: " + str(auth_resp.status_code))
    sys.exit(1)

token = auth_resp.json()["token"]
if re.match("[A-Za-z_-]", token) is None:
    print("Token format is invalid: " + token)
    sys.exit(1)

rest_url = 'http://crm-api-spring-boot-server-azure.466356a5ffaf4931ba8e.canadacentral.aksapp.io/crm'
rest_headers = {'Content-type': 'application/json', 'Authorization': 'Bearer ' + token}
rest_user = rest_url + '/rest/persons'
rest_resp = requests.get(rest_user, headers=rest_headers)

if rest_resp.status_code != 200:
    print("Request status is not valid: " + str(rest_resp.status_code))
    sys.exit(1)

print(rest_resp.json())
sys.exit(0)
