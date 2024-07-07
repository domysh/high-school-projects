import numpy as np
import math
import cv2

#################### VARIABILI GLOBALI ######################

#Parametri per la stampa su immagine dei risultati

font = cv2.FONT_HERSHEY_SIMPLEX
put_text_color = (18,0,255)
put_text_pos = (60,50)

#Parametri per la thresh

lower_thresh1 = 129 
upper_thresh1 = 255

#Variabili controllo stampa lettere

NFrame=0
letters=['']
letter_correspond=""
FQframe=10
printletter_control = False
invertBW = False

#################### FUNZIONI ######################

def imInvertBW(img):
	B=W=0
	for x in range(0,len(img)):
		for y in range(0,len(img[x])):
			if img[x][y] > 0:
				W+=1
			else:
				B+=1
	return [B,W]

#################### APERTURA STREAM VIDEO ######################
cap = cv2.VideoCapture(0)


#################### LOOP ANALISI/STAMPA FRAME ######################
while cap.isOpened():
	#Read Flip and Crop
	ret, img = cap.read()
	img = cv2.imread("b.jpg")
	img = cv2.flip(img,1)
	cv2.rectangle(img,(60,60),(300,300),(255,255,2),4) #outer most rectangle 
	crop_img = img[70:300, 70:300]
	#Create other Image
	grey = cv2.cvtColor(crop_img, cv2.COLOR_BGR2GRAY)
	hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
	#Set Limits
	lower_red = np.array([0,150,50])
	upper_red = np.array([195,255,255])
	#Filter
	mask = cv2.inRange(hsv, lower_red, upper_red)

	res = cv2.bitwise_and(img,img, mask= mask)

	value = (35, 35)
	blurred = cv2.GaussianBlur(grey, value, 0)
	_, thresh1 = cv2.threshold(blurred, lower_thresh1, upper_thresh1, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)
	
	#B and W Control
	if invertBW:
		#B_pixel,W_pixel=imInvertBW(thresh1)
		#if W_pixel > B_pixel: 
			#thresh1 = (255-thresh1)
		thresh1 = (255-thresh1)
	
		
	#xyz,
	contours, hierarchy = cv2.findContours(thresh1.copy(),cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
	cnt = max(contours, key = lambda x: cv2.contourArea(x))
	x,y,w,h = cv2.boundingRect(cnt)
	cv2.rectangle(crop_img,(x,y),(x+w,y+h),(0,0,255),1)

	hull = cv2.convexHull(cnt)
	drawing = np.zeros(crop_img.shape,np.uint8)

	cv2.drawContours(drawing,[cnt],0,(0,255,0),0)
	cv2.drawContours(drawing,[hull],0,(0,255,255),0)

	hull  = cv2.convexHull(cnt,returnPoints = False)
			
	defects = cv2.convexityDefects(cnt,hull)

	count_defects = 0
	cv2.drawContours(thresh1, contours, -1, (0,255,0), 3)
	if type(defects) != type(None):
		for i in range(defects.shape[0]):
			s,e,f,d = defects[i,0]
			start = tuple(cnt[s][0])
			end = tuple(cnt[e][0])
			far = tuple(cnt[f][0])
			
			a = math.sqrt((end[0] - start[0])**2 + (end[1] - start[1])**2)
			b = math.sqrt((far[0] - start[0])**2 + (far[1] - start[1])**2)
			c = math.sqrt((end[0] - far[0])**2 + (end[1] - far[1])**2)

			angle = math.acos((b**2 + c**2 - a**2)/(2*b*c)) * 60
			cv2.circle(crop_img,far,4,[0,0,255],-1)                 

			if angle <= 90:
				count_defects += 1
				
			cv2.line(crop_img,start,end,[0,255,0],3)
		   
	area = cv2.contourArea(cnt)

	(x,y),radius = cv2.minEnclosingCircle(cnt)
	center = (int(x),int(y))
	radius = int(radius)
	cv2.circle(crop_img,center,radius,(255,0,0),2)

	area_of_circle=math.pi*radius**2

	hull_test = cv2.convexHull(cnt)
	hull_area = cv2.contourArea(hull_test)
	solidity = float(area)/hull_area

	aspect_ratio = float(w)/h

	(x,y),(MA,ma),angle_t = cv2.fitEllipse(cnt)

	#################### ANALISI LETTERA IN BASE AI DATI RACCOLTI ######################			
	
	if area_of_circle - area < 5000:

		letter_correspond = "A"
		cv2.putText(img, "The Letter is :  A (CALIBRATED)", put_text_pos, font, 1 , put_text_color, 2, cv2.LINE_AA) 	

	elif count_defects ==1:

		if angle_t < 10:

			letter_correspond = "V"
			cv2.putText(img, "The Letter is :  V", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA) 

		elif 40 < angle_t < 66:

			letter_correspond = "C"
			cv2.putText(img, "The Letter is :   C", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA) 

		elif 20 < angle_t < 35:
					
			letter_correspond = "L"
			cv2.putText(img, "The Letter is :   L", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)
		else:
			letter_correspond = "Y"
			cv2.putText(img,"The Letter is :  Y", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)
				
	elif count_defects == 2:  # Its either W or F

		if angle_t > 100:
			letter_correspond = "F"
			cv2.putText(img, "The Letter is :  F", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)

		else:
			letter_correspond = "W"
			cv2.putText(img, "The Letter is :  W", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA) 
			
	elif count_defects == 4:
		letter_correspond = "CALIBRATE"
		cv2.putText(img,"Hello There ! Callibrate by letter A", put_text_pos, font,1, put_text_color, 2, cv2.LINE_AA) 
	else:
		if area > 12000:
			letter_correspond = "B"
			cv2.putText(img,"The Letter is : B", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA) 
				
		else:

			if solidity < 0.85:
				if aspect_ratio < 1:
					if angle_t < 20:
						letter_correspond = "D"
						cv2.putText(img,"The Letter is :  D", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)

					elif 169<angle_t <180:
						letter_correspond = "I"
						cv2.putText(img,"The Letter is :  I", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)
					elif angle_t < 168:
						letter_correspond = "J"
						cv2.putText(img,"The Letter is :  J", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)
				elif aspect_ratio > 1.01:
					letter_correspond = "Y"

					cv2.putText(img,"The Letter is :  Y", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA) 
			else:
				if angle_t > 30 and angle_t < 100:
					letter_correspond = "H"
					cv2.putText(img,"The Letter is :  H", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)    
				elif angle_t > 120:
						
					letter_correspond = "I"
					cv2.putText(img,"The Letter is :  I", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)
				else:
					letter_correspond = "U"
					cv2.putText(img,"The Letter is :  U", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA)

	#################### STAMPA DEL RISULTATO SUL TERMINALE DOPO ANALISI ######################
	if printletter_control == True:
		if NFrame < FQframe: #Immagazzinamento dati secondo una frequenza di frame
			letters.insert(NFrame,letter_correspond)
			NFrame+=1
		else:	#Analisi dati accumulati
			result = "NULL"
			analyse = [['',0]]
			del analyse[0]
			NFrame=0 #Reset contatore del numero di dati
			printletter_control = False #Blocco della prossima analisi
			for let in letters:
				control=False
				for ind in range(0,len(analyse)):#Conteggio elementi gia trovati
					if let == analyse[ind][0]:
						analyse[ind][1]+=1
						control = True
						break
				if control:
					control=False
				else:		#Aggiunta di elemento non trovato prima e conteggio di questo 
					if let:
						analyse.append([let,1])

			tmp=0
			for factor in analyse:	#Ricerca del fattore più grande e scrittura
				if factor[1]>tmp:
					tmp=factor[1]
					result=factor[0]
			del tmp 

			print(result, end='')
			#print(analyse)	#RISULTATO ANALISI

			del letters 		#distruzione/ricostruzione variabili di analisi
			letters=['']
			del analyse

	#################### AGGIORNAMENTO DEL FRAME ######################		
	
	#cv2.imshow('Contours', drawing)
	#cv2.imshow('Defects', crop_img)
	cv2.imshow('Binary Image', thresh1)
	cv2.imshow('Gesture', img)

	#################### VERIFICA DI EVENTUALI INTERRUPTS ######################
	
	k = cv2.waitKey(10) & 0xFF 

	if k == 27 or k == ord('q'):
		break
	elif k == 13:
		printletter_control = True
	elif k == ord(' '):
		print(' ')
	elif k == ord('r'):
		invertBW ^= True;
