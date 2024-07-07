import cv2,os
files = []
for (dirpath, dirnames, filenames) in os.walk('.'):files.extend(filenames)

for link in files:
    try:
        img = cv2.imread(link)
        cv2.imwrite('save.'+link,img)
        print(link+' DONE!')
        os.remove(link)
        os.rename('save.'+link,link)
    except:
        pass

input('Press ENTER for close the program...')