import os
import re
from pathlib import Path


def process_md_files(folder_path):
    if not folder_path.exists():
        print(f"not exist'{folder_path}'")
        return

    rules = [
        # ==== PHASE 1: Remove Large Blocks & Specific Lines (Execute first to eliminate noise) ====
        (re.compile(r'^ *\|.*\| *\r?\n *\| *[:\- \t|]+ *\| *\r?\n(?: *\|.*\| *\r?\n?)*', re.MULTILINE), ''),
        # Remove Markdown tables
        (re.compile(r'^.*\b[A-Z]\s?[a-z0-9]\s*=.*$\r?\n?', re.MULTILINE), ''),
        # Remove formula/variable definition lines
        (re.compile(r'∑.*?(?=\r?\n|$)'), ''),  # Remove mathematical summation lines

        # ==== PHASE 2: Remove Document Markers & Isolated Characters ====
        (re.compile(r'\bMSC \d+/\d+/Add\.\d+\b'), ''),  # Remove MSC document numbers
        (re.compile(r'\bAnnex \d+,\s*page\s*\d+\b\n?'), ''),  # Remove Annex and page markers
        (re.compile(r'(?<=^)[1-7]\r?\n(?=\r?\n)', re.MULTILINE), ''),  # Remove isolated numbers 1-7
        (re.compile(r'(?<=^)[a-zA-Z]\r?\n(?=\r?\n)', re.MULTILINE), ''),  # Remove isolated single letters
        (re.compile(r'(?<=^)[a-zA-Z]\t[a-zA-Z]\r?\n?', re.MULTILINE), ''),  # Remove tab-separated double letters
        (re.compile(r'\b[a-zA-Z]\s[a-zA-Z]\r?\n'), ''),  # Remove double letters before a newline

        # ==== PHASE 3: Style Normalization (Must run before paragraph matching to avoid asterisk interference) ====
        (re.compile(r'\*(.*?)\*'), r'\1'),  # Unwraps bold text (e.g., *1.1* becomes 1.1)
        (re.compile(r'(?<!^)\*'), ''),  # Remove remaining trailing asterisks
        (re.compile(r'^#{7,}(?=\s)', re.MULTILINE), '######'),  # Normalize markdown headings with 7+ hashes to 6

        # ==== PHASE 4: Paragraph Number Normalization (Strict order from specific to general) ====
        # Process bullet-list clauses (e.g., "- 1.1.") first to prevent general rules from misidentifying them
        (re.compile(r'(?<=^-\s)\b(\d+\.\d+(?:\.\d+)*\.)', re.MULTILINE), r'paragraph \1'),
        # Process general clauses (safeguarded against already modified or table-scoped items)
        (re.compile(r'(?<!Table\s)(?<!paragraph\s)\b(\d+\.\d+(?:\.\d+)+)\b'), r'paragraph \1'),

        # ==== PHASE 5: Clean Up Residual Whitespace & Newlines (Execute last for final polish) ====
        (re.compile(r' {2,}'), ' '),  # Merge consecutive spaces
        (re.compile(r'[ \t]{2,}'), ' '),  # Merge consecutive tabs and spaces
        (re.compile(r'(\r?\n){3,}'), r'\1\1'),  # Compress 3 or more consecutive newlines down to 2
        # (re.compile(r"(?<=\d\.)paragraph\s*"))
    ]

    for root, dirs, files in os.walk(folder_path):
        for file in files:
            if file.endswith('.md'):
                file_path = os.path.join(root, file)

                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()

                    original_content = content
                    for pattern, replacement in rules:
                        content = pattern.sub(replacement, content)

                    if content != original_content:
                        with open(file_path, 'w', encoding='utf-8') as f:
                            f.write(content)
                        print(f"updated: {file_path}")

                except Exception as e:
                    print(f"{file_path} failed: {e}")


if __name__ == "__main__":
    target_folder = Path("./md/from-pdf")

    print("start")
    process_md_files(target_folder)
    print("complete")
