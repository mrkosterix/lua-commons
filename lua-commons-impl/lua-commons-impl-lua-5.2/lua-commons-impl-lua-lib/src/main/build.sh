#!/bin/bash

gcc -O2 -Wall -Wno-unused-parameter -fPIC -Ijava -Ijava-includes -Ijava-includes/native -Ilua-includes -I/usr/local/include   -c -o lua2java.o c/lua2java.c
gcc -shared -o lua2java-lib-$1 lua2java.o libs/liblua$2.a
