package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.ColourTunerOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.ColourThreshold;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.centerstage.PurplePixel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.centerstage.WhitePixel;

/**
 * Taste the Ocean
 */
@TeleOp
public class CellphoneTunerTest extends ColourTunerOpMode {
    @NonNull
    @Override
    protected CameraName setCamera() {
        return ClassFactory.getInstance().getCameraManager().nameFromCameraDirection(BuiltinCameraDirection.BACK);
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
