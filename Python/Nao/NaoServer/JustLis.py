#!/usr/bin/python
# -*- coding: utf-8 -*-

###########################
######### IMPORTS #########
###########################

import numpy as np
import cv2
import math
import JustShow as Simg

############################
##### GLOBAL VARIABLES #####
############################

#Parametri per la scitta su immagine
font = cv2.FONT_HERSHEY_SIMPLEX
put_text_color = (18,0,255)
put_text_pos = (60,50)
#Parametri per la thresh
lower_thresh1 = 129 
upper_thresh1 = 255
#Parametri per ritagliare l'immagine
lower_red = np.array([0,150,50])
upper_red = np.array([195,255,255])
#Thread for show img
threadIMG = Simg.ShowImg('Gesture') #Global Variable usefull for show general image
threadCIMG = Simg.ShowImg('Crop_img') #Global Variable usefull for show cropped image

###########################
######## FUNCTIONS ########
###########################

#FUNCTION FOR LIS
def trd_lis(img):
	
	img = cv2.flip(img,1)
	crop_img = handDetect(img)
	grey = cv2.cvtColor(crop_img, cv2.COLOR_BGR2GRAY)

	hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
	
	mask = cv2.inRange(hsv, lower_red, upper_red)
	res = cv2.bitwise_and(img,img, mask= mask)

	value = (35, 35)
	blurred = cv2.GaussianBlur(grey, value, 0)
	_, thresh1 = cv2.threshold(blurred, lower_thresh1, upper_thresh1, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)

	#xyz,
	contours, hierarchy = cv2.findContours(thresh1.copy(),cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
	try:
		cnt = max(contours, key = lambda x: cv2.contourArea(x))
	except:
		return None
	area_of_contour = cv2.contourArea(cnt)
	
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
		
	moment = cv2.moments(cnt)   
	perimeter = cv2.arcLength(cnt,True)
	area = cv2.contourArea(cnt)


	(x,y),radius = cv2.minEnclosingCircle(cnt)
	center = (int(x),int(y))
	radius = int(radius)
	cv2.circle(crop_img,center,radius,(255,0,0),2)

	area_of_circle=math.pi*radius*radius

	hull_test = cv2.convexHull(cnt)
	hull_area = cv2.contourArea(hull_test)
	solidity = float(area)/hull_area

	aspect_ratio = float(w)/h

	rect_area = w*h
	extent = float(area)/rect_area

	(x,y),(MA,ma),angle_t = cv2.fitEllipse(cnt)

	if area_of_circle - area < 30000: #Inizialmente era 5000 ma per migliorare il riconoscimento ho modificato il valore (Con 5000 si doveva allontanare molto la mano dalla Webcam)
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
		letter_correspond = "NON STAI FACENDON UN SEGNO"
		cv2.putText(img,"DO A SIGN!", put_text_pos, font,1, put_text_color, 2, cv2.LINE_AA) 
	else:
		if area > 12000:
			letter_correspond = "B"
			cv2.putText(img,"The Letter is :  B", put_text_pos, font, 1, put_text_color, 2, cv2.LINE_AA) 		
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
	threadIMG.update(img)
	return letter_correspond
#FUNCION THAT TRACK THE HAND AND RETURN ROI
def handDetect(img):
	grey = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

	hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
	
	mask = cv2.inRange(hsv, lower_red, upper_red)
	res = cv2.bitwise_and(img,img, mask= mask)

	value = (35, 35)
	blurred = cv2.GaussianBlur(grey, value, 0)
	_, thresh1 = cv2.threshold(blurred, lower_thresh1, upper_thresh1, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)
	
	#xyz,
	contours, hierarchy = cv2.findContours(thresh1.copy(),cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
	try:
		cnt = max(contours, key = lambda x: cv2.contourArea(x))
	except:
		return None
	
	area_of_contour = cv2.contourArea(cnt)
	
	x,y,w,h = cv2.boundingRect(cnt)

	cv2.rectangle(img,(x,y),(x+w,y+h),(0,0,255),1)
	crop_img=img[y-10:y+h+10, x-10:x+w+10]
	threadCIMG.update(crop_img)
	return crop_img
