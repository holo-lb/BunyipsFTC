package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.teleop;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.ThreeWheelLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.accumulators.PeriodicIMUAccumulator;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.RoadRunnerDrive;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.tuning.RoadRunnerTuningOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.drive.MecanumDrive;
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
        return new MecanumDrive(robot.driveModel, robot.motionProfile, robot.mecanumGains, robot.fl, robot.bl, robot.br, robot.fr, robot.imu, hardwareMap.voltageSensor)
                .withLocalizer(new ThreeWheelLocalizer(robot.driveModel, robot.localiserParams, robot.dwleft, robot.dwright, robot.dwx))
                .withAccumulator(new PeriodicIMUAccumulator(robot.imu.get(), Seconds.of(3)));
    }
}
