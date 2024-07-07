import numpy as np
import cv2

cap = cv2.VideoCapture(0)
#Settings
cascfile_face = '/Users/domysh/Dropbox/Coding/Python/Open-CV/cascades/face.xml'
cascfile_eye = '/Users/domysh/Dropbox/Coding/Python/Open-CV/cascades/haarcascade_eye.xml'
face_det = cv2.CascadeClassifier(cascfile_face)
eye_dec = cv2.CascadeClassifier(cascfile_eye)


while(True):
	# Capture frame-by-frame
	ret, frame = cap.read()
	frame = cv2.flip(frame,1)
	gray_img = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
	#Face-Detect
	faces = face_det.detectMultiScale(
	gray_img,
	scaleFactor=1.1,
	minNeighbors=5,
	minSize=(30, 30)
	)
	eyes = eye_dec.detectMultiScale(
		gray_img,
		scaleFactor=1.1,
		minNeighbors=5,
		minSize=(10,10)
	)
	for (x,y,w,h) in faces:
		cv2.rectangle(frame,(x,y),(x+w,y+h), (0,0,255),2)
		cv2.rectangle(gray_img,(x,y),(x+w,y+h), (0,0,255),2)

	for (x,y,w,h) in eyes:
		cv2.rectangle(frame,(x,y),(x+w,y+h), (0,255,0),2)
		cv2.rectangle(gray_img,(x,y),(x+w,y+h), (0,0,255),2)
	# Display the resulting frame
	cv2.imshow('frame_gray',gray_img)
	cv2.imshow('frame',frame)
	if cv2.waitKey(1) & 0xFF == ord('q'):
		break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()