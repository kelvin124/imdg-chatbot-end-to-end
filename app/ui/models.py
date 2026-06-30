import streamlit
from langchain_nvidia_ai_endpoints import ChatNVIDIA, NVIDIAEmbeddings

from config import app_config

NEMOGUARD_CONTENT_SAFETY = "nvidia/llama-3.1-nemoguard-8b-content-safety"
NEMOGUARD_TOPIC_CONTROL = "nvidia/llama-3.1-nemoguard-8b-topic-control"
LLAMA_3_1_8B_INSTRUCT = "meta/llama-3.1-8b-instruct"
MISTRAL_8B_INSTRUCT = "mistralai/mistral-7b-instruct-v0.3"

content_safety_llm = ChatNVIDIA(
    model=NEMOGUARD_CONTENT_SAFETY,
    nvidia_api_key=app_config.get_nvidia_api_key()
)

topic_control_llm = ChatNVIDIA(
    model=NEMOGUARD_TOPIC_CONTROL,
    nvidia_api_key=app_config.get_nvidia_api_key()
)

mistral_chat_model = ChatNVIDIA(
    model=MISTRAL_8B_INSTRUCT,
    temperature=0.1, top_p=0.8,
    nvidia_api_key=app_config.get_nvidia_api_key()
)

llama_chat_model = ChatNVIDIA(
    model=LLAMA_3_1_8B_INSTRUCT,
    temperature=0.1, top_p=0.8,
    nvidia_api_key=app_config.get_nvidia_api_key()
)

simulator_model = ChatNVIDIA(
    model=LLAMA_3_1_8B_INSTRUCT,
    temperature=0.9,
    nvidia_api_key=app_config.get_nvidia_api_key()
)

embedding_model = NVIDIAEmbeddings(
    model="nvidia/nv-embedqa-e5-v5",
    nvidia_api_key=app_config.get_nvidia_api_key()
)