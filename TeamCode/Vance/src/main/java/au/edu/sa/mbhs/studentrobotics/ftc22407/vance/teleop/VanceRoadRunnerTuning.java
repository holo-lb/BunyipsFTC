package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.RoadRunnerDrive;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.tuning.RoadRunnerTuningOpMode;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * brrrrrrrrrrrrrm, brrrrrrrrrrrrrrm, brrrrrrrrrrrrrrrrrrrm
 *
 * @author Lachlan Paul, 2024
 */
@TeleOp
public class VanceRoadRunnerTuning extends RoadRunnerTuningOpMode {
    @NonNull
    @Override
    protected RoadRunnerDrive getDrive() {
        Vance robot = new Vance();
        robot.init(this);
        return robot.drive;
    }
}
