package org.murraybridgebunyips.pbody.autonomous;


import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.trajectorysequence.TrajectorySequence;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;
import org.murraybridgebunyips.bunyipslib.tasks.WaitTask;
import org.murraybridgebunyips.bunyipslib.tasks.groups.ParallelTaskGroup;
import org.murraybridgebunyips.pbody.components.PbodyConfig;

/**
 * Primary Autonomous OpMode (Left Park)
 */
@Config
@Autonomous(name = "Backdrop Placer (Left Park)")
public class PbodyBackdropPlacerLeftPark extends AutonomousBunyipsOpMode implements RoadRunner {
    /**
     * Scaling
     */
    public static double FIELD_TILE_SCALE = 1;
    private final PbodyConfig config = new PbodyConfig();
    private MecanumDrive drive;
    private HoldableActuator arm;
    private DualServos claws;

    @Override
    protected void onInitialise() {
        config.init();
        arm = new HoldableActuator(config.arm);
        drive = new MecanumDrive(config.driveConstants, config.mecanumCoefficients, config.imu, config.fl, config.fr, config.bl, config.br);
        claws = new DualServos(config.ls, config.rs, 0.6, 0.9, 0.7, 0.4);
        setOpModes(StartingPositions.use());
    }

    // Set which direction the robot will strafe at the backdrop. Overridden in the right park variant.
    protected RoadRunnerTask afterPixelDropDriveAction(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeLeft(0.95 * FIELD_TILE_SCALE, FieldTile)
                .buildTask();
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null)
            return;

        // Go to backdrop
        Reference<TrajectorySequence> blueLeft = Reference.empty();
        Reference<TrajectorySequence> blueRight = Reference.empty();
        TrajectorySequence redLeft = makeTrajectory()
                .forward(1.7 * FIELD_TILE_SCALE, FieldTiles)
                .strafeRight(2.8 * FIELD_TILE_SCALE, FieldTiles)
                .turn(-Math.PI / 2)
                .strafeRight(1 * FIELD_TILE_SCALE, FieldTile)
                .mirrorToRef(blueRight)
                .build();
        TrajectorySequence redRight = makeTrajectory()
                .lineToLinearHeading(new Pose2d(1 * FIELD_TILE_SCALE, -1 * FIELD_TILE_SCALE, -90.0), FieldTiles, Degrees)
                .mirrorToRef(blueLeft)
                .build();

        StartingPositions startingPosition = (StartingPositions) selectedOpMode.require();
        TrajectorySequence targetSequence = null;
        switch (startingPosition) {
            case STARTING_RED_LEFT:
                targetSequence = redLeft;
                break;
            case STARTING_RED_RIGHT:
                targetSequence = redRight;
                break;
            case STARTING_BLUE_LEFT:
                targetSequence = blueLeft.require();
                break;
            case STARTING_BLUE_RIGHT:
                targetSequence = blueRight.require();
                break;
        }
        assert targetSequence != null;
        makeTrajectory()
                .runSequence(targetSequence)
                .withName("Navigate to Backdrop")
                .addTask();

        makeTrajectory()
                .forward(30, Centimeters)
                .withName("Forward Align Backdrop")
                .addTask();

        // Place pixels and park to the left of the backdrop
        addTask(arm.tasks.delta(-2600).withName("Deploy Arm"));
        addTask(claws.tasks.openBoth().withName("Drop Pixels"));
        addTask(new WaitTask(Seconds.of(1)).withName("Wait for Pixels"));
        addTask(new ParallelTaskGroup(
                afterPixelDropDriveAction(makeTrajectory().back(10)),
                arm.tasks.delta(2600)
        ).withName("Stow and Move to Park"));

        makeTrajectory()
                .forward(0.98 * FIELD_TILE_SCALE, FieldTiles)
                .setAccelConstraint(atAcceleration(0.1, FieldTiles.per(Second).per(Second)))
                .withName("Finish Park")
                .addTask();
    }

    @NonNull
    @Override
    public MecanumDrive getDrive() {
        return drive;
    }
}
