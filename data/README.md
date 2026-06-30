# Data

This folder contains the raw and processed data used by the IMDG chatbot, including IMDG amendment 42-24 documents and vessel-related datasets.

## Structure

1. imdg/: scripts and data for converting IMDG PDFs into markdown, cleaning the content, chunking it for retrieval, and preparing it for vector search.
   - source: https://www.cepa.be/wp-content/uploads/IMDG_Code-amdt_42_24.pdf
2. vessel/: scripts for preprocessing vessel description text into structured JSON data used by the stowage planning workflow.
   - source: https://github.com/maritimelab/Stowage-Planning-Benckmark/tree/master/vessel_data

## IMDG data pipeline

The IMDG pipeline turns source PDFs into searchable knowledge chunks:

1. Convert PDF documents to markdown with imdg/convert_to_md.py.
2. Clean the generated markdown with imdg/md_cleaning.py.
3. Split the content into retrieval-friendly chunks with imdg/chunk_md.py.
4. Load the resulting chunks into Milvus using imdg/init_milvus.py.

Typical folders used by this pipeline:

- imdg/pdf/: source PDFs
- imdg/md/: converted markdown files
- imdg/md/chunks/: chunked retrieval data
- imdg/md/manual-updated/: curated markdown input for chunking

## Vessel data pipeline

The vessel pipeline converts text-based vessel specifications into JSON data:

1. Place input vessel text files under vessel/source/.
2. Run vessel/preprocess.py to normalize and parse the data.
3. Generated JSON files are written to vessel/seed/ for downstream use.

Typical folders used by this pipeline:

- vessel/source/: raw vessel text inputs
- vessel/temp/: formatted intermediate files
- vessel/seed/: generated JSON seed data
