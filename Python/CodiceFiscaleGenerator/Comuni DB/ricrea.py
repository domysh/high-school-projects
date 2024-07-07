import json

dataj = json.load(open("comuni.json"))
newdata = [{"provincia":"","paesi":[{"comune":"","belfiore":""}]}]
del newdata[0]
for paese in dataj:
	thereis = False
	for da in newdata:
		if paese["provincia"] == da["provincia"]:
			da["paesi"].append({"comune":paese["comune"],"belfiore":paese["cod_fisco"]})
			thereis=True
			break

	if not thereis:
		newdata.append({"provincia":paese["provincia"],"paesi":[{"comune":paese["comune"],"belfiore":paese["cod_fisco"]}]})

with open("output.json", "w") as outfile:
    json.dump(newdata, outfile, indent=4)
