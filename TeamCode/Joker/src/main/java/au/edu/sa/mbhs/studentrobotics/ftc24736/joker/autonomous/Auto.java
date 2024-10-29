package au.edu.sa.mbhs.studentrobotics.ftc24736.joker.autonomous;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.localization.MecanumLocalizer;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.parameters.DriveModel;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.Joker;
import au.edu.sa.mbhs.studentrobotics.ftc24736.joker.components.GoToHandoverPoint;

@Autonomous(name = "Autonomous")
public class Auto extends AutonomousBunyipsOpMode {
    private final Joker robot = new Joker();

    @Override
    protected void onInitialise() {
        robot.init();
        DriveModel dm = new DriveModel.Builder()

                .build();
        robot.drive
                .withLocalizer(new MecanumLocalizer(dm, robot.frontLeft, robot.backLeft, robot.backRight, robot.frontRight, robot.imu));
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        addTask(new GoToHandoverPoint(robot.lift, robot.handoverPoint));
    }
}