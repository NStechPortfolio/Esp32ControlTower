#!/bin/bash

set -e

# 1. 現在のUNIX時間を取得
CURRENT_TIME=$(date +%s)

echo -e "\e[36m--------------------------------------------------"
echo "【1】ESP32のフリをして、データをPOSTリクエストします..."
echo "送信タイムスタンプ (UNIX時間): $CURRENT_TIME"
echo -e "--------------------------------------------------\e[0m"

# curl -X POST http://localhost:8080/api/telemetry \
#   -H "Content-Type: application/json" \
#   -d "{\"satelliteId\":\"ESP32-STATION-01\", \"latitude\":35.6812, \"longitude\":139.7671, \"temperature\":24.5, \"timestamp\":$CURRENT_TIME}"
# 

echo -e "\n\e[32m--------------------------------------------------"
echo "【2】Vue.jsのフリをして、/history にGETリクエストします..."
echo -e "--------------------------------------------------\e[0m"

curl -X GET http://localhost:8080/api/telemetry/history > history.json
echo "" # 改行用
