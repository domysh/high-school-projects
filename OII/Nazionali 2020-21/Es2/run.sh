#!/bin/bash

cd "$(dirname $0)"

COMPILE=0

if [ -f "hashes.txt" ]; then
    sha256sum *.cpp > tmp.txt
    if [ "`cat tmp.txt`" = "`cat hashes.txt`" ]; then
        rm tmp.txt
    else
        mv tmp.txt hashes.txt
        COMPILE=1
    fi
else
    sha256sum *.cpp > hashes.txt
    COMPILE=1
fi

if [ "$COMPILE" = "1" ]; then
    echo "Compiling"
    (c++ -g -fsanitize=address -std=gnu++17 -o compiled *.cpp && echo "Success") || (echo "Failed" && rm compiled hashes.txt)
fi

if [ -f "./compiled" ];then
    if [ "$1" = "" ]; then
        ./compiled
    else
        echo "--- Out ---"
        cat *.input$1.txt | ./compiled
        echo "--- Expected ---"
        cat *.output$1.txt
        echo "--- END ---"
    fi
fi

