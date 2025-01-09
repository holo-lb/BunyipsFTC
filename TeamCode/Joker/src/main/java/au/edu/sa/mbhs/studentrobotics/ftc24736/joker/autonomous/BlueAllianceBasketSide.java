package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.autonomous;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;
import dev.frozenmilk.util.cell.RefCell;

@Autonomous(name = "Blue Basket Side", preselectTeleOp = "TeleOp")
public class BlueAllianceBasketSide extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
        //robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void onReady(@Nullable RefCell<?> selectedOpMode) {
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

        robot.drive.makeTrajectory()
                .strafeTo(new Vector2d(24*1.5, 8), Inches)
                .strafeTo(new Vector2d(24*2.1, 8), Inches)
                .strafeTo(new Vector2d(24*2.1, 24*3-(9+3.5-2.5)), Inches)
                .strafeTo(new Vector2d(24*2.1, 8), Inches)
                .strafeTo(new Vector2d(24*2.7, 8), Inches)
                .strafeTo(new Vector2d(24*2.7, 24*2.2+1), Inches)
                .strafeTo(new Vector2d(24*2.7, 8), Inches)
                .strafeTo(new Vector2d(24*3.1, 8), Inches)
                .strafeTo(new Vector2d(24*3.1, 24*2-2), Inches)
                .strafeTo(new Vector2d(24*3.1-6, 24*2-2-6), Inches)
                .addTask();

        run(robot::toggleOuttakeGrip);

        add(robot.drive.makeTrajectory(new Pose2d(24*3.1-6, 24*2-2-6, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(14+13, 0), Inches, 180, Degrees)
                .build()
                .with(robot.lift.tasks.goTo(2600))
        );

        add(robot.lift.tasks.goTo(3000));
    }
}