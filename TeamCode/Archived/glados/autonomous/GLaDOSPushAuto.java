package org.murraybridgebunyips.glados.autonomous;

import static org.murraybridgebunyips.common.personalitycore.CompanionCubeColours.BLUE_ELEMENT_B;
import static org.murraybridgebunyips.common.personalitycore.CompanionCubeColours.BLUE_ELEMENT_G;
import static org.murraybridgebunyips.common.personalitycore.CompanionCubeColours.BLUE_ELEMENT_R;
import static org.murraybridgebunyips.common.personalitycore.CompanionCubeColours.RED_ELEMENT_B;
import static org.murraybridgebunyips.common.personalitycore.CompanionCubeColours.RED_ELEMENT_G;
import static org.murraybridgebunyips.common.personalitycore.CompanionCubeColours.RED_ELEMENT_R;

import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.Inches;
import org.murraybridgebunyips.bunyipslib.OpModeSelection;
import org.murraybridgebunyips.bunyipslib.RoadRunnerAutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.GetTeamPropTask;
import org.murraybridgebunyips.bunyipslib.tasks.bases.RobotTask;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.TeamProp;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

import java.util.List;

/**
 * Autonomous with the intention of pushing a single pixel onto a spike mark.
 * If this is your last resort, Moon Lord have mercy on you :pensive:.
 *
 * @author Lachlan Paul, 2024
 */
@Autonomous(name = "Push Auto")
public class GLaDOSPushAuto extends RoadRunnerAutonomousBunyipsOpMode<MecanumDrive> {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private TeamProp processor;
    private GetTeamPropTask initTask;

    @Override
    protected void onInitialise() {
        config.init();
        processor = new TeamProp();
        initTask = new GetTeamPropTask(processor);
    }

    protected MecanumDrive setDrive() {
        return new DualDeadwheelMecanumDrive(config.driveConstants, config.mecanumCoefficients,
                hardwareMap.voltageSensor, config.imu, config.frontLeft, config.frontRight, config.backLeft,
                config.backRight, config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel);
    }

    @Nullable
    @Override
    protected List<OpModeSelection> setOpModes() {
        return StartingPositions.use();
    }

    @Nullable
    @Override
    protected RobotTask setInitTask() {
        return null;
    }

    @Override
    protected void onQueueReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null) {
            return;
        }

        StartingPositions startingPosition = (StartingPositions) selectedOpMode.require();

        switch (startingPosition) {
            case STARTING_RED_LEFT:
            case STARTING_RED_RIGHT:
                processor.setColours(RED_ELEMENT_R, RED_ELEMENT_G, RED_ELEMENT_B);
                break;

            case STARTING_BLUE_LEFT:
            case STARTING_BLUE_RIGHT:
                processor.setColours(BLUE_ELEMENT_R, BLUE_ELEMENT_G, BLUE_ELEMENT_B);
                break;
        }
    }

    protected void onStart() {
        if (processor != null)
            addRetainedTelemetry("Spike mark locked: %", initTask.getPosition().toString());

        switch (initTask.getPosition()) {
            case LEFT:
                makeTrajectory(new Pose2d(11.75, -70.29, Math.toRadians(90.00)))
                        .splineTo(new Vector2d(9.28, -42.25), Math.toRadians(98.29))
                        .build();
                break;
            case RIGHT:
                makeTrajectory(new Pose2d(11.94, -70.11, Math.toRadians(90.00)))
                        .splineTo(new Vector2d(20.08, -42.44), Math.toRadians(76.94))
                        .build();
                break;
            case FORWARD:
                makeTrajectory()
                        .forward(Inches.fromFieldTiles(1))
                        .build();
                break;
        }
    }
}
