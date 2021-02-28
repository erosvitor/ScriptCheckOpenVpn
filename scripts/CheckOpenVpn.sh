#!/bin/bash

vpnIsDown() {
  resultPing=`ping $ipToCheck -c 1 | grep 'Time to live exceeded'`
  if [[ "$resultPing" == *"Time to live exceeded"* ]]
  then
    result=1
  else
    result=0
  fi
  return "$result"
}

startVpn() {
  sudo openvpn --config settings.ovpn &
}

if [ $# -eq 0 ]
then
  echo "CheckOpenVpn"
  echo ""
  echo "  Usage: sh CheckOpenVpn.sh <ip-to-check>"
  echo ""
  echo ""
  exit -1
fi

ipToCheck=$1

while (true)
do
  vpnIsDown
  result=$?
  now=$(date +"%d/%m/%Y %H:%M:%S.%3N")
  if [ "$result" == 1 ]
  then
    echo "$now: VPN disconnected. Reconnecting..."
    startVpn
  else
    echo "$now: VPN connected!"
  fi
  sleep 3
done


