def build_answering_instruction_prompt(user_input: str) -> str:
	prompt = "##### USER QUESTION #####\n"
	prompt += f"User question: {user_input}\n\n"
	prompt += "##### YOUR ANSWER #####\n"
	prompt += (f"1. Before answering, think it step-by-step\n"
			   f"  - Extract EVERY dangerous goods mentioned in the question \n"
			   f"  - Compare each user-given description against the Dangerous Goods reference data.\n"
			   f"  - If there are contradictions between user's input and the data, trust the data ONLY, your FIRST sentence MUST be a correction.\n"
			   f"2. Provide your reasoning first, but do not list out your steps, give the answer naturally.	\n"
			   f"3. DO NOT use \"The user\", use \"you\" to replace \"user\".\n"
			   f"4. Proceed to provide correction and answer the question.\n"
			   f"Answer: \n"
	)
	return prompt


def build_unknown_data_prompt(data_name, stringified_unknown_data: list[str]):
	if not stringified_unknown_data:
		return ""
	unknown_un_no_str = ",".join(stringified_unknown_data)
	prompt = ""
	prompt += f"##### ${data_name} #####\n"
	prompt += f"User asked about these ${data_name}, but they are unknown: {unknown_un_no_str}\n\n"
	return prompt

def convert_dg_data_to_str(dg_data: dict) -> str:
	if not dg_data:
		return "Dangerous Goods: no data found."

	lines: list[str] = ["##### Dangerous Goods #####"]

	hazard_class = dg_data.get("class", None)
	hazard_class_substance = dg_data.get("classSubstance", None)
	if hazard_class:
		lines.append(f"- Class: {hazard_class}")
	if hazard_class_substance:
		lines.append(f"- Class substance: {hazard_class_substance}")

	division = dg_data.get("division", None)
	hazard_division_substance = dg_data.get("divisionSubstance", None)
	if division:
		lines.append(f"- Division: {division}")
	if hazard_division_substance:
		lines.append(f"- Division substance: {hazard_division_substance}")

	un_no = dg_data.get("unNo")
	if un_no not in (None, ""):
		lines.append(f"- UN Number: {un_no}")

	psn = dg_data.get("psn")
	if psn not in (None, ""):
		lines.append(f"- Proper Shipping Name: {psn}")

	subsidiary_hazard = dg_data.get("subsidiaryHazard")
	if subsidiary_hazard not in (None, "", [], {}):
		lines.append(f"- Subsidiary Hazard: {_stringify_value(subsidiary_hazard)}")

	segregation = dg_data.get("segregation")
	if isinstance(segregation, list) and segregation:
		lines.append("- Segregation:")
		for item in segregation:
			if isinstance(item, dict):
				code = item.get("code", None)
				description = item.get("description", None)
				if code and description:
					lines.append(f"  - {code}: {description}")
				elif code:
					lines.append(f"  - {code}")
				elif description:
					lines.append(f"  - {description}")
	else:
		lines.append("- Segregation: This dangerous goods has NO segregation codes assigned.")

	segregation_group = dg_data.get("segregationGroup")
	if isinstance(segregation_group, list) and segregation_group:
		lines.append("- Segregation Group:")
		for item in segregation_group:
			if isinstance(item, dict):
				code = item.get("code", None)
				description = item.get("description", None)
				group = item.get("group", None)
				details = []
				if code:
					details.append(str(code))
				if description:
					details.append(str(description))
				if group:
					details.append(f"group {group}")
				if details:
					lines.append(f"  - {', '.join(details)}")
	else:
		lines.append("- Segregation group: This dangerous goods has NO segregation group codes assigned.")

	return "\n".join(lines)


def convert_sg_data_to_str(sg_data: dict) -> str:
	if not sg_data:
		return "Segregation Code: no data found."

	lines: list[str] = ["##### Segregation Code #####"]

	code = sg_data.get("code")
	if code not in (None, ""):
		lines.append(f"- Code: {code}")

	description = sg_data.get("description")
	if description not in (None, ""):
		lines.append(f"- Description: {description}")

	allowed_with = sg_data.get("allowedWith")
	if isinstance(allowed_with, list) and allowed_with:
		lines.append(f"- Allowed with group: {', '.join(str(item) for item in allowed_with)}")
	elif allowed_with not in (None, "", [], {}):
		lines.append(f"- Allowed with group: {_stringify_value(allowed_with)}")

	return "\n".join(lines)


def convert_sgg_data_to_str(sgd_data: dict) -> str:
	if not sgd_data:
		return "Segregation Group: no data found."

	lines: list[str] = ["##### Segregation Group #####"]

	code = sgd_data.get("code")
	if code not in (None, ""):
		lines.append(f"- Code: {code}")

	description = sgd_data.get("description")
	if description not in (None, ""):
		lines.append(f"- Description: {description}")

	group = sgd_data.get("group")
	if group not in (None, ""):
		lines.append(f"- Group: {group}")

	return "\n".join(lines)


def is_number(s):
	try:
		float(s)
		return True
	except ValueError:
		return False


def convert_segregation_rules_to_str(segregation_rules: dict) -> str:
	if not segregation_rules:
		return "Segregation Rules: no data found."

	lines: list[str] = ["##### Segregation Rules #####"]

	division = segregation_rules.get("division")
	if division in (None, ""):
		lines.append("- No division provided.")
		return "\n".join(lines)

	rules = segregation_rules.get("rules")
	if not isinstance(rules, dict) or not rules:
		lines.append(f"- No rules found for class (division) {division}.")
		return "\n".join(lines)

	lines.append(f"- Source class (division): {division}")
	for target_division, requirement in rules.items():
		if requirement in (None, ""):
			continue

		target_label = str(target_division)
		requirement_text = str(requirement)
		if is_number(target_label):
			lines.append(
				f"- class (division) {division} shall be {requirement_text} class (division) {target_label}"
			)
		elif target_label in ("X", "*"):
			lines.append(f"- {requirement_text}")
		else:
			lines.append(f"- {target_label}: {requirement_text}")

	if len(lines) == 2:
		lines.append("- No populated rule entries.")
	return "\n".join(lines)


def convert_comp_grp_to_str(comp_grp_data: dict) -> str:
	if not comp_grp_data:
		return "Compatibility Group: no data found."

	lines: list[str] = ["##### Compatibility Group #####"]

	code = comp_grp_data.get("code")
	if code not in (None, ""):
		lines.append(f"- Code: {code}")

	allowed_with = comp_grp_data.get("allowedWith")
	if isinstance(allowed_with, list) and allowed_with:
		lines.append(f"- Allowed with group: {', '.join(str(item) for item in allowed_with)}")
	elif allowed_with not in (None, "", [], {}):
		lines.append(f"- Allowed with group: {_stringify_value(allowed_with)}")

	return "\n".join(lines)


def convert_hazard_class_def_to_str(hazard_class_data: dict) -> str:
	if not hazard_class_data:
		return ""

	lines: list[str] = ["##### Hazard class definition #####"]
	hazard_class = hazard_class_data.get("hazardClass", None)

	if hazard_class:
		lines.append(f"- Hazard class: {hazard_class}")
		substance = hazard_class_data.get("substance", None)
		if substance:
			lines.append(f"- substance: {substance}")
		return "\n".join(lines)
	else:
		return ""


def convert_hazard_division_def_to_str(hazard_division_data: dict) -> str:
	if not hazard_division_data:
		return ""

	lines: list[str] = ["##### Hazard division definition #####"]
	hazard_class = hazard_division_data.get("hazardClass", None)

	if hazard_class:
		lines.append(f"- Hazard class: {hazard_class}")
		division = hazard_division_data.get("division", None)
		if division:
			lines.append(f"- Hazard division: {division}")
		substance = hazard_division_data.get("substance", None)
		if substance:
			lines.append(f"- substance: {substance}")
		return "\n".join(lines)
	else:
		return ""


def _stringify_value(value) -> str:
	if isinstance(value, list):
		if not value:
			return "[]"
		if all(not isinstance(item, (dict, list)) for item in value):
			return ", ".join(str(item) for item in value)
		return "; ".join(_stringify_value(item) for item in value)

	if isinstance(value, dict):
		if not value:
			return "{}"
		parts = [f"{k}={_stringify_value(v)}" for k, v in sorted(value.items()) if v not in (None, "", [], {})]
		return ", ".join(parts) if parts else "{}"

	return str(value)

