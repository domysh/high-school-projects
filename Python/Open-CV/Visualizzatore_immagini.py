import cv2

filename = input("Inserisci il percorso del file (o trascina):")
filename = filename.replace('"','')
W = 1000.
oriimg = cv2.imread(filename)
height, width, depth = oriimg.shape
newimg = cv2.resize(oriimg,(int(W),int(height*W/width)))
cv2.imshow("image",newimg)
cv2.waitKey(0)
