from typing import List

from langchain_core.messages import SystemMessage, HumanMessage, AIMessage

from data_classes import ContainerAgentState

def build_system_prompt(state: ContainerAgentState) -> SystemMessage:
    prompt = (
        f"You are a stowage planner. The plan_id is {state.plan_id}\n"
        f"Given a user input, you need to respond to the user.\n"
        f"### GUIDELINE ###\n"
        f"1. Think it step by step, then decide what to do next.\n"
        f"2. Do you have enough data? If this is not enough data, use tools to get the data first.\n"
        f"3. Do you need to use tool?\n"
        f"4. Which tool should you use?\n"
        f"5. NEVER generate arguments on your own, the arguments must be found in the history or by using tools\n\n"
        f"### EXAMPLE ###\n"
        f"User: put a container on ship\n"
        f"AI: to put a container on ship, I need to have a container_no to work on but user does not provide it.\n"
        f"I need to use tool to find the container_no first before proceed to put a container on ship.\n"
    )
    return SystemMessage(content=prompt)

def summarize_state(state: ContainerAgentState):
    pass

def build_history_prompt(state: ContainerAgentState) -> list[SystemMessage]:
    history_prompt = []
    for message in state.messages:
        if isinstance(message, AIMessage):
            history_prompt.append(SystemMessage(content=f"bot: {message.content}"))
        if isinstance(message, HumanMessage):
            history_prompt.append(SystemMessage(content=f"user: {message.content}"))
    return history_prompt