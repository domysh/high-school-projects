def analyser(ele_arr):
    analyse = [['',0]]
    del analyse[0]
    result=""
    for ele in ele_arr:
        control=False
        for ind in range(0,len(analyse)):
            if ele == analyse[ind][0]:
                analyse[ind][1]+=1
                control = True
                break
        if (not control) and ele:
            analyse.append([ele,1])

    analyse_len = len(analyse)

    if analyse_len == 1:
        result = analyse[0][0]
    else:
        try:
            result,tmp=analyse[0]
        except IndexError:
            return [False,False]
        for pos in range(1,analyse_len):
            if analyse[pos][1]>tmp:
                tmp=analyse[pos][1]
                result=analyse[pos][0]
        del tmp
    return [analyse,result]