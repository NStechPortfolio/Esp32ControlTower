# ESP32疑似人工衛星管制塔システム (IoT Telemetry System)

本プロジェクトは、疑似人工衛星(ESP32機体)から送信される、宇宙空間を模したリアルタイムなテレメトリデータ(位置、高精度時刻、環境温度)を受信・蓄積し、デジタル地球儀(Cesium.js)上へハンズフリーで3Dプロットする疑似人工衛星管制塔システムである。

IoT組み込み(C++)、高速通信(WebSocket)、webバックエンド(Java)、webフロントエンド(Vue.js + Cesium.js + Typescript)、インフラ(Docker)にいたる全レイヤーの実装を行った。

全体の作業時間: 約3人日(24時間程度)

---

## システムアーキテクチャ・技術スタック

本システムは、開発環境に依存しない動作環境の構築ため、地上局コンポーネントをすべてマルチコンテナで完全コード化している。

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

本システムはマルチリポジトリ構成となっている。システムの起動手順は以下の通り:

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

## ポートフォリオの制作意図とアピールポイント

本プロジェクトは、地上局システムおよびリアルタイムパイプラインの概念実証(PoC)を目的として制作を行った。  
単なる机上の設計にとどまらず、**IoT組み込みからWebバックエンド、3D可視化フロントエンド、インフラまでを一気通貫で繋ぐ最小構成を、約3人日の短期間で迅速にプロトタイピング・完動させる開発推進力**を証明するものである。

具体的には、以下の技術的強みを集約している:

* **フルレイヤーを跨ぐ迅速なプロトタイピング能力**
  * 既存の要素技術を組み合わせ、アイデアから「実際に動くリアルタイムシステム」へ約3人日で落とし込む実装スピード。
* **C++によるエッジ/ハードウェア制御の実践力**
  * 製品によってわずかな様差のあるGPSモジュールやI2C温度センサの制御ロジックをC++で自作・最適化できる低レイヤ知識。
* **コンテナ化(Docker)によるポータビリティの確保**
  * バックエンド・DB・フロントエンドをマルチコンテナ構成でコード化し、環境に依存せずローカル再現できる環境構築力。


## 今後の拡張プラン

現在のシステムは、単一の機体(ESP32)および単一のバックエンドサーバーによる概念実証(PoC)構成となっている。  
今後複数衛星の同時運用や高頻度データ処理へスケールさせるにあたって、以下のアーキテクチャ拡張を検討している:

### 1. データ受信層の非同期化・イベント駆動化
* **現状の課題:**  
	HTTP POST直後にDB保存とWebSocket配信を同期的に実行しているため、データ送信頻度が急増した際にAPIがボトルネックとなる可能性がある。
* **拡張案:**  
	メッセージブローカー(Apache Kafka やRabbitMQ など)を導入。受入処理と保存・配信処理を分離し、データロスを防ぐイベント駆動型アーキテクチャへ刷新する。

### 2. 時系列データベース(TimescaleDB / InfluxDB)への移行
* **現状の課題:**  
	標準的なRDBMSでログを保持しているため、データ量が数百万件規模に膨らんだ際の過去データ検索・集計パフォーマンスが低下する。
* **拡張案:**  
	タイムスタンプインデックスの保持・圧縮に特化した時系列データベースへ変更し、過去の軌道データや環境ログの高速な解析を可能にする。

### 3. WebSocketサーバーの水平スケール化
* **現状の課題:**  
	バックエンドサーバーを複数台に分散させた場合、特定サーバーのメモリ上にしかWebSocketセッションが保持されず、テレメトリデータのブロードキャストが全体に行き渡らない。
* **拡張案:**  
	Redis Pub/Sub をメッセージバックボーンとして挟み、どのサーバーに接続しているクライアント(ブラウザ)に対しても遅延なくテレメトリを同期配信できる構成にする。

### 4. アップリンク機能の追加
* **現状の課題:**  
	現在は衛星から地上への「ダウンリンク(テレメトリ受信)」のみの一方向通信。
* **拡張案:**  
	地上から衛星に対して設定変更や観測指示を送るアップリンク用APIおよびキューイング機構を実装し、双方向の衛星運用シミュレーションへ発展させる。

### 5. クラウドインフラ環境へのデプロイとIAAC化
* **現状の課題:**  
  現在はDocker Composeを用いたローカル環境での動作に留まっており、インターネット経由の外部接続や本番相当のインフラ構成になっていない。
* **拡張案:**  
  AWS へデプロイし、リモート環境からのテレメトリ受信用パブリックエンドポイントを構築する。また、CloudFormation やTerraform 等のIaC ツールを用いてインフラ構成をコード管理化する。

### 6. テスト自動化およびCI/CDパイプラインの構築
* **現状の課題:**  
  迅速なプロトタイピングを優先したため、ユニットテスト・統合テスト等のテストコードおよび自動ビルド環境が未整備である。
* **拡張案:**  
  JUnit / Mockitoを用いたバックエンドのテストコードを拡充。また、GitHub Actionsを導入してPR・Push時の自動テストおよびAWSへの自動デプロイパイプラインを構築し、回帰テストの自動化と品質担保を行う。

