from pynput.keyboard import Key, Listener
import os, time

downComm = False
upCommand = False
returnCommand = False
Arrow = "<-"

def clearSc():
	print(chr(27)+'[2j',end = "")
	print('\033c',end = "")
	print('\x1bc',end = "")

def on_press(key):
	global upCommand,downComm,returnCommand
	if key == Key.up:
		print("\b \b\b \b\b \b\b \b\b")
		upCommand = True
		return False
	elif key == Key.down:
		print("\b \b\b \b\b \b\b \b\b")
		downComm = True
		return False
	elif key == Key.enter:
		returnCommand = True
		return False


def StartListener():
	global upCommand,downComm,returnCommand
	downComm = False
	upCommand = False
	returnCommand = False
	with Listener(on_press=on_press,) as listener:
		listener.join()

def PrintLineMenu(line,pos,arrow,SymArrow=Arrow):
	ToPrint = line
	if pos == arrow:
		ToPrint+=" "+SymArrow
	print(ToPrint)

def PrintMenu(lines,arrow,intestation,SymArrow=Arrow):
	clearSc()
	if intestation != "":
		print(intestation)
	for n in range(0,len(lines)):
		PrintLineMenu(lines[n],n,arrow,SymArrow)

def MenuCreate(voice,intestation="",SymArrow=Arrow,start=0):
	global returnCommand,upCommand,downComm
	n = start
	while not returnCommand:
		time.sleep(0.1)
		PrintMenu(voice,n,intestation,SymArrow)
		StartListener()
		if upCommand:
			if n>0:n-=1
			else:n = len(voice)-1
		elif downComm:
			if n<(len(voice)-1):n+=1
			else:n=0
	input()
	returnCommand = upCommand = downComm = False
	return n


if __name__ == "__main__":
	MenuCreate(["Voce1","Voce2","Voce3"])