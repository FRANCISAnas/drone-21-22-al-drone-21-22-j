import os
from flask import Flask

app = Flask(__name__)


@app.route('/', methods=["POST"])
def home():
    return "SUCCESS"


@app.route('/status')
def status():
    return 'READY'


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=int(os.getenv('PORT', 5000)))
