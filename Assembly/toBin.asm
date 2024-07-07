.model small
data segment  
    
req1 db "Inserire numero decimale:",'$'
res1 db "Risultato in binario",'$'
    
str db 50 dup(0)
code segment

jmp main


;FUNCTIONS

MACRO BAK 
    push ax
    push bx
    push cx
    push dx
ENDM

MACRO ACAPO
    BAK 
    mov dx,13
    mov ah,2
    int 21h  
    mov dx,10
    mov ah,2
    int 21h
    RBAK
ENDM 
MACRO printch n
    BAK 
    mov dx,n
    mov ah,2
    int 21h  
    RBAK
ENDM

MACRO RBAK
    pop dx
    pop cx
    pop bx
    pop ax
ENDM

MACRO print v
    BAK
    mov ah,09h
    lea dx,v
    int 21h
    RBAK
ENDM

MACRO toInt v
    push bx
    push cx
    push dx
    lea dx, v
    call parseInt
    pop dx 
    pop cx
    pop bx
ENDM

MACRO toStr num v
    BAK
    lea dx,v
    mov ax,num
    call parseStr
    RBAK
ENDM 

MACRO input v 
    BAK 
    lea bx,v
    call inputChByCh
    RBAK
ENDM

inputChByCh:;Out from dx
    mov di,0
    inpCh:
        ;INP
        push dx 
        mov ah,01h
        int 21h 
        pop dx 
        ;end inp
        cmp al,0Dh
        je endinpCh
        cmp al,	08h
        jne continuainpCh
        dec di
        mov [bx][di],'$'
        ;print [bx]
        printch 20h
        printch 08h 
         
        jmp inpCh
        continuainpCh:
        mov [bx][di],al
        inc di
        mov [bx][di],'$'
        jmp inpCh
    endinpCh:
    ;print [bx]
    ACAPO
        
ret

parseInt:;INP FROM dx OUT from ax

mov bx,dx
mov ax,0
mov si,0
NUMTROVATO:
    cmp [bx][si],'$'
    je DOLLAROTROVATO
    ;x 10
    mov dx,10
    mul dx
    ;SOTTRAZIONE di 0
    mov cx,[bx][si]
    sub cx,'0'  
    mov ch,0
    add ax,cx ;SOMMA
    ;FINE SOTTRAZIONE E 
    inc si
    
    
    jmp NUMTROVATO
DOLLAROTROVATO:

ret

parseStr:;INP ax,dx out dx
mov bx,dx  
mov di,0
stringparse: 
    mov dx,0
    mov cx,10
    div cx
    add dx,'0'
    mov [bx][di],dx      
    inc di
    cmp ax,0
    jne stringparse
mov [bx][di],'$'
call reverseStr
ret     

reverseStr: ;INP bx, di->$
mov ax,di
reversciclo:
   dec di
   mov dl,[bx][di]
   mov dh,0
   push dx
   cmp di,0
   je fine1rev
   jmp reversciclo
fine1rev:
mov di,ax
reversciclo2:
   dec di
   pop dx
   mov [bx][di],dl
   cmp di,0
   je fine2rev
   jmp reversciclo2
fine2rev:  

ret

;MAIN CODE
main:

mov ax,@data
mov ds,ax

print req1
input str
toInt str
mov dx,0
mov cx,0
mov bx,2

INIZIOCICLO:
cmp ax,0
je FINECICLO
div bx
push dx
mov dx,0
inc cx
jmp INIZIOCICLO 

FINECICLO:
cmp cx,0
jne CONTINUA
mov dx,0
push dx
inc cx
CONTINUA:
mov di,0
CICLO2:
cmp cx,0
je FINE
pop dx
add dx,48
mov str[di],dl
inc di
dec cx
jmp CICLO2
FINE:
mov str[di],'$'
print str
hlt
