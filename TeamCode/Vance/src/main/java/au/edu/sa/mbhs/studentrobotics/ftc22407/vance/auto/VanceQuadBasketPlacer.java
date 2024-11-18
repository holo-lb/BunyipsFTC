package au.edu.sa.mbhs.studentrobotics.ftc22407.vance.auto;

import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees;
import static au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.IdentityPoseMap;
import com.acmerobotics.roadrunner.PoseMap;
import com.acmerobotics.roadrunner.Vector2d;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.MessageTask;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.Vance;
import au.edu.sa.mbhs.studentrobotics.ftc22407.vance.tasks.BasketPlacer;

/**
 * Auto that scores a pre load and three extra scoring elements
 * Name shamelessly stolen from the Bunyips
 *
 * @author Lachlan Paul, 2024
 */
public class VanceQuadBasketPlacer extends AutonomousBunyipsOpMode {
    protected final Vance robot = new Vance();
    private MessageTask waitMessage;
    private SymmetricPoseMap symmetricPoseMap;
    private Vector2d basketPlacerPos;
    private PoseMap currentPoseMap;
    final int rightSampleXPos = -48;

    @Override
    protected void onInitialise() {
        robot.init();
        robot.verticalLift.withTolerance(25, true);

        setOpModes(
                StartingConfiguration.redLeft().tile(2),
                StartingConfiguration.blueLeft().tile(2)
        );

        symmetricPoseMap = new SymmetricPoseMap();
        basketPlacerPos = new Vector2d(-57.13, -56.35);
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, @NonNull Controls selectedButton) {
        if (selectedOpMode == null) return;
        StartingConfiguration.Position startingPosition = (StartingConfiguration.Position) selectedOpMode.require();
        currentPoseMap = startingPosition.isRed() ? new SymmetricPoseMap() : new IdentityPoseMap();

        robot.drive.makeTrajectory(new Vector2d(-36.26, -69.39), Inches, 90.00, Degrees, currentPoseMap)
                .splineTo(basketPlacerPos, Inches, 230.00, Degrees)
                .strafeToLinearHeading(new Vector2d(rightSampleXPos, -38.87), Inches, 90.00, Degrees)
                .addTask();

        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));

        robot.drive.makeTrajectory(currentPoseMap)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .strafeToLinearHeading(new Vector2d(rightSampleXPos - 10, -38.87), Inches, 90.00, Degrees)
                .addTask();

        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));

        robot.drive.makeTrajectory(currentPoseMap)
                .strafeToSplineHeading(basketPlacerPos, Inches, 230.00, Degrees)
                .strafeToSplineHeading(new Vector2d(-55, -25.7), Inches, 180, Degrees)
                .addTask();

        add(new BasketPlacer(robot.verticalLift, robot.basketRotator, robot.drive));
    }
}
