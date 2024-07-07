#!/usr/bin/env python3
# -*- coding: utf8 -*-

#This solution is too slow....
import sys

sys.setrecursionlimit(10**6) 

class Int1:
    def __init__(self,lamp:int):
        self.lamp = lamp
    
    def is_linked(self,id:int):
        return self.lamp == id
    
class Int2:
    def __init__(self,lamp1:int,lamp2:int):
        self.lamp1 = lamp1
        self.lamp2 = lamp2

    def go(self,lamp):
        if lamp == self.lamp1:
            return self.lamp2
        return self.lamp1

    def is_linked(self,id:int):
        return self.lamp1 == id or self.lamp2 == id


def getGraph(X:list,Y:list,Z:list):
    INT1 = [Int1(l) for l in Z]
    INT2 = [Int2(X[i],Y[i]) for i in range(len(X))]
    return INT1, INT2


def s_solution(lampid,INT1,INT2,mem,skip_in2_ = []):
    if mem[lampid] != -1: return mem[lampid]

    skip_in2 = skip_in2_[:]
    for i in INT1:
        if i.is_linked(lampid):
            mem[lampid] = 1
            return 1

    min = float('inf')
    for in2 in range(len(INT2)):
        if in2 in skip_in2: continue
        if INT2[in2].is_linked(lampid): 
            skip_c = skip_in2[:]+[in2]
            sol = 1+s_solution(INT2[in2].go(lampid),INT1,INT2,mem,skip_c)
            #print(INT2[in2].lamp1,INT2[in2].lamp2,"SOL:",sol)
            #print(LAMPS_C)
            if sol < min:
                min = sol
    mem[lampid] = min
    return min





def solve():
    input()
    N, A, B = map(int, input().split())
    Z = [None] * A
    X = [None] * B
    Y = [None] * B

    for j in range(A):
        Z[j] = int(input())
    for j in range(B):
        X[j], Y[j] = map(int, input().split())
    
    idx,num = 0,0

    INT1,INT2 = getGraph(X,Y,Z)
    mem = [-1]*N
    for idl in range(N):
        
        #print("Init",LAMPS)
        numl = s_solution(idl,INT1,INT2,mem)
        #print("LAMP:",idl,"NUM:",numl)
        if numl != float('inf') and numl > num:
            num = numl
            idx = idl
    
    # aggiungi codice...

    return (idx, num)


T = int(input())

for t in range(1, T+1):
    print("Case #{}: {} {}".format(t, *solve()))