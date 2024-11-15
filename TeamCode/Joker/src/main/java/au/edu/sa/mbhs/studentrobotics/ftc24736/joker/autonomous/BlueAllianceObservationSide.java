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

@Autonomous(name = "Blue Observation Side", preselectTeleOp = "TeleOp")
public class BlueAllianceObservationSide extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
        //robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {

        robot.drive.setPose(new Pose2d(-24*1.5, 24*3-9, Math.toRadians(270)));

        robot.drive.makeTrajectory(new Pose2d(-24*1.5, 24*3-9, Math.toRadians(270)))
                .strafeTo(new Vector2d(-24*1.5, 8), Inches)
                .strafeTo(new Vector2d(-24*2.1, 8), Inches)
                .strafeTo(new Vector2d(-24*2.1, 24*2.2+1), Inches)
                .strafeTo(new Vector2d(-24*2.1, 8), Inches)
                .strafeTo(new Vector2d(-24*2.75, 8), Inches)
                .strafeTo(new Vector2d(-24*2.75, 24*2.2+1), Inches)
                .strafeTo(new Vector2d(-24*2.75, 8), Inches)
                .strafeTo(new Vector2d(-24*3.25, 8), Inches)
                .strafeTo(new Vector2d(-24*3.25, 24*2.2+1), Inches)
                .strafeTo(new Vector2d(-24*3.25+6, 24*2.2-6), Inches)
                .addTask();

        add(robot.drive.makeTrajectory(new Pose2d(-24*3.25+6, 24*2.2-6, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-24*2.5, 56), Inches, 90, Degrees)
                .build()
                .with(robot.lift.tasks.home())
        );

        //run(() -> robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_OUT_POSITION));

        //run(() -> robot.outtakeGrip.setPosition(Joker.OUTTAKE_GRIP_OPEN_POSITION));
    }
}