#!/usr/bin/python
# -*- coding: utf-8 -*-

###########################
######### IMPORTS #########
###########################

import os
import select
import socket
import sys
from _socket import SOL_SOCKET, SO_REUSEADDR
import pysftp as sftp
import cv2
import JustLisLI as LIS #JustLisLI
import JustBraille as BRAILLE

############################
##### GLOBAL VARIABLES #####
############################

###REMOTE PARAMETERS DEFAULT

#################################################################################################
################################################################################################# #
USER =                  "nao" #Username of ssh                                               ####  #
PASS =                  "nao" #Password relative to USER                                     ####   #
LIS_NAME=               "lis" #Name of folder where NAO save the image of lis                ####    #
BRAILLE_NAME=           "braille" #Name of folder where NAO save the image of braille        ####    #
IMG_NAME =              "in.jpg" #Name of image that NAO take                                ####   #
FILE_LOCAL = "./"+    	"b.jpg" #Name of local image imported form NAO                       ####  #
################################################################################################# #
#################################################################################################

###JOB VARIABLES
HOME = "/var/persistent/home/"+USER+"/"
LIS_PATH_IMG = HOME+LIS_NAME+"/"+IMG_NAME
BRAILLE_PATH_IMG = HOME+BRAILLE_NAME+"/"+IMG_NAME

CONNECTION_LIST = []
ROBOTS_CONNECTION = [["NAME","IP"]]
del ROBOTS_CONNECTION[0]
RECV_BUFFER = 4048

###########################
######## FUNCTIONS ########
###########################

#FUNCTION THAT CONFERM THAT THE CONNECTION IS POSSIBLE
def try_connect(ip):
	try:
		cnn = sftp.Connection(host=ip,username=USER,password=PASS)
		cnn.close()
		return True
	except:
		return False
#THIS FUNCTION DELETE "b''" IN THE STRING (BUG CREATED FROM JAVA SOCKET) CALLED FROM ME "BUG B"
def Bug_b(message):
	if message != "b' '":
		if message[0] == "b" and message[1] == "'":
			message = message[2:]
			message = message[:len(message)-1]
		while len(message)>=2 and message[0] == " ":
			message = message[1:]
		return message
	else:
		return False
#SEND TO ALL THE COMMAND
def broadcast_data(message):
	for i in range(1,len(CONNECTION_LIST)):
		message = str(message)
		try:
			CONNECTION_LIST[i].send(message.encode('utf-8'))
		except Exception as e:
				print("BAD BROADCAST!")
				print(e)
				CONNECTION_LIST[i].close()
				CONNECTION_LIST.remove(CONNECTION_LIST[i])	
	print("Broadcast -> "+str(message))
#TRY TO TAKE A FILE AND IMPORT HERE THAT AND DELETE IT FROM REMOTE HOST (PYSFTP CONNECTION)
def take_and_delete(connection,remote,local):
	try:
		connection.get(remote,local)
		connection.remove(remote)
		return True
	except FileNotFoundError:
		return False
#FUNCTION THAT VERIFY THE EXIST OF FILES
def lis_braille():
	for i in range(0,len(ROBOTS_CONNECTION)):
		try:
			cnn = sftp.Connection(host=ROBOTS_CONNECTION[i][1],username=USER,password=PASS)
		except:
			print("Robot: "+str(ROBOTS_CONNECTION[i][0])+" ELIMINATED")
			del ROBOTS_CONNECTION[i]
			break
		if take_and_delete(cnn,BRAILLE_PATH_IMG,FILE_LOCAL):
			print("|:+:-:+-> TRANSLATING BRAILLE <-+:-:+:|")
			try:
				out = BRAILLE.braille_tr(cv2.imread(FILE_LOCAL))
			except Exception as e:
				print(str(e))
				out = "NADA"
			broadcast_data(ROBOTS_CONNECTION[i][0]+";Speech;"+out)
		if take_and_delete(cnn,LIS_PATH_IMG,FILE_LOCAL):
			print("|:+:-:+-> TRANSLATING LIS <-+:-:+:|")
			try:
				out = LIS.trd_lis(cv2.imread(FILE_LOCAL))
			except Exception as e:
				print(str(e))
				out = "NADA"
			broadcast_data(str(ROBOTS_CONNECTION[i][0])+";Speech;"+str(out))
		try:
			os.remove(FILE_LOCAL)
		except:
			pass
		cnn.close()		

############################
########### MAIN ###########
############################

if __name__ == "__main__":
	#Input request and bind server
	if len(sys.argv) == 1:
		IP_SERVER = input("Insert IP Address:")
		PORT = input("Insert Port:")
	else:
		if len(sys.argv) < 3:
			print("Please provide ip and port number as arguments...")
			os.system('pause')
			sys.exit()
		elif len(sys.argv) > 3:
			print("Too many arguments...")
			os.system('pause')
			sys.exit()
		else:
			PORT = sys.argv[2]
			IP_SERVER = sys.argv[1]

	PORT = int(PORT)
	server_socket = socket.socket()
	server_socket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
	try:
		server_socket.bind((IP_SERVER, PORT))
	except OSError:
		print("The address isn't valid!")
		os.system('pause')
		sys.exit()
	print("IP Address -> "+str(IP_SERVER))
	server_socket.listen(10)
	CONNECTION_LIST.append(server_socket)
	print("Server started on port -> "+str(PORT)+"\n")

	#Server Cicle
	while True:
		read_sockets,_,_ = select.select(CONNECTION_LIST, [], [])
		for sock in read_sockets:
			if sock == server_socket:
				sockfd, addr = server_socket.accept()
				thereis = False
				for i in range(1,len(CONNECTION_LIST)):
					if CONNECTION_LIST[i].getpeername()[0] == addr[0]:
						CONNECTION_LIST[i] = sockfd
						thereis = True
						break
				if not thereis:
					CONNECTION_LIST.append(sockfd)
					print("Connesso! -> "+str(addr))
			else:
				try:
					data = sock.recv(RECV_BUFFER)
				except:
					data = False
					pass
				data = Bug_b(str(data))
				if data:
					#SPECIAL COMMANDS
					if data[0] == "!":
						data = str(data)[1:]
						print("Server Command -> "+str(data))
						if data == "STOP" or data == "EXIT":
							print("### THE SERVER IS STOPPING... ###")
							server_socket.close()
							print("### SOCKET CLOSED ###")
							sys.exit()
						elif data == "BRAILLELIS":
							print("SEARCH OF FILES STARTED!")
							lis_braille()
						else:
							data=data.split(";")
							data_argn = len(data)
							if data[0] == "NAOSUB" and data_argn >=2:
								ip_add = sock.getpeername()[0]
								if data[1] != "VNAO":
									if try_connect(ip_add):
										for i in range(0,len(ROBOTS_CONNECTION)):
											if ROBOTS_CONNECTION[i][1] == ip_add:
												thereis = True
												break
											if ROBOTS_CONNECTION[i][0] == data[1]:
												ROBOTS_CONNECTION[i][1] = ip_add
												thereis=True
												break
										if not thereis:
											ROBOTS_CONNECTION.append([data[1],ip_add])
											print("ROBOT SUBSCRIBED -> "+str(data[1])+" / "+str(ip_add))
										else:
											print(str(data[1])+" UPDATED")
									else:
										print("SFTP CONNECTION FAILED...")
								else:
									print("VIRTUAL ROBOT CAN'T SUBSCRIBE TO ROBOTS")
							else:
								print("COMMAND NOT CORRECT...")
					#NORMAL COMMANDS SEND TO ROBOTS
					else:
						broadcast_data(data)

	server_socket.close()
