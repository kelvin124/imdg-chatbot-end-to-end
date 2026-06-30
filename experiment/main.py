import json
import logging

from langchain.agents import create_agent
from langchain.agents.middleware import ToolCallLimitMiddleware
from langchain_core.messages import HumanMessage, SystemMessage, ToolMessage

from container_agent import container_agent
from data_classes import MultiAgentState
from enquiry_agent import enquiry_agent
from models import CHAT_MODEL, ollama_llama_8b_instruct
from tools.container_tools import extract_container_id
from transformers import pipeline

extract_container_id_agent = create_agent(
    model=CHAT_MODEL,
    tools=[extract_container_id],
    middleware=[ToolCallLimitMiddleware(
        run_limit=5,
        exit_behavior="end"
    )],
    system_prompt=(
        "User is working on a stowage plan, and you are the assistant to the user.\n"
        "You need to identify the container_id first.\n"
        "### RULES ###\n"
        "0. Base on the conversation, determine the container_id that the user want to work on.\n"
        "1. NEVER expose the tools you have to users.\n"
        "2. DO NOT generate container_id, it MUST be based on the messages so far.\n"
    )
)

INTENT_STOWAGE_PLAN = "stowage_plan"
INTENT_DATA_QUERY = "data_query"
INTENT_CHAT = "chat"

def intent_classification(str_input: str):
    classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli", )
    candidate_labels = [INTENT_STOWAGE_PLAN, INTENT_DATA_QUERY, INTENT_CHAT]
    result = classifier(str_input, candidate_labels)
    return result["labels"][0]

def extract_container_id(state: MultiAgentState) -> str | None:
    sys_message = SystemMessage(content=f"the stowage_plan_id is {state.stowage_plan_id}")
    all_messages = extract_container_id_agent.invoke({
        "messages": [sys_message] + state.messages
    })

    container_id = None
    for message in reversed(all_messages["messages"]):
        if isinstance(message, ToolMessage) and message.name == "extract_container_id":
            try:
                container_id = json.loads(message.content)["selected_container_id"]
            except Exception as e:
                pass

    return container_id

chatbot_state = MultiAgentState(
    messages=[], stowage_plan_id="ecc44ca4-1193-4093-92fe-53a2d0ef8729",
    target_container_id = None
)

while True:

    user_input = input("You: ")
    if user_input.lower() in ("exit", "quit"):
        break

    intent = intent_classification(user_input)
    logging.info(f"intent: {intent}")

    human_message = HumanMessage(content=f"{user_input}\n")
    bot_message = "unknown error"

    if intent == INTENT_CHAT:
        system_message = SystemMessage(content="You a stowage plan assistant to help user to do stowage planning,\n"
                                               "plan a container on vessel.\n"
                                               "Do not answer things you do not know or generate the data by yourself.\n"
                                               "If you don't know the answer, say so.")
        ai_message = ollama_llama_8b_instruct.invoke(
            [system_message] + chatbot_state.messages + [human_message]
        )
        chatbot_state.messages.append(ai_message)
        bot_message = ai_message.content

    if intent == INTENT_DATA_QUERY:
        target_container_id = extract_container_id(chatbot_state)

        sys_message = SystemMessage(content=f"the stowage_plan_id is {chatbot_state.stowage_plan_id}\n")
        all_messages = enquiry_agent.invoke({
            "messages": [sys_message] + chatbot_state.messages + [human_message]
        })
        latest_message = all_messages["messages"][-1]
        chatbot_state.messages.append(latest_message)
        bot_message = latest_message.content

    if intent == INTENT_STOWAGE_PLAN:

        print(f"planning container: {chatbot_state.target_container_id}")
        sys_message_1 = SystemMessage(content=f"the stowage_plan_id is {chatbot_state.stowage_plan_id}\n"
                                            f"user want to plan this container: {chatbot_state.target_container_id}\n")
        sys_message_2 = SystemMessage(content=f"plan this container: {chatbot_state.target_container_id} for the user\n")
        all_messages = container_agent.invoke({
            "messages": [sys_message_1, sys_message_2] + chatbot_state.messages + [human_message]
        })
        latest_message = all_messages["messages"][-1]
        chatbot_state.messages.append(latest_message)
        bot_message = latest_message.content

    print(f"Bot: {bot_message}")

