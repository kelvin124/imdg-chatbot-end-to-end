import ast
import re
import uuid
from typing import Any

import httpx
from langchain_core.messages import BaseMessage

def parse_as_dict(data_str: str, field_name) -> Any:
    target_field_value = None
    match = re.search(r"\{.*?\}", data_str)
    dict_string_alike = None
    if match:
        dict_string_alike = match.group(0)

    if dict_string_alike is not None:
        potential_dict_data = ast.literal_eval(dict_string_alike)
        is_dict_output = isinstance(potential_dict_data, dict)
        if is_dict_output:
            target_field_value = find_nested_key(potential_dict_data, field_name)
    return target_field_value

def generate_message_id(prefix: str) -> str:
    return f"{prefix}-{uuid.uuid4().hex}"

def find_message_by_id(message_id: str, message_list: list[BaseMessage]) -> BaseMessage | None:
    return next((message for message in message_list if message.id == message_id), None)

def find_nested_key(data, target_key):
    if isinstance(data, dict):
        if target_key in data:
            return data[target_key]
        for value in data.values():
            result = find_nested_key(value, target_key)
            if result is not None:
                return result
    elif isinstance(data, list):
        for item in data:
            result = find_nested_key(item, target_key)
            if result is not None:
                return result
    return None


def flatten_row_tier_data(bay_data: dict) -> list[dict]:
    row_data: list[dict] | None = bay_data.get("rowSnapshots", None)
    if row_data is None:
        return []

    flatten_data = []
    for row in [row for row in row_data if row.get("rowIndex", None) is not None]:
        row_index = row.get("rowIndex")
        deck = row.get("deckSnapshot", None)
        hold = row.get("holdSnapshot", None)
        if deck is not None:
            cells_on_deck = deck.get("cellSnapshots", [])
            for cell in cells_on_deck:
                flatten_data.append({
                    "tcg": row.get("tcg", None),
                    "vcg": deck.get("vcg", None),
                    "on_deck": True,
                    "row_index": row_index,
                    "allow_reefer": cell.get("allowReefer", None),
                    "tier_index": cell.get("tierIndex", None),
                })
        if hold is not None:
            cells_in_deck = hold.get("cellSnapshots", [])
            for cell in cells_in_deck:
                flatten_data.append({
                    "on_deck": False,
                    "tcg": row.get("tcg", None),
                    "vcg": hold.get("vcg", None),
                    "row_index": row_index,
                    "allow_reefer": cell.get("allowReefer", None),
                    "tier_index": cell.get("tierIndex", None),
                })
    return flatten_data

def extract_info_from_status_error(error: httpx.HTTPStatusError) -> dict[str, Any]:
    error_response_json = error.response.json()
    error_detail = error_response_json.get("detail", "N/A")
    status = error_response_json.get("status", None)
    status_code = error.response.status_code
    request_url = error.request.url
    return {
        "is_error": True,
        "status_code": error.response.status_code,
        "status": error_response_json.get("status", None),
        "detail": error_detail,
        "error_log": (f"HTTP error, status: {status}, code: {status_code}, "
                      f"url: {request_url}, "
                      f"detail: {error_detail}")
    }

def convert_dict_to_list_form(input_dict: dict):
    if input_dict:
        return "- ".join(f"{str(key)}: {str(value)}\n" for key, value in input_dict.items())
    else:
        return ""