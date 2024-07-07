#!/usr/bin/env python3
# -*- coding: utf8 -*-

import sys

# se preferisci leggere e scrivere da file
# ti basta decommentare le seguenti due righe:

# sys.stdin = open('input.txt')
# sys.stdout = open('output.txt', 'w')

def solve():
    input()
    N, = map(int, input().split())
    A = list(map(int, input().split()))

    F = [0]*N # memorizza qui la risposta

    for i in range(len(A)):
        A[i] = [A[i],i,[],-1]
    A.sort(key=lambda x: x[0])

    #DONE = []
    for to_vote in A:
        #to_vote = A.pop(0)
        for i in reversed(range(len(A))):
            if A[i][1] == to_vote[1]:continue
            if len(A[i][2]) < A[i][0]:
                A[i][2].append(to_vote[1])
                to_vote[3] = A[i][1]
                break
        #DONE.append(to_vote)

    A.sort(key=lambda x:x[1])

    F = [ele[3] for ele in A]
    # aggiungi codice...

    return F


T = int(input())

for t in range(1, T+1):
    print("Case #" + str(t) + ":", " ".join(map(str, solve())))