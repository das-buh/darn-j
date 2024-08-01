@echo off

dir /s /b *.java > sources.txt
javac -d bin/ @sources.txt