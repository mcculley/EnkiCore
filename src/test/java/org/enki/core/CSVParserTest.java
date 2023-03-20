package org.enki.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVParserTest {

    public static class DataLine {

        public final String state;
        public final String type;
        public final int observedNumber;
        public final LocalDate weekEndingDate;
        public final int averageExpectedCount;
        public final int excessEstimate;

        public DataLine(@CSVParser.Column("State") String state,
                        @CSVParser.Column("Type") String type,
                        @CSVParser.Column("Observed Number") int observedNumber,
                        @CSVParser.Column("Week Ending Date") LocalDate weekEndingDate,
                        @CSVParser.Column("Average Expected Count") int averageExpectedCount,
                        @CSVParser.Column("Excess Estimate") int excessEstimate) {
            this.state = state;
            this.type = type;
            this.observedNumber = observedNumber;
            this.weekEndingDate = weekEndingDate;
            this.averageExpectedCount = averageExpectedCount;
            this.excessEstimate = excessEstimate;
        }

    }

    @Test
    public void testCSVParser() {
        final String header = "State,Type,Observed Number,Week Ending Date,Average Expected Count,Excess Estimate";
        final String dataLine1 = "Florida,purple,5,2022-05-20,5,0";

        final CSVParser<DataLine> p = new CSVParser.Builder<>(DataLine.class, header.split(",")).build();
        final DataLine v = p.apply(dataLine1.split(","));
        assertEquals("Florida",v.state);
        assertEquals("purple",v.type);
        assertEquals(5,v.observedNumber);
        assertEquals(LocalDate.parse("2022-05-20"),v.weekEndingDate);
        assertEquals(5,v.averageExpectedCount);
        assertEquals(0,v.excessEstimate);
    }

}
