# -*- coding: utf-8 -*-

#COSTANTI
maxbase=16

#Intestazione
print("-|-|-|-|-|-|____|-|-|-|-|-|-")
print("############################")
print("########CONVERTITORE########")
print("#########UNIVERSALE#########")
print("############################")
print("-|-|-|-|-|-|____|-|-|-|-|-|-")
print("\n")

#Convertitore Numerale ABCDEF -->1,2,3...
def num_cvt(ele):
	switcher = {
	'0':0,'1':1,'2':2,'3':3,'4':4,'5':5,'6':6,'7':7,'8':8,'9':9,
	'A':10,'10':'A',  'B':11,'11':'B',
	'C':12,'12':'C',  'D':13,'13':'D',
	'E':14,'14':'E',  'F':15,'15':'F',
	'-':0,'+':0
	}
	return switcher.get(str(ele),"0")

def stringRePos(strd,ind,obj):
	tmp = ""
	for ind_l in range(0,len(strd)):
		if ind_l == ind:
			tmp += obj
		else:
			tmp += strd[ind_l]
	return tmp

def addBinaryString(strbin, ind):
	if ind < 0:
		return "1"+ strbin
	else:
		if strbin[ind] == "1":
			strbin = stringRePos(strbin,ind,"0")
			return addBinaryString(strbin,ind-1)
		elif strbin[ind] == "0":
			return stringRePos(strbin,ind,"1")

def complementoA1(instr):
	complemento_a_1 = "1"
	for bit in instr:
		if bit == "0":
			complemento_a_1 +="1"
		elif bit == "1":
			complemento_a_1 += "0"
	return complemento_a_1

def complementoA2(instr):
	instr = complementoA1(instr)
	return addBinaryString(instr,len(instr)-1)

def toDecimal(instr,base_n):
	if base_n != 10:
		base10=0
		tmp_pos=len(instr)-1
		for sym in instr:
			base10+=base_n**tmp_pos*num_cvt(sym)
			tmp_pos-=1
		return base10
	else:
		return int(num)

def fromDecimalToBase(dec,baseto):
	output = ""
	if baseto != 10:
		tmp_output=""
		while dec != 0:
			tmp_output+=str(num_cvt(str(dec % baseto)))
			dec = int(dec/baseto)
		tmp_i=len(tmp_output)-1
		while tmp_i >= 0:
			output+=str(tmp_output[tmp_i])
			tmp_i-=1
	else:
		output = dec
	return output

def fromBasetoBase(inNum,inBase,outBase):
	if inBase == outBase:
		return inNum
	else:
		return fromDecimalToBase(toDecimal(inNum,inBase),outBase)

#Input
#Variabili utili al controllo
minnum = 2
negative_binary = False;
e=True
#INPUT
while e:
	num = input("Inserire numero da convertire:")
	num = num.upper()
	for ele in num:
		numint = num_cvt(ele)
		if numint == "0":
			e=True
			print("Inserire numero valido!")
			break
		else:
			minnum = max(minnum,numint+1)
			e=False
e=True
while e:
	try:
		base = input("In che base è il numero? (Massimo "+str(maxbase)+", Minimo "+str(minnum)+"):")
		base = int(base)
	except:
		base = -1
	if base >= minnum and base <= maxbase:
		e = False
	else:
		print("Questa base non è valida per il numero inserito prima!")
		e = True

e=True
while e:
	try:
		baseout = input("In che base tradurre?(da 2 a "+str(maxbase)+"):")
		baseout = int(baseout)
	except:
		baseout = -1
	if baseout >= 2 and baseout <= maxbase:
		e = False
	else:
		print("Questa base non è valida!")
		e = True

negative_binary = baseout == 2 and num[0]=="-"

if negative_binary:
	e=True
	while e:
		try:
			negativo_ID = input("\nINSERIRE SCELTA PER NUMERO NEGATIVO\n1 -> Numero Negativo (Modulo e segno)\n2 -> Numero Negativo (Complemento a 1)\n3 -> Numero Negativo (Complemento a 2)\nScelta:");
			negativo_ID = int(negativo_ID)
		except:
			negativo_ID = -1
		if negativo_ID > 3 and negativo_ID < 0:
			print("Input non valido!")
			e = True
		else:
			e = False
#Calcolo
output = fromBasetoBase(num,base,baseout)

if negative_binary:
	if negativo_ID == 1:
		output = "-" + output
	elif negativo_ID ==2:
		output = complementoA1(output)
	elif negativo_ID == 3:
			output = complementoA2(output)

if baseout != 2:
	if num[0] == '-':
		output = "-" + output
	elif num[0] == '+':
		output = "+" + output
#Stampa
print("_-_-_ Risultato _-_-_")
print("Da: "+str(num)+" (base "+str(base)+");")
print("A: "+str(output)+" (base "+str(baseout)+");")
#END
input("Premi invio per chiudere il programma...")
