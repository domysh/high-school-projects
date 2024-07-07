#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys
import time
import os
import telepot
import random
import MySQLdb as dbSQL
import signal
#TOKEN
TOKEN = ""
#DATABASE
HOST=""
USER=""
PASS=""
DBNAME=""

#BOT OBJECT CREATION
bot = telepot.Bot(TOKEN)

class data:
	##########   Settings   ##########
	#Difficulty
	mineasy = 3
	maxeasy = 10
	
	minnormal = 10
	maxnormal = 18
	
	minhard = 15
	maxhard = 35

	#Number of session
	maxsession = 6

	#Esercizi list
	toracex = ["Piegamenti con piano d'appoggio","Piegamenti in ginocchio","Push UP",\
	"Piegamenti A Braccia Aperte","Box Piegamenti","Piegamenti Hindu",\
	"Piegamenti Sfasati","Push Up & Rotate","Piegamenti Con Gambe Sollevate",\
	"Burpee","Piegamenti \"A Diamante\"","Piegamenti A Ragno"\
	]
	gambex = ["Squat","Sollevamento Laterale Gamba Da Sdraiati Sinistra",\
	"Sollevamento Laterale Gamba Da Sdraiati Destra","Affondo Indietro","Calci A Sinistra",\
	"Calci A Destra","Alzata Polpacci Contro Il Muro","Squat Sumo Alzata Polpacci Contro Il Muro",\
	"Idranti A Sinistra","Indranti A Destra","LungeSide Leg Circles Sinistra","Side Leg Circles Destra",\
	"Squat Sumo","Sforbiciata Prona","Alzata Polpaccio Con Piede All'Infuori","Saltelli Con Una Sola","Gamba Polpaccio Sinistro",\
	"Saltelli Con Una Sola Gamba Polpaccio Destro","Bottom Leg Lift Sinistra","Bottom Leg Lift Destra","Affondo Incrociato",\
	"Che Salta Gli Squat","Calcio Posteriore Sinistro In Quadrupedia","Calcio Posteriore Destro In Quadrupedia",\
	"Alzate Stretching Appoggiati","Alzata Su Una Gamba Polpaccio Sinistro Contro Il Muro",\
	"Alzata Su Una Gamba Polpaccio Destro Contro Il Muro"\
	]
	spscex = ["Spinte Con I Romboidi","Piegamenti In Ginocchio","Piegamenti Per Tricipiti In Posizione Prona",\
	"Contrazioni Dei Romboidi Con Inclinazione All'Indietro","Allungo All'indietro Dei Tricipiti",\
	"Piegamenti Con Piano D'Appoggio","Tricep Dip A Terra","Piegamenti Sui Fianchi","Piegamenti Hover",\
	"Superman e Nuotatore","Ipertensione","Piegamenti Pike","Piegamenti","Bruco","Piegamenti In Posizione Supina",\
	"Sollevamento A Y A Terra","Angelo Rovesciato Nella Neve"\
	]
	
	bracciaex = ["Tricep Dip","Piegamenti \"A Diamante\"","Press Pulse Al Petto",\
	"Flessione Con La Gamba Sinistra Come Bilanciere","Flessione Con La Gamba Destra Come Bilanciere",\
	"Plank Diagonale","Push Up","Bruco","Piegamenti Al Muro","Piegamenti MIlitari","Push Up & Rotate",\
	"Tricep Dip A Terra","Burpee","Crunch Con Felssione Del Braccio Sinistro",\
	"Crunch Con Felssione Del Braccio Destro","Rotazione Delle Spalle In Avanti",\
	"Flessione Su Stipite A Sinistra","Flessione Su Stipite A Destra"\
	]
	addominaliex = ["Adominal Cruch","Torsione Russo","Montagna Scalatore","Tocco Dei Talloni",\
	"Sollevamento Gambe","Crunch Incrociato","Ponte Laterale A Sinistra","Ponte Laterale A Destra",\
	"Ponte Per Glutei","Scricchilio Della Bicicletta","V-UP"\
	]
	stretchingex = ["Jumping Jacks","Allungamento In Cobra","Allugamento Del Busto","Allugamento Delle Spalle",\
	"Rotazione Delle Braccia","Planck","Allungamento E Torsione Zona Lombare Sinistra",\
	"Allungamento E Torsione Zona Lombare Destra","Right Side Planck","Left Side Planck",\
	"Sollevamento Braccia","Sollevamento Laterale Del Braccio","Cerchi Delle Braccia In Senso Orario",\
	"Cerchi Delle Braccia In Senso Antiorario","Pugni","Allungamento Tricipite Destro","Allungamento Tricipite Sinistro",\
	"Allungamento Dei Bicipiti In Posizione Eretta - Sinistra",\
	"Allungamento Dei Bicipiti In Posizione Eretta - Destra","Ganci Alternati","Saltelli Senza Corda",\
	"Incocio Delle Braccia","Posizione Del Gatto","Posa Del Bambino","Allunghi A Sinistra Distesi Su Un Fianco",\
	"Allunghi A Destra Distesi Su Un Fianco","Saltello Laterale","Stretching Quadricipite Sinistro Contro La Parete",\
	"Stretching Quadricipite Destro Contro La Parete","Allungo Con Ginocchio Sinistro Al Torace",\
	"Allungo Con Ginocchio Destro Al Torace","Allungamento Polpaccio Sinistro","Allungamento Polpaccio Destro",\
	"Wall Sit","Stretching Farfalla Sdraiati"\
	]

	#Session MAX
	maxtorace = len(toracex)-1
	maxgambe = len(gambex)-1
	maxspsc = len(spscex)-1
	maxbraccia = len(bracciaex)-1
	maxaddominali = len(addominaliex)-1
	maxstretching = len(stretchingex)-1

	#chat IDs
	domylol = '358947306'
	redfox = '330254492'

#Log File
def logprint(message):
	file = open("bot.log","at")
	printms=str(time.strftime("%d/%m/%Y"))+" "+str(time.strftime("%H:%M:%S")+": "+str(message))
	print(printms)
	file.write(printms+"\n")
	file.close()

def killMe():
	try:
		time.sleep(0.1)
		logprint("#######  IL BOT E' STATO TERMINATO  #######")
		bot.sendMessage(data.domylol,"#######  IL BOT E' STATO TERMINATO  #######")
		os.kill(int(os.getpid()), signal.SIGTERM)
	except:
		logprint("#######  ERRORE NELLA CHIUSURA DEL BOT  #######")
		bot.sendMessage(data.domylol,"#######  ERRORE NELLA CHIUSURA DEL BOT  #######")
try:
	db = dbSQL.connect(HOST,USER,PASS,DBNAME)
	db.close()
	logprint("########## IL BOT E' STATO AVVIATO CORRETTAMENTE ##########")
	bot.sendMessage(data.domylol,"IL BOT E' STATO AVVIATO CORRETTAMENTE!")
except:
	logprint("########## Errore di avvio (DataBase error connection) ##########")
	killMe()

def executedb(command):
	try:
		db = dbSQL.connect(HOST,USER,PASS,DBNAME)
		cursor = db.cursor()
		cursor.execute(command)
		resul = cursor.fetchall()
		db.commit()
		out = []
		i=0
		if resul:
			for e in resul:
				for ele in e:
					out.insert(i,ele)
				i+=1
		db.close()
		return out
	except:
		logprint("Errore nell'esecuzione di un comando SQL")

def errorAllert(chat_id):
	try:
		bot.sendMessage(data.domylol,"Errore da "+str(chat_id))
		logprint("Errore da "+str(chat_id))
		bot.sendMessage(chat_id,"Errore :(")
	except:
		logprint("Errore nella funzione errorAllert")

def numex(dif):
	if dif == 1: #easy
		return random.randint(data.mineasy,data.maxeasy)
	elif dif == 2: #normal
		return random.randint(data.minnormal,data.maxnormal)
	elif dif == 3: #hard
		return random.randint(data.minhard,data.maxhard)
	else:
		logprint("Errore nella funzione numex (Numero difficolta non identificato).")
		return 0	

def exgenstr(ses):
	if ses == 1:    #Torace Session 1
		return data.toracex[random.randint(0,data.maxtorace)]
	elif ses == 2:  #Gambe Session 2
		return data.gambex[random.randint(0,data.maxgambe)]
	elif ses == 3:  #spalle_schiena Session 3
		return data.spscex[random.randint(0,data.maxspsc)]
	elif ses == 4:  #Braccia 4
		return data.bracciaex[random.randint(0,data.maxbraccia)]
	elif ses == 5:  #Addominali 5
		return data.addominaliex[random.randint(0,data.maxaddominali)]
	elif ses == 6:  #stretching 6
		return data.stretchingex[random.randint(0,data.maxstretching)]
	else: #ERRORE
		logprint("Errore nella funzione ExGenSTR")

def exgen(ses,dif):
	try:
		return str("Fai "+str(numex(dif))+" "+exgenstr(ses))
	except:
		logprint("Errore nella funzione ExGen")

def welcomemessage(chat_id):
	bot.sendMessage(chat_id,\
"Benvenuto nel bot!🏋️💪\nQuesto bot può generare \nper te esercizi randomici\nda fare per allarsi!\nDai un'occhiata ai comandi...\
👍\nSe non conosci qualche esercizio,\nCerca su internet! 🖥\nPer iniziare prova /all e /random")

def difmessage(chat_id):
	bot.sendMessage(chat_id,\
"Scegli la tua difficoltà:\nScrivere /exit per annullare!\n⭕️Difficoltà attuale: "\
+giveDifficultySTR(chat_id)+"\n🔷  /Easy //  /Normal //  /Hard 🔷")

def allmessage(chat_id):
	bot.sendMessage(chat_id,">> 🏋Allena tutto il corpo!💪 <<\nDifficoltà Impostata\
: "+giveDifficultySTR(chat_id)+"\n🏃️Stretching: "+exgen(6,giveDifficulty(chat_id))+"\n\
💃️Gambe: "+exgen(2,giveDifficulty(chat_id))+"\n🏋️️Braccia: "+exgen(4,giveDifficulty(chat_id))+"\n\
🤾‍♂️️Spalle e Schiena: "+exgen(3,giveDifficulty(chat_id))+"\n🤸‍♂️️Torace: "+exgen(1,giveDifficulty(chat_id))+"\n\
🤽‍♂️️Addominali: "+exgen(5,giveDifficulty(chat_id))+"\n🏃‍♀️️Stretching Finale: "+exgen(6,giveDifficulty(chat_id)))

def isSUB(chat_id):
	try:
		res = executedb("SELECT `chat_id` FROM `CHAT_ID_CONF` WHERE `chat_id`="+str(chat_id))
		if res:
			return True
		else:
			return False
	except :
		errorAllert(chat_id)
		logprint("Errore nella funzione isSUB")

def isSUBalarm(chat_id):
	try:
		res = executedb("SELECT `chat_id` FROM `ALARM_TAB` WHERE `chat_id`="+str(chat_id))
		if res:
			return True
		else:
			return False
	except :
		errorAllert(chat_id)
		logprint("Errore nella funzione isSUBalarm")

def isActiveAlarm(chat_id):
	if isSUBalarm(chat_id) == True:
		try:
			res = executedb("SELECT `active` FROM `ALARM_TAB` WHERE `chat_id`="+str(chat_id))
			if res[0] == 1:
				return True
			else:
				return False
		except :
			errorAllert(chat_id)
	else:
		errorAllert(chat_id)
		logprint("Errore nella funzione isActiveAlarm")

def difficultyMOD(chat_id,username,dif):
	try:
		if isSUB(chat_id) == True:
			executedb("UPDATE `CHAT_ID_CONF` SET `difficulty` = "+str(dif)+" WHERE `chat_id` = "+str(chat_id))
		else:
			executedb("INSERT INTO `CHAT_ID_CONF`(`chat_id`,`difficulty`,`username`) VALUES ("+str(chat_id)+","+str(dif)+",'"+str(username)+"')")
		bot.sendMessage(chat_id,"Difficoltà impostata.👍")
	except:
		errorAllert(chat_id)
		logprint("Errore nella funzione difficultyMOD")

def giveDifficulty(chat_id):
	try:
		res = executedb("SELECT `difficulty` FROM `CHAT_ID_CONF` WHERE `chat_id`="+str(chat_id))
		if res:
			return res[0]
	except:
		errorAllert(chat_id)
		logprint("Errore nella funzione giveDifficulty")

def giveDifficultySTR(chat_id):
	dif = giveDifficulty(chat_id)
	if dif == 1:
		return "EASY"
	elif dif == 2:
		return "NORMAL"
	elif dif == 3:
		return "HARD"
	else:
		errorAllert(chat_id)
		logprint("Errore nella funzione giveDifficultySTR")

def listexSTR(array):
	out = ""
	for e in array:
		out+=e+"\n"
	return out

def timeHM(inp):
	i=0
	arou=0
	s=0
	out=[]
	while i < len(inp):
		if inp[i] == ":":
			out.insert(arou,inp[s:i])
			arou+=1
			s=i+1
		i+=1
	out.insert(arou,inp[s:i])
	if len(out) == 2 :
		try:
			out[0]= int(out[0])
			out[1]= int(out[1])
			if out and out[0]>=0 and out[0]<24 and out[1]>=0 and out[1]<60:
				return out
			else:
				return 0
		except:
			return 0
	else:
		return 0

def alarmMOD(chat_id,time,status):
	try:
		if isSUBalarm(chat_id) == True:
			executedb("UPDATE `ALARM_TAB` SET `H` = "+str(time[0])+", `M` = "+str(time[1])+", `active` = "+str(status)+" WHERE `chat_id` = "+str(chat_id))
		else:
			executedb("INSERT INTO `ALARM_TAB`(`chat_id`,`H`,`M`,`active`) VALUES ("+str(chat_id)+","+str(time[0])+","+str(time[1])+","+str(status)+")")
		bot.sendMessage(chat_id,"L'operazione è avvenuta con successo!👍")
	except:
		logprint("Errore nella funzione alarmMOD")
		errorAllert(chat_id)

def giveHalarm(chat_id):
	if isSUBalarm(chat_id) == True:
		try:
			res = executedb("SELECT `H` FROM `ALARM_TAB` WHERE `chat_id`="+str(chat_id))
			if res:
				return res[0]
		except :
			errorAllert(chat_id)
			logprint("Errore nella funzione giveHalarm")
	else:
		errorAllert(chat_id)
		logprint("Errore nella funzione giveHalarm")

def giveMalarm(chat_id):
	if isSUBalarm(chat_id) == True:
		try:
			res = executedb("SELECT `M` FROM `ALARM_TAB` WHERE `chat_id`="+str(chat_id))
			if res:
				return res[0]
		except :
			errorAllert(chat_id)
			logprint("Errore nella funzione giveMalarm")
	else:
		errorAllert(chat_id)
		logprint("Errore nella funzione giveMalarm")

def giveTimeAlarm(chat_id):
	return str(str(giveHalarm(chat_id))+":"+str(giveMalarm(chat_id)))

def alarmMSG(chat_id):
	if isSUBalarm(chat_id) == True:
		ans="Stato Allarme: "
		if isActiveAlarm(chat_id) == True:
			ans+="Attivo\n"
		else:
			ans+="Disattivato\n"
		ans +="Orario impostato:"+giveTimeAlarm(chat_id)+"\n"
		bot.sendMessage(chat_id,\
		ans+"⭕Modifica l'orario con ➡️ /alarm_time\n⭕Attiva con ➡️ /alarm_on\n⭕Disattiva con ➡️ /alarm_off")
	else:
		bot.sendMessage(chat_id,\
	"Crea il tuo allarme personale!⏰\nCon questa funzione il bot,\nti ricorda ad un ora che preferisci🏋\ndi fare gli esercizi e ti invia la lista!\nper annullare l'operazione scrivi /exit")
		createService(chat_id,1)

def isChatInService(chat_id):
	return os.path.exists("service/"+str(chat_id))

def createService(chat_id,id_service):
	try:
		servicefile = open("service/"+str(chat_id),"wt")
		servicefile.write(str(id_service))
		servicefile.close()
	except:
		logprint("Errore nella funzione createService")

def giveIDService(chat_id):
	if isChatInService(chat_id) == True:
		readf = open("service/"+str(chat_id))
		res = readf.read()
		return int(res)
	else:
		errorAllert(chat_id)
		logprint("Errore nella funzione giveIDService")

def rmService(chat_id):
	if isChatInService(chat_id) == True:
		os.remove("service/"+str(chat_id))
	else:
		errorAllert(chat_id)
		logprint("Errore nella funzione rmService")

def commandNotFound(chat_id):
	bot.sendMessage(chat_id,"Comando non trovato!")

def MSGsett(chat_id):
	bot.sendMessage(chat_id,\
		"Impostazioni:\n⭕️ Imposta la difficoltà ➡️ /Difficulty\n⭕️ Imposta/Modifica il tuo allarme ➡️ /alarm\n⭕️ Vedi gli esercizi che ti possono capitare ➡️ /info")

def everyMessage(string):
	res = executedb("SELECT chat_id FROM CHAT_ID_CONF")
	succesfulsend = 0
	if res:
		for chat_id in res:
			try:
				bot.sendMessage(chat_id,string)
				succesfulsend+=1
			except:
				logprint("IL MESSAGIO A "+str(chat_id)+" NON è STATO INVIATO PER UN EVENTUALE BLOCCO CHAT!")
		logprint("IL MESSAGGIO \""+string+"\" E' STATO INVIATO A:"+str(succesfulsend)+" PERSONE")
		bot.sendMessage(data.domylol,"IL MESSAGGIO \""+string+"\" E' STATO INVIATO A:"+str(succesfulsend)+" PERSONE")
	else:
		logprint("IL MESSAGGIO \""+string+"\" E' STATO INVIATO A NESSUNO!")
		bot.sendMessage(data.domylol,"IL MESSAGGIO \""+string+"\" E' STATO INVIATO A NESSUNO!")

#ChatControl
def handle(msg):
	content_type, chat_type, chat_id = telepot.glance(msg)
	try:
		username = '@'+msg['from']['username']
	except:
		username = msg['from']['first_name']

	if content_type == 'text':
		command = msg['text']
		command = command.encode('utf-8')
		if isChatInService(chat_id) == True:

			service = giveIDService(chat_id)
			logprint('Got command: %s' % command + '   -->   '+str(username)+"   Service N*"+str(service))

			if command == "/exit":
				rmService(chat_id)
				bot.sendMessage(chat_id,"Operazione annullata ❌")
			else:
				if service == 1: #Service ID ALARM --> IMPOSTA ORARIO
					time = timeHM(command)
					if time:
						alarmMOD(chat_id,time,1)
						rmService(chat_id)
					else:
						bot.sendMessage(chat_id,"Inserisci l'orario⏰,\nCon la seguente modalità = HH:MM 🏋\nGuarda l'esempio = 17:30")
				elif service == 2: #Service ID FIRST ENTRY --> IMPOSTA DIFFICOLTA INIZIALE
					if command == '/Easy':
						difficultyMOD(chat_id,username,1)
						rmService(chat_id)
					elif command == '/Normal':
						difficultyMOD(chat_id,username,2)
						rmService(chat_id)
					elif command == '/Hard':
						difficultyMOD(chat_id,username,3)
						rmService(chat_id)
					else:
						welcomemessage(chat_id)
						bot.sendMessage(chat_id,"Per iniziare a usare il bot,\nscegli la tua difficoltà iniziale💪\n🔷  /Easy //  /Normal //  /Hard 🔷")
				elif service == 3: #Service ID CHANGE DIFFICULTY --> CAMBIA DIFFICOLTA'
					if command == '/Easy':
						difficultyMOD(chat_id,username,1)
						rmService(chat_id)
					elif command == '/Normal':
						difficultyMOD(chat_id,username,2)
						rmService(chat_id)
					elif command == '/Hard':
						difficultyMOD(chat_id,username,3)
						rmService(chat_id)
					else:
						difmessage(chat_id)
				elif service == 4: #Service ID MESSAGE TO ALL --> INVIA UN MESSAGGIO A TUTTI!
					if command:
						everyMessage(command)
						rmService(chat_id)
					else:
						bot.sendMessage(data.domylol,"Inserisci il messaggio,📨\n/exit per annullare.❌")
				else:
					rmService(chat_id)
					errorAllert(chat_id)
					bot.sendMessage(chat_id,"Ci è stato un errore... \npuoi continuare comunque a usere il bot,\nl'amministratore è stato già contattato")
					logprint("Creato file Service errato a: "+str(chat_id))
		else:
			logprint('Got command: %s' % command + '   -->   '+str(username) )
			if isSUB(chat_id) == True:
				if command == '/start':
					welcomemessage(chat_id)
				elif command == '/random':
					bot.sendMessage(chat_id,">> 🏋Esercizi Random <<\n"+exgen(random.randint(1,data.maxsession),giveDifficulty(chat_id)))
				elif command == '/torace':
					bot.sendMessage(chat_id,">> 🏋Esercizi Al Torace <<\n"+exgen(1,giveDifficulty(chat_id)))
				elif command == '/gambe':
					bot.sendMessage(chat_id,">> 🏋Esercizi Alle Gambe <<\n"+exgen(2,giveDifficulty(chat_id)))
				elif command == '/spalle_schiena':
					bot.sendMessage(chat_id,">> 🏋Esercizi Alle Spalle E Schiena <<\n"+exgen(3,giveDifficulty(chat_id)))
				elif command == '/braccia':
					bot.sendMessage(chat_id,">> 🏋Esercizi Alle Braccia <<\n"+exgen(4,giveDifficulty(chat_id)))
				elif command == '/addominali':
					bot.sendMessage(chat_id,">> 🏋Esercizi Agli Addominale <<\n"+exgen(5,giveDifficulty(chat_id)))
				elif command == '/stretching':
					bot.sendMessage(chat_id,">> 🏋Stretching <<\n"+exgen(6,giveDifficulty(chat_id)))
				elif command == '/info':
					bot.sendMessage(chat_id,\
				">> 🏋INFO ESERCIZI <<\n⭕️Torace ➡️/torace_list\n⭕️Gambe ➡️/gambe_list\n⭕️Spalle e Schiena ➡️/spalle_schiena_list\n⭕Braccia ➡️/braccia_list\n⭕️Addominali ➡️/addominali_list\n⭕️Stretching ➡️/stretching_list")
				elif command == '/torace_list':
					bot.sendMessage(chat_id,">> 🏋Torace Lista <<\n"+listexSTR(data.toracex))
				elif command == '/gambe_list':
					bot.sendMessage(chat_id,">> 🏋Gambe Lista <<\n"+listexSTR(data.gambex))
				elif command == '/spalle_schiena_list':
					bot.sendMessage(chat_id,">> 🏋Spalle E Schiena Lista<<\n"+listexSTR(data.spscex))
				elif command == '/braccia_list':
					bot.sendMessage(chat_id,">> 🏋Braccia Lista<<\n"+listexSTR(data.bracciaex))
				elif command == '/addominali_list':
					bot.sendMessage(chat_id,">> 🏋Addominale Lista<<\n"+listexSTR(data.addominaliex))
				elif command == '/stretching_list':
					bot.sendMessage(chat_id,">> 🏋Stretching Lista<<\n"+listexSTR(data.stretchingex))
				elif command == '/all':
					allmessage(chat_id)
				elif command == '/Difficulty':
					createService(chat_id,3)
					difmessage(chat_id)
				elif command == '/donation':
					bot.sendMessage(chat_id,\
				"Questo bot è stato creato da:\nDomingo Dirutigliano 🖥\nCon l'aiuto di Gianfranco Baccarella 💪\nLink Donazione: paypal.me/domylol")
				elif command == '/settings':
					MSGsett(chat_id)
				elif command == '/alarm': #Service ID 1
					alarmMSG(chat_id)
				elif command == '/alarm_time':
					if isSUBalarm(chat_id):
						bot.sendMessage(chat_id,"Inserisci l'orario⏰, /exit per annullare")
						createService(chat_id,1)
					else:
						commandNotFound(chat_id)
				elif command == '/alarm_on':
					if isSUBalarm(chat_id):
						alarmMOD(chat_id,timeHM(giveTimeAlarm(chat_id)),1)
						bot.sendMessage(chat_id,"Allarme attivato👍")
					else:
						commandNotFound(chat_id)
				elif command == '/alarm_off':
					if isSUBalarm(chat_id):
						alarmMOD(chat_id,timeHM(giveTimeAlarm(chat_id)),0)
						bot.sendMessage(chat_id,"Allarme disattivato👍")
					else:
						commandNotFound(chat_id)
				elif command == '/exercise':
					bot.sendMessage(chat_id,">> Scegli l'esercizio che vuoi fare🏋<<\n⭕️ /gambe\n⭕️ /torace\n⭕️ /spalle_schiena\n⭕️ /braccia\n⭕️ /addominali\n⭕️ /stretching")
				#ADMIN COMMANDS
				elif str(chat_id) == data.domylol:
					if command == '/message':
						bot.sendMessage(chat_id,"Inserisci il messaggio, 📨\n/exit per annullare.❌")
						createService(chat_id,4)
					elif command == '/stop':
						killMe()
					else:
						commandNotFound(chat_id)
				else:
					commandNotFound(chat_id)
			else:
				createService(chat_id,2)
				welcomemessage(chat_id)
				bot.sendMessage(chat_id,"Per iniziare a usare il bot,\nscegli la tua difficoltà iniziale💪\n🔷  /Easy //  /Normal //  /Hard 🔷")
	else:
		logprint('Got command (Type): ' + str(content_type) + '   -->   '+str(username) )
		commandNotFound(chat_id)
#Ininitialization
bot.message_loop(handle)
#Loop Commands
logprint("I am listening")
while True:
	try:
		time.sleep(55)
		Ht = time.strftime("%H")
		Mt = time.strftime("%M")
		res = executedb("SELECT chat_id FROM ALARM_TAB WHERE H = "+str(Ht)+" AND M = "+str(Mt)+" AND active = 1")
		if res:
			for chat_id in res:
				bot.sendMessage(chat_id,"É l'ora di allenarsi!!⏰🏋")
				allmessage(chat_id)
				logprint("Messaggio di alarm inviato a:"+str(chat_id))
	except KeyboardInterrupt:
		logprint("#####  STOP COMMAND FROM TERMINAL!  #####")
		killMe()
	except :
		logprint("ERRORE NEL LOOP WHILE")

'''

DataBase Struct
CHAT_ID_CONF{
	[chat_id(9 Num)]
	[difficulty(1 Num)]
	[username(NoLimit String)]
}
ALARM_TAB{
	[chat_id(9 Num)]
	[H(2 Num)]
	[M(2 Num)]
	[active(1 Num)]
}

'''
