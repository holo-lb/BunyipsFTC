package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.autonomous;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.MecanumLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;

@Autonomous(name = "Autonomous")
public class Auto extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        DriveModel dm = new DriveModel.Builder()

                .build();
        robot.drive
                .withLocalizer(new MecanumLocalizer(dm, robot.frontLeft, robot.backLeft, robot.backRight, robot.frontRight, robot.imu.get()));
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        add(robot.lift.tasks.goTo(robot.handoverPoint));

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
        */
    }
}