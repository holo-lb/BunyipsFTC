package au.edu.sa.mbhs.studentrobotics.cellphone.debug;


import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Centimeters;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.ColourThreshold.DEFAULT_MAX_AREA;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.ColourThreshold.DEFAULT_MIN_AREA;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.opencv.core.Scalar;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.Vision;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.ColourThreshold;
import au.edu.sa.mbhs.studentrobotics.cellphone.components.CellphoneConfig;

/**
 * Test all 2023-2024 CENTERSTAGE pixel detections.
 */
@TeleOp(name = "Pixel Vision Test (Cellphone)")
@Config
@SuppressWarnings("MissingJavadoc")
public class CellphonePixelTest extends BunyipsOpMode {
    private final CellphoneConfig config = new CellphoneConfig();
    private Vision visionB;
//    private Vision visionF
    @NonNull
    public static Scalar LOWER_YCRCB = new Scalar(0, 0, 0);
    @NonNull
    public static Scalar UPPER_YCRCB = new Scalar(140, 110, 200);
    public static double MIN_AREA = DEFAULT_MIN_AREA;
    public static double MAX_AREA = DEFAULT_MAX_AREA;
    public static boolean SHOW_MASKED_INPUT = true;

    public static double ERODE = 3.5;
    public static double DILATE = 3.5;
    public static int BLUR = 3;
    public static boolean EXTERN = false;

    @Override
    protected void onInit() {
        config.init(this);
//        VisionPortal p = VisionPortal.easyCreateWithDefaults(
//                config.cameraB,
//                new ColorBlobLocatorProcessor.Builder()
//                        .setRoi(ImageRegion.entireFrame())
//                        .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
//                        .setTargetColorRange(new ColorRange(ColorSpace.YCrCb, WhitePixel.LOWER_YCRCB, WhitePixel.UPPER_YCRCB))
//                        .build()
//                new PredominantColorProcessor.Builder()
//                        .setRoi(ImageRegion.asUnityCenterCoordinates(-0.2, 0.2, 0.2, -0.2))
//                        .setSwatches(PredominantColorProcessor.Swatch.RED)
//                        .build()
//        );
        visionB = new Vision(config.cameraB);
//        MultiColourThreshold proc = new MultiColourThreshold(Pixels.createProcessors());
//        ColourLocator proc = new ColourLocator(
//                b -> b
//                        .setRoi(ImageRegion.entireFrame())
//                        .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
//                        .setTargetColorRange(new ColorRange(ColorSpace.YCrCb, WhitePixel.LOWER_YCRCB, WhitePixel.UPPER_YCRCB))
//        );
//        ColourSensor proc = new ColourSensor(ImageRegion.asUnityCenterCoordinates(-0.2, 0.2, 0.2, -0.2), PredominantColorProcessor.Swatch.RED);
        ColourThreshold proc = new ColourThreshold() {
            {
                setColourSpace(ColourSpace.YCrCb);
                setContourAreaMinPercent(() -> MIN_AREA);
                setContourAreaMaxPercent(() -> MAX_AREA);
                setLowerThreshold(() -> LOWER_YCRCB);
                setUpperThreshold(() -> UPPER_YCRCB);
                setBoxColour(Color.CYAN);
                setShowMaskedInput(() -> SHOW_MASKED_INPUT);
                setDilateSize(() -> DILATE);
                setErodeSize(() -> ERODE);
                setBlurSize(() -> BLUR);
                setExternalContoursOnly(() -> EXTERN);

                setPnP(Centimeters.of(7), Centimeters.of(2.5));
            }

            @NonNull
            @Override
            public String getId() {
                return "proc";
            }
        };
        visionB.init(proc);
        visionB.start(proc);
        visionB.startPreview();
//        Task testTask = new AlignToContourTask(Seconds.of(3), visionB, () -> proc.getData().stream().map(ColourBlob::toContourData).collect(Collectors.toList()), new PController(0));
    }

    @Override
    protected void activeLoop() {
        telemetry.add(visionB.getAllData());
    }
}
