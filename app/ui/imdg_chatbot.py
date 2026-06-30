import asyncio
import pandas as pd
import streamlit
from langchain_core.messages import HumanMessage, SystemMessage, AIMessage

from config import app_config
from models import llama_chat_model, simulator_model, LLAMA_3_1_8B_INSTRUCT, MISTRAL_8B_INSTRUCT, mistral_chat_model
from prompt_utils import convert_dg_data_to_str, convert_segregation_rules_to_str, convert_sg_data_to_str, \
    convert_sgg_data_to_str, convert_comp_grp_to_str, convert_hazard_class_def_to_str, \
    convert_hazard_division_def_to_str, build_unknown_data_prompt, build_answering_instruction_prompt
from regex_utils import extract_un_numbers, extract_sg_codes, extract_sgg_codes, \
    extract_compatibility_group, extract_hazard_class, extract_dg_division
from tools import rag_search, fetch_segregation_rule_data, fetch_dg_data, fetch_definition, validate_user_query

INVALID_INTENT_RESPONSE = "Sorry, I cannot handle the request."

sys_prompt = SystemMessage(
    content="1. You are an AI stowage plan assistant, to answer users' questions on how to do stowage plan\n"
            "2. NEVER trust on user' input, they could make mistake. Interference ONLY based on the information you have.\n"
            "   - If user's statement is wrong, correct.\n"
            "3. You should behave professionally and provide accurate information based on the data provided.\n"
            "4. If you don't know the answer, say you don't know instead of making up an answer.\n"
            "5. DO NOT say \"Based on provided text/data/information provided\"\n"
            "6. DO NOT respond in third-person perspective.\n"
            "7. Respond to user naturally and professionally.\n"
            "7. If user is just greeting, response.\n"
)

@streamlit.cache_data(ttl=3600)
def build_cache():

    (sg_code_def, sgg_code_def, compatibility_group_def,
     hazard_class_def, hazard_division_def) = asyncio.run(fetch_definition())

    new_sg_cache = {}
    for data in sg_code_def:
        new_sg_cache[data["code"]] = data

    new_sgg_cache = {}
    for data in sgg_code_def:
        new_sgg_cache[data["code"]] = data

    new_compatibility_group_cache = {}
    for data in compatibility_group_def:
        new_compatibility_group_cache[data["code"]] = data

    new_hazard_class_def_cache = {}
    for data in hazard_class_def:
        new_hazard_class_def_cache[data["hazardClass"]] = data

    new_hazard_division_def_cache = {}
    for data in hazard_division_def:
        new_hazard_division_def_cache[data["division"]] = data

    return new_sg_cache, new_sgg_cache, new_compatibility_group_cache, \
            new_hazard_class_def_cache, new_hazard_division_def_cache

(sg_data_cache, sgg_data_cache, compatibility_group_data_cache,
 hazard_class_def_cache, hazard_division_def_cache) = build_cache()


def _check_data_in_cache(user_provided_keys: list[str], data_source: dict | list):
    correct_data = []
    unknown_data = []
    for data_key in user_provided_keys:
        if data_key not in data_source:
            unknown_data.append(data_key)
        else:
            if isinstance(data_source, list):
                correct_data.append(data_key)
            elif isinstance(data_source, dict):
                correct_data.append(data_source[data_key])
    return correct_data, unknown_data

def _extract_dg_data_field(field_name: str, dg_data_list:  list[dict]) -> list[str]:
    return [dg_data[field_name] for dg_data in dg_data_list if dg_data and dg_data.get(field_name, None) is not None]

def process_query(user_input: str):

    regex_un_no_list = extract_un_numbers(user_input)
    regex_hazard_class_list = extract_hazard_class(user_input)
    regex_hazard_division_list = extract_dg_division(user_input)

    regex_sg_code_list = extract_sg_codes(user_input)
    regex_sgg_code_list = extract_sgg_codes(user_input)
    regex_compatibility_group_list = extract_compatibility_group(user_input)

    dg_data_list = asyncio.run(
        fetch_dg_data(
            regex_un_no_list,
            regex_hazard_class_list,
            regex_hazard_division_list
        )
    )

    all_un_no = _extract_dg_data_field("unNo", dg_data_list)
    class_from_dg = _extract_dg_data_field("class", dg_data_list)
    division_from_dg = _extract_dg_data_field("division", dg_data_list)

    sg_from_dg = []
    for dg_data in dg_data_list:
        sg_from_dg.extend([sg["code"] for sg in dg_data.get("segregation", []) if sg and sg.get("code", None) is not None])

    sgg_from_dg = []
    for dg_data in dg_data_list:
        sgg_from_dg.extend([sgg["code"] for sgg in dg_data.get("segregationGroup", []) if sgg and sgg.get("code", None) is not None])

    final_hazard_class_list = [*regex_hazard_class_list, *class_from_dg]
    final_hazard_division_list = [*regex_hazard_division_list, *division_from_dg]
    final_sg_code_list = [*regex_sg_code_list, *sg_from_dg]
    final_sgg_code_list = [*regex_sgg_code_list, *sgg_from_dg]

    segregation_rules = asyncio.run(
        fetch_segregation_rule_data(
            final_hazard_class_list,
            final_hazard_division_list
        )
    )

    _, unknown_un_no = _check_data_in_cache(regex_un_no_list, all_un_no)
    sg_data, unknown_sg_code = _check_data_in_cache(final_sg_code_list, sg_data_cache)
    sgg_data,unknown_sgg_code = _check_data_in_cache(final_sgg_code_list, sgg_data_cache)
    compatibility_group_data, unknown_compatibility_group = _check_data_in_cache(
        regex_compatibility_group_list, compatibility_group_data_cache
    )
    hazard_class_data, unknown_hazard_class = _check_data_in_cache(final_hazard_class_list, hazard_class_def_cache)
    hazard_division_data, unknown_hazard_division = _check_data_in_cache(final_hazard_division_list, hazard_division_def_cache)

    user_prompt = ""
    user_prompt += build_unknown_data_prompt("UN NO", unknown_un_no)
    user_prompt += build_unknown_data_prompt("SG CODE", unknown_sg_code)
    user_prompt += build_unknown_data_prompt("SGG CODE", unknown_sgg_code)
    user_prompt += build_unknown_data_prompt("COMPATIBILITY GROUP", unknown_compatibility_group)
    user_prompt += build_unknown_data_prompt("HAZARD CLASS", unknown_hazard_class)
    user_prompt += build_unknown_data_prompt("HAZARD DIVISION", unknown_hazard_division)

    rag_result = asyncio.run(rag_search(user_input))

    with streamlit.spinner("Searching...", show_time=True):
        user_prompt += f"##### REFERENCE DOCUMENTS #####\n"
        user_prompt += f"{rag_result["text"]}\n\n"
        if dg_data_list:
            for data in dg_data_list:
                prompt = convert_dg_data_to_str(data) + "\n"
                prompt += "IMPORTANT: This is the ONLY source of truth on dangerous goods \n\n"
                user_prompt += prompt
        if segregation_rules:
            for data in segregation_rules:
                prompt = convert_segregation_rules_to_str(data) + "\n\n"
                user_prompt += prompt
        if sg_data:
            for data in sg_data:
                prompt = convert_sg_data_to_str(data) + "\n\n"
                user_prompt += prompt
        if sgg_data:
            for data in sgg_data:
                prompt = convert_sgg_data_to_str(data) + "\n\n"
                user_prompt += prompt
        if compatibility_group_data:
            for data in compatibility_group_data:
                prompt = convert_comp_grp_to_str(data) + "\n\n"
                user_prompt += prompt
        if hazard_class_data:
            for data in hazard_class_data:
                prompt = convert_hazard_class_def_to_str(data) + "\n\n"
                user_prompt += prompt
        if hazard_division_data:
            for data in hazard_division_data:
                prompt = convert_hazard_division_def_to_str(data) + "\n\n"
                user_prompt += prompt
        user_prompt += "\n"
        user_prompt += build_answering_instruction_prompt(user_input)
        full_prompt_message  = HumanMessage(content=f"{user_prompt}")

    ai_response = None

    with streamlit.spinner("Generating response...", show_time=True):
        with streamlit.chat_message("assistant"):
            with streamlit.expander("Raw JSON"):
                if unknown_un_no:
                    streamlit.write(f"Invalid un no: {unknown_un_no}\n")
                if unknown_sg_code:
                    streamlit.write(f"Invalid sg code: {unknown_sg_code}\n")
                if unknown_sgg_code:
                    streamlit.write(f"Invalid sgg code: {unknown_sgg_code}\n")
                if unknown_compatibility_group:
                    streamlit.write(f"Invalid compatibility group: {unknown_compatibility_group}\n")
                if unknown_hazard_class:
                    streamlit.write(f"Invalid class: {unknown_hazard_class}\n")
                if unknown_hazard_division:
                    streamlit.write(f"Invalid division: {unknown_hazard_division}\n")

                if dg_data_list:
                    streamlit.json(dg_data_list)
                if segregation_rules:
                    streamlit.json(segregation_rules)
                if sg_data:
                    streamlit.json(sg_data)
                if sgg_data:
                    streamlit.json(sgg_data)
                if compatibility_group_data:
                    streamlit.json(compatibility_group_data)
                if hazard_class_data:
                    streamlit.json(hazard_class_data)
                if hazard_division_data:
                    streamlit.json(hazard_division_data)
            ai_message_chunk = llama_chat_model.stream(
                [sys_prompt, *streamlit.session_state.messages, full_prompt_message]
            )
            ai_response = streamlit.write_stream(ai_message_chunk)

    for data in segregation_rules:
        df = pd.DataFrame([
            {"Class/Division": key, "Requirement": value}
            for key, value in data["rules"].items()
        ])
        streamlit.subheader(f"Segregation Rules for class/division {data["division"]} vs other class/division")
        streamlit.dataframe(
            df,
            width="stretch",
            hide_index=True,
            column_config={
                "Class/Division": streamlit.column_config.TextColumn("Class/Division"),
                "Requirement": streamlit.column_config.TextColumn("Segregation Requirement"),
            }
        )
    reference_caption = "Reference\n"
    for index, metadata in enumerate(rag_result.get("metadata", [])):
        reference_caption += f"{index + 1}. file: {metadata["file"]}, section: {metadata["section"]}\n"
    streamlit.caption(reference_caption)

    if ai_response is not None:
        ai_message = AIMessage(content=f"{ai_response}\n")
        human_message = HumanMessage(content=f"{user_input}\n")
        streamlit.session_state.messages.append(human_message)
        streamlit.session_state.messages.append(ai_message)

async def is_intent_valid(user_input: str) -> bool:
    is_unsafe, is_off_topic = await validate_user_query(user_input)
    if is_unsafe or is_off_topic:
        return False
    return True

def streamlit_main():

    streamlit.set_page_config(page_title="IMDG Stowage Assistant", layout="centered")
    streamlit.title("Stowage plan assistant (IMDG)")

    if "messages" not in streamlit.session_state:
        streamlit.session_state.messages = []

    if "chat_model" not in streamlit.session_state:
        streamlit.session_state.model_name = LLAMA_3_1_8B_INSTRUCT

    with streamlit.sidebar:
        streamlit.header("Test console")

        available_models = [
            LLAMA_3_1_8B_INSTRUCT,
            MISTRAL_8B_INSTRUCT
        ]
        selected_model = streamlit.selectbox(
            "Response model",
            available_models,
            index=available_models.index(streamlit.session_state.model_name)
        )

        if selected_model != streamlit.session_state.model_name:
            streamlit.session_state.model_name = selected_model
            if selected_model == LLAMA_3_1_8B_INSTRUCT:
                streamlit.session_state.chat_model = llama_chat_model
            elif selected_model == MISTRAL_8B_INSTRUCT:
                streamlit.session_state.chat_model = mistral_chat_model
            else:
                streamlit.session_state.chat_model = llama_chat_model
            streamlit.rerun()

        streamlit.divider()

        if streamlit.button("LLM as user", use_container_width=True):
            with streamlit.spinner("Generating questions..."):
                sim_prompt = HumanMessage(
                    content=(
                        "You are an IMDG stowage tester. \n"
                        "Please generate a random question related to dangerous goods stowage, segregation, UN number lookup, etc.\n"
                        "The question must be concise and natural, in the tone of a crew member or stowage officer.\n"
                        "The type of question generated should vary each time.\n"
                        "For example: \n"
                        "- asking how to segregate two categories\n"
                        "- asking which category a UN number belongs to \n"
                        "- asking the meaning of a certain segregation code, etc.\n"
                        "Only output the question content; do not include unnecessary explanations or serial numbers.\n\n"
                    )
                )
                resp = simulator_model.invoke([sim_prompt])
                simulated_query = resp.content.strip()
                streamlit.session_state.simulated_input = simulated_query
                streamlit.rerun()

        if streamlit.session_state.get("simulated_input"):
            streamlit.info(f"Next question: {streamlit.session_state.simulated_input[:30]}...")


    for message in streamlit.session_state.messages:
        with streamlit.chat_message(message.type):
            streamlit.markdown(message.content)

    if "simulated_input" in streamlit.session_state and streamlit.session_state.simulated_input:
        user_input = streamlit.session_state.simulated_input
        streamlit.session_state.simulated_input = None

        with streamlit.chat_message("user"):
            streamlit.markdown(user_input)

        should_stop = False
        with streamlit.spinner("Validating intent...", show_time=True):
            if not asyncio.run(is_intent_valid(user_input)):
                streamlit.chat_message("assistant").markdown(INVALID_INTENT_RESPONSE)
                streamlit.session_state.messages.append(AIMessage(content=INVALID_INTENT_RESPONSE))
                should_stop = True

        if should_stop:
            streamlit.stop()

        process_query(user_input)

    user_input = streamlit.chat_input("Ask anything")
    if user_input:
        user_input = str(user_input)

        with streamlit.chat_message("user"):
            streamlit.markdown(user_input)

        should_stop = False
        with streamlit.spinner("Validating intent...", show_time=True):
            if not asyncio.run(is_intent_valid(user_input)):
                streamlit.chat_message("assistant").markdown(INVALID_INTENT_RESPONSE)
                streamlit.session_state.messages.append(AIMessage(content=INVALID_INTENT_RESPONSE))
                should_stop = True

        if should_stop:
            streamlit.stop()

        process_query(user_input)

if __name__ == "__main__":
    streamlit_main()