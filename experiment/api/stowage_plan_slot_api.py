import httpx

from data_classes import StowagePlanSlot

STOWAGE_PLAN_API_URL = "http://localhost:8080/api/v1/stowage-plan-slot"

# def add_slot(plan_id: str, container_no: str,
#              bay_index: int, row_index: int, tier_index: int) -> StowagePlanSlot:
#     response = httpx.post(STOWAGE_PLAN_API_URL + "/add",
#               json={
#                   "stowagePlanId": plan_id,
#                   "bayIndex": bay_index,
#                   "rowIndex": row_index,
#                   "tierIndex": tier_index,
#                   "containerNo": container_no,
#                   "operationType": "LD"
#               }
#       )
#     response.raise_for_status()
#     return response.json()

def update_slot(plan_id: str, container_no: str,
             bay_index: int, row_index: int, tier_index: int) -> StowagePlanSlot:
    response = httpx.post(STOWAGE_PLAN_API_URL + "/add",
              json={
                  "stowagePlanId": plan_id,
                  "bayIndex": bay_index,
                  "rowIndex": row_index,
                  "tierIndex": tier_index,
                  "containerNo": container_no,
                  "operationType": "LD"
              }
      )
    response.raise_for_status()
    return response.json()