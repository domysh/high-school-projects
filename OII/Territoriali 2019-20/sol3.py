#!/usr/bin/env python3
# -*- coding: utf8 -*-

import sys

# se preferisci leggere e scrivere da file
# ti basta decommentare le seguenti due righe:

# sys.stdin = open('input.txt')
# sys.stdout = open('output.txt', 'w')

sys.setrecursionlimit(10**6) 
def solution(V:list,v_len:int,G:list,g_len:int,mem:list):
    #print("VAL:",m)
    if g_len == 0: 
        return v_len
    if v_len == 0: return 0

    if not mem[g_len-1][v_len-1] is None:
        return mem[g_len-1][v_len-1]

    visitator_pass = 1+solution(V,v_len-1,G,g_len,mem)
    guide_pass = solution(V,v_len,G,g_len-1,mem)
    visitator_and_guide = 0
    if V[v_len-1]<G[g_len-1]:
        visitator_and_guide = 2+solution(V,v_len-1,G,g_len-1,mem)

    mem[g_len-1][v_len-1] = max(visitator_and_guide,guide_pass,visitator_pass)
    return mem[g_len-1][v_len-1]

def solve():
    input()
    input()
    V = list(map(int, input().split()))
    G = list(map(int, input().split()))
    mem = [ [None]*len(V) for _ in range(len(G))]
    risposta = solution(V,len(V),G,len(G),mem)

    

    # aggiungi codice...

    return risposta


T = int(input())

for t in range(1, T+1):
    print("Case #" + str(t) + ":", solve())