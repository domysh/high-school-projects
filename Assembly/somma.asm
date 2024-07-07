.model small
.data

num dw 0000h      
req db "Inserire numero:",'$' 
risp db "Risultato:",'$'
CR db 0Dh,10,'$'
input db 10,?,10 dup(0)
testo db 50 dup('$')

.code 

MACRO INP buf
    mov ah,0Ah 
    lea dx,buf
    int 21h 
    PRINT CR
ENDM 

MACRO PRINT val
    mov ah,09h
    lea dx,val
    int 21h
ENDM    

MACRO RESET
    mov ax,0
    mov bx,0
    mov cx,0
    mov dx,0
ENDM

MACRO BAK
    push ax
    push bx
    push cx
    push dx
ENDM

MACRO RES
    pop dx
    pop cx
    pop bx
    pop ax
ENDM

jmp INIT ;VAI AL "MAIN"
   
CVTNUM:
    push bx
    push cx
    push dx
    RESET    
    mov ax,0
    push ax
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


CVTSTR:
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
    inc bx
    mov testo[bx],'$'
    RES
    ret  

    
;END CVTSTR

INIT:
;SET DATA SEGMENT
mov ax,@data
mov ds,ax 
;REQ FIRST NUMBER          
PRINT req           
INP input
call CVTNUM
mov num,ax
;REQ SECOND NUMBER + SUM      
PRINT req
INP input
call CVTNUM
add ax,num
;PRINT SUM 
call CVTSTR 
PRINT risp
PRINT testo
   
hlt