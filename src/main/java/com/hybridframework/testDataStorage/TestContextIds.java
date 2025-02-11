package com.hybridframework.testDataStorage;

public enum TestContextIds {
    BOOKING_TEST_ID_ONE("BOOKING_TEST_ID_ONE"),
    BOOKING_TEST_ID_TWO("BOOKING_TEST_ID_TWO"),
    BOOKING_TEST_ID_THREE("BOOKING_TEST_ID_THREE");

    private final String value;

    TestContextIds(String value) {
        this.value = value;
    }

    /**
     * Retrieves the string value of the test ID.
     *
     * @return the test ID as a string
     */
    public String getTestId() {
        return value;
    }
}
