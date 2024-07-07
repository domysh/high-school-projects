import cv2
import numpy as np
import Analyser
from naoqi import ALProxy
import vision_definitions
from PIL import Image
import sys

class DefinedColors():
    BLUE=1,
    GREEN=2,
    WHITE=3,
    ORANGE=4,
    RED=5,
    YELLOW=6,
    BLACK=7

IP="127.0.0.1"
PORT=9559

def TakeColors(howMany):
    #Blue
    lower_blue = np.array([110,50,50])
    upper_blue = np.array([130,255,255])
    #Green
    lower_green=np.array([36, 25, 25])
    upper_green=np.array([86, 255,255])
    #white 
    lower_white = np.array([0, 0, 200])
    upper_white = np.array([180, 255, 255])
    #Orange
    lower_orange = np.array([1, 190, 200])
    upper_orange = np.array([18, 255, 255])
    # lower mask (0-10) red
    lower_red = np.array([0,50,50])
    upper_red = np.array([10,255,255])
    # upper mask (170-180) red
    lower_red2 = np.array([170,50,50])
    upper_red2 = np.array([180,255,255])
    #yellow
    lower_yellow=np.array([23,41,133])
    #upper_yellow=np.array([40,150,255])
    upper_yellow=np.array([50,180,255])
    #black
    lower_black= np.array([0, 0, 0])
    upper_black= np.array([180, 255, 30])
    
    resultArr = []
    TRVal = [[[255]]]
    #'''

    photoProxy = ALProxy( "ALVideoDevice",IP,PORT)
    
    subID=""
    resolution = 2
    colorSpace = vision_definitions.kBGRColorSpace
    fps = 20

    subID = photoProxy.subscribe(subID,resolution,colorSpace,fps)
    

    img = photoProxy.getImageRemote(subID)
    #'''
    #cam = cv2.VideoCapture(0)
    #inserire video capture
    for i in range(howMany):
        #_,img=cam.read()
        #'''
        result = photoProxy.getImageRemote(subID)
        # create image
        width = result[0]
        height = result[1]
        img = np.zeros((height, width, 3), np.uint8)

        # get image

        if result == None:
            print('cannot capture.')
        elif result[6] == None:
            print('no image data string.')
        else:

            # translate value to mat
            values = map(ord, list(result[6]))
            i = 0
            for y in range(0, height):
                for x in range(0, width):
                    img.itemset((y, x, 0), values[i + 0])
                    img.itemset((y, x, 1), values[i + 1])
                    img.itemset((y, x, 2), values[i + 2])
                    i += 3
        #'''
        #Convert to BGR OpenCv

        img = cv2.flip(img, 1)
        height, width, _ = img.shape
        Halfh = int(height/2)
        Halfw = int(width/2)
        cv2.imwrite("immagine.jpg",img)
        img = cv2.rectangle(img,(Halfw-10,Halfh-10),
        (Halfw+10,Halfh+10),[0,0,255])
        cv2.imshow("Nao",img)
        img = cv2.blur(img,(3,3))
        img = cv2.cvtColor(img,cv2.COLOR_BGR2HSV)
        col = img[Halfh,Halfw]
        col = np.array([[col]])
        mask_blue = cv2.inRange(col,lower_blue,upper_blue)
        mask_green = cv2.inRange(col,lower_green,upper_green)
        mask_white = cv2.inRange(col,lower_white,upper_white)
        mask_orange = cv2.inRange(col,lower_orange,upper_orange)
        mask_red = cv2.inRange(col,lower_red,upper_red)
        mask_red += cv2.inRange(col,lower_red2,upper_red2)
        mask_yellow = cv2.inRange(col,lower_yellow,upper_yellow)
        mask_black = cv2.inRange(col,lower_black,upper_black)
        #print(f"Blue:{mask_blue},Green:{mask_green},White:{mask_white},Orange:{mask_orange},Red:{mask_red},Yellow:{mask_yellow},Black:{mask_black}")
        if mask_blue == TRVal:
            resultArr.append(DefinedColors.BLUE)
        elif mask_black == TRVal:
            resultArr.append(DefinedColors.BLACK)
        elif mask_green == TRVal:
            resultArr.append(DefinedColors.GREEN)
        elif mask_orange == TRVal:
            resultArr.append(DefinedColors.ORANGE)
        elif mask_red == TRVal:
            resultArr.append(DefinedColors.RED)
        elif mask_yellow == TRVal:
            resultArr.append(DefinedColors.YELLOW)
        elif mask_white == TRVal:
            resultArr.append(DefinedColors.WHITE)

        

    while True:
        if cv2.waitKey (30) >= 0 :break
    cv2.destroyAllWindows()
    photoProxy.unsubscribe(subID)
    return resultArr


def main():
    v=TakeColors(5)
    _,resCol=Analyser.analyser(v)
    print(resCol)

if __name__ == '__main__':
    if len(sys.argv) > 1:
        IP=sys.argv[1]
    else:
        print("Inserire gli argomenti necessari.")
        sys.exit(0)
    if len(sys.argv) == 3:
        PORT=sys.argv[2]
    main()