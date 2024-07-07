#include <iostream>
#include <vector>
//Parte per syspause
#ifdef _WIN64
	void SysPause(){
		std::cout<<std::endl;
		system("pause");
	}
#else
	#include <termios.h>
	#include <unistd.h>

	int getch() {
		using namespace std;
		struct termios oldt,
		newt;
		int ch;
		tcgetattr( STDIN_FILENO, &oldt );
		newt = oldt;
		newt.c_lflag &= ~( ICANON | ECHO );
		tcsetattr( STDIN_FILENO, TCSANOW, &newt );
		ch = getchar();
		tcsetattr( STDIN_FILENO, TCSANOW, &oldt );
		return ch;
	}	
	void SysPause(){
		std::cout << "\nPremere un tasto per continuare...";
		getch();
		std::cout<<std::endl;
	}
#endif
//Fine parte per syspause
using namespace std;

struct node{
	int repeted; //Ripetizione del valore nella stringa
	string value; // Valore selezionato
	struct node *zero; //Puntatore a nodo 0
	struct node *uno; // Puntatore a nodo 1
};


void PrintUltimeteElements(struct node root,string binary=""){
	//Se trovo un nodo base lo stampo
	if(root.zero == nullptr && root.uno == nullptr){
		cout << endl<< "Valore:" << root.value << " -> Frequenza:" << root.repeted << " Codifica:" << binary;
	}else{
		//Vado nei nodi più profondi per trovare il nodo base

		//cout << "\nPassaggio al figlio zero:" << root.zero << "FreqTot:"<<root.zero->repeted<<endl;
		PrintUltimeteElements(*(root.zero),binary+"0");
		//cout << "\nPassaggio al figlio uno:" << root.uno << "FreqTot:"<<root.uno->repeted<< endl; 
		PrintUltimeteElements(*(root.uno),binary+"1");
	}
}

void OrderElemets(vector<struct node> &v){
	int min,posMin;
	//Selection sort
	for(int i=0;i<v.size();i++){
		min = v[i].repeted;posMin = i;
		for(int ind=i; ind < v.size();ind++){
			if(v[ind].repeted<min){
				min = v[ind].repeted;
				posMin = ind;
			}
		}
		swap(v[i],v[posMin]);
	}
}

node SyncMinFreqNode(vector<struct node> &v){
	//Decommenta per debug
	/*
	cout << "\n###################################\nPrinting Vector Element:\n###################################\n";
	for (int i=0;i<v.size();i++){
		cout << "Key:" << v[i].value << " - RepetedFor:" << v[i].repeted << endl; 
	}
	cout << "###################################\n End print vector";*/
	int c=1;
	if(v.size()>=2){
		//Ordino gli elementi per frequenza 
		OrderElemets(v);
		//creo nuovi elementi da collegare a 1 nodo padre uguali ai primi 2 trovati nel vettore (Quindi i minori)
		struct node *n1= new node(),*n2= new node();
		n1->repeted=v[0].repeted;n1->value=v[0].value;n1->zero=v[0].zero;n1->uno=v[0].uno;
		n2->repeted=v[1].repeted;n2->value=v[1].value;n2->zero=v[1].zero;n2->uno=v[1].uno;
		struct node father = {v[0].repeted+v[1].repeted,"",n1,n2};
		//elimino i nodi uniti e inserisco il nodo padre
		v[1]= father;
		v[0]= v[v.size()-1];
		v.erase(v.begin()+v.size()-1);
		//ricorro alla stessa funzione per ricondurre tutto a un solo nodo
		return SyncMinFreqNode(v);
	}else if(v.size()==1){
		//svuotamento del vettore e restituzione del nodo radice
		struct node rootNode = v[0];
		v.clear();
		return rootNode;
	}else{
		//restituisco un nodo vuoto poichè un ho ricevuto un vettore vuoto
		struct node no = {0,"",nullptr,nullptr};
		return no;
	}

}

void GetFreq(vector<struct node> &v,const string &frase){
	bool findInV; //Salvo se l'elemento è stato trovato nel vettore
	int indInV; //Salvo la posizione dell'elemento nel vettore
	//Trovo la frequenza di ogni carattere.
	for(int i = 0;i<frase.size();i++){
		findInV = false;
		//Controllo se il carattere è già presente
		for(int ind=0;ind<v.size();ind++){
			if(v[ind].value[0] == frase[i]){
				findInV = true;
				indInV = ind;
				break;
			}
		}
		//Aggiungo uno alle ripetizioni di un carattere se trovato nel vettore
		if (findInV){
			v[indInV].repeted++;
		}else{
			//Creo un nuovo nodo se non ho trovato l'elemento nel vettore e lo aggiungo a questo
			struct node nuovo = {1,"",nullptr,nullptr};
			nuovo.value+=frase[i];
			v.push_back(nuovo);
		}
	}

}

int main(){
	//Intestazione
	cout << "Huffman Code Creator\n";
	vector<struct node> v;string inp;
	//Input
	cout << "Benvenuto nel programma di compressione" << endl << "Inserire la frase:";
	getline(cin,inp);
	//Inserisco i dati delle frequenze nel vettore
	GetFreq(v,inp);
	//Associo i vari nodi nel vettore e creo il nodo radice
	struct node risultato = SyncMinFreqNode(v);
	//Stampo gli elementi insieme alla codifica ripercorrendo l'albero
	cout << "\nNumero di caratteri totali:" << risultato.repeted <<endl;
	PrintUltimeteElements(risultato);
	SysPause();
}
