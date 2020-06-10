package org.openchs.web.request.search;

import java.time.LocalDate;

public class MinMax {

    private LocalDate minValue;

    private LocalDate maxValue;

    private Integer minValueInt;

    private Integer maxValueInt;

    public LocalDate getMinValue() {
        return minValue;
    }

    public void setMinValue(LocalDate minValue) {
        this.minValue = minValue;
    }

    public LocalDate getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(LocalDate maxValue) {
        this.maxValue = maxValue;
    }
}
