import httpx

STABILITY_API_URL = "http://localhost:8080/api/v1/stability"

def get_vessel_stability_metric(plan_id: str) -> dict:
    response = httpx.post(
        url=STABILITY_API_URL + "/vessel/validate",
        json={
            "stowagePlanId": plan_id,
        }
    )
    json_response = response.json()
    response.raise_for_status()
    return json_response

def get_current_cg(plan_id: str) -> dict:
    response = httpx.post(
        url=STABILITY_API_URL + "/cg/compute",
        json={
            "stowagePlanId": plan_id,
        }
    )
    json_response = response.json()
    response.raise_for_status()
    return json_response

def get_current_kg(plan_id: str) -> dict:
    response = httpx.post(
        url=STABILITY_API_URL + "/kg/compute",
        json={
            "stowagePlanId": plan_id,
        }
    )
    json_response = response.json()
    response.raise_for_status()
    return json_response

