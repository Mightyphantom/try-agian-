@echo off
cd /d "%~dp0"
python server\server.py
if errorlevel 1 py server\server.py
pause
