- Bluesky の Record を定期的に削除するプログラム
- デフォルトでは以下のような設定になっています
  - 1 ヶ月以上経過した Record を削除する
  - 毎日 04:00 に実行される
- 実行には JDK と Scala CLI が必要です

1. .env ファイルを編集する
2. 以下のコマンドを実行する

```bash
$ git clone https://github.com/l7u7ch/fumus
$ cd fumus
$ scala-cli .
```
