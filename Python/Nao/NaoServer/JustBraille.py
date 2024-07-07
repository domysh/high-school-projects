#!/usr/bin/python
# -*- coding: utf-8 -*-

###########################
######### IMPORTS #########
###########################

import cv2

###########################
######## FUNCTIONS ########
###########################

def codificaBraille(a1,a2,a3,b1,b2,b3):
	if( a1==1 and a2==0 and a3==0 and b1==0 and b2==0 and b3==0 ):
		return "a"
	elif( a1==1 and a2==1 and a3==0 and b1==0 and b2==0 and b3==0 ):
		return "b"
	elif( a1==1 and a2==0 and a3==0 and b1==1 and b2==0 and b3==0 ):
		return "c"
	elif( a1==1 and a2==0 and a3==0 and b1==1 and b2==1 and b3==0 ):
		return "d"
	elif( a1==1 and a2==0 and a3==0 and b1==0 and b2==1 and b3==0 ):
		return "e"
	elif( a1==1 and a2==1 and a3==0 and b1==1 and b2==0 and b3==0 ):
		return "f"
	elif( a1==1 and a2==1 and a3==0 and b1==1 and b2==1 and b3==0 ):
		return "g"
	elif( a1==1 and a2==1 and a3==0 and b1==0 and b2==1 and b3==0 ):
		return "h"
	elif( a1==0 and a2==1 and a3==0 and b1==1 and b2==0 and b3==0 ):
		return "i"
	elif( a1==0 and a2==1 and a3==0 and b1==1 and b2==1 and b3==0):
		return "j"
	elif( a1==1 and a2==0 and a3==1 and b1==0 and b2==0 and b3==0 ):
		return "k"
	elif( a1==1 and a2==1 and a3==1 and b1==0 and b2==0 and b3==0 ):
		return "l"
	elif( a1==1 and a2==0 and a3==1 and b1==1 and b2==0 and b3==0 ):
		return "m"
	elif( a1==1 and a2==0 and a3==1 and b1==1 and b2==1 and b3==0 ):
		return "n"
	elif( a1==1 and a2==0 and a3==1 and b1==0 and b2==1 and b3==0 ):
		return "o"
	elif( a1==1 and a2==1 and a3==1 and b1==1 and b2==0 and b3==0 ):
		return "p"
	elif( a1==1 and a2==1 and a3==1 and b1==1 and b2==1 and b3==0 ):
		return "q"
	elif( a1==1 and a2==1 and a3==1 and b1==0 and b2==1 and b3==0 ):
		return "r"
	elif( a1==0 and a2==1 and a3==1 and b1==1 and b2==0 and b3==0 ):
		return "s"
	elif( a1==0 and a2==1 and a3==1 and b1==1 and b2==1 and b3==0 ):
		return "t"
	elif( a1==1 and a2==0 and a3==1 and b1==0 and b2==0 and b3==1 ):
		return "u"
	elif( a1==1 and a2==1 and a3==1 and b1==0 and b2==0 and b3==1 ):
		return "v"
	elif( a1==0 and a2==1 and a3==0 and b1==1 and b2==1 and b3==1 ):
		return "w"
	elif( a1==1 and a2==0 and a3==1 and b1==1 and b2==0 and b3==1 ):
		return "x"
	elif( a1==1 and a2==0 and a3==1 and b1==1 and b2==1 and b3==1 ):
		return "y"
	elif( a1==1 and a2==0 and a3==1 and b1==0 and b2==1 and b3==1):
		return "z"
	elif( a1==1 and a2==1 and a3==1 and b1==0 and b2==1 and b3==1 ):
		return "à"
	elif( a1==0 and a2==1 and a3==1 and b1==1 and b2==0 and b3==1 ):
		return "è"
	elif( a1==1 and a2==1 and a3==1 and b1==1 and b2==1 and b3==1 ):
		return "é"
	elif( a1==0 and a2==0 and a3==1 and b1==1 and b2==0 and b3==0 ):
		return ""
	elif( a1==0 and a2==0 and a3==1 and b1==1 and b2==0 and b3==1 ):
		return "ò"
	elif( a1==0 and a2==1 and a3==1 and b1==1 and b2==1 and b3==1 ):
		return "ù"
	elif( a1==0 and a2==0 and a3==1 and b1==0 and b2==0 and b3==0 ):
		return "'"
	elif( a1==0 and a2==1 and a3==0 and b1==0 and b2==1 and b3==1 ):
		return "."
	elif( a1==0 and a2==1 and a3==0 and b1==0 and b2==0 and b3==0 ):
		return ","
	elif( a1==0 and a2==1 and a3==0 and b1==0 and b2==1 and b3==0 ):
		return ":"
	elif( a1==0 and a2==1 and a3==1 and b1==0 and b2==0 and b3==0 ):
		return ";"
	elif( a1==0 and a2==1 and a3==0 and b1==0 and b2==0 and b3==1 ):
		return "?"
	elif( a1==0 and a2==1 and a3==1 and b1==0 and b2==1 and b3==0 ):
		return "!"
	elif( a1==0 and a2==1 and a3==1 and b1==0 and b2==0 and b3==1 ):
		return "\""
	elif( a1==0 and a2==0 and a3==1 and b1==0 and b2==1 and b3==1 ):
		return "\""
	elif( a1==0 and a2==1 and a3==1 and b1==0 and b2==1 and b3==1 ):
		return "("
	elif( a1==0 and a2==0 and a3==1 and b1==0 and b2==1 and b3==0 ):
		return "*"
	elif( a1==0 and a2==0 and a3==1 and b1==0 and b2==0 and b3==1 ):
		return "-\n"
	elif( a1==0 and a2==0 and a3==0 and b1==0 and b2==0 and b3==0 ):
		return " "
	return ""
#DECODE BRAILLE NUMBERS FROM PHOTO
def codificaNumeri(a1,a2,a3,b1,b2,b3):
	if( a1==1 and a2==0 and a3==0 and b1==0 and b2==0 and b3==0 ):
		return "1"
	elif( a1==1 and a2==1 and a3==0 and b1==0 and b2==0 and b3==0 ):
		return "2"
	elif( a1==1 and a2==0 and a3==0 and b1==1 and b2==0 and b3==0 ):
		return "3"
	elif( a1==1 and a2==0 and a3==0 and b1==1 and b2==1 and b3==0 ):
		return "4"
	elif( a1==1 and a2==0 and a3==0 and b1==0 and b2==1 and b3==0 ):
		return "5"
	elif( a1==1 and a2==1 and a3==0 and b1==1 and b2==0 and b3==0 ):
		return "6"
	elif( a1==1 and a2==1 and a3==0 and b1==1 and b2==1 and b3==0 ):
		return "7"
	elif( a1==1 and a2==1 and a3==0 and b1==0 and b2==1 and b3==0 ):
		return "8"
	elif( a1==0 and a2==1 and a3==0 and b1==1 and b2==0 and b3==0 ):
		return "9"
	elif( a1==0 and a2==1 and a3==0 and b1==1 and b2==1 and b3==0):
		return "0"
	return ""
#DROW A RECTANGLE IN THE ROI (REGION OF INTEREST)
def coloraRett(X,Y,image):
	for y in range(Y-4, Y+4):
		for x in range(X-4, X+4):
			image[y,x]=[0,255,0]
#ALLINEATE POINT IN ASS X
def AllineaX(x, Y,image):
	for y in range(Y, image.shape[0]):
		if(image[y,x+1][0]==0 and image[y,x+1][1]==0 and image[y,x+1][2]==255):
			image[y,x]=[0,0,255]
			image[y,x+1]=[0,255,0]
		elif(image[y,x+2][0]==0 and image[y,x+2][1]==0 and image[y,x+2][2]==255):
			image[y,x]=[0,0,255]
			image[y,x+2]=[0,255,0]
		elif(image[y,x+3][0]==0 and image[y,x+3][1]==0 and image[y,x+3][2]==255):
			image[y,x]=[0,0,255]
			image[y,x+3]=[0,255,0]
		elif(image[y,x+4][0]==0 and image[y,x+4][1]==0 and image[y,x+4][2]==255):   
			image[y,x]=[0,0,255]
			image[y,x+4]=[0,255,0]
		elif(image[y,x+5][0]==0 and image[y,x+5][1]==0 and image[y,x+5][2]==255):   
			image[y,x]=[0,0,255]
			image[y,x+5]=[0,255,0]
		elif(image[y,x+6][0]==0 and image[y,x+6][1]==0 and image[y,x+6][2]==255):   
			image[y,x]=[0,0,255]
			image[y,x+6]=[0,255,0]
#ALLINEATE POINT IN ASS Y
def AllineaY (X,y,image):
	for x in range(X, image.shape[1]):
		if(image[y+1,x][0]==0 and image[y+1,x][1]==0 and image[y+1,x][2]==255):
			image[y,x]=[0,0,255]
			image[y+1,x]=[0,255,0]
		elif(image[y+2,x][0]==0 and image[y+2,x][1]==0 and image[y+2,x][2]==255):
			image[y,x]=[0,0,255]
			image[y+2,x]=[0,255,0]
		elif(image[y-1,x][0]==0 and image[y-1,x][1]==0 and image[y-1,x][2]==255):
			image[y,x]=[0,0,255]
			image[y-1,x]=[0,255,0]
		elif(image[y-2,x][0]==0 and image[y-2,x][1]==0 and image[y-2,x][2]==255):
			image[y,x]=[0,0,255]
			image[y-2,x]=[0,255,0]
		elif(image[y+3,x][0]==0 and image[y+3,x][1]==0 and image[y+3,x][2]==255):
			image[y,x]=[0,0,255]
			image[y+3,x]=[0,255,0]
		elif(image[y-3,x][0]==0 and image[y-3,x][1]==0 and image[y-3,x][2]==255):
			image[y,x]=[0,0,255]
			image[y-3,x]=[0,255,0]
#TRANSLATE BRAILLE PHOTOS IN STRINGS
def braille_tr(image):
	# Threshold al contrario, creo sfondo nero e punti bianchi
	gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	for y in range(0, gray.shape[0]):
		for x in range(0, gray.shape[1]):
			if(gray[y,x]>120):
				gray[y,x]=0
			else: gray[y,x]=255


	blurred = cv2.GaussianBlur(gray, (5, 5), 0)
	thresh = cv2.threshold(blurred, 127, 255, cv2.THRESH_BINARY)[1]

	# Trovo i contorni di ogni punto
	cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
		cv2.CHAIN_APPROX_SIMPLE)
	if len(cnts) == 2:
		cnts = cnts[0]
	elif len(cnts) == 3:
		cnts = cnts[1]
	else:
		raise Exception(("Contours tuple must have length 2 or 3, "
				"otherwise OpenCV changed their cv2.findContours return "
				"signature yet again. Refer to OpenCV's documentation "
				"in that case"))
	# Ciclo su ogni punto
	for c in cnts:
		# Trovo il centroide del punto
		M = cv2.moments(c)
		cX = int(M["m10"] / M["m00"])
		cY = int(M["m01"] / M["m00"])
		# Disegno il centroide e il quadrato attorno al punto
		#cv2.drawContours(image, [c], -1, (0, 255, 0), 1)
		#cv2.circle(image, (cX, cY), 1, (0, 255, 0), -1)
		cv2.rectangle(image, (cX-4,cY-4), (cX+4,cY+4), (0,255,0), 1)
		coloraRett(cX,cY,image)
		image[cY,cX]=[0,0,255]

	for y in range(0, image.shape[0]):
		for x in range(0,image.shape[1]):
			if(image[y,x][0]==0 and image[y,x][1]==0 and image[y,x][2]==255):
				AllineaX(x,y,image)
				AllineaY(x,y,image)
	for y in range(0, image.shape[0]):
		for x in range(0, image.shape[1]):
			if(image[y,x][0]==0 and image[y,x][1]==0 and image[y,x][2]==255):
				image[y,x]=[0,0,255]
			else: image[y,x]=[255,255,255]

	for y in range(0, image.shape[0]):
		for x in range(0, image.shape[1]):
			if(image[y,x][0]==0 and image[y,x][1]==0 and image[y,x][2]==255):
				cv2.rectangle(image, (x-4,y-4), (x+4,y+4), (0,255,0), 1)
				coloraRett(x,y,image)
				image[y,x]=[0,0,255]

	#CASINO PER CREARE LE LINEE BLU
	y=0
	x=0
	while y<image.shape[0]:
		x=0
		while x<image.shape[1]:
			if(image[y,x,0]==0 and image[y,x,1]==0 and image[y,x,2]==255):
				cv2.line(image,(x+5,0),(x+5,image.shape[0]),(255,0,0),1)
				cv2.line(image,(0,y+5),(image.shape[1],y+5),(255,0,0),1)
			x+=1
		y+=1
	cv2.line(image,(image.shape[1]-1,0), (image.shape[1]-1,image.shape[0]),(255,0,0),1)
	#CASINO PER PRENDERE LE CASELLE E IL VALORE AL LORO INTERNO
	y=1
	last=0
	count=0
	x=0
	while(x<image.shape[1]):
		if((image[y, x][0] == 255 and image[y, x][1] == 0 and image[y, x][2] == 0) and x-last>image.shape[1]/20):
			count+=1
			last=x
		x+=1
	print (count)
	matrice= []
	matrice.append([])
	ym=0
	xm=0
	while y<image.shape[0]:
		x=0
		savey=y
		while x<image.shape[1]-1:
			f=False
			y=savey
			if((image[y, x][0] == 255 and image[y, x][1] == 0 and image[y, x][2] == 0) or x == 0):
				l=x+1
				m=y
				while ( not(image[y,l,0]==255 and image[y, l][1] ==0 and image[y, l][2] ==0) and l<image.shape[1]-1):
					l+=1
				while ( not(image[m,x+1,0]==255 and image[m, x+1][1] ==0 and image[m, x+1][2] ==0) and m<image.shape[0]-1):
					m+=1
				for a in range(x, l):
					for b in range(y,m):
						if (image[b,a,0]==0 and image[b,a,1]==0 and image[b,a,2]==255):
							matrice[ym].append(1)
							if(xm==count):
								ym+=1
								matrice.append([])
								xm=0
							else:
								xm+=1
							f=True
				if(l-x<4): f=True
				if(m-y<5) : f=True
				x=l
				y=m
			if(f==False and x<image.shape[1]-1):
				matrice[ym].append(0)
				if(xm==count):
					ym+=1
					matrice.append([])
					xm=0
				else:
					xm+=1
		y+=1


	matrice.pop()
	print(matrice)

	y=0
	x=0
	frase=""
	capitalizeNext=False
	numberNext=False
	while y<len(matrice):
		x=0
		while x< len(matrice[y]): #SCANDISCO PRENDENDO OGNI VOLTA 3R x 2C
			if(y+2<=len(matrice) and x+2<=len(matrice[y])):
				a1,a2,a3=matrice[y][x],matrice[y+1][x],matrice[y+2][x]
				b1,b2,b3=matrice[y][x+1],matrice[y+1][x+1],matrice[y+2][x+1]
				if(capitalizeNext==True):
					frase+=codificaBraille(a1,a2,a3,b1,b2,b3).capitalize()
					capitalizeNext=False
				elif(numberNext==True):
					frase+=codificaNumeri(a1,a2,a3,b1,b2,b3)
					numberNext=False
				elif(a1==0 and a2==0 and a3==0 and b1==1 and b2==0 and b3==1): #SE è MAIUSCOLA
					capitalizeNext=True
				elif(a1==0 and a2==0 and a3==1 and b1==1 and b2==1 and b3==1): #SE è UN NUMERO
					numberNext=True
				else:	#CASO NORMALE
					frase+=codificaBraille(a1,a2,a3,b1,b2,b3)
			x+=2
		y+=3

	print("La frase codificata è: \""+str(frase)+"\"")
	return frase
	#cv2.imshow("Image", image)
	#cv2.imshow("tresh",thresh)