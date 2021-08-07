package org.enki.geo;

import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static systems.uom.common.USCustomary.DEGREE_ANGLE;
import static systems.uom.common.USCustomary.MILE;
import static systems.uom.common.USCustomary.NAUTICAL_MILE;
import static tech.units.indriya.unit.Units.METRE;

public class LatLong {

    public final double latitude; // The latitude in degrees.
    public final double longitude; // The longitude in degrees.

    /**
     * Construct a LatLong using a supplied latitude and longitude in degrees.
     *
     * @param latitude  latitude in degrees
     * @param longitude longitude in degrees
     */
    public LatLong(final double latitude, final double longitude) {
        if (abs(longitude) > 180) {
            throw new IllegalArgumentException("invalid longitude " + longitude);
        }

        if (abs(latitude) > 90) {
            throw new IllegalArgumentException("invalid latitude " + latitude);
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Construct a LatLong using a supplied LatLong.
     *
     * @param other the existing LatLong object
     */
    public LatLong(final LatLong other) {
        this(other.latitude, other.longitude);
    }

    /**
     * Calculate distance squared between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return distance in meters
     */
    public Quantity<Length> distanceSquared(final LatLong b) {
        // Radius of the Earth, according to WGS-84 (https://apps.dtic.mil/sti/pdfs/ADA280358.pdf).
        final Quantity<Length> radiusOfEarth = Quantities.getQuantity(6378137, METRE);

        final double latDistance = toRadians(b.latitude - latitude);
        final double lonDistance = toRadians(b.longitude - longitude);
        final double a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(toRadians(latitude)) * cos(toRadians(b.latitude)) * sin(lonDistance / 2) * sin(lonDistance / 2);
        final double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return Quantities.getQuantity(Math.pow(radiusOfEarth.getValue().doubleValue() * c, 2), METRE);
    }

    /**
     * Compute distance in meters between this coordinate and another.
     *
     * @param b the other LatLong coordinate
     * @return the distance in meters
     */
    public Quantity<Length> distance(final LatLong b) {
        return Quantities.getQuantity(sqrt(distanceSquared(b).getValue().doubleValue()), METRE);
    }

    /**
     * Return a heading in degrees from this location to a specified coordinate.
     *
     * @param b the coordinate
     * @return the heading in degrees
     */
    public Quantity<Angle> heading(final LatLong b) {
        final double Δlong = toRadians(b.longitude - longitude);
        final double lat1 = toRadians(latitude);
        final double lat2 = toRadians(b.latitude);
        final double θ = atan2(sin(Δlong) * cos(lat2), cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(Δlong));
        final double headingInDegrees = toDegrees(θ);
        return Quantities.getQuantity(headingInDegrees < 0 ? 360 + headingInDegrees : headingInDegrees, DEGREE_ANGLE);
    }

    @Override
    public String toString() {
        return "LatLong{latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    public static void main(final String[] args) {
        final LatLong hq = new LatLong(27.479587, -80.336276);
        final LatLong yard = new LatLong(27.460798, -80.328079);
        final Quantity<Length> distanceMeters = hq.distance(yard);
        final Quantity<Length> distanceMiles = distanceMeters.to(MILE);
        System.err.println("distanceMeters=" + distanceMeters);
        System.err.println("distanceMiles=" + distanceMiles);

        final LatLong equator1 = new LatLong(0, 0);
        final LatLong equator2 = new LatLong(1.0 / 60.0, 0);
        final Quantity<Length> distanceMetersEquator = equator1.distance(equator2);
        System.err.println("distanceMetersEquator=" + distanceMetersEquator);
        System.err.println("distanceNauticalMilesEquator=" + distanceMetersEquator.to(NAUTICAL_MILE));
    }

    static {
        // FIXME: I am not convinced this is right. Why do I need to round? Remove round() and fix.
        assert round(new LatLong(27, -80).heading(new LatLong(26, -80)).getValue().doubleValue()) == 180;
        assert round(new LatLong(26, -80).heading(new LatLong(27, -80)).getValue().doubleValue()) == 0;
        assert round(new LatLong(26, -80).heading(new LatLong(26, -81)).getValue().doubleValue()) == 270;
        assert round(new LatLong(26, -81).heading(new LatLong(26, -80)).getValue().doubleValue()) == 90;
    }

}
