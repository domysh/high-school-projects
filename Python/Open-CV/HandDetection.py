import cv2
import numpy as np
import copy
import math

# parametri della ROI(Region of Interest)
cap_region_x_begin=0.5  # punto iniziale
cap_region_y_end=0.8  # punto di fine della ROI
threshold = 60  #  Soglia sulla scala colore in binario
blurValue = 41  # Parametro per la creazione del frame GaussianBlur
bgSubThreshold = 50
learningRate = 0

isBgCaptured = 0   # boolean che avvia il programma quando ho calcolato il background
triggerSwitch = False  # boolean che avvia l'ascoltatore sulla tastiera

def detectFingers(res,drawing):
	
	hull = cv2.convexHull(res, returnPoints=False)
	if len(hull) > 3:
		defects = cv2.convexityDefects(res, hull)
		if type(defects) != type(None):  # avoid crashing.   (BUG not found)

			cnt = 0
			for i in range(defects.shape[0]):  # calculate the angle
				s, e, f, d = defects[i][0]
				start = tuple(res[s][0])
				end = tuple(res[e][0])
				far = tuple(res[f][0])
				a = math.sqrt((end[0] - start[0]) ** 2 + (end[1] - start[1]) ** 2)
				b = math.sqrt((far[0] - start[0]) ** 2 + (far[1] - start[1]) ** 2)
				c = math.sqrt((end[0] - far[0]) ** 2 + (end[1] - far[1]) ** 2)
				angle = math.acos((b ** 2 + c ** 2 - a ** 2) / (2 * b * c))  
				if angle <= math.pi / 2:  
					cnt += 1
					cv2.circle(drawing, far, 8, [211, 84, 0], -1)
			return True, cnt
	return False, 0

def removeBG(frame): 
	fgmask = bgModel.apply(frame,learningRate=learningRate)
	kernel = np.ones((3, 3), np.uint8)
	fgmask = cv2.erode(fgmask, kernel, iterations=1)
	res = cv2.bitwise_and(frame, frame, mask=fgmask)
	return res
	

# PROGRAMMA
camera = cv2.VideoCapture(0)
camera.set(10,200)

while camera.isOpened():
	ret, frame = camera.read()
	threshold = 20
	frame = cv2.bilateralFilter(frame, 5, 50, 100)  # smoothing filter
	frame = cv2.flip(frame, 1)  # flip the frame horizontally
	cv2.rectangle(frame, (int(cap_region_x_begin * frame.shape[1]), 0),
				 (frame.shape[1], int(cap_region_y_end * frame.shape[0])), (255, 0, 0), 2)
	cv2.imshow('original', frame)

	#  Inizio il calcolo solo dopo aver registrato il Background
	if isBgCaptured == 1:  
		img = removeBG(frame) #elimino il vecchio BG e registro il nuovo valore (scala colore)
		img = img[0:int(cap_region_y_end * frame.shape[0]),
					int(cap_region_x_begin * frame.shape[1]):frame.shape[1]]  
		cv2.imshow('Grigio', img)

		# converto l'immagine in binario e calcolo il GaussianBlur per avere la scala di colori
		gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
		blur = cv2.GaussianBlur(gray, (blurValue, blurValue), 0)
		ret, thresh = cv2.threshold(blur, threshold, 255, cv2.THRESH_BINARY)
		cv2.imshow('Black and White', thresh)


		# Traccia contorni
		thresh1 = copy.deepcopy(thresh)
		contours, hierarchy = cv2.findContours(thresh1, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE) #funzione che trova i contorni
		length = len(contours)
		maxArea = -1
		if length > 0:
			for i in range(length):  # trovo il contorno piu grande
				temp = contours[i]
				area = cv2.contourArea(temp) #traccio il contorno
				if area > maxArea:
					maxArea = area
					ci = i

			res = contours[ci]
			hull = cv2.convexHull(res)
			drawing = np.zeros(img.shape, np.uint8)
			cv2.drawContours(drawing, [res], 0, (0, 255, 0), 2)
			cv2.drawContours(drawing, [hull], 0, (0, 0, 255), 3)

			isFinishCal,cnt = detectFingers(res,drawing)
			if triggerSwitch is True:
				if isFinishCal is True and cnt <= 2:
					print (cnt)
		 
		cv2.imshow('output', drawing)

	# Ascoltatore Tastiera
	k = cv2.waitKey(10)
	if k == 27:  # ESC per uscire
		break
	elif k == ord('b'):  # b per catturare il Background
		bgModel = cv2.createBackgroundSubtractorMOG2(0, bgSubThreshold)
		isBgCaptured = 1
        