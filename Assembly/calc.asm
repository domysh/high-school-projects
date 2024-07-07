.model small

data segment
     
input db 20,?,20 dup(0);request by CVTNUM
testo db 20,?,20 dup(0);Request by CVTSTR

num1 dw 0
num2 dw 0 
ris dw 0

CR equ 13,10
GIU db CR,'$'        

menu db CR,"           Calcolatrice Assembly:",CR,CR,"  1) Somma",CR,"  2) Sottrazione",CR,"  3) Moltiplicazione",CR,"  4) Divisione",CR,"  5) End",'$' 
reqnum db "Inserisci un numero:",'$'
risul db "Risultato:",'$'
errstr db "Inserire valore valido",'$'
restostr db "Resto:",'$'
waitstr db "Premi invio per continuare...",'$'

code segment 

jmp START;jmp to init of program


MACRO INP buf; Macro for do the input in a buffer
    BAK
    mov ah,0Ah 
    lea dx,buf
    int 21h 
    PRINT GIU 
    RES
ENDM 

MACRO PRINT val; Macro for print a vector of char
    BAK
    mov ah,09h
    lea dx,val
    int 21h
    RES
ENDM

MACRO CLEAR; Macro for clear the screen
    BAK
    push ax
    mov ax,0000h
    int 10h
    pop ax
    RES
ENDM

MACRO RESET; Macro for reset all register
    mov ax,0
    mov bx,0
    mov cx,0
    mov dx,0
ENDM

MACRO BAK;Macro for push all register
    push ax
    push bx
    push cx
    push dx
ENDM

MACRO RES;Macro for pop all register
    pop dx
    pop cx
    pop bx
    pop ax
ENDM 
; Operation in the calculator
MACRO SOMMA
    push ax
    mov ax,num1
    add ax,num2
    mov ris,ax
    pop ax
ENDM   

MACRO SOTTR 
    push ax
    mov ax,num1
    sub ax,num2
    mov ris,ax
    pop ax
ENDM
             
MACRO MOLTI
    push ax
    mov ax,num1
    mul num2
    mov ris,ax
    pop ax
    
ENDM

MACRO DIVIS
    push ax
    mov dx,0
    mov ax,num1
    div num2
    mov ris,ax 
    pop ax
    
ENDM

   
CVTNUM:;PUT DATE IN input RETURN IN AX
    push bx
    push cx
    push dx ;Save registers: result in ax
    RESET;Reset register    
    push ax ;First result in the stack
    mov di,2
    mov cl,0 
    CICLO01:   
    cmp cl,input[1]
    je FINE01 
    mov al,input[di]
    mov ah,00h
    sub ax,'0'
    push ax
    mov ax,10
    mov bl,input[1]
    dec bl
    sub bl,cl
    mul bl
    COND01:
    cmp al,0
    je SET1
    jmp ENDCOND01
    SET1:
    mov ax,1  
    ENDCOND01:
    pop bx
    mul bx 
    pop bx
    add ax,bx
    push ax
    inc cl
    inc di
    jmp CICLO01
    
    FINE01:
    pop ax;Risultato
    pop dx    
    pop cx
    pop bx
    ;Ritorno 
    ret    


CVTSTR:;Input ax return in testo
    BAK
    mov cx,0
    mov dx,0
    mov bx,10
    CICLO02:
    cmp ax,10
    jl ENDCICLO02
    div bx
    add dx,'0'
    push dx
    inc cx
    jmp CICLO02
    ENDCICLO02:
    add ax,'0'
    push ax
    inc cx
    mov bx,0
    RIEMPI1:
    cmp cx,0
    je END2       
    pop ax
    mov testo[bx],al
    inc bx
    dec cx
    jmp RIEMPI1
    END2:
    mov testo[bx],'$';Add Stop character
    RES
    ret
    
      
START:
;Set data segment      
mov ax,@data
mov ds,ax 

MAIN:
;Reset variables
mov num1,0
mov num2,0
mov ris,0

CLEAR;Clear the screen
;Print menu
PRINT menu 
PRINT giu
PRINT giu 

REQMENU:
;Input request
PRINT reqnum
INP input  

call CVTNUM  ;convert input in a number
;Control of the value
cmp ax,5
ja ERRINP
cmp ax,0
je ERRINP
jmp NEXT

ERRINP:
;Error message
PRINT errstr
PRINT giu

jmp REQMENU  

NEXT:
     
;If want exit     
cmp ax,5
je BASTA
;Save the option selected
push ax

PRINT giu
PRINT giu   

;input num1 and num2
PRINT reqnum
INP input
call CVTNUM
mov num1,ax  
PRINT reqnum
INP input
call CVTNUM
mov num2,ax
;Restore selected option
pop ax

cmp ax,1
je OP1
cmp ax,2
je OP2
cmp ax,3
je OP3
cmp ax,4
je OP4

;Operation
OP1:
SOMMA
jmp ENDFASE
OP2:
SOTTR
jmp ENDFASE
OP3:
MOLTI
jmp ENDFASE
OP4: 
DIVIS
push dx
PRINT giu
PRINT restostr;Print only for division
pop ax
call CVTSTR
PRINT testo


;Print result
ENDFASE:
PRINT giu
PRINT risul

mov ax,ris
call CVTSTR;Print result converted as string 

PRINT testo 

PRINT giu
PRINT waitstr
INP input
jmp MAIN;Restart the program


BASTA:hlt;Stop the program