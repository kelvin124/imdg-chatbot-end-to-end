import re
from typing import List, Tuple


def extract_un_numbers(text: str) -> list[str]:
    if not text or not isinstance(text, str):
        return []
    pattern = re.compile(
        r'(?i)\bun(?:\s*(?:number|num\.?|no\.?|nr\.?|#)|no)?\s*[-:.]?\s*(\d{4})\b'
    )
    results = pattern.findall(text)
    return list(set(results))

def extract_sg_codes(text: str) -> List[str]:
    if not text or not isinstance(text, str):
        return []
    pattern = re.compile(
        r'(?i)\b(?:SG(?!G)\s*[-.:]?\s*(\d{1,2})|segregation\s+code\s*[-.:]?\s*(\d{1,2}))\b'
    )
    full_codes: list[str] = []
    for match in pattern.finditer(text):
        number = match.group(1) or match.group(2)
        if number is not None:
            full_codes.append(f"SG{number}")
    return list(dict.fromkeys(full_codes))

def extract_sgg_codes(text: str) -> List[str]:
    if not text or not isinstance(text, str):
        return []
    pattern = re.compile(
        r'(?i)\b(?:SGG\s*[-.:]?\s*(\d{1,2})|segregation\s+(?:group|grp|gp)\s*(?:code\s*)?[-.:]?\s*(\d{1,2}))\b'
    )
    full_codes: list[str] = []
    for match in pattern.finditer(text):
        number = match.group(1) or match.group(2)
        if number is not None:
            full_codes.append(f"SGG{number}")
    return list(dict.fromkeys(full_codes))

def extract_hazard_class(text: str) -> List[str]:
    if not text or not isinstance(text, str):
        return []

    pattern = re.compile(
        r'(?i)\b(?:'
        r'(?:imdg|hazard)\s*class|'
        r'dg\s*(?:class|cls|cl)?|'
        r'class'
        r')\s*[-.:]?\s*([1-9]\d?)(?!\.\d)\b'
    )
    results: list[str] = []
    for match in pattern.finditer(text):
        number = match.group(1)
        if number not in results:
            results.append(number)
    return results

def extract_dg_division(text: str) -> List[str]:
    if not text or not isinstance(text, str):
        return []
    division_pattern = re.compile(
        r'(?i)\b(?:'
        r'dg\s*(?:class|division(?:s)?|div)?|'
        r'hazard\s*division(?:s)?|'
        r'division(?:s)?|'
        r'class|'
        r'subclass(?:es)?'
        r')\s*[-.:]?\s*([1-9]\.[1-9](?:\s*,\s*[1-9]\.[1-9])*)'
    )
    subclass_compact_pattern = re.compile(
        r'(?i)\bsubclass(?:es)?\s*[-.:]?\s*([1-9][1-9](?:\s*,\s*[1-9][1-9])*)\b'
    )

    results: list[str] = []

    for match in division_pattern.finditer(text):
        parts = [part.strip() for part in match.group(1).split(",")]
        for part in parts:
            if re.fullmatch(r'[1-9]\.[1-9]', part):
                results.append(part)

    for match in subclass_compact_pattern.finditer(text):
        parts = [part.strip() for part in match.group(1).split(",")]
        for part in parts:
            if re.fullmatch(r'[1-9][1-9]', part):
                results.append(f"{part[0]}.{part[1]}")

    return list(dict.fromkeys(results))


def extract_compatibility_group(text: str) -> List[str]:
    if not text or not isinstance(text, str):
        return []

    pattern = re.compile(
        r'(?i)\b(?:'
        r'cg\s*[-.:]?\s*([A-S])|'
        r'(?:compatibilit(?:y|ies)|comp\.?)\s*(?:group|grp|gp)s?\s+[-.:]?\s*([A-S])'
        r')\b'
    )

    results: list[str] = []
    for match in pattern.finditer(text):
        letter = (match.group(1) or match.group(2)).upper()
        if letter not in results:
            results.append(letter)
    return results
