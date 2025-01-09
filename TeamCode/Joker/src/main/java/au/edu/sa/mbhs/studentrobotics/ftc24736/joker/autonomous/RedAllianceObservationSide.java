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

@Autonomous(name = "Red Observation Side", preselectTeleOp = "TeleOp")
public class RedAllianceObservationSide extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        robot.lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
        //robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    protected void onReady(@Nullable RefCell<?> selectedOpMode) {

        robot.drive.setPose(new Pose2d(24, -24*3+9, Math.toRadians(90)));

        robot.drive.makeTrajectory()
                .strafeTo(new Vector2d(24*1.75, -24*1.5), Inches)
                .strafeTo(new Vector2d(24*1.75, -8), Inches)
                .strafeTo(new Vector2d(24*2.4, -8), Inches)
                .strafeTo(new Vector2d(24*2.4, -24*2.2-1), Inches)
                .strafeTo(new Vector2d(24*2.4, -8), Inches)
                .strafeTo(new Vector2d(24*3, -8), Inches)
                .strafeTo(new Vector2d(24*3, -24*2.2-1), Inches)
                .strafeTo(new Vector2d(24*3, -8), Inches)
                .strafeTo(new Vector2d(24*3.45, -8), Inches)
                .strafeTo(new Vector2d(24*3.45, -24*2.2-1), Inches)
                .strafeTo(new Vector2d(24*3.45-6, -24*2.2+6), Inches)
                .strafeToLinearHeading(new Vector2d(24*2.5, -56), Inches, 270, Degrees)
                .addTask();

        //run(() -> robot.outtakeAlign.setPosition(Joker.OUTTAKE_ALIGN_OUT_POSITION));

        //run(() -> robot.outtakeGrip.setPosition(Joker.OUTTAKE_GRIP_OPEN_POSITION));
    }
}