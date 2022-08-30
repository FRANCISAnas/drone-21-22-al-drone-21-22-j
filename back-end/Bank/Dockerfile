FROM python:3.8

WORKDIR /usr/src/app

RUN apt-get update && apt-get install -y netcat

COPY requirements.txt ./
RUN python3 -m pip install --no-cache-dir -r requirements.txt

COPY . .
RUN chmod +x start.sh

ENTRYPOINT [ "./start.sh" ]