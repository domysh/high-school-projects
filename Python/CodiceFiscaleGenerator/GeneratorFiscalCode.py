# -*- coding: utf-8 -*-

import DBComuni as DB

def IsAVocale(lett):
	lett = lett.upper()
	return (lett == 'A' or lett == 'E' or lett == 'I' or lett=='O' or lett=='U')

def IsAConsonante(lett):
	return not IsAVocale(lett)

def ReturnSeparateLetters(text):
	resV = resC = ""
	for let in text:
		if IsAVocale(let):
			resV+=let
		else:
			resC+=let
	return [resV,resC]

def CalculateSurename(surename):
	res = ""
	voc,cons = ReturnSeparateLetters(surename)
	if len(surename)<3:
		res = cons+voc
		for i in range(1,4-len(surename)):
			res+="X"
	elif len(cons)<3:
		res = cons
		for let in voc:
			res+=let
			if len(res) == 3:
				break
	else:
		for i in range(0,3):
			res += cons[i]
	return res

def CalculateName(name):
	res = ""
	voc,cons = ReturnSeparateLetters(name)
	if len(name)<3:
		res = cons+voc
		for i in range(1,4-len(name)):
			res+="X"
	elif len(cons)<3:
		res = cons
		for let in voc:
			res+=let
			if len(res) == 3:
				break
	elif len(cons)==3:
		res = cons
	else:
		res+=cons[0]+cons[2]+cons[3]
	return res

def DateToNum(date):
	date.replace(" ","")
	arr = date.split("/")
	if len(arr) == 3:
		try:
			day = int(arr[0])
			month = int(arr[1])
			year = int(arr[2])
			return [day,month,year]
		except:
			pass
	return False

def YearCode(Year):
	Year = str(Year)
	if len(Year) == 4:
		return str(Year[2]+Year[3])
	return False

def MonthCode(month):
	if month == 1:
		return "A"
	elif month == 2:
		return "B"
	elif month == 3:
		return "C"
	elif month == 4:
		return "D"
	elif month == 5:
		return "E"
	elif month == 6:
		return "H"
	elif month == 7:
		return "L"
	elif month == 8:
		return "M"
	elif month == 9:
		return "P"
	elif month == 10:
		return "R"
	elif month == 11:
		return "S"
	elif month == 12:
		return "T"
	else:
		return False

def MaleOrFemale(text):
	if text[0] == 'F':
		return True
	else:
		return False

def DayCode(Day,sex):
	res = ""
	if sex:
		Day+=40
	if Day == 0:
		return False
	elif Day <10 and Day >0:
		return "0" + str(Day)
	else:
		return str(Day)

def SexAndBirth(sex,birth):
	d,m,y = DateToNum(birth)
	res=""
	res += YearCode(y)
	res += MonthCode(m)
	res += DayCode(d,MaleOrFemale(sex))
	return res

def ConvertDispariPos(let):
	if let == "0":
		return 1
	elif let == "1":
		return 0
	elif let == "2":
		return 5
	elif let == "3":
		return 7
	elif let == "4":
		return 9
	elif let == "5":
		return 13
	elif let == "6":
		return 15
	elif let == "7":
		return 17
	elif let == "8":
		return 19
	elif let == "9":
		return 21
	elif let == "A":
		return 1
	elif let == "B":
		return 0
	elif let == "C":
		return 5
	elif let == "D":
		return 7
	elif let == "E":
		return 9
	elif let == "F":
		return 13
	elif let == "G":
		return 15
	elif let == "H":
		return 17
	elif let == "I":
		return 19
	elif let == "J":
		return 21
	elif let == "K":
		return 2
	elif let == "L":
		return 4
	elif let == "M":
		return 18
	elif let == "N":
		return 20
	elif let == "O":
		return 11
	elif let == "P":
		return 3
	elif let == "Q":
		return 6
	elif let == "R":
		return 8
	elif let == "S":
		return 12
	elif let == "T":
		return 14
	elif let == "U":
		return 16
	elif let == "V":
		return 10
	elif let == "W":
		return 22
	elif let == "X":
		return 25
	elif let == "Y":
		return 24
	elif let == "Z":
		return 23
	else:
		return False

def ConvertPariPos(let):
	let = let.upper()
	if (ord(let) >= ord('0')) and (ord(let) <= ord('9')):
		return int(let)
	else:
		return ord(let)-ord("A")

def ConvertNumberToDigit(num):
	return chr(num+ord("A"))

def CheckDigit(FiscalCode):
	partext = ""
	disptext = ""
	resNum = 0
	swi = False
	for let in FiscalCode:
		if swi:
			partext+=let
		else:
			disptext+=let
		swi ^= True
	for let in partext:
		resNum+=ConvertPariPos(let)
	for let in disptext:
		resNum+= ConvertDispariPos(let)
	return ConvertNumberToDigit(resNum%26)

def SeparateProvinceToCity(birthplace):
	province = ""
	city = ""
	priority = 0
	for l in birthplace:
		if l == "(":
			priority+=1
		elif l== ")":
			priority-=1
		elif priority == 0:
			city+=l
		else:
			province+=l
	city = city.replace(" ","")
	province = province.replace(" ","")
	if (province == "") or (city == ""):
		return False
	return [city,province]

def SearchinDB(city,province):
	pos=-1
	for i in range(0,len(DB.DBComuni)):
		if DB.DBComuni[i]["provincia"] == province:
			pos = i
			break
	if pos == -1:
		return False
	else:
		for ct in DB.DBComuni[i]["paesi"]:
			look = ct["comune"].upper()
			look = look.replace(" ","")
			if look == city.upper():
				return ct["belfiore"].upper()
	return False

def GiveBelfiore(birthplace):
	c,p = SeparateProvinceToCity(birthplace)
	return SearchinDB(c,p)
def inNumberString(text):
	for l in text:
		if (ord(l) >= ord("0")) and (ord(l) <= ord("9")):
			return True
	return False


if __name__ == "__main__":

	while(True):
		nome = input("INSERIRE IL NOME:")
		nome = nome.upper()
		if not inNumberString(nome):
			break
		print("Nome non valido, i numeri non sono ammessi!")

	while(True):
		cognome = input("INSERIRE IL COGNOME:")
		cognome = cognome.upper()
		if not inNumberString(nome):
			break
		print("Cognome non valido, i numeri non sono ammessi!")

	while(True):
		sesso = input("INSERIRE M (Maschio) o F (Femmina):")
		sesso = sesso.upper()
		if (len(sesso) == 1) and (sesso == "M" or sesso == "F"):
			break
		print("Inserire solo M o F!")

	while(True):
		nascita_data = input("Inserire la data (GG/MM/AAAA):")
		nascita_data = nascita_data.upper()
		if DateToNum(nascita_data):
			break
		print("Inserire una data Valida! (dividi con lo \"/\")")


	while(True):
		nascita_luogo = input ("INSERIRE LA CITTA' DI NASCITA (CON LA SIGLA DELLA PROVINCIA):")
		nascita_luogo = nascita_luogo.upper()
		if SeparateProvinceToCity(nascita_luogo):
			break
		print("Inserire paese valido (Inserire la provincia tra parentesi!)")

	firstpart = CalculateSurename(cognome)+CalculateName(nome)+SexAndBirth(sesso,nascita_data)+GiveBelfiore(nascita_luogo)
	print(firstpart+CheckDigit(firstpart))
