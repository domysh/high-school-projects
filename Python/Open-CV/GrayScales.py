import cv2

img = cv2.imread("img.jpg",0)
cv2.imwrite("grey.png",img)

for x in range(len(img)):
    for y in range(len(img[x])):
        if img[x,y]>(255/2):
            img[x,y] = 255
        else:
            img[x,y] = 0
cv2.imwrite("BaW.png",img)


