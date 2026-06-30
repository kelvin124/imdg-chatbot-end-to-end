import httpx

from config import AppConfig, app_config

IMDG_SERVER_API_URL = f"http://{app_config.get_imdg_server_url()}/api/v1/imdg"

async def get_dg_data_un_no(un_nos: list[str]) -> list[dict]:
    if not un_nos:
        return []
    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(
                url=IMDG_SERVER_API_URL + f"/no/batch",
                json=un_nos
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_dg_data_by_class(hazard_classes: list[str]) -> list[dict]:
    if not hazard_classes:
        return []
    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(
                url=IMDG_SERVER_API_URL + f"/class/batch",
                json=hazard_classes
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_dg_data_by_division(hazard_divisions: list[str]) -> list[dict]:
    if not hazard_divisions:
        return []
    async with httpx.AsyncClient() as client:
        try:
            params = { "count": 1 }
            response = await client.post(
                url=IMDG_SERVER_API_URL + f"/division/batch",
                json=hazard_divisions,
                params=params
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_sg_code_data() -> list[dict]:
    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(
                url=IMDG_SERVER_API_URL + f"/segregation-code/all",
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_sgg_code_data() -> list[dict]:
    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(
                url=IMDG_SERVER_API_URL + f"/segregation-group-code/all",
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

# async def get_segregation_rules_by_hazard_class(hazard_classes: list[str]) -> list[dict]:
#     if not hazard_classes:
#         return []
#     async with httpx.AsyncClient() as client:
#         try:
#             response = await client.post(
#                 url=IMDG_API_URL + f"/segregation-rule/class/batch",
#                 json=hazard_classes
#             )
#             response.raise_for_status()
#         except Exception as ex:
#             return []
#         return response.json()

async def get_segregation_rules_by_hazard_division(hazard_divisions: list[str]) -> list[dict]:
    if not hazard_divisions:
        return []
    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(
                url=IMDG_SERVER_API_URL + f"/segregation-rule/division/batch",
                json=hazard_divisions
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_compatibility_group_data() -> list[dict]:
    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(
                url=IMDG_SERVER_API_URL + f"/compatibility-group/all",
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_hazard_class_definition() -> list[dict]:
    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(
                    url=IMDG_SERVER_API_URL + f"/hazard-class-definition",
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()

async def get_hazard_division_definition() -> list[dict]:
    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(
                url=IMDG_SERVER_API_URL + f"/hazard-division-definition",
            )
            response.raise_for_status()
        except Exception as ex:
            return []
        return response.json()