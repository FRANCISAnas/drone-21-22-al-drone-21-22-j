import os

from engine import run

# from dotenv import load_dotenv


if __name__ == '__main__':
    # load_dotenv()
    print('Hello world')
    input('Press Enter to continue:')
    run(os.getenv('DRONE_NAME'))
