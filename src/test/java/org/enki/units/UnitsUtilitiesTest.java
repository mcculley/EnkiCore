package org.enki.units;

import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.METRE_PER_SECOND;

public class UnitsUtilitiesTest {

    @Test
    public void test() {
        final Quantity<Speed> c = Quantities.getQuantity(299792458, METRE_PER_SECOND);
        final Quantity<Length> yard = Quantities.getQuantity(0.9144, METRE);
        final Quantity<Temperature> bodyTemp = Quantities.getQuantity(37, CELSIUS);
        assertTrue(UnitsUtilities.isSpeed(c));
        assertFalse(UnitsUtilities.isLength(c));
        assertFalse(UnitsUtilities.isLength(bodyTemp));
        assertTrue(UnitsUtilities.isLength(yard));
        assertFalse(UnitsUtilities.isSpeed(yard));
        assertFalse(UnitsUtilities.isSpeed(null));
        assertFalse(UnitsUtilities.isSpeed(bodyTemp));
        assertFalse(UnitsUtilities.isLength(null));
    }

}
