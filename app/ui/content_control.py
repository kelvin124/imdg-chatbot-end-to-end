import json

import streamlit
from langchain_core.messages import SystemMessage, HumanMessage
from langchain_nvidia_ai_endpoints import ChatNVIDIA

from models import topic_control_llm, content_safety_llm


async def is_content_unsafe(query: str | None):
    if not query:
        return False
    system_message = SystemMessage(
        content=("You are a maritime safety filter for an IMDG (International Maritime Dangerous Goods) stowage assistant. \n"
                 "Your ONLY job is to distinguish between: \n"
                 "1. LEGITIMATE maritime operations e.g. \n"
                 "- Stowage planning, segregation \n"
                 "- UN numbers, hazard classes \n"
                 "- Emergency procedures, shipping documentation \n"
                 "- Cargo handling. \n"
                 "2. ILLEGITIMATE/HARMFUL queries, e.g. \n"
                 "- Violence \n"
                 "- Sexual \n"
                 "- Criminal Planning/Confessions \n"
                 "- Guns and Illegal Weapons \n"
                 "- Controlled/Regulated Substances \n"
                 "- Suicide and Self Harm \n"
                 "- Sexual (minor) \n"
                 "- Hate/Identity Hate \n"
                 "- PII/Privacy \n"
                 "- Harassment \n"
                 "- Threat \n"
                 "- Profanity \n"
                 "- Needs Caution \n"
                 "- Other \n"
                 "- Manipulation \n"
                 "- Fraud/Deception \n"
                 "- Malware \n"
                 "- High Risk Gov Decision Making \n"
                 "- Political/Misinformation/Conspiracy \n"
                 "- Copyright/Trademark/Plagiarism \n"
                 "- Unauthorized Advice \n"
                 "- Illegal Activity \n"
                 "- Immoral/Unethical \n\n")
    )
    human_message = HumanMessage(
        content=f"User input: {query}"
    )
    response_message = await content_safety_llm.ainvoke([system_message, human_message])
    try:
        json_response = json.loads(response_message.content)
    except json.decoder.JSONDecodeError:
        return False
    return "unsafe" == json_response["User Safety"]

async def check_is_off_topic(query: str | None):
    if not query:
        return False
    system_message = SystemMessage(
        "You are a stowage planner expert, stowage planning is the only thing you understand.\n"
        "You need to determine whether user is asking the right question.\n"
        "Determine if the topic is allowed or disallowed ONLY.\n"
        "NEVER answer users' question.\n\n"
        "### DISALLOWED TOPICS ###\n"
        "1. Anything about coding\n"
        "2. Criminal actions\n"
        "3. Politics\n"
        "4. Stock prediction\n"
        "5. Illegal activities\n"
        "6. Sexual content\n\n"
    )
    human_message = HumanMessage(
        content=f"User input: {query}"
    )
    response_message = await topic_control_llm.ainvoke([system_message, human_message])
    return "off-topic" == response_message.content.strip()