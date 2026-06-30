import asyncio

from config import app_config
from content_control import is_content_unsafe, check_is_off_topic

from imdg_api import (
    get_dg_data_un_no,
    get_dg_data_by_class,
    get_dg_data_by_division,
    get_sg_code_data,
    get_sgg_code_data,
    get_segregation_rules_by_hazard_division,
    get_compatibility_group_data,
    get_hazard_class_definition,
    get_hazard_division_definition,
)
from pymilvus import MilvusClient

from models import embedding_model

IMDG_DATABASE_NAME = "stowage_plan"
IMDG_COLLECTION_NAME = "imdg"

milvus_client = MilvusClient(
    uri=app_config.get_milvus_uri(),
    user=app_config.get_milvus_user(),
    password=app_config.get_milvus_password()
)

if IMDG_DATABASE_NAME not in milvus_client.list_databases():
    milvus_client.create_database(IMDG_DATABASE_NAME)

milvus_client.use_database(IMDG_DATABASE_NAME)

if not milvus_client.has_collection(IMDG_COLLECTION_NAME):
    milvus_client.create_collection(IMDG_COLLECTION_NAME, dimension=1024)

def _unique_dg_records(*record_groups: list[dict] | None) -> list[dict]:
    unique_records: list[dict] = []
    seen_un_nos: set[str] = set()
    seen_signatures: set[str] = set()

    for group in record_groups:
        if not group:
            continue

        for record in group:
            if not isinstance(record, dict):
                continue

            un_no = _extract_un_no(record)
            if un_no:
                if un_no in seen_un_nos:
                    continue
                seen_un_nos.add(un_no)
                unique_records.append(record)
                continue

            signature = repr(sorted(record.items()))
            if signature in seen_signatures:
                continue
            seen_signatures.add(signature)
            unique_records.append(record)

    return unique_records


def _extract_un_no(record: dict) -> str | None:
    for key in ("unNo", "un_no", "un_number", "unNumber", "un"):
        value = record.get(key)
        if value not in (None, ""):
            return str(value).strip()
    return None


async def rag_search(query: str) -> dict:
    search_result = milvus_client.search(
        collection_name=IMDG_COLLECTION_NAME,
        data=[embedding_model.embed_query(query)],
        limit=3,
        output_fields=["chunk", "file_name", "metadata"],
    )
    if search_result[0]:
        all_hits = search_result[0]
        text = ""
        metadata = []
        for hit in all_hits:
            text += hit["entity"]["chunk"] + "\n"
            metadata.append({
                "file": hit["entity"].get("file_name", ""),
                "section": hit["entity"].get("metadata", {}).get("section", "")
            })
        return {
            "text": text,
            "metadata": metadata
        }
    return {"text": "", "file": "", "section": ""}


async def fetch_dg_data(
        un_nos: list[str], hazard_class: list[str],
        hazard_division: list[str]
):
    dg_by_un_no, dg_by_class, dg_by_division = await asyncio.gather(
        get_dg_data_un_no(un_nos),
        get_dg_data_by_class(hazard_class),
        get_dg_data_by_division(hazard_division)
    )
    return _unique_dg_records(dg_by_un_no, dg_by_class, dg_by_division)


async def fetch_segregation_rule_data(hazard_class: list[str], hazard_division: list[str]):
    [result] = await asyncio.gather(
        get_segregation_rules_by_hazard_division([*hazard_class, *hazard_division]),
    )
    return result


async def fetch_definition():
    return await asyncio.gather(
        get_sg_code_data(),
        get_sgg_code_data(),
        get_compatibility_group_data(),
        get_hazard_class_definition(),
        get_hazard_division_definition()
    )

async def validate_user_query(query: str | None):
    return await asyncio.gather(
        is_content_unsafe(query),
        check_is_off_topic(query)
    )
