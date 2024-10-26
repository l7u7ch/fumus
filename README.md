- Bluesky の Record を定期的に削除するプログラム
- 実行するには Docker が必要です

```bash
$ git clone https://github.com/l7u7ch/fumus
$ cd fumus
$ docker build -t fumus .
$ docker run -d \
  -e IDENTIFIER=<USER_IDENTIFIER> \
  -e PASSWORD=<USER_PASSWORD> \
  --restart unless-stopped \
  fumus
```
