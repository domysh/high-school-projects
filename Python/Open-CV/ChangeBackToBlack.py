import cv2, math

LIMIT = 0

def color_eq(a,b):
    if math.fabs(a[0]-b[0])<=LIMIT:
        if LIMIT <= 80:
            if math.fabs(a[1]-b[1])<=LIMIT:
                    return True
        else:
            return True
    return False
try:
    path = input("Path:")
    path = path.replace("'","")
    path = path.replace(" ","")
    LIMIT = int(input("Treshsold:"))
    img = cv2.imread(path)
    img = cv2.cvtColor(img,cv2.COLOR_BGR2HLS)
    sel = img[0,0].copy()
    for x in range(len(img)):
        for y in range(len(img[0])):
            if color_eq(sel,img[x,y]):
                #print(f"FROM:{img[x,y]} TO:{[0,0,0]}")
                img[x,y] = [0,0,0]
                #print(f"RESULT:{img[x,y]}")
    img = cv2.cvtColor(img,cv2.COLOR_HLS2BGR)
    cv2.imwrite("new.png",img)
except KeyboardInterrupt:
    print("\n-> User killed the program!")


        
