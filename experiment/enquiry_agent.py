import json
import re
import uuid

from langchain.agents import create_agent
from langchain.agents.middleware import ToolCallLimitMiddleware
from langchain_core.messages import HumanMessage, ToolMessage

from data_classes import EnquiryAgentState
from models import CHAT_MODEL, nvidia_llama_3_3_70b, ollama_llama_8b_instruct
from tools.container_tools import get_pending_container_count, get_container_id, get_container_properties, \
    get_container_example
from tools.stowage_plan_tools import get_vessel_general_info, check_bay_supports_reefer

chatbot_state = EnquiryAgentState(messages=[])

enquiry_agent = create_agent(
    # model=CHAT_MODEL,
    model=nvidia_llama_3_3_70b,
    tools=[
        get_pending_container_count, check_bay_supports_reefer,
        get_container_example
    ],
    middleware=[ToolCallLimitMiddleware(
        run_limit=4,
        exit_behavior="end"
    )],
    system_prompt=(
        "User is working on a stowage plan, and you are the assistant to the user.\n\n"
        "### RULES ###\n"
        "1. use your tools to get information\n"
        "2. NEVER generate data by yourself, the data is either from the tools, or just tell the user you don't know\n"
        # "1. Try to answer user naturally.\n"
        # "2. ONLY use tools when you are ABSOLUTELY need it, otherwise DO NOT use it\n"
        "3. NEVER expose the tools you have to users.\n\n"
        # "### EXAMPLE 1 ###\n"
        # "User: Hello\n"
        # "You: Hello, what can I help you\n"
        # "### EXAMPLE 2 ###\n"
        # "User: help me to do stowage plan\n"
        # "You: sure, which container you want to work on?\n"
    )
)

# print("Stowage Planning Chatbot. Type 'exit' to quit.\n")
# while True:
#     user_input = input("You: ")
#     if user_input.lower() in ("exit", "quit"):
#         break
#
#     this_round_message_id = uuid.uuid4().hex
#     this_round_message = None
#
#     chatbot_state.messages.append(
#         HumanMessage(content=user_input)
#     )
#     all_messages = enquiry_agent.invoke({
#         "messages": chatbot_state.messages
#     })
#     latest_message = all_messages["messages"][-1]
#     last_tool_message = None
#     for message in reversed(all_messages["messages"]):
#         if isinstance(message, ToolMessage) and message.name == "remember_user_container_choice":
#             chatbot_state.container_no = json.loads(message.content)["selected_container_id"]
#
#     chatbot_state.messages.append(latest_message)
#     print(f"Bot: {latest_message.content}")