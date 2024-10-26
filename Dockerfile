FROM ubuntu:latest

RUN apt-get update && apt-get install -y curl gzip

RUN curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && \
    chmod +x cs && \
    ./cs setup -y

ENV PATH="$PATH:/root/.local/share/coursier/bin"

WORKDIR /app

COPY . /app/

CMD ["scala-cli", "."]
