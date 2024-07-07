/*
Code of @DomySh
This code is create for personal hobby...
You can use my code citing me!
Comments can be in ITALIAN!
GNU General Public License v3.0
*/

#include <iostream>
#include <vector>
#include <thread>
#include <chrono>



#ifdef _WIN64
	#include <windows.h>
	int detectKey(){
		while(true){
			if(GetAsyncKeyState(VK_RETURN)<0){
				std::cin.ignore();
				return VK_RETURN;
			}else if(GetAsyncKeyState(VK_UP)<0){
				return VK_UP;
			}else if(GetAsyncKeyState(VK_DOWN)<0){
				return VK_DOWN;
			}
		}
	}
	
	void inBufferClear(){
		using namespace std;
		cin.clear();
		cin.sync();
		fflush(stdin);  
	}

	void SysPause(){
		inBufferClear();
		std::cout<<std::endl;
		system("pause");
	}
#else
	#include <termios.h>
	#include <unistd.h>
	
	#define VK_RETURN 10 
	#define VK_UP 65
	#define VK_DOWN 66

	#ifdef __APPLE__
		void inBufferClear(){
			using namespace std;
			cin.clear();
			cin.sync();
			fflush(stdin);  
		}
	#else
		#include <stdio_ext.h>
		void inBufferClear(){
			using namespace std;
			__fpurge(stdin);
			cin.clear();
			cin.sync();
			fflush(stdin);  
		}
	#endif
	int getch(void){
		using namespace std;
		struct termios oldt, newt;
		int ch;

		tcgetattr(STDIN_FILENO, &oldt);
		newt = oldt;
		newt.c_lflag &= ~(ICANON | ECHO);
		tcsetattr(STDIN_FILENO, TCSANOW, &newt);
		ch = getchar();
		tcsetattr(STDIN_FILENO, TCSANOW, &oldt);

		return ch;
	}

	int detectKey(){
		char c;
		c = getch();
		if(c == 27){
			c = getch();
			c = getch();
		}
		return c;
	}
	void SysPause(){
		std::cout << "\nPremere un tasto per continuare...";
		inBufferClear();
		getch();
		std::cout<<std::endl;
	}
#endif

void clearSc(){
	std::cout << "\e[1;1H\e[2J";
}

std::vector<std::string> ToVec(std::string arr[],int len){
	using namespace std;
	vector<string> v;
	for(int i=0;i<len;i++){
		v.push_back(arr[i]);
	}
	return v;
}
std::vector<std::string> ToVec(char *arr[],int len){
	using namespace std;
	vector<string> v;
	for(int i=0;i<len;i++){
		v.push_back(arr[i]);
	}
	return v;
}

void PrintLineMenu(const std::vector<std::string> &lines,int pos,int arrow,const char *SymArrow="<-"){
	using namespace std;
	cout << lines[pos];
	if(pos == arrow){
		cout << " " << SymArrow;
	}
	cout << endl;
}

void PrintMenu(const std::vector<std::string> &lines,int arrow,std::string intestation="",const char* arrsym="<-"){
	using namespace std;
	clearSc();
	cout << intestation ;
	for(int i =0;i<lines.size();i++){
		PrintLineMenu(lines,i,arrow,arrsym);
	}
}

int MenuCreate(const std::vector<std::string> &voice,std::string intestation="",int n=0,const char* arrsym="<-"){
	using namespace std;
	bool enter=false;
	inBufferClear();
	while(!enter){
		PrintMenu(voice,n,intestation,arrsym);
		this_thread::sleep_for(chrono::milliseconds(100));
		switch(detectKey()){
			case VK_RETURN:
				enter = true;
			break;
			case VK_UP:{
				if(n>0){
					n--;
				}else{
					n=voice.size()-1;
				}
			}
			break;
			case VK_DOWN:{
				if(n<voice.size()-1){
					n++;
				}else{
					n=0;
				}
			}
			break;
		}
	}
	clearSc();
	inBufferClear();
	return n;			
}
int MenuCreate(std::string arr[],int length,std::string intestation="",int n=0,const char* arrsym="<-"){
	return MenuCreate(ToVec(arr,length),intestation,n,arrsym);
}
int MenuCreate(char ** arr,int length,std::string intestation="",int n=0,const char* arrsym="<-"){
	return MenuCreate(ToVec(arr,length),intestation,n,arrsym);
}
