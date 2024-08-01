@echo off

dir /s /b *.java > sources.txt
javac -Werror -d bin/ @sources.txt