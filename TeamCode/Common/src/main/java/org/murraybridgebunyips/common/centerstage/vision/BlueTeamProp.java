package org.murraybridgebunyips.common.centerstage.vision;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;

import org.murraybridgebunyips.bunyipslib.vision.processors.ColourThreshold;
import org.opencv.core.Scalar;

/**
 * Processor for the blue team prop.
 *
 * @author Lucas Bubner, 2024
 */
@Config
public class BlueTeamProp extends ColourThreshold {
    // TODO: Implement bounds

    /**
     * The lower YCrCb bounds for the blue team prop.
     */
    public static Scalar LOWER = new Scalar(0, 190, 0);
    /**
     * The upper YCrCb bounds for the blue team prop.
     */
    public static Scalar UPPER = new Scalar(200, 255, 102);
    /**
     * The minimum contour area percentages for the blue team prop.
     */
    public static double MIN = 5;
    /**
     * The maximum contour area percentages for the blue team prop.
     */
    public static double MAX = 100;

    /**
     * Defines a new colour thresholding processor for a specific colour space, which your
     * lower and upper scalars will be based on.
     */
    public BlueTeamProp() {
        super(ColourSpace.YCrCb);
    }

    @NonNull
    @Override
    public String toString() {
        return "blueteamprop";
    }

    @Override
    public double getContourAreaMinPercent() {
        return MIN;
    }

    @Override
    public double getContourAreaMaxPercent() {
        return MAX;
    }

    @Override
    protected Scalar setLower() {
        return LOWER;
    }

    @Override
    protected Scalar setUpper() {
        return UPPER;
    }

    @Override
    public int getBoxColour() {
        return 0xff0000ff;
    }

    @Override
    public boolean showMaskedInput() {
        return true;
    }
}
