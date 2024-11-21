package org.murraybridgebunyips.glados.debug;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.murraybridgebunyips.bunyipslib.vision.ColourTuner;
import org.murraybridgebunyips.bunyipslib.vision.processors.ColourThreshold;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.PurplePixel;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.WhitePixel;

/**
 * ContourTuner testing
 */
@TeleOp
@Disabled
public class GLaDOSCTTest extends ColourTuner {
    @NonNull
    @Override
    protected CameraName setCamera() {
        return (WebcamName) hardwareMap.get("webcam");
    }

    @NonNull
    @Override
    protected ColourThreshold[] setThresholdsToTune() {
        return new ColourThreshold[]{
                new WhitePixel(),
                new PurplePixel()
        };
    }
}
