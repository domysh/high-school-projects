#!/usr/bin/python
# -*- coding: utf-8 -*-

###########################
######### IMPORTS #########
###########################

import cv2
import numpy as np
from threading import Thread

###########################
######### CLASSES #########
###########################

#THREAD THAT CAN SHOW AN IMAGE CV2
class ShowImg(Thread):
	def __init__(self,name,img=np.array([])):
		Thread.__init__(self)
		self.img = img
		self.name = name
		self.start()
	def run(self):
		while True:
			try:
				cv2.imshow(self.name, self.img)
				k = cv2.waitKey(10) & 0xFF 
				if k == ord('q'):
					break
			except:
				pass
	def update(self,img):
		self.img = img