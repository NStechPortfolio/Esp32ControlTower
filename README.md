# 疑似人工衛星管制塔システム

## システム概要

- Docker でwebバックエンドサーバ(Java)、フロントエンドサーバ(Typescript + Vue.js)、DBサーバ(Postgresql)
- ESP32にGPS とサーモセンサを実装し、10秒に一度「位置情報、温度、現在時刻」をバックエンドサーバに送信する。
- Java/SpringBoot でWebSocket を実装する。
	- "/api/telemetry" でESP32 からのPOST リクエストを処理する。
	- "/api/telemetry/history" でVue.js からのGET リクエストを処理する。
- Vue.js で現在の位置情報と温度情報を表示する。
	- Cesium.js を使って現在の場所を画面表示する。
- Postgresql でESP32 から送信された情報をDB に格納する。

## テスト方法
```
$ docker compose up -d
```
