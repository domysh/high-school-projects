#include <stdio.h> 
#include <sys/types.h> 
#include <unistd.h> 
#include <sys/wait.h>
#include <stdlib.h>
#include <time.h>
#include <sys/mman.h>

//Program Compile Options

#define MAX_PROCESSES 30
//#define MONO_CORE_MERGE
#define DO_CONFIRM
//#define PRINT_ARRAYS
//#define PRINT_WORKERS_STATUS

//Shared Memory Malloc
void * sm_malloc(size_t mem_size){
        void * res = mmap(NULL,mem_size,PROT_READ|PROT_WRITE,MAP_SHARED|MAP_ANONYMOUS,0,0);
        if (res == NULL){
                puts("Si è verificato un problema!");
                exit(1);
        }
        return res;
}

void * ctrl_malloc(size_t mem_size){
        void * res = malloc(mem_size);
        if (res == NULL){
                puts("Si è verificato un problema!");
                exit(1);
        }
        return res;
}

void sm_free(void* mem,size_t mem_size){
    if ((int)munmap(mem, mem_size) == -1){
            exit(1);
    }
}

int growing_ord (const void * a, const void * b) {
    if (*(long*)a == *(long*)b) return 0;
    if( *(long*)a < *(long*)b ) return -1;
    else return 1;
}

pid_t qsort_fork(long * mem, size_t init, size_t end){
    pid_t pid_proc = fork();
    if(pid_proc==-1)exit(1);
    if(pid_proc == 0){
        qsort(&(mem[init]), end-init, sizeof(long),growing_ord);
        exit(0);
    }
    return pid_proc;
}

int join(pid_t pid_proc){
    int status;    
    waitpid(pid_proc,&status,0);
    return status;
}

//TIMER METHODS
struct timespec start,end;
#define BILLION 1000000000.0
void t_start(){
    clock_gettime(CLOCK_REALTIME, &start);
}

double t_end(){
    clock_gettime(CLOCK_REALTIME, &end);
    return (end.tv_sec - start.tv_sec) +
        (end.tv_nsec - start.tv_nsec) / BILLION;
}

size_t get_min_index(long** blks_p,size_t* blks_i,size_t* blks_len,size_t len){
    size_t min = -1;
    for(size_t i=0;i<len;i++){
        if(blks_i[i]>=blks_len[i]) continue;
        if (min == -1){ min = i; continue;}
        if(blks_p[i][blks_i[i]]<blks_p[min][blks_i[min]]) min = i;
    }
    return min;
}

void array_copy(long* a,long* b,size_t l){
    for (size_t i=0;i<l;i++){
        a[i] = b[i];
    }
}

//General purpose merge with n ordered blocks
void merge_all(long *mem, size_t* blks,size_t len_blks_list,size_t end){

    size_t blks_lens[len_blks_list];
    long * blks_pointers[len_blks_list];
    for (size_t i = 0;i<len_blks_list-1;i++){
        blks_lens[i] = blks[i+1]-blks[i];
        blks_pointers[i] = (long*)ctrl_malloc(sizeof(long)*blks_lens[i]);
        array_copy(blks_pointers[i],&(mem[blks[i]]),blks_lens[i]);
    }
    blks_lens[len_blks_list-1] = end-blks[len_blks_list-1];
    blks_pointers[len_blks_list-1] = (long*)ctrl_malloc(sizeof(long)*blks_lens[len_blks_list-1]);
    array_copy(blks_pointers[len_blks_list-1],&(mem[blks[len_blks_list-1]]),blks_lens[len_blks_list-1]);

    size_t blks_i[len_blks_list];
    for(size_t i=0;i<len_blks_list;i++) blks_i[i] = 0;
    size_t g_count = blks[0];
    while(true){
        size_t min_ele = get_min_index(blks_pointers,blks_i,blks_lens,len_blks_list);
        if(min_ele == -1) break;
        mem[g_count] = blks_pointers[min_ele][blks_i[min_ele]];
        blks_i[min_ele]++;
        g_count++;
    }

    for(size_t i = 0;i<len_blks_list-1;i++)
        free(blks_pointers[i]);

}

void merge2(long *mem, size_t blk1, size_t blk2,size_t end){
    size_t tmp_arr[2];
    tmp_arr[0] = blk1;tmp_arr[1] = blk2;
    merge_all(mem,tmp_arr,2,end);
}

void merge3(long *mem, size_t blk1, size_t blk2, size_t blk3, size_t end){
    size_t tmp_arr[3];
    tmp_arr[0] = blk1;tmp_arr[1] = blk2;tmp_arr[2] = blk3;
    merge_all(mem,tmp_arr,3,end);
}

pid_t merge2_fork(long *mem, size_t blk1, size_t blk2,size_t end){
    pid_t pid_proc = fork();
    if(pid_proc==-1)exit(1);
    if(pid_proc == 0){
        merge2(mem,blk1,blk2,end);
        exit(0);
    }
    return pid_proc;
}
pid_t merge3_fork(long *mem, size_t blk1, size_t blk2, size_t blk3, size_t end){
    pid_t pid_proc = fork();
    if(pid_proc==-1)exit(1);
    if(pid_proc == 0){
        merge3(mem,blk1,blk2,blk3,end);
        exit(0);
    }
    return pid_proc;
}


int main(){

    srand(time(NULL));
    puts("----- Array sorter -----");

    size_t DIM_ARRAYS = 0;
    int THREADING_LIMIT = 1;
    printf("Inserire dimensione array: ");
    if (scanf("%zu",&DIM_ARRAYS) < 0) exit(1);

    while(true){
        printf("Inserire il numero di processi da avviare: ");
        if(scanf("%d",&THREADING_LIMIT)<0)exit(1);
        if (THREADING_LIMIT>1 && THREADING_LIMIT<=MAX_PROCESSES) break;
        else printf("Inserire valori tra 2 e %d !",MAX_PROCESSES);
    }

    // Array Generation
    long* mono_core_array = (long*)ctrl_malloc(sizeof(long)*DIM_ARRAYS);
    long* multi_core_array = (long*)sm_malloc(sizeof(long)*DIM_ARRAYS);

    for (size_t i=0; i<DIM_ARRAYS; i++){
        long rand_var=rand();
        mono_core_array[i] = rand_var;
        multi_core_array[i] = rand_var;
    }

    // Ordino con un solo processo
    t_start();
    qsort(mono_core_array, DIM_ARRAYS, sizeof(long),growing_ord);
    printf("Tempo ordinamento Mono processo: %.5fs\n", t_end());


    size_t array_units = DIM_ARRAYS/THREADING_LIMIT;
    pid_t workers[THREADING_LIMIT];
    #ifdef MONO_CORE_MERGE
    size_t blks_inits[THREADING_LIMIT];
    #endif
    t_start();
    for (size_t i=0;i<THREADING_LIMIT-1;i++){
        #ifdef MONO_CORE_MERGE
        blks_inits[i] = array_units*i;
        #endif
        workers[i] = qsort_fork(multi_core_array,array_units*i,array_units*(i+1));
    }
    #ifdef MONO_CORE_MERGE
    blks_inits[THREADING_LIMIT-1] = array_units*(THREADING_LIMIT-1);
    #endif
    workers[THREADING_LIMIT-1] = qsort_fork(multi_core_array,array_units*(THREADING_LIMIT-1),DIM_ARRAYS);

    for (size_t i=0;i<THREADING_LIMIT;i++){
        join(workers[i]);
    }
    #ifndef MONO_CORE_MERGE
    size_t n_blocks = THREADING_LIMIT;
    
    size_t first_block = array_units;
    while(n_blocks!=1){
        pid_t workers[n_blocks/2];
        #ifdef PRINT_WORKERS_STATUS
        printf("WORKERS: %d\n",n_blocks/2);
        printf("UNIT: %d\n",array_units);
        #endif
        size_t free_work = 1;
        if(n_blocks%2 != 0){
            size_t last = (first_block+array_units*3>=DIM_ARRAYS)?DIM_ARRAYS:(first_block+array_units*2);
            workers[0] = merge3_fork(multi_core_array,0,first_block,first_block+array_units,last);
            #ifdef PRINT_WORKERS_STATUS
            printf("| %d %d %d %d |\n",0,first_block,first_block+array_units,last);
            #endif
            first_block = last;
        }else{
            size_t last = (first_block+(array_units*2)>=DIM_ARRAYS)?DIM_ARRAYS:first_block+array_units;
            workers[0] = merge2_fork(multi_core_array,0,first_block,last);
            #ifdef PRINT_WORKERS_STATUS
            printf("| %d %d %d |\n",0,first_block,last);
            #endif
            first_block = last;
        }
        
        n_blocks/=2;
        size_t mult = 1;
        for(size_t i = 1;i<n_blocks;i++){
                mult+=2;
            size_t last = first_block+(array_units*(mult-3));
            size_t sep = first_block+(array_units*(mult-2));
            size_t next = (first_block+(array_units*(mult))>=DIM_ARRAYS)?DIM_ARRAYS:(first_block+array_units*(mult-1));
            workers[free_work++] = merge2_fork(
                multi_core_array,last,sep,next
            ); 
            #ifdef PRINT_WORKERS_STATUS
            printf("| %d %d %d |\n",last,sep,next); 
            #endif           
        }
        #ifdef PRINT_WORKERS_STATUS
        puts("");
        #endif
        for(size_t i = 0;i<n_blocks;i++){
            join(workers[i]);
        }
        array_units*=2;
    }
    #else
    //Single thread solution merging
    merge_all(multi_core_array,blks_inits,THREADING_LIMIT,DIM_ARRAYS);
    #endif
    printf("Tempo ordinamento processi paralleli: %.5fs\n", t_end());
    
    #ifdef DO_CONFIRM
    //Verify equality
    bool equals = true;
    for (size_t i=0;i<DIM_ARRAYS;i++){
        if(mono_core_array[i] != multi_core_array[i]){
            equals = false;
            break;
        }
    }
    #endif

    #ifdef PRINT_ARRAYS
    puts("");
    for(size_t i=0;i<DIM_ARRAYS;i++)
        printf("%d ",mono_core_array[i]);
    puts("\n");
    for(size_t i=0;i<DIM_ARRAYS;i++)
        printf("%d ",multi_core_array[i]);
    puts("\n");
    #endif


    free(mono_core_array);
    sm_free(multi_core_array,DIM_ARRAYS);
    #ifdef DO_CONFIRM
    if (equals){
        puts("Gli array sono identici!");
    }else{
        puts("ATTENZIONE! : Gli array sono diversi! D:");
    }
    #endif
    



    
}
