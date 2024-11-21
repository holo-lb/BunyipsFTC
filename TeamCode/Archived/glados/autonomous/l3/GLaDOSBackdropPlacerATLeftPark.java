package org.murraybridgebunyips.glados.autonomous.l3;

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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.murraybridgebunyips.bunyipslib.AutonomousBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.Direction;
import org.murraybridgebunyips.bunyipslib.Reference;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.drive.DualDeadwheelMecanumDrive;
import org.murraybridgebunyips.bunyipslib.external.pid.PIDController;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.roadrunner.trajectorysequence.TrajectorySequence;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.DriveToPoseTask;
import org.murraybridgebunyips.bunyipslib.tasks.GetDualSplitContourTask;
import org.murraybridgebunyips.bunyipslib.tasks.RoadRunnerTask;
import org.murraybridgebunyips.bunyipslib.tasks.WaitTask;
import org.murraybridgebunyips.bunyipslib.tasks.groups.ParallelTaskGroup;
import org.murraybridgebunyips.bunyipslib.vision.AprilTagPoseEstimator;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.bunyipslib.vision.processors.AprilTag;
import org.murraybridgebunyips.bunyipslib.vision.processors.ColourThreshold;
import org.murraybridgebunyips.bunyipslib.vision.processors.centerstage.SpikeMarkBackdropId;
import org.murraybridgebunyips.common.centerstage.vision.BlueTeamProp;
import org.murraybridgebunyips.common.centerstage.vision.RedTeamProp;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Backdrop Placer Autonomous for Left Parking with AprilTag Detection
 *
 * @author Lucas Bubner, 2024
 */
@Config
@Autonomous(name = "Backdrop Placer (Left Park, Vision)", group = "L3")
@Disabled
public class GLaDOSBackdropPlacerATLeftPark extends AutonomousBunyipsOpMode implements RoadRunner {
    /**
     * Multiplicative scale for all RoadRunner distances.
     */
    public static double FIELD_TILE_SCALE = 1;
    /**
     * X offset to DriveToPose AprilTag
     */
    public static float APRILTAG_FORWARD_OFFSET = -9.0f;
    /**
     * Y offset to DriveToPose AprilTag
     */
    public static float APRILTAG_SIDE_OFFSET = 0;
    /**
     * Position delta (in ticks) of the arm extension at backboard
     */
    public static int ARM_DELTA_BACKDROP = 1600;
    /**
     * Whether a heading estimate is also used from AprilTag data.
     */
    public static boolean USING_HEADING_ESTIMATE = true;

    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    protected HoldableActuator arm;
    protected DualServos claws;
    protected Direction spikeMark;
    protected StartingPositions startingPosition;
    private DualDeadwheelMecanumDrive drive;
    private Vision vision;
    private AprilTag aprilTag;
    private ColourThreshold teamProp;
    private GetDualSplitContourTask getTeamProp;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(config.driveConstants, config.mecanumCoefficients, config.imu, config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.localizerCoefficients, config.parallelDeadwheel, config.perpendicularDeadwheel);
        arm = new HoldableActuator(config.arm).withMovingPower(0.5);
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 0.0, 1.0);
        vision = new Vision(config.webcam);

        aprilTag = new AprilTag();
        AprilTagPoseEstimator atpe = new AprilTagPoseEstimator(aprilTag, drive)
                .setHeadingEstimate(USING_HEADING_ESTIMATE)
                .setCameraOffset(config.robotCameraOffset);
        onActiveLoop(atpe);

        setOpModes(StartingPositions.use());

        getTeamProp = new GetDualSplitContourTask();
        setInitTask(getTeamProp);
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }

    // Set which direction the robot will strafe at the backdrop. Overridden in the right park variant.
    protected RoadRunnerTask afterPixelDropDriveAction(RoadRunnerTrajectoryTaskBuilder builder) {
        return builder
                .strafeLeft(0.85 * FIELD_TILE_SCALE, FieldTile)
                .buildTask();
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null)
            return;

        startingPosition = (StartingPositions) selectedOpMode.require();
        // Facing FORWARD from the starting position as selected
        setPose(startingPosition.getPose());

        // Go to backdrop
        Reference<TrajectorySequence> blueRight = Reference.empty();
        TrajectorySequence redLeft = makeTrajectory()
                .setScale(FIELD_TILE_SCALE)
                .forward(1.8, FieldTiles)
                .strafeRight(2.8, FieldTiles)
                .turn(-Math.PI / 2)
                .strafeRight(1, FieldTile)
                .mirrorToRef(blueRight)
                .build();
        TrajectorySequence redRight = makeTrajectory()
                .lineToLinearHeading(startingPosition.getPose()
                        .plus(RoadRunner.unitPose(new Pose2d(1, 1, -90), FieldTiles, Degrees, FIELD_TILE_SCALE)))
                .build();
        TrajectorySequence blueLeft = makeTrajectory()
                .lineToLinearHeading(startingPosition.getPose()
                        .plus(RoadRunner.unitPose(new Pose2d(1, -1, 90), FieldTiles, Degrees, FIELD_TILE_SCALE)))
                .build();

        TrajectorySequence targetSequence = null;
        switch (startingPosition) {
            case STARTING_RED_LEFT:
                teamProp = new RedTeamProp();
                targetSequence = redLeft;
                break;
            case STARTING_RED_RIGHT:
                teamProp = new RedTeamProp();
                targetSequence = redRight;
                break;
            case STARTING_BLUE_LEFT:
                teamProp = new BlueTeamProp();
                targetSequence = blueLeft;
                break;
            case STARTING_BLUE_RIGHT:
                teamProp = new BlueTeamProp();
                targetSequence = blueRight.require();
                break;
        }
        vision.init(aprilTag, teamProp);
        vision.start(teamProp);
        getTeamProp.setProcessor(teamProp);
        assert targetSequence != null;
        makeTrajectory()
                .runSequence(targetSequence)
                .withName("Navigate to Backdrop")
                .addTask();

        // Place pixels and park to the left of the backdrop
        addTask(arm.tasks.delta(ARM_DELTA_BACKDROP).withName("Deploy Arm"));
        addTask(claws.tasks.openBoth().withName("Drop Pixels"));
        addTask(new WaitTask(Seconds.of(1)).withName("Wait for Pixels"));
        addTask(new ParallelTaskGroup(
                afterPixelDropDriveAction(makeTrajectory(RoadRunner.unitPose(
                        new Pose2d(123, 90 * (startingPosition.isRed() ? -1 : 1), 0), Centimeters, Degrees
                ))),
                arm.tasks.delta(-ARM_DELTA_BACKDROP)
        ).withName("Stow and Move to Park"));

        makeTrajectory()
                .forward(0.98 * FIELD_TILE_SCALE, FieldTiles)
                .setVelConstraint(atVelocity(0.1, FieldTiles.per(Second)))
                .withName("Finish Park")
                .addTask();
    }

    @Override
    protected void onStart() {
        spikeMark = getTeamProp.getMappedPosition(Direction.FORWARD, Direction.RIGHT, Direction.LEFT);
        int id = SpikeMarkBackdropId.get(spikeMark, startingPosition);
        AprilTagMetadata aprilTagDetection = AprilTagGameDatabase.getCenterStageTagLibrary().lookupTag(id);
        if (aprilTagDetection == null) {
            telemetry.log("apriltag not found, seeing tag: %", id);
            return;
        }
        VectorF targetPos = aprilTagDetection.fieldPosition;
        // Offset from the tag to the backdrop to not drive directly into the board
        targetPos.add(new VectorF(APRILTAG_FORWARD_OFFSET, APRILTAG_SIDE_OFFSET, 0));

        addTaskAtIndex(1, new DriveToPoseTask(Seconds.of(5), drive,
                new Pose2d(targetPos.get(0), targetPos.get(1), 0),
                new PIDController(0.1, 0, 0),
                new PIDController(0.1, 0, 0),
                new PIDController(4, 0, 0)
        ));

        vision.stop(teamProp);
        vision.start(aprilTag);
    }
}
