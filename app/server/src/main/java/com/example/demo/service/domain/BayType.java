package com.example.demo.service.domain;

public class BayType {

    public static BayTypeEnum fromCode(String code) {
        for (BayTypeEnum type : BayTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid BayType code: " + code);
    }

    public static final String REGEX_PATTERN = "^(D|H)$";
    public static final String DECK = "D";
    public static final String HOLD = "H";

    private BayType() {
    }

    public enum BayTypeEnum {
        DECK(BayType.DECK),
        HOLD(BayType.HOLD);

        private final String code;

        BayTypeEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }


}
