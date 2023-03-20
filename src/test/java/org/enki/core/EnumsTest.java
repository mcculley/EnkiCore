package org.enki.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumsTest {

    private enum Suit {
        club,
        heart,
        spade,
        diamond
    }

    @Test
    public void testEnums() {
        assertEquals(Suit.heart, Enums.nextModular(Suit.club));
        assertEquals(Suit.club, Enums.nextModular(Suit.diamond));
        assertEquals(Suit.club, Enums.prevModular(Suit.heart));
        assertEquals(Suit.diamond, Enums.prevModular(Suit.club));
    }

}
