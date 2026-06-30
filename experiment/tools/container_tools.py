import json
import logging

import httpx
from langchain_core.tools import tool
from pydantic import Field, BaseModel

from experiment.api.container_api import get_pending_container_count_api, get_container_properties_api, get_random_container_api
from field_description import CONTAINER_ID_DESC
from utils import extract_info_from_status_error, convert_dict_to_list_form

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logging.basicConfig(
    level=logging.INFO,
    format='[%(asctime)s] %(filename)s:%(lineno)d - %(levelname)s - %(message)s'
)

class ExtractContainerIdInput(BaseModel):
    container_id: str = Field(description=CONTAINER_ID_DESC)

@tool("extract_container_id",
      args_schema=ExtractContainerIdInput,
      description="use this tool on to extract the container_id")
def extract_container_id(container_id: str) -> dict :
    return {
        "selected_container_id": container_id,
    }

class GetContainerExampleInput(BaseModel):
    count: int = Field(description="how many container_id you want to get")

@tool("get_container_example",
      args_schema=GetContainerExampleInput,
      description="this tool give you n container with their properties, by specifying the count (n)")
def get_container_example(count: int = 3, **kwargs) -> str :
    try:
        container_data = get_random_container_api(count)
        all_container_properties = ""
        for container in get_random_container_api(count):
            all_container_properties += f"### Properties of container: {container["containerNo"]}\n"
            all_container_properties += f"{convert_dict_to_list_form(container)}\n"
        return all_container_properties
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "try again"
    except KeyError as ex:
        logger.error(f"KeyError: {ex}, server returned container data, but without container_id: detail: {json.dumps(container_data)}")
        return "try again"

@tool("get_pending_container_count",
      description="this tool will give the no. of containers that needs to be planned")
def get_pending_container_count() -> str:
    try:
        return f"there are total {get_pending_container_count_api()} containers"
    except Exception as e:
        return "try again"

class GetContainerIdInput(BaseModel):
    count: int = Field(description="how many container_id you want to get")

@tool("get_container_id",
      args_schema=GetContainerIdInput,
      description="this tool only return container_id list")
def get_container_id(count: int = 1, **kwargs) -> str:
    try:
        container_data = get_random_container_api(count)
        all_container_id = [container["containerNo"] for container in get_random_container_api(count)]
        return f"Here are {count} container_id: {", ".join(all_container_id)}"
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "try again"
    except KeyError as ex:
        logger.error(f"KeyError: {ex}, server returned container data, but without container_id: detail: {json.dumps(container_data)}")
        return "try again"

class GetContainerPropertiesInput(BaseModel):
    container_id: str = Field(description=CONTAINER_ID_DESC)

@tool("get_container_properties",
      args_schema=GetContainerPropertiesInput,
      description="specify the container_id and get container properties")
def get_container_properties(container_id: str, **kwargs) -> str :
    try:
        container_data = get_container_properties_api(container_id)
        return (f"- container properties: \n"
                f"{convert_dict_to_list_form(container_data)}")
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "try again"
    except KeyError as ex:
        logger.error(f"KeyError: {ex}, server returned container data, but without container_id: detail: {json.dumps(container_data)}")
        return "try again"