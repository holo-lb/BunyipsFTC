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

@Autonomous(name = "Red Basket Side", preselectTeleOp = "TeleOp")
public class RedAllianceBasketSide extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
        //robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {

        robot.drive.setPose(new Pose2d(-24*1.5, -24*3+9, Math.toRadians(90)));

        robot.drive.makeTrajectory()
                .strafeTo(new Vector2d(-24*1.5, -8), Inches)
                .strafeTo(new Vector2d(-24*2.1, -8), Inches)
                .strafeTo(new Vector2d(-24*2.1, -24*3+(9+3.5-2.5)), Inches)
                .strafeTo(new Vector2d(-24*2.1, -8), Inches)
                .strafeTo(new Vector2d(-24*2.7, -8), Inches)
                .strafeTo(new Vector2d(-24*2.7, -24*2.2-1), Inches)
                .strafeTo(new Vector2d(-24*2.7, -8), Inches)
                .strafeTo(new Vector2d(-24*3.1, -8), Inches)
                .strafeTo(new Vector2d(-24*3.1, -24*2+2), Inches)
                .strafeTo(new Vector2d(-24*3.1+6, -24*2+2+6), Inches)
                .addTask();

        run(robot::toggleOuttakeGrip);

        add(robot.drive.makeTrajectory(new Pose2d(-24*3.1+6, -24*2+2+6, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-14-13, 0), Inches, 0, Degrees)
                .build()
                .with(robot.lift.tasks.goTo(2600))
        );

        add(robot.lift.tasks.goTo(3000));

        //run(() -> robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_OUT_POSITION));

        //run(() -> robot.outtakeGrip.setPosition(Joker.OUTTAKE_GRIP_OPEN_POSITION));
    }
}