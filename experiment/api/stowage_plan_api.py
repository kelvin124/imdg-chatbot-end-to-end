import httpx

STOWAGE_PLAN_API_URL = "http://localhost:8080/api/v1/stowage-plan"

def get_bay_has_reefer_cell_api(plan_id: str, bay_index: int) -> bool:
    params = { "bayIndex": bay_index }
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/bay/has-reefer-cell", params=params)
    response.raise_for_status()
    return response.json()

def get_vessel_profile_api(plan_id: str) -> dict:
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/vessel-profile")
    response.raise_for_status()
    return response.json()

def get_bay_count_api(plan_id: str) -> dict:
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/bay/count")
    response.raise_for_status()
    return response.json()

def get_all_possible_bays(plan_id: str) -> dict:
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/bay/all")
    response.raise_for_status()
    return response.json()


def get_all_possible_rows(plan_id: str, bay_index: int) -> dict:
    params = { "bayIndex": bay_index }
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/row/all", params=params)
    response.raise_for_status()
    return response.json()


def get_all_possible_tiers(plan_id: str, bay_index: int, row_index: int) -> dict:
    params = { "bayIndex": bay_index, "rowIndex": row_index }
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/tier/all", params=params)
    response.raise_for_status()
    return response.json()

def get_bay(plan_id: str, bay_index: int) -> dict:
    params = { "bayIndex": bay_index }
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/bay", params=params)
    response.raise_for_status()
    return response.json()

def get_row(plan_id: str, bay_index: int, row_index: int) -> dict:
    params = { "bayIndex": bay_index, "rowIndex": row_index }
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/row", params=params)
    response.raise_for_status()
    return response.json()

def get_tier(plan_id: str, bay_index: int, row_index: int, tier_index: int) -> dict:
    params = { "bayIndex": bay_index, "row_index": row_index, "tierIndex": tier_index }
    response = httpx.get(STOWAGE_PLAN_API_URL + f"/{plan_id}/tier", params=params)
    response.raise_for_status()
    return response.json()

def verify_if_cell_exists(plan_id: str, bay_index: int, row_index: int, tier_index: int):
    params = { "bayIndex": bay_index, "rowIndex": row_index, "tierIndex": tier_index }
    response = httpx.get(url=STOWAGE_PLAN_API_URL + f"/{plan_id}/tier", params=params)
    response.raise_for_status()
    return response.json()

def verify_if_row_exists(plan_id: str, bay_index: int, row_index: int):
    params = { "bayIndex": bay_index, "rowIndex": row_index }
    response = httpx.get(url=STOWAGE_PLAN_API_URL + f"/{plan_id}/row", params=params)
    response.raise_for_status()
    return response.json()

def verify_if_bay_exists(plan_id: str, bay_index: int):
    params = { "bayIndex": bay_index }
    response = httpx.get(url=STOWAGE_PLAN_API_URL + f"/{plan_id}/bay", params=params)
    response.raise_for_status()
    return response.json()