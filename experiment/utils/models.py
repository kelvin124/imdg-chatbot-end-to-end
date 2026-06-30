from langchain.chat_models import init_chat_model
from langchain_core.language_models import BaseChatModel
from langchain_nvidia_ai_endpoints import ChatNVIDIA
from langchain_ollama import ChatOllama

nim_llama_8b_instruct = ChatNVIDIA(
    model="meta/llama-3.1-8b-instruct",
    temperature=0.1, top_p=0.8
)

ollama_phi4_mini = ChatOllama(
    model="phi4-mini",
    temperature=0.1, top_p=0.9,
    format="json",
    num_predict=1024
)

ollama_llama_8b_instruct = ChatOllama(
    model="llama3.1:8b",
    temperature=0.1, top_p=0.9,
    # format="json",
    num_predict=1024
)

nvidia_llama_3_3_70b = ChatNVIDIA(
  model="meta/llama-3.3-70b-instruct",
  temperature=0.2,
  top_p=0.9,
  max_tokens=2048,
)

CHAT_MODEL: BaseChatModel = nvidia_llama_3_3_70b