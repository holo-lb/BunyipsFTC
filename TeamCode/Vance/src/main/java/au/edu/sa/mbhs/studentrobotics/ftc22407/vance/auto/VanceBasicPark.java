package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;

/**
 * Quick auto for easy 3 points. Use as backup.
 * @author Lachlan Paul, 2024
 */

@Autonomous
public class VanceBasicPark extends AutonomousBunyipsOpMode {
    private final Vance robot = new Vance();

    @Override
    protected void onInitialise() {
        robot.init();

        setOpModes(
            StartingConfiguration.redRight().tile(2),
            StartingConfiguration.blueRight().tile(2)
        );
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();

        robot.drive.setPose(startingPosition.toFieldPose());

        robot.drive.makeTrajectory(/*new Vector2d(23.23, -64.29), Inches, 90.00, Degrees*/)
                .strafeTo(new Vector2d(58.38, -64.62), Inches)
                .addTask();
    }
}
