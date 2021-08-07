package org.enki;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import java.awt.geom.Point2D;
import java.util.function.Function;
import static tech.units.indriya.unit.Units.RADIAN;

public class PolarCoordinate {

    public final double r;
    public final double theta; // angle in radians

    /**
     * Create new PolarCoordinate.
     *
     * @param r     the radius of the point
     * @param theta the angle of the point
     */
    public PolarCoordinate(final double r, final Quantity<Angle> theta) {
        this.r = r;
        this.theta = theta.to(RADIAN).getValue().doubleValue();
    }

    /**
     * Convert a PolarCoordinate to a Cartesian coordinate in Point2D.Double.
     *
     * @return a Point2D.Double
     */
    public final Point2D.Double toCartesian() {
        return toCartesian(Function.identity());
    }

    /**
     * Convert a PolarCoordinate to a Cartesian coordinate in Point2D.Double using a transformation for the angle.
     *
     * @param thetaTransformer the Function to apply to the angle
     * @return a Point2D.Double
     */
    public final Point2D.Double toCartesian(final Function<Double, Double> thetaTransformer) {
        final double rotatedTheta = thetaTransformer.apply(theta);
        final double x = r * Math.cos(rotatedTheta);
        final double y = r * Math.sin(rotatedTheta);
        return new Point2D.Double(x, y);
    }

    @Override
    public String toString() {
        return "PolarCoordinate{r=" + r + ", theta=" + theta + '}';
    }

}