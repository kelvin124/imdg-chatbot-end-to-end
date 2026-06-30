import logging

from langchain_nvidia_ai_endpoints import NVIDIAEmbeddings
from pymilvus import MilvusClient, DataType
from tqdm import tqdm

try:
    # Support module execution: python -m imdg.imdg_milvus
    from data.imdg.chunk_md import chunk_md
except ImportError:
    # Support script execution from this folder.
    from data.imdg.chunk_md import chunk_md

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logging.basicConfig(
    level=logging.WARNING,
    format='[%(asctime)s] %(filename)s:%(lineno)d - %(levelname)s - %(message)s'
)

EMBEDDING_DIM = 1024
IMDG_DATABASE_NAME = "stowage_plan"
IMDG_COLLECTION_NAME = "imdg"

embedding_model = NVIDIAEmbeddings(
    model="nvidia/nv-embedqa-e5-v5",
)

milvus_client = MilvusClient(uri="http://localhost:19530")

def init_milvus():

    if IMDG_DATABASE_NAME not in milvus_client.list_databases():
        milvus_client.create_database(IMDG_DATABASE_NAME)

    milvus_client.use_database(IMDG_DATABASE_NAME)

    if milvus_client.has_collection(IMDG_COLLECTION_NAME):
        milvus_client.drop_collection(IMDG_COLLECTION_NAME)

    if not milvus_client.has_collection(IMDG_COLLECTION_NAME):
        schema = MilvusClient.create_schema()
        schema.add_field(
            field_name="id",
            is_primary=True,
            auto_id=True,
            datatype=DataType.INT64
        )
        schema.add_field(field_name="embedding", datatype=DataType.FLOAT_VECTOR, dim=EMBEDDING_DIM)
        schema.add_field(field_name="chunk", datatype=DataType.VARCHAR, max_length=2000)
        schema.add_field(field_name="file_name", datatype=DataType.VARCHAR, max_length=1024)
        schema.add_field(field_name="metadata", datatype=DataType.JSON, nullable=True)
        milvus_client.create_collection(IMDG_COLLECTION_NAME, schema=schema)

def insert_data(chunks):
    records = []

    for file_data in tqdm(chunks, desc="Preparing chunks"):
        file_name = file_data.get("file_name", "")
        for chunk_data in file_data.get("chunks", []):
            chunk_text = chunk_data.get("chunk", "")
            if not chunk_text:
                continue

            records.append(
                {
                    "chunk": chunk_text,
                    "file_name": file_name or chunk_data.get("file_name", ""),
                    "metadata": chunk_data.get("metadata", {}),
                }
            )

    if not records:
        print("No chunks to insert.")
        return

    vectors = embedding_model.embed_documents([record["chunk"] for record in records])

    data = []
    for record, vector in zip(records, vectors):
        data.append(
            {
                "embedding": vector,
                "chunk": record["chunk"],
                "file_name": record["file_name"],
                "metadata": record["metadata"],
            }
        )

    milvus_client.insert(
        collection_name=IMDG_COLLECTION_NAME,
        data=data
    )
    print(f"Inserted {len(data)} chunks.")

if __name__ == "__main__":
    init_milvus()
    chunks = chunk_md()
    insert_data(chunks)
