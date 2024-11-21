package org.murraybridgebunyips.glados.debug;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.WhitePixel;

/**
 * Testing for multi-camera
 */
@TeleOp
@Disabled
public class GLaDOSMultiCameraTest extends BunyipsOpMode {
    private Vision vision1;
    private Vision vision2;

    @Override
    protected void onInit() {
        CameraName cam1 = hardwareMap.get(WebcamName.class, "webcam");
        CameraName cam2 = hardwareMap.get(WebcamName.class, "webcam2");
        vision1 = new Vision(cam1);
        vision2 = new Vision(cam2);

        WhitePixel a = new WhitePixel();
        vision1.init(vision1.raw, a);
        vision2.init(vision2.raw);
        vision1.start(vision1.raw, a);
        vision2.start(vision2.raw);
        vision1.startPreview();
        vision2.startPreview();
        vision1.setPreview(a);
    }

    @Override
    protected void activeLoop() {
    }
}
