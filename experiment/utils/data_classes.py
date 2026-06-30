from dataclasses import dataclass

from langchain_core.messages import BaseMessage


@dataclass
class StowagePlanSlot:
    container_no: str
    bay_index: int
    row_index: int
    tier_index: int

@dataclass
class Cell:
    bay_index: int | None
    row_index: int | None
    tier_index: int | None

@dataclass
class MultiAgentState:
    messages: list[BaseMessage]
    stowage_plan_id: str
    target_container_id: str | None


@dataclass
class EnquiryAgentState:
    messages: list[BaseMessage]

    def __init__(self, messages: list):
        self.messages = messages

@dataclass
class ContainerAgentState:

    def __init__(self, messages: list, plan_id: str, container_no: str | None = None):
        self.messages = messages
        self.plan_id = plan_id
        self.container_no = container_no
