#!/usr/bin/python

from sys import argv,exit
from time import sleep
import os

def vpnIsDown():
  cmd = os.popen('ping ' + ipToCheck + ' -c 1 | grep "Time to live exceeded"')
  resultPing = cmd.read()
  if resultPing.find("Time to live exceeded") > -1:
    result = 0
  else:
    result = 1
  return result

def startVpn():
  os.system("sudo openvpn --config settings.ovpn &")

if len(argv) < 2:
  print("CheckOpenVpn")
  print("")
  print("  Usage: python CheckOpenVpn.py <ip-to-check>")
  print("")
  print("")
  exit(1)

ipToCheck = argv[1]

while True:
  result = vpnIsDown()
  if result == 0:
    print("VPN disconnected. Reconnecting...")
    startVpn()
  else:
    print("VPN connected!")
  
  sleep(3)
