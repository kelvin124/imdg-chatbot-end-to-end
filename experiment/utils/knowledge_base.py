def get_vessel_stability_knowledge(file_path):
    return ("1. CG (Center of Gravity)\n"
"- CG is the exact point where the total weight of the vessel (and all its contents) is concentrated\n"
"- Low CG: Improves stability. A lower CG provides a larger righting arm (the distance between gravity and buoyancy), helping the vessel quickly return to an upright position after rolling\n"
"- High CG: Reduces stability. If the CG is too high, the vessel becomes top-heavy, making it sluggish to right itself or highly prone to capsizing.\n"
"2. KG (Vertical Center of Gravity)\n"
"- KG is the vertical distance of the Center of Gravity, measured upward from the Keel (K)\n"
"- When KG is low, The CG is deep in the hull. This creates a stiff vessel that rights itself quickly, though it may roll violently in heavy weather\n"
"- When KG is high, The CG is near the deck. This creates a tender vessel with a dangerously small margin of stability. Even a slight tilt can cause it to capsize\n"
"- Adding Weight Low (e.g., loading ballast water): Lowers the KG, improving stability\n"
"- Adding Weight High (e.g., stacking containers on deck): Raises the KG, decreasing stability\n\n"
            "")