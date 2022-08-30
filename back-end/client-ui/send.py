#!/usr/bin/env python3
import os
import requests

# créer un paquet_1
# créer une station
# créer un warehouse
# créer un drone

CUSTOMER_CARE_URL = f"http://{os.getenv('CUSTOMER_CARE_HOST', 'localhost')}:{os.getenv('CUSTOMER_CARE_PORT', '8081')}"


def start_paquet_delivering(paquet):
    res = requests.post(f"{CUSTOMER_CARE_URL}/customers/package/cost", json=paquet)
    cost_estimation = res.json()
    temp_id = cost_estimation["temporaryId"]
    print("Received cost estimation: ", cost_estimation)
    input("press enter to confirm the purchase...")
    res = requests.post(f"{CUSTOMER_CARE_URL}/customers/package/confirm/{temp_id}", json={
        'cvv': '000',
        'cardNumber': '8888999944445555',
        'expirationDate': '08-23'
    })
    assert res.status_code in range(200, 300)
    confirmation_result = res.json()
    # print(confirmation_result)
    tracking_number = str(confirmation_result["trackingNumber"])
    print("Order confirm, Traking number: ", tracking_number)
