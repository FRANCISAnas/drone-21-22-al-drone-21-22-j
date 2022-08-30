FROM python:3.8-bullseye

RUN apt-get update
RUN apt-get install wait-for-it
COPY . .
RUN python3 -m pip install -r requirements.txt



RUN chmod +x start.sh

ENTRYPOINT [ "./start.sh" ]