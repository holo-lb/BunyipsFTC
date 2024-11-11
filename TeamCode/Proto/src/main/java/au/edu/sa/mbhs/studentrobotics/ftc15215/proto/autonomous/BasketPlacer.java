package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Second;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.WaitTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto;

/**
 * Places a preloaded sample into the high basket.
 */
@Autonomous
public class BasketPlacer extends AutonomousBunyipsOpMode {
    private final Proto robot = new Proto();

    @Override
    protected void onInitialise() {
        robot.init();
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @Nullable Controls selectedButton) {
        robot.drive.setPose(StartingConfiguration.blueLeft()
                .tile(2)
                .backward(Inches.of(5))
                .rotate(Degrees.of(90))
                .build().toFieldPose());
        add(robot.clawRotator.tasks.open());
        add(robot.drive.makeTrajectory()
                .strafeToLinearHeading(new Vector2d(56, 56), Math.PI / 4)
                .build()
                .with(robot.clawLift.tasks.goTo(900).withTimeout(Seconds.of(3))));
        add(robot.clawRotator.tasks.setTo(0.2));
        add(new WaitTask(1, Second));
        add(robot.claws.tasks.openBoth());
    }
}
