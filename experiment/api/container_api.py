import ast
import json
import re
import uuid
from typing import Any

import httpx
from langchain_core.messages import BaseMessage

CONTAINER_API_URL = "http://localhost:8080/api/v1/container"

def get_container_properties_api(container_no: str, **kwargs) -> dict:
    response = httpx.get(
        url=CONTAINER_API_URL + f"/{container_no}",
    )
    response.raise_for_status()
    return response.json()

def get_random_container_api(count: int, **kwargs) -> list[dict]:
    params = {"count": count}
    response = httpx.get(
        url=CONTAINER_API_URL + f"/random", params=params
    )
    response.raise_for_status()
    return response.json()

def get_pending_container_count_api() -> dict:
    response = httpx.get(
        url=CONTAINER_API_URL + f"/pending-count",
    )
    response.raise_for_status()
    return response.json()
