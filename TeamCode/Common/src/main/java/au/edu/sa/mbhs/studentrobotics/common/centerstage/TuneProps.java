package au.edu.sa.mbhs.studentrobotics.common.centerstage;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import au.edu.sa.mbhs.studentrobotics.common.centerstage.vision.BlueTeamProp;
import au.edu.sa.mbhs.studentrobotics.common.centerstage.vision.RedTeamProp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.ColourTunerOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.vision.processors.ColourThreshold;

/**
 * Colour tuner for red+blue team props
 */
@TeleOp(name = "[Debug] Tune for R+B Team Props", group = "z")
@Disabled
public class TuneProps extends ColourTunerOpMode {
    /**
     * The camera from HardwareMap to use for vision processing.
     *
     * @return the camera to use
     */
    @NonNull
    @Override
    protected CameraName setCamera() {
        return hardwareMap.get(CameraName.class, "webcam");
    }

    /**
     * The processors to tune in this OpMode.
     *
     * @return the processors to tune, will be able to switch between them during runtime
     */
    @NonNull
    @Override
    protected ColourThreshold[] setThresholdsToTune() {
        return new ColourThreshold[]{
                new RedTeamProp(),
                new BlueTeamProp()
        };
    }
}
