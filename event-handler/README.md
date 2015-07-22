curl -X PUT localhost:8080/events --data "{'type':'startContext', 'runId':'1', 'data':'d'}"
curl -X PUT localhost:8080/events --data "{'type':'interContext', 'runId':'1', 'reference':'1', 'data':'d'}"
curl -X PUT localhost:8080/events --data "{'type':'stopContext', 'runId':'1', 'data':'d', 'total':1}"