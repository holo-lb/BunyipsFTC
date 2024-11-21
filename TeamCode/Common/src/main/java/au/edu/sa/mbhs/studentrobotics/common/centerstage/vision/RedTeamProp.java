package au.edu.sa.mbhs.studentrobotics.common.centerstage.vision;

import androidx.annotation.NonNull;

import org.opencv.core.Scalar;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.ColourThreshold;

/**
 * Processor for the red team prop.
 *
 * @author Lucas Bubner, 2024
 */
//@Config
public class RedTeamProp extends ColourThreshold {
    /**
     * The lower YCrCb bounds for the red team prop.
     */
    @NonNull
    public static Scalar LOWER = new Scalar(0, 0, 0);
    // RokLive configuration
//    public static Scalar LOWER = new Scalar(27.74, 137.99, 0.0);
//    public static Scalar UPPER = new Scalar(145.56, 255.0, 255);
    /**
     * The upper YCrCb bounds for the red team prop.
     */
    @NonNull
    public static Scalar UPPER = new Scalar(100, 255, 255);
    /**
     * The minimum contour area percentages for the red team prop.
     */
    public static double MIN = 3;
    /**
     * The maximum contour area percentages for the red team prop.
     */
    public static double MAX = 100;

    /**
     * Defines a new colour thresholding processor for a specific colour space, which your
     * lower and upper scalars will be based on.
     */
    public RedTeamProp() {
        setColourSpace(ColourSpace.YCrCb);
        setContourAreaMinPercent(() -> MIN);
        setContourAreaMaxPercent(() -> MAX);
        setLowerThreshold(() -> LOWER);
        setUpperThreshold(() -> UPPER);
        setBoxColour(0xffff0000);
    }

    @NonNull
    @Override
    public String getId() {
        return "redteamprop";
    }
}
