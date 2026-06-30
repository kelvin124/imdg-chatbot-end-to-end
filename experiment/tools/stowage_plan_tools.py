import json
import random

import httpx
from langchain_core.messages import SystemMessage, HumanMessage
from langchain_core.tools import tool
from pydantic import BaseModel, Field

from experiment.api.container_api import get_container_properties_api
from experiment.api.stowage_plan_api import get_all_possible_bays, get_bay, get_vessel_profile_api, get_bay_has_reefer_cell_api
from experiment.api.stowage_plan_slot_api import update_slot
from experiment.api.vessel_stability_api import get_vessel_stability_metric
from data_classes import Cell
from field_description import CONTAINER_ID_DESC, STOWAGE_PLAN_ID_DESC, BAY_INDEX_DESC
from knowledge_base import get_vessel_stability_knowledge
from models import ollama_phi4_mini
from utils import flatten_row_tier_data, extract_info_from_status_error, convert_dict_to_list_form


import logging

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logging.basicConfig(
    level=logging.INFO,
    format='[%(asctime)s] %(filename)s:%(lineno)d - %(levelname)s - %(message)s'
)

class GetBayHasReeferCellInput(BaseModel):
    stowage_plan_id: str = Field(description=STOWAGE_PLAN_ID_DESC)
    bay_index: int = Field(description=BAY_INDEX_DESC)

@tool("check_bay_supports_reefer",
      args_schema=GetBayHasReeferCellInput,
      description="this tool will tell you whether a bay support reefer container")
def check_bay_supports_reefer(stowage_plan_id: str, bay_index: int, **kwargs) -> str:
    try:
        return f"support reefer container: {get_bay_has_reefer_cell_api(stowage_plan_id, bay_index)}"
    except Exception as e:
        return "error, try again"

class GetVesselProfileInput(BaseModel):
    stowage_plan_id: str = Field(description=STOWAGE_PLAN_ID_DESC)

@tool("get_vessel_general_info",
      args_schema=GetVesselProfileInput,
      description="this tool will give general information of the vessel related to the stowage plan")
def get_vessel_general_info(stowage_plan_id: str, **kwargs) -> str:
    try:
        vessel_profile = get_vessel_profile_api(stowage_plan_id)
        del vessel_profile["hydroPoints"]
        del vessel_profile["tanks"]
        del vessel_profile["vesselId"]
        return ("Vessel information\n"
                f"{convert_dict_to_list_form(vessel_profile)}")
    except Exception as e:
        return "error, try again"

@tool("get_vessel_general_info",
      args_schema=GetVesselProfileInput,
      description="this tool will give general information of the vessel related to the stowage plan")
def get_vessel_general_info(stowage_plan_id: str, **kwargs) -> str:
    try:
        vessel_profile = get_vessel_profile_api(stowage_plan_id)
        del vessel_profile["hydroPoints"]
        del vessel_profile["tanks"]
        del vessel_profile["vesselId"]
        return ("Vessel information\n"
                f"{convert_dict_to_list_form(vessel_profile)}")
    except Exception as e:
        return "unknown error, try again"


def find_bay_index(container_id: str, stowage_plan_id: str, **kwargs) -> int | str:
    all_possible_bays = []
    try:
        container_data = get_container_properties_api(container_id)
        knowledge = get_vessel_stability_knowledge("../prompt/vessel_stability.txt")
        stability_metric = get_vessel_stability_metric(stowage_plan_id)
        all_possible_bays = get_all_possible_bays(stowage_plan_id)
    except Exception as e:
        return "unknown error, try again"

    all_possible_bay_prompt = f"### AVAILABLE BAYS ###\n"
    for index, bay in enumerate(all_possible_bays):
        if not bay.get("hasCell", False):
            all_possible_bay_prompt += (f"{index+1}, bay_index: {bay["bayIndex"]}, "
                                        f"lcg: {bay["lcg"]}, "
                                        f"constWeight: {bay["constWeight"]}, "
                                        f"constWeightVcg: {bay["constWeightVcg"]}, "
                                        f"hasReeferCell: {bay["hasReeferCell"]}\n")
    feedback_messages = []
    for i in range(3):
        sys_message = SystemMessage(content=f"You are a stowage planning assistant.\n"
                                            f"Given a container, decide which bay this container should go to.\n"
                                            f"One container is not enough for you to have a perfect plan to make the vessel stable.\n"
                                            f"Use your knowledge to choose the bay with can lead the vessel stable.\n"
                                            f"If not enough, you can choose any one of the available bays\n"
                                            f"Output the bay index in JSON\n\n"
                                            f"### KNOWLEDGE ###\n"
                                            f"{knowledge}\n\n"
                                            f"### OUTPUT JSON ONLY ###\n"
                                            f"{{ \"bay_index\": 3 }}\n\n"
                          )
        human_message = HumanMessage(content="### CONTAINER PROPERTIES ###\n"
                                             f"{convert_dict_to_list_form(container_data)}\n\n"
                                             f"{all_possible_bay_prompt}\n\n"
                                             f"### CURRENT CG and KG ###\n"
                                             f"1. CG: {stability_metric["cg"]}\n"
                                             f"2. KG: {stability_metric["kg"]}\n"
                         )
        messages = [sys_message, human_message]
        response = ollama_phi4_mini.invoke(messages)
        try:
            json_response = json.loads(response.content)
            logger.info(f"find_bay result: {json.dumps(json_response)}")
            return json_response["bay_index"]
        except json.JSONDecodeError as e:
            logger.error(f"Failed to decode JSON: {e}")
            feedback_messages.append(
                SystemMessage(content=f"### INVALID JSON FORMAT ###\n"
                                      f"1. JSON should use double \"\" on keys\n"
                                      f"2. Should always start with {{ and end end with }}")
            )
        except KeyError as e:
            logger.error(f"KeyError: {e}")
            feedback_messages.append(
                SystemMessage(content=f"### MISSING IMPORTANT JSON KEY ###\n"
                                      f"1. missing JSON key: {e}\n")
            )

    return random.choice([item["bayIndex"] for item in all_possible_bays if item.get("hasCell", False)])

def find_cell(container_id: str, stowage_plan_id: str, bay_index: int, **kwargs) -> Cell | dict | str:
    container_data = None
    row_tier = []
    try:
        container_data = get_container_properties_api(container_id)
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "failed to make request, retry."

    try:
        bay_data = get_bay(stowage_plan_id, bay_index)
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "failed to make request, retry."

    try:
        row_tier = flatten_row_tier_data(bay_data)
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return  error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "failed to make request, retry."

    row_tier_prompt = f"### AVAILABLE CELLS IN BAY {bay_index} ###\n"
    last_row_index = -1
    for index, row_tier_data in enumerate(row_tier):
        if last_row_index != row_tier_data["row_index"]:
            row_tier_prompt += f"\n### row_index: {row_tier_data["row_index"]}, vcg: {row_tier_data["vcg"]} ###\n"

        row_tier_prompt += (f"{index+1}. tier_index: {row_tier_data["tier_index"]}, "
                            f"allow_reefer: {row_tier_data["allow_reefer"]}\n")
        last_row_index = row_tier_data["row_index"]
    try:
        knowledge = get_vessel_stability_knowledge("prompt/vessel_stability.txt")
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return  error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "failed to make request, retry."

    feedback_messages = []
    for i in range(3):
        sys_message = SystemMessage(content=f"You are a stowage planning assistant\n"
                                            f"Given a container, and all the possible cell, decide the row_index and tier_index\n"
                                            f"Output the both row_index and tier_index in JSON\n\n"
                                            f"### KNOWLEDGE ###\n"
                                            f"{knowledge}\n\n")
        human_message1= HumanMessage(content=f"### CONTAINER ###\n"
                                             f"{convert_dict_to_list_form(container_data)}\n\n")
        human_message2 = HumanMessage(content=f"{row_tier_prompt}\n")
        sys_message2 = SystemMessage(content=f"### OUTPUT JSON ONLY ###\n"
                                             f"{{ \"row_index\": 1, \"tier_index\": 1 }}\n\n")
        messages = [*feedback_messages, sys_message, human_message1, human_message2, sys_message2, ]
        response = ollama_phi4_mini.invoke(messages)
        feedback_messages.clear()
        try:
            json_response = json.loads(response.content)
            return Cell(bay_index=bay_index,
                        row_index=json_response["row_index"],
                        tier_index=json_response["tier_index"])
        except json.JSONDecodeError as e:
            logger.error(f"Failed to decode JSON: {e}")
            feedback_messages.append(
                SystemMessage(content=f"### INVALID JSON FORMAT ###\n"
                                      f"This is invalid output: {response.content}\n"
                                      f"1. JSON should use double \"\" on keys\n"
                                      f"2. Should always start with {{ and end end with }}\n\n")
            )
        except KeyError as e:
            logger.error(f"KeyError: {e}")
            feedback_messages.append(
                SystemMessage(content=f"### MISSING IMPORTANT JSON KEY ###\n"
                                      f"This is invalid output: {response.content} \n"
                                      f"1. missing JSON key: {e}\n\n")
            )

    if row_tier:
        radom_choice = random.choice(row_tier)
        return Cell(bay_index=bay_index,
                        row_index=radom_choice["row_index"],
                        tier_index=radom_choice["tier_index"])
    else:
        return "cannot find a suitable cell"

class PlanContainerInput(BaseModel):
    container_id: str = Field(description=CONTAINER_ID_DESC)
    stowage_plan_id: str = Field(description=STOWAGE_PLAN_ID_DESC)
    # row_index: str = Field(description=ROW_INDEX_DESC)
    # tier_index: str = Field(description=TIER_INDEX_DESC)

@tool("plan_container",
      args_schema=PlanContainerInput,
      description="this tool is so assign a container to a cell, "
                  "you MUST provide container_id and plan_id")
def plan_container(container_id: str | None = None, stowage_plan_id: str | None = None, **kwargs) -> dict | str:
    if not container_id or container_id.lower() == 'none' or container_id.lower() == 'null':
        return "missing container_id, use get_container_id to find a container_id"

    if not stowage_plan_id or stowage_plan_id.lower() == 'none' or stowage_plan_id.lower() == 'null':
        return "missing stowage_plan_id"

    cell = Cell(bay_index=None, row_index=None, tier_index=None)

    container_data = {}
    try:
        container_data = get_container_properties_api(container_id)
        container_id = container_data["containerNo"]
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "cannot make request, retry."
    except KeyError as ex:
        logger.error(f"KeyError: {ex}, server returned container data, but without container_id: detail: {json.dumps(container_data)}")
        return "retry"

    if not cell.bay_index:
        find_bay_index_result = find_bay_index(container_id, stowage_plan_id)
        if isinstance(find_bay_index_result, str):
            return find_bay_index_result
        else:
            if find_bay_index_result:
                cell.bay_index = find_bay_index_result

    if cell.bay_index is None:
        logger.error(f"cannot find bay for container_id: {container_id}")
        return "missing bay_index"

    logger.info(f"selected bay_index: {cell.bay_index}")

    if not cell.row_index or not cell.tier_index:
        find_cell_result = find_cell(container_id, stowage_plan_id, cell.bay_index)

        if isinstance(find_cell_result, str):
            return find_cell_result
        if isinstance(find_cell_result, dict):
            cell.row_index = find_cell_result.get("row_index", None)
            cell.tier_index = find_cell_result.get("tier_index", None)
        if isinstance(find_cell_result, Cell):
            result_row_index = find_cell_result.row_index
            result_tier_index = find_cell_result.tier_index
            if result_row_index:
                cell.row_index = result_row_index
            if result_tier_index:
                cell.tier_index = result_tier_index

    if not cell.row_index:
        return "try again"

    if not cell.tier_index:
        return "try again"

    logger.info(f"selected row_index: {cell.row_index}, selected tier_index: {cell.tier_index}")

    try:
        update_slot(stowage_plan_id, container_id, cell.bay_index, cell.row_index, cell.tier_index)
        return "success"
    except httpx.HTTPStatusError as ex:
        error = extract_info_from_status_error(ex)
        logger.error(f"{error["error_log"]}")
        return error["detail"]
    except httpx.HTTPError as ex:
        argument_str = " ".join(ex.args)
        logger.error(f"Error while requesting: {ex.request.url}, detail: {argument_str}")
        return "failed to make request, retry"
