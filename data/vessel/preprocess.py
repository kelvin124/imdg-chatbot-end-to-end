from pathlib import Path

import json

def parse_vessel_txt(file_path):
    vessel_data = {
        "shipInfo": {},
        "hydroPoints": [],
        "tanks": [],
        "bays": []
    }

    current_bay = None
    current_stack = None
    current_tank = None
    current_deck_type = None
    # 用於追蹤當前「活動中」的標籤資訊
    active_level = 0
    active_label = ""
    active_keys = []

    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            # 1. 處理標頭行 (例如 # Ship, ## Bay, ### Stack)
            if line.startswith('#'):
                # 計算 # 的數量
                active_level = line.count('#', 0, line.find(' '))
                header_part = line.lstrip('#').strip()

                if ':' in header_part:
                    active_label, keys_str = header_part.split(':', 1)
                    active_label = active_label.strip()
                    active_keys = keys_str.strip().split()
                else:
                    active_label = header_part.strip()
                    active_keys = []
                continue

            # 2. 處理數據行 (Data lines)
            values = line.split()
            # 轉換數據型態
            processed_values = []
            for v in values:
                try:
                    processed_values.append(float(v) if '.' in v else int(v))
                except ValueError:
                    processed_values.append(v)

            # 將數據與對應的 Keys 結合
            data_dict = dict(zip(active_keys, processed_values))

            # --- 根據當前的 active_label 決定數據去向 ---
            if active_label == "Ship":
                vessel_data["shipInfo"] = data_dict

            elif active_label == "HydroPoints":
                vessel_data["hydroPoints"].append(data_dict)

            elif active_label == "Tanks":  # 處理 Tank 頂層
                current_tank = data_dict
                current_tank["bayCoverages"] = []
                vessel_data["tanks"].append(current_tank)

            elif active_label == "BayCoverage":  # 處理 Tank 的覆蓋範圍
                if current_tank is not None:
                    current_tank["bayCoverages"].append(data_dict)

            elif active_label == "Bay":
                current_bay = data_dict
                current_bay["rows"] = []
                vessel_data["bays"].append(current_bay)

            elif active_label == "BuoyancyPoints":
                if current_bay is not None:
                    # 浮力點通常是一組數值，代表不同吃水下的浮力常數
                    if "buoyancyPoints" not in current_bay:
                        current_bay["buoyancyPoints"] = []
                    current_bay["buoyancyPoints"].append(processed_values[0])

            elif active_label == "Row":
                if current_bay is not None:
                    current_stack = data_dict
                    current_stack["hold"] = None
                    current_stack["deck"] = None
                    current_bay["rows"].append(current_stack)

            elif active_label == "BelowDeck":
                if current_stack is not None:
                    current_deck_type = "hold"
                    current_stack["hold"] = data_dict
                    current_stack["hold"]["cells"] = []

            elif active_label == "AboveDeck":
                if current_stack is not None:
                    current_deck_type = "deck"
                    current_stack["deck"] = data_dict
                    current_stack["deck"]["cells"] = []

            elif active_label == "Cell":
                if current_stack and current_deck_type:
                    data_dict["isReefer"] = data_dict.pop("reefer") == 1
                    current_stack[current_deck_type]["cells"].append(data_dict)

    return vessel_data

def parse_and_save(formatted_txt, output_path, vessel_size):
    vessel_name = "vessel-" + vessel_size
    vessel_data = parse_vessel_txt(formatted_txt)
    vessel_data["vesselName"] = vessel_name
    all_bays = vessel_data.pop("bays")
    with open(output_path/f"shipInfo_{vessel_size}.json", 'w', encoding='utf-8') as f:
        json.dump(vessel_data, f, indent=4, ensure_ascii=False)
    for bay in all_bays:
        bay["vesselName"] = vessel_name
        with open(output_path/f"bay_{bay["index"]}_{vessel_size}.json", 'w', encoding='utf-8') as f:
            json.dump(bay, f, indent=4, ensure_ascii=False)


def formating(file_path, output_path):
    updated_lines = []
    with open(file_path, 'r') as file:
        lines = file.readlines()
        for line in lines:
            line = line.replace('cap(ton)', 'capTon') \
                .replace('coverage(ratio)', 'coverageRatio') \
                .replace('bay_idx(zero based)', 'bayIdxZeroBased') \
                .replace('vcg_empty', 'vcgEmpty') \
                .replace('vcg_full', 'vcgFull') \
                .replace('tcgTollerance', 'tcgTolerance') \
                .replace('### Stack:', '### Row:') \
                .replace('# Ship: bays stacks', '# Ship: bays rows')
            updated_lines.append(line)

    with open(output_path, 'w') as file:
        file.writelines(updated_lines)


# def output_seed_data(input_vessel_json, vessel_size):
#     with open(input_vessel_json, 'r') as file:
#         data = json.load(file)
#         all_bays = data.pop("bays")
#
#         with open(os.path.join(os.path.dirname(__file__), f'seed/vessel-{vessel_size}/shipInfo_{vessel_size}.json'),
#                   'w') as f:
#             data["vesselName"] = f"vessel-{vessel_size}"
#             json.dump(data, f, indent=4, ensure_ascii=False)
#             for bay in all_bays:
#                 json.dump(bay, indent=4, ensure_ascii=False)


if __name__ == "__main__":

    try:

        source_folder = Path('./source')
        temp_folder = Path('./temp')

        source_file_path_s = source_folder/"vessel_S.txt"
        source_file_path_m = source_folder/"vessel_M.txt"
        source_file_path_l = source_folder/"vessel_L.txt"

        temp_folder.mkdir(parents=True, exist_ok=True)

        formating(source_file_path_s, temp_folder/"vessel_S.txt")
        formating(source_file_path_m, temp_folder/"vessel_M.txt")
        formating(source_file_path_l, temp_folder/"vessel_L.txt")

        formatted_vessel_txt_path = Path('./temp')
        input_file_path_s = formatted_vessel_txt_path/"vessel_S.txt"
        input_file_path_m = formatted_vessel_txt_path/"vessel_M.txt"
        input_file_path_l = formatted_vessel_txt_path/"vessel_L.txt"

        seed_data_path = Path('./seed')
        seed_data_path.mkdir(parents=True, exist_ok=True)

        path_for_s_vessel = seed_data_path/"vessel-s"
        path_for_m_vessel = seed_data_path/"vessel-m"
        path_for_l_vessel = seed_data_path/"vessel-l"

        path_for_s_vessel.mkdir(parents=True, exist_ok=True)
        path_for_m_vessel.mkdir(parents=True, exist_ok=True)
        path_for_l_vessel.mkdir(parents=True, exist_ok=True)

        parse_and_save(
            formatted_vessel_txt_path/"vessel_S.txt",
            path_for_s_vessel,
            "s"
        )
        parse_and_save(
            formatted_vessel_txt_path/"vessel_M.txt",
            path_for_m_vessel,
            "m"
        )
        parse_and_save(
            formatted_vessel_txt_path/"vessel_L.txt",
            path_for_l_vessel,
            "l"
        )

        print("complete")

    except Exception as e:
        print(f"failed: {e}")
