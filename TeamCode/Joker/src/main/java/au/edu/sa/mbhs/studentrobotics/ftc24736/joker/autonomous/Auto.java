package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.autonomous;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.RunTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

@Autonomous(name = "Autonomous")
public class Auto extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN);
        robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        //add(robot.lift.tasks.goTo(robot.handoverPoint));

        /*
        //MeepMeep path (drops preloaded sample in the basket, then grabs and places 2 more):

        drive.makeTrajectory(new Pose2d(24*1.5, 24*3-Math.sqrt(81*2), Math.toRadians(45)))
        .strafeTo(new Vector2d(24*2, 24*2), Inches)
        .waitSeconds(2)
        .strafeToLinearHeading(new Vector2d(24*2+0.5, 40), Inches, 270, Degrees)
        .waitSeconds(2)
        .strafeToLinearHeading(new Vector2d(24*2, 24*2), Inches, 45, Degrees)
        .waitSeconds(2)
        .strafeToLinearHeading(new Vector2d(24*2.5-1.5, 40), Inches, 270, Degrees)
        .waitSeconds(2)
        .strafeToLinearHeading(new Vector2d(24*2, 24*2), Inches, 45, Degrees)
        .addTask();

        //MeepMeep path (drops preloaded sample in the basket and pushes the floor samples into the basket zone)

        drive.makeTrajectory(new Pose2d(24*1.5, 24*3-9, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(24*2, 24*2), Inches, 45, Degrees)
                .waitSeconds(2)
                .strafeToLinearHeading(new Vector2d(40, 24), Inches, 270, Degrees)
                .strafeTo(new Vector2d(24*2, 0), Inches)
                .strafeTo(new Vector2d(24*2+6, 0), Inches)
                .strafeTo(new Vector2d(24*2+6, 24*2.2), Inches)
                .strafeTo(new Vector2d(24*2+6, 0), Inches)
                .strafeTo(new Vector2d(61, 0), Inches)
                .strafeTo(new Vector2d(61, 24*2.2), Inches)
                .addTask();
        */

        robot.drive.setPose(new Pose2d(24*1.5, 24*3-9, Math.toRadians(270)));

        add(robot.drive.makeTrajectory(new Pose2d(24*1.5, 24*3-9, Math.toRadians(270)))
                .strafeTo(new Vector2d(24*1.5, 54))
                .strafeToLinearHeading(new Vector2d(24*2.25, 24*2.25), Inches, 45, Degrees)
                .build()
                .with(robot.lift.tasks.goTo(4900)));

        add(new RunTask(() -> robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_OUT_POSITION)));

        robot.drive.makeTrajectory(new Pose2d(24*2.25, 24*2.25, Math.toRadians(45)))
                .strafeTo(new Vector2d(24*2.3, 24*2.3), Inches)
                //.waitSeconds(0.2)
                .addTask();
        add(new RunTask(() -> robot.outtakeGrip.setPosition(Joker.OUTTAKE_GRIP_OPEN_POSITION)));

        add(robot.drive.makeTrajectory(new Pose2d(24*2.3, 24*2.3, Math.toRadians(45)))
                .strafeToLinearHeading(new Vector2d(24*1.5, 24*1.5), Inches, 270, Degrees)
                .strafeTo(new Vector2d(24*1.5, 8), Inches)
                .strafeTo(new Vector2d(24*2, 8), Inches)
                .strafeTo(new Vector2d(24*2, 24*3-(9+3.5+2)), Inches)
                .strafeTo(new Vector2d(24*2, 8), Inches)
                .strafeTo(new Vector2d(24*2.5, 8), Inches)
                .strafeTo(new Vector2d(24*2.5, 24*2.2+1), Inches)
                .strafeTo(new Vector2d(24*2.5, 8), Inches)
                .strafeTo(new Vector2d(24*3, 8), Inches)
                .strafeTo(new Vector2d(24*3, 24*2-2), Inches)
                .strafeTo(new Vector2d(24*2.5, 24*1.5), Inches)
                .build()
                .with(robot.lift.tasks.home()));
    }
}