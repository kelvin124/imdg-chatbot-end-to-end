from pathlib import Path
import re

from langchain_text_splitters import MarkdownHeaderTextSplitter, RecursiveCharacterTextSplitter


CHUNK_SIZE = 1000
CHUNK_OVERLAP = 100
MANUAL_MD_DIR = Path("md/manual-updated")
HEADERS_TO_SPLIT_ON = [
    ("#", "chapter"),
    ("##", "section"),
]


def _get_section_parts(section) -> tuple[str, str]:
    header_lines = []

    chapter = section.metadata.get("chapter", "").strip()
    subsection = section.metadata.get("section", "").strip()
    content = section.page_content.strip()

    if chapter:
        header_lines.append(chapter)
    if subsection:
        header_lines.append(subsection)

    return "\n".join(header_lines).strip(), content


def _split_section(section, base_text_splitter: RecursiveCharacterTextSplitter) -> list[str]:
    header_text, content = _get_section_parts(section)

    if not header_text:
        return base_text_splitter.split_text(content) if len(content) > CHUNK_SIZE else [content]

    if not content:
        return [header_text]

    combined_text = f"{header_text}\n{content}"
    if len(combined_text) <= CHUNK_SIZE:
        return [combined_text]

    content_chunk_size = max(1, CHUNK_SIZE - len(header_text) - 1)
    content_chunk_overlap = min(CHUNK_OVERLAP, max(0, content_chunk_size - 1))
    content_splitter = RecursiveCharacterTextSplitter(
        chunk_size=content_chunk_size,
        chunk_overlap=content_chunk_overlap,
    )

    return [f"{header_text}\n{content_chunk}" for content_chunk in content_splitter.split_text(content)]


def _extract_hazard_metadata(chapter_title: str) -> dict:
    chapter_title = (chapter_title or "").strip()
    chapter_match = re.match(r"^Chapter\s+2\.([1-9])\b", chapter_title, flags=re.IGNORECASE)
    if not chapter_match:
        return {}

    class_match = re.search(r"class\s+(\d+)", chapter_title, flags=re.IGNORECASE)
    hazard_class = int(class_match.group(1)) if class_match else int(chapter_match.group(1))

    explicit_substance_match = re.match(
        r"^Chapter\s+2\.[1-9]\s*-\s*Class\s+\d+\s*-\s*(.+)$",
        chapter_title,
        flags=re.IGNORECASE,
    )
    if explicit_substance_match:
        substance = explicit_substance_match.group(1).strip()
    else:
        fallback_substance_match = re.match(r"^Chapter\s+2\.[1-9]\s*-\s*(.+)$", chapter_title, flags=re.IGNORECASE)
        if fallback_substance_match:
            substance = re.sub(r"\s*\(class\s+\d+\)\s*", " ", fallback_substance_match.group(1), flags=re.IGNORECASE)
            substance = re.sub(r"\s+", " ", substance).strip(" -")
        else:
            substance = ""

    metadata: dict[str, object] = {"hazard_class": hazard_class}
    if substance:
        metadata["substance"] = substance

    return metadata


def chunk_md():
    markdown_splitter = MarkdownHeaderTextSplitter(headers_to_split_on=HEADERS_TO_SPLIT_ON)
    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=CHUNK_SIZE,
        chunk_overlap=CHUNK_OVERLAP,
    )

    all_chunked_files = []
    md_files = sorted(MANUAL_MD_DIR.glob("*.md"))

    for file_index, file_path in enumerate(md_files, start=1):
        md_text = file_path.read_text(encoding="utf-8")
        header_sections = markdown_splitter.split_text(md_text)
        file_chunks = []

        for section_index, section in enumerate(header_sections, start=1):
            section_chunks = _split_section(section, text_splitter)
            if not section_chunks:
                continue

            for chunk_index, chunk in enumerate(section_chunks, start=1):
                chapter_title = section.metadata.get("chapter", "")
                hazard_metadata = _extract_hazard_metadata(chapter_title)
                chunk_metadata = {
                    **dict(section.metadata),
                    **hazard_metadata,
                    "file_name": file_path.name,
                }

                file_chunks.append(
                    {
                        "file_name": file_path.name,
                        "section_index": section_index,
                        "chunk_index": chunk_index,
                        "metadata": chunk_metadata,
                        "chunk": chunk,
                    }
                )

        all_chunked_files.append(
            {
                "file_name": file_path.name,
                "chunks": file_chunks,
            }
        )

        if file_index % 4 == 0:
            print(f"--- {file_path.name}: first 2 chunks ---")
            for preview_index, chunk_data in enumerate(file_chunks[:2], start=1):
                print(f"Chunk {preview_index}:\n{chunk_data['chunk']}\n")

    return all_chunked_files


if __name__ == "__main__":
    chunked_files = chunk_md()
    total_chunks = sum(len(file_data["chunks"]) for file_data in chunked_files)
    print(f"Processed {len(chunked_files)} files into {total_chunks} chunks.")
