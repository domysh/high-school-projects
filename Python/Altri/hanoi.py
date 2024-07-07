def hanoi(n,da,a,app):
    if n ==1:
        print(f'{da} a {a}')
    else:
        hanoi(n-1,da,app,a)
        print(f'{da} a {a}')
        hanoi(n-1,app,a,da)


num = int(input("numero:"))
hanoi(num,1,3,2)
