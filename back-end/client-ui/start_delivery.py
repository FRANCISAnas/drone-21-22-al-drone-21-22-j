from send import start_paquet_delivering

paquet1 = {
    "weight": 200.6,
    "dimensions": {
        "height": 100,
        "width": 60,
        "length": 200
    },
    "destination": {"x": 10, "y": 350},
    "origin": {"x": 469.6, "y": 101.7}
}

paquet2 = {
    "weight": 200.6,
    "dimensions": {
        "height": 100,
        "width": 60,
        "length": 200
    },
    "destination": {"x": 310.4, "y": 287},
    "origin": {"x": 448, "y": -68}
}

if __name__ == '__main__':
    input('Press enter to begin 1st delivery of package')
    start_paquet_delivering(paquet1)
    input('Press enter to begin 2nd delivery of package')
    start_paquet_delivering(paquet2)
