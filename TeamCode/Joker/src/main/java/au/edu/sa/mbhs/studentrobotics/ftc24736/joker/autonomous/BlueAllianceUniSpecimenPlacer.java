package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.autonomous;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

/**
 * "I've bathed in the terrestrial Juice. Witness my omnipotence (bad autonomouse)."
 */
@Autonomous(name = "Blue Uni Specimen Placer", preselectTeleOp = "TeleOp")
public class BlueAllianceUniSpecimenPlacer extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
        //robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {

        robot.drive.setPose(new Pose2d(-24, 24*3+9, Math.toRadians(90)));

        robot.drive.makeTrajectory(new Pose2d(-24, 24*3-9, Math.toRadians(270)))
                .strafeTo(new Vector2d(-24*1.5, 24*1.3), Inches)
                .strafeTo(new Vector2d(-24*1.5, 8), Inches)
                .strafeTo(new Vector2d(-24*2.1, 8), Inches)
                .strafeTo(new Vector2d(-24*2.1, 24*2.2+1), Inches)
                .strafeTo(new Vector2d(-24*2.1, 24*1.5), Inches)
                .turnTo(90, Degrees)
                .strafeTo(new Vector2d(-24*2.1, 24*3-9), Inches)
                .addTask();

        run(robot::toggleOuttakeGrip);

        add(robot.drive.makeTrajectory(new Pose2d(-24*2.1, 24*3-9, Math.toRadians(90)))
                .strafeTo(new Vector2d(-24*2.1, 24*2.5), Inches)
                .strafeToLinearHeading(new Vector2d(0, 24+9), Inches, 270, Degrees)
                .build()
                .with(robot.lift.tasks.goTo(5000)));

        add(robot.lift.tasks.goTo(4500));

        run(robot::toggleOuttakeGrip);

        add(robot.drive.makeTrajectory(new Pose2d(0, 24+9, Math.toRadians(270)))
                .strafeTo(new Vector2d(-24*1.5, 24*2.5), Inches)
                .build()
                .with(robot.lift.tasks.home()));
    }
}