import ast
import json
import uuid

from langchain.agents import create_agent
from langchain.agents.middleware import ToolCallLimitMiddleware
from langchain_core.messages import HumanMessage, AIMessage, ToolMessage, SystemMessage

from data_classes import ContainerAgentState
from models import CHAT_MODEL, ollama_llama_8b_instruct
from tools.stowage_plan_tools import plan_container

chatbot_state = ContainerAgentState(
    messages=[], plan_id="ecc44ca4-1193-4093-92fe-53a2d0ef8729"
)

container_agent = create_agent(
    model=ollama_llama_8b_instruct,
    tools=[plan_container],
    middleware=[ToolCallLimitMiddleware(
        run_limit=7,
        exit_behavior="end"
    )],
    system_prompt=(
        "You a stowage plan assistant to help user to do stowage planning, plan a container on vessel."
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
#     agent_latest_state = container_agent.invoke({
#         "messages": chatbot_state.messages
#     })
#     latest_message = agent_latest_state["messages"][-1]
#     chatbot_state.messages.append(latest_message)
#     print(f"Bot: {latest_message.content}")