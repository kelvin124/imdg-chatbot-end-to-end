# IMDG Chatbot — End-to-End

A full-stack dangerous-goods stowage assistant for maritime shipping. This project combines a **Spring Boot backend** (structured maritime data and planning rules), a **Streamlit chat UI** (LLM-powered assistant with guardrails), **data pipelines** (IMDG code documents and vessel profiles), and **Kubernetes manifests** for local or cloud deployment.

## Documentation

Detailed documentation for each component is available in the respective subfolder:

- [**`app/server/README.md`**](app/server/README.md) — IMDG Server architecture, API endpoints, testing, and project structure
- [**`app/ui/README.md`**](app/ui/README.md) — Streamlit chatbot UI, guardrails, RAG pipeline, and configuration
- [**`data/README.md`**](data/README.md) — Data pipelines for IMDG documents and vessel profiles
- [**`k8s/README.md`**](k8s/README.md) — Kubernetes manifests, deployment guide, and folder overview

## ArogCD for building platform & deployment
- ArgoCD is the tool for building the platform & deployment
- Repository: https://github.com/kelvin124/imdg-chatbot-argocd

## Repository Structure

```
├── app/
│   ├── server/          # Spring Boot backend (Java)
│   └── ui/              # Streamlit chatbot frontend (Python)
├── data/
│   ├── imdg/            # IMDG amendment 42-24 data pipeline (PDF → markdown → chunks → Milvus)
│   └── vessel/          # Vessel profile preprocessing pipeline (text → JSON)
├── docker/
│   ├── milvus/          # Milvus vector store Docker resources
│   ├── mongodb/         # MongoDB Docker resources
│   └── ollama/          # Ollama LLM Docker resources
├── experiment/          # Experimental agents and API prototypes
├── k8s/                 # Kubernetes manifests for local (Minikube) deployment
├── note/                # Setup notes and reference documentation
├── oracle-cloud/        # Oracle Cloud container registry notes
├── requirements.txt     # Python dependencies (UI, data pipelines, experiments)
└── .gitignore
```

## Components

### 1. Backend Server (`app/server/`)

A **Spring Boot** application that owns structured maritime data and enforces planning rules for dangerous-goods stowage. Key domains:

| Domain               | Description |
|----------------------|-------------|
| **IMDG**             | Dangerous-goods records, compatibility groups, segregation rules, codes, and hazard definitions |
| **Vessel**           | Vessel profiles and structural data — bays, rows, cells |
| **Container**        | Container records and synthetic container generation for testing |
| **Stowage Plan**     | Creates plans, snapshots vessel data into plans, exposes plan views |
| **Stowage Slot**     | Validates and manages container placement within a stowage plan |
| **Vessel Stability** | Computes CG, KG, and overall stability based on stowage-plan state |
| **Data Operations**  | Imports seed data and generates synthetic container data |

All REST endpoints are prefixed with `/api/v1`. The server uses **MongoDB** for persistence and includes Spring Boot Actuator endpoints for health checks and metrics.

### 2. Chatbot UI (`app/ui/`)

A **Streamlit** application that provides a chat-style interface for dangerous-goods queries. Users can ask about:

- UN number lookups
- Hazard class and division interpretation
- Segregation code and segregation-group guidance
- Compatibility group checks
- Stowage-related rule explanations

**Architecture flow:**

1. User asks a question → validated for safety and relevance (NVIDIA NIM guardrails)
2. Structured entities extracted via regex (deterministic parsing)
3. Backend IMDG API and Milvus vector store queried in parallel
4. Retrieved context assembled into a grounded prompt
5. LLM (8B model) generates the final answer
6. Answer displayed in the Streamlit UI

**Guardrails (NVIDIA NIM):**
- Topic control — keeps conversation within the IMDG stowage-planning domain
- Content safety filtering — rejects harmful, abusive, or off-topic requests

### 3. Data Pipelines (`data/`)

**IMDG data pipeline** — converts the IMDG Code amendment 42-24 PDF into searchable knowledge chunks:

1. `convert_to_md.py` — PDF → markdown
2. `md_cleaning.py` — clean and normalize markdown
3. `chunk_md.py` — split into retrieval-friendly chunks
4. `init_milvus.py` — load chunks into Milvus vector store

**Vessel data pipeline** — converts text-based vessel specifications into structured JSON:

1. Place raw vessel text files in `vessel/source/`
2. Run `vessel/preprocess.py` to normalize and parse
3. Generated JSON written to `vessel/seed/` for downstream use

### 4. Kubernetes Manifests (`k8s/`)

Baseline Kubernetes manifests for deploying the full stack on **Minikube** (reusable on other Kubernetes distributions with minimal changes):

| Directory            | Purpose |
|----------------------|---------|
| `imdg-server/`       | Backend deployment, service, ConfigMap, RBAC |
| `imdg-chatbot-ui/`   | Streamlit UI deployment, service, ConfigMap, RBAC |
| `milvus/`            | Milvus vector store deployment |
| `argocd/`            | Argo CD manifests and values |
| `cert-manager/`      | Certificate manager manifests |
| `grafana/`           | Grafana monitoring deployment |
| `prometheus/`        | Prometheus monitoring configuration |

### 5. Docker (`docker/`) - Local development

Docker configurations for supporting infrastructure:

- `mongodb/` — MongoDB container setup
- `milvus/` — Milvus vector store container setup
- `ollama/` — Ollama LLM container setup

### 6. Experiments (`experiment/`)

Prototype agents and API experiments, including container agents, enquiry agents, and utility modules for prompt engineering and tool integration.

## Monitoring

- **Backend:** Spring Boot Actuator at `/actuator/health` and `/actuator/metrics`
- **Kubernetes:** Prometheus and Grafana dashboards under `k8s/prometheus/` and `k8s/grafana/`