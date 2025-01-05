package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.debug;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.RoadRunnerDrive;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.tuning.RoadRunnerTuningOpMode;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

@TeleOp
public class JokerRoadRunnerTuning extends RoadRunnerTuningOpMode {
    private final Joker robot = new Joker();

    @NonNull
    @Override
    protected RoadRunnerDrive getDrive() {
        robot.init();
        return robot.drive;
    }
}
