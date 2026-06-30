import unittest

from regex_utils import (
    extract_compatibility_group,
    extract_hazard_class,
    extract_dg_division,
    extract_sg_codes,
    extract_sgg_codes,
    extract_un_numbers,
)


class TestRegexUtils(unittest.TestCase):
    def test_extract_un_numbers_patterns(self):
        samples = [
            "UN 1234",
            "UN1234",
            "un num 1234",
            "un no. 1234",
            "un no 1234",
            "un number 1234",
            "unno 1234",
            "text UN1234 tail",
        ]
        for text in samples:
            self.assertEqual(extract_un_numbers(text), ["1234"])

    def test_extract_sg_codes_patterns(self):
        self.assertEqual(extract_sg_codes("SG11"), ["SG11"])
        self.assertEqual(extract_sg_codes("SG 1"), ["SG1"])
        self.assertEqual(extract_sg_codes("segregation code 12"), ["SG12"])
        self.assertEqual(extract_sg_codes("text SGG1 should not match"), [])
        self.assertEqual(
            extract_sg_codes("use sg 2 and segregation code 2"),
            ["SG2"],
        )

    def test_extract_sgg_codes_patterns(self):
        self.assertEqual(extract_sgg_codes("aaaa SGG1 bbbb SGG12"), ["SGG1", "SGG12"])
        self.assertEqual(extract_sgg_codes("SGG11"), ["SGG11"])
        self.assertEqual(extract_sgg_codes("SGG 1"), ["SGG1"])
        self.assertEqual(extract_sgg_codes("segregation group code 12"), ["SGG12"])
        self.assertEqual(extract_sgg_codes("segregation grp code 12"), ["SGG12"])
        self.assertEqual(extract_sgg_codes("segregation gp code 12"), ["SGG12"])
        self.assertEqual(extract_sgg_codes("segregation gp 12"), ["SGG12"])
        self.assertEqual(extract_sgg_codes("segregation grp 12"), ["SGG12"])
        self.assertEqual(extract_sgg_codes("segregation group 12"), ["SGG12"])

    def test_extract_dg_class_patterns(self):
        self.assertEqual(extract_hazard_class("aaaa class 1 bbbb"), ["1"])
        self.assertEqual(extract_hazard_class("dg class 1"), ["1"])
        self.assertEqual(extract_hazard_class("hazard class 1"), ["1"])
        self.assertEqual(extract_hazard_class("imdg class 12"), ["12"])
        self.assertEqual(extract_hazard_class("hazard class 12"), ["12"])
        self.assertEqual(extract_hazard_class("dg 1"), ["1"])
        self.assertEqual(extract_hazard_class("dg cls 12"), ["12"])
        self.assertEqual(extract_hazard_class("dg cl 12"), ["12"])

    def test_extract_dg_division_patterns(self):
        self.assertEqual(extract_dg_division("aaaa class 1.1 bbbb"), ["1.1"])
        self.assertEqual(extract_dg_division("dg class 1.1"), ["1.1"])
        self.assertEqual(extract_dg_division("hazard division 1.1"), ["1.1"])
        self.assertEqual(extract_dg_division("division 6.1"), ["6.1"])
        self.assertEqual(extract_dg_division("dg divisions 2.1, 3.1"), ["2.1", "3.1"])
        self.assertEqual(extract_dg_division("dg div 1.3"), ["1.3"])
        self.assertEqual(extract_dg_division("subclass 12"), ["1.2"])
        self.assertEqual(extract_dg_division("subclasses 12"), ["1.2"])

    def test_extract_dg_class_does_not_capture_division(self):
        self.assertEqual(extract_hazard_class("dg class 1.1"), [])

    def test_extract_compatibility_group_patterns(self):
        self.assertEqual(extract_compatibility_group("aaaa Compatibility group A bbbb"), ["A"])
        self.assertEqual(extract_compatibility_group("compatibility group A"), ["A"])
        self.assertEqual(extract_compatibility_group("CGA"), ["A"])
        self.assertEqual(extract_compatibility_group("CG A"), ["A"])
        self.assertEqual(extract_compatibility_group("compatibility grp A"), ["A"])
        self.assertEqual(extract_compatibility_group("comp group A"), ["A"])
        self.assertEqual(extract_compatibility_group("comp grp A"), ["A"])
        self.assertEqual(extract_compatibility_group("comp gp A"), ["A"])
        self.assertEqual(extract_compatibility_group("compatibility groups S"), ["S"])
        self.assertEqual(extract_compatibility_group("comp groups T"), [])


if __name__ == "__main__":
    unittest.main()

