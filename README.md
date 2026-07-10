# ESP32疑似人工衛星管制塔システム (IoT Telemetry System)

本プロジェクトは、疑似人工衛星(ESP32機体)から送信される、宇宙空間を模したリアルタイムなテレメトリデータ(位置、高精度時刻、環境温度)を受信・蓄積し、デジタル地球儀(Cesium.js)上へハンズフリーで3Dプロットする疑似人工衛星管制塔システムです。

IoT組み込み(C++)、高速通信(WebSocket)、webバックエンド(Java)、webフロントエンド(Vue.js + Cesium.js + Typescript)、インフラ(Docker)にいたる全レイヤーを実装いたしました。

---

## システムアーキテクチャ・技術スタック

本システムは、開発環境に依存しない動作環境の構築ため、地上局コンポーネントをすべてマルチコンテナで完全コード化しています。

```
[ 疑似人工衛星 (ESP32) ]

    │  (WiFi / HTTP POST)
    │
    │
    ▼

[ 地上局システム (Dockerコンテナ群) ]

    ├── ① Web Backend (Spring Boot 3.4.5 / Java 21)
    │     ├── REST API : テレメトリデータの受信用
    │     └── WebSocket: フロントエンドへのリアルタイム配信
    │
    ├── ② Database (PostgreSQL)
    │
    └── ③ Frontend (Vue 3 / TypeScript / Vite)
      └── Cesium.js: 3Dデジタル地球儀へのリアルタイム・カメラトラッキング
```

### 疑似人工衛星(Edge / Firmware)
* **Microcontroller:** ESP32-DevKitC-32E (C++ / Arduino framework)
* **Peripherals:**
	* GPSモジュール (GT-502MGG-N みちびき2機(194/195)対応) -> *人工衛星よりUTC時刻および現在地を抽出*
  * 温度センサ(ADT7410) -> *I2C通信16-bit高解像度モードによる±0.5°C高精度機体温度測定*

### 地上局(Backend / Infrastructure)
* **Backend Framework:** Spring Boot 3.4.5 (Java 21)
* **Real-time Pipeline:** WebSocket (STOMP/Native)
* **Database:** PostgreSQL
* **Infrastructure:** Docker Compose

### 管制画面(Frontend / Visualization)
* **Frontend Framework:** Vue 3
* **Language:** TypeScript
* **Build Tool:** Vite
* **3D GIS Engine:** Cesium.js (via `vite-plugin-cesium`)

---

## クイックスタート

本システムはマルチリポジトリ構成となっております。以下のコマンドを実行してください。

```bash
mkdir IoTControlTower
cd IoTControlTower
git clone https://github.com/NStechPortfolio/Esp32ControlTower
git clone https://github.com/NStechPortfolio/Esp32ControlTowerViewer

cd Esp32ControlTower

docker compose up -d
```

管制タワー画面: http://localhost:3000  
地上局API: http://localhost:8080  

## API仕様
```
POST /api/telemetry : ESP32機体からの最新データ受信用 (JSON)
GET  /api/telemetry/history : 過去の軌道・環境ログ取得用(tools/postman.sh で実行可能)
WS   /ws/telemetry : 管制画面へのリアルタイム配信パイプライン

// テレメトリデータ構造
{
  "satelliteId": "ESP32-SATELLITE-01",
  "latitude": 35.6812,
  "longitude": 139.7671,
  "temperature": 24.58,
  "timestamp": "2026/07/07 12:34:56.000"
}
```

## 動作イメージ
サンプル動画では以下の手順を実行している:
1. Docker起動
2. ESP32へプログラム書き込み&起動
3. ブラウザでCesium.js初期画面表示
4. ESP32からのPOSTリクエストを受け付け次第、自動的にブラウザの現在位置を更新
5. 手動で地図を移動してもPOSTリクエストを受け付け次第ブラウザの現在位置が自動更新される

https://github.com/user-attachments/assets/7b8b63c8-6bec-4742-9dc7-829acb76cb91
