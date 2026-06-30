from pathlib import Path
from docling.document_converter import DocumentConverter
from tqdm import tqdm

converter = DocumentConverter()
folders = ["chapter1", "chapter2", "chapter3", "chapter4", "chapter5", "chapter6", "chapter7",  "misc"]

for folder in tqdm(folders):
        for file_path in Path(f"./pdf/{folder}").iterdir():
                if file_path.is_file():
                        result = converter.convert(Path(file_path))
                        markdown_content = result.document.export_to_markdown()

                        output_dir = Path(f"./md")
                        output_dir.mkdir(parents=True, exist_ok=True)

                        output_file_name = f"{file_path.name.replace(".pdf","")}.md"
                        output_path = Path(f"./{output_dir.name}/{output_file_name}")
                        output_path.write_text(markdown_content, encoding="utf-8")
