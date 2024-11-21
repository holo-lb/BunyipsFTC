package org.murraybridgebunyips.glados.autonomous.l5;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Degrees;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTile;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.FieldTiles;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.MetersPerSecond;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Second;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Seconds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
import org.murraybridgebunyips.bunyipslib.tasks.DynamicTask;
import org.murraybridgebunyips.bunyipslib.tasks.GetDualSplitContourTask;
import org.murraybridgebunyips.bunyipslib.tasks.RunTask;
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
 * 45 Point CENTERSTAGE Autonomous
 * <p>
 * Non-composite refined Ultimate Preload OpMode. This is to fix the shortcomings of the composite OpModes which was
 * developed rapidly in order to fit within limited testing time. Compared to the composite OpModes, this OpMode will
 * be more aware of where the Spike Mark is, adding extra routes to avoid displacing a placed Purple Pixel.
 * <p>
 * This Autonomous will place the Purple Pixel on the left claw on the appropriate vision-detected spike mark, navigate
 * to the backboard, place the Yellow Pixel on the appropriate side, then park to the left. A right park variant exists
 * which extends this class to provide a right park. This OpMode is accomplished with the help of RoadRunner and the AprilTag
 * Pose Estimator.
 * <p>
 * This class operates an effective total of 12 OpModes per parking position, to a total of 24 OpModes in one implementation.
 *
 * @author Lucas Bubner, 2024
 */
@Config
@Autonomous(name = "Ultimate Preload (Purple on Left, Yellow on Right, Left Park)", group = "L5")
public class GLaDOSUltimatePreloadLeftPark extends AutonomousBunyipsOpMode implements RoadRunner {
    /**
     * X offset to DriveToPose AprilTag in inches
     */
    public static float APRILTAG_FORWARD_OFFSET = 13.0f;
    /**
     * Y offset to DriveToPose AprilTag in inches
     */
    public static float APRILTAG_SIDE_OFFSET = -3.0f;
    /**
     * Position delta (in ticks) of the arm extension at backboard
     */
    public static int ARM_DELTA_BACKDROP = 1500;
    /**
     * Whether a heading estimate is also used from AprilTag data.
     */
    public static boolean USING_HEADING_ESTIMATE = false;
    /**
     * Arm to ground from stow in ticks.
     */
    public static int ARM_DELTA_GROUND = 2000;
    /**
     * Strafe left distance for left park, field tiles.
     */
    public static double PARK_LEFT_TILES = 0.9;
    /**
     * Strafe right distance for right park, field tiles. Used in the Right Park override.
     */
    public static double PARK_RIGHT_TILES = 0.9;
    /**
     * Angled spike mark, move forward initially, field tiles
     */
    public static double ANGLED_INIT_FWD_TILES = 0.65;
    /**
     * Forward spike mark, move forward initially, field tiles
     */
    public static double M_FORWARD_INIT_FWD_TILES = 0.5;
    /**
     * Forward spike mark, forward centimeters
     */
    public static double M_FORWARD_ALIGN_FWD_CM = 15;
    /**
     * Left spike mark, degrees turn
     */
    public static double M_LEFT_ALIGN_TURN_DEG = 40;
    /**
     * Right spike mark, degrees turn
     */
    public static double M_RIGHT_ALIGN_TURN_DEG = -40;

    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private DualDeadwheelMecanumDrive drive;
    private HoldableActuator arm;
    private DualServos claws;

    private Vision vision;
    private AprilTag aprilTag;
    private ColourThreshold teamProp;
    private GetDualSplitContourTask getTeamProp;

    private Direction spikeMark;
    private VectorF backdropPose;
    private StartingPositions startingPosition;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new DualDeadwheelMecanumDrive(
                config.driveConstants, config.mecanumCoefficients, config.imu,
                config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.localizerCoefficients,
                config.parallelDeadwheel, config.perpendicularDeadwheel
        );
        arm = new HoldableActuator(config.arm).withMovingPower(0.5);
        claws = new DualServos(config.leftPixel, config.rightPixel, 1.0, 0.0, 0.0, 1.0);
        vision = new Vision(config.webcam);

        aprilTag = new AprilTag();
        AprilTagPoseEstimator atpe = new AprilTagPoseEstimator(aprilTag, drive)
                .setHeadingEstimate(USING_HEADING_ESTIMATE)
                .setCameraOffset(config.robotCameraOffset);
        // Will run in the background updating the robot pose depending on AprilTag detection
        onActiveLoop(atpe);

        setOpModes(StartingPositions.use());

        getTeamProp = new GetDualSplitContourTask();
        setInitTask(getTeamProp);
    }

    // Set which direction the robot will strafe at the backdrop. Overridden in the right park variant.
    protected Direction getParkingDirection() {
        return Direction.LEFT;
    }

    @Override
    protected void onReady(@Nullable Reference<?> selectedOpMode, Controls selectedButton) {
        if (selectedOpMode == null)
            return;

        startingPosition = (StartingPositions) selectedOpMode.require();
        // Facing FORWARD from the starting position as selected
        setPose(startingPosition.getPose());

        // Approach Spike Marks, lazy loaded as this is can only be calculated at runtime
        addTask(new DynamicTask(
                () -> new ParallelTaskGroup(
                        arm.tasks.delta(ARM_DELTA_GROUND).withName("Extend Arm").withTimeout(Seconds.of(4)),
                        makeTrajectory(startingPosition.getPose())
                                .forward(spikeMark == Direction.FORWARD ? M_FORWARD_INIT_FWD_TILES : ANGLED_INIT_FWD_TILES, FieldTile)
                                .withName("Move Forward to Spike Marks")
                                .buildTask()
                )
        ).withName("Move to Spike Marks"));

        // Align arm to Spike Mark, need to defer this task until runtime as this data is not available until then
        addTask(new DynamicTask(() -> {
            if (spikeMark == Direction.FORWARD) {
                // No rotation required
                return makeTrajectory()
                        .forward(M_FORWARD_ALIGN_FWD_CM, Centimeters)
                        .withName("Push to Spike Mark")
                        .buildTask();
            }
            return makeTrajectory()
                    .turn(spikeMark == Direction.LEFT ? M_LEFT_ALIGN_TURN_DEG : M_RIGHT_ALIGN_TURN_DEG, Degrees)
                    .withName("Rotate to Spike Mark")
                    .buildTask();
        }));

        // Place Purple Pixel (loaded left)
        addTask(claws.tasks.openLeft().withName("Open Left Claw"));
        addTask(new DynamicTask(() -> {
            boolean armWillHitTrussOnRed = spikeMark == Direction.RIGHT && startingPosition == StartingPositions.STARTING_RED_LEFT;
            boolean armWillHitTrussOnBlue = spikeMark == Direction.LEFT && startingPosition == StartingPositions.STARTING_BLUE_RIGHT;
            if (armWillHitTrussOnRed || armWillHitTrussOnBlue) {
                // Retracting the arm will cause it to get caught on the Truss, we need to back up
                return makeTrajectory()
                        .back(10)
                        .buildTask();
            }
            return new RunTask();
        }));
        addTask(arm.tasks.delta(-ARM_DELTA_GROUND).withName("Retract Arm").withTimeout(Seconds.of(4)));

        addTask(new DynamicTask(() -> {
            double turn = spikeMark == Direction.FORWARD ? 0 : spikeMark == Direction.LEFT ? -M_LEFT_ALIGN_TURN_DEG : -M_RIGHT_ALIGN_TURN_DEG;
            return makeTrajectory(drive.getPoseEstimate())
                    // Try to align
                    .turn(turn, Degrees)
                    .buildTask();
        }));

//        addTask(() -> setPose(new Pose2d(-36, 36 * (startingPosition.isRed() ? -1 : 1), Math.toRadians(-90) * (startingPosition.isRed() ? -1 : 1))));

        // Go to backdrop from current position
        // We do not know where the robot is in terms of a relative space, so these pathways need to be
        // in a global inferred context, accomplished by deferring construction to runtime
        addTask(new DynamicTask(() -> {
            Reference<TrajectorySequence> blue = Reference.empty();
            RoadRunnerTrajectoryTaskBuilder redBuilder = makeTrajectory().mirrorToRef(blue);

            // Far side backdrop navigation
            if (startingPosition == StartingPositions.STARTING_RED_LEFT || startingPosition == StartingPositions.STARTING_BLUE_RIGHT) {
                if (spikeMark == Direction.FORWARD) {
                    // The Spike Mark is in front of the robot and we are on the far alliance.
                    // Take the route that goes under the Truss, rotate now as we have time too
                    redBuilder.forward(4);
                    redBuilder.turn(-90, Degrees);
                    // We are 3 field tiles away from the tile before the backdrop
                    redBuilder.forward(2, FieldTiles);
                } else {
                    // We do not have to worry about crashing into the Spike Mark (left or right), take the long way to ensure no collisions.
                    // This ensures the right partner does not have a barreling robot removing their Spike Mark
                    redBuilder.lineTo(new Vector2d(-36, -11.5));
                    // Follow the original path from the L3 OpMode
                    redBuilder
                            .strafeRight(3.14, FieldTiles)
                            .turn(-Math.PI / 2)
                            .strafeRight(1, FieldTile);
                }
            } else {
                // Close side backdrop navigation
                boolean onRedWithMarkInterception = startingPosition == StartingPositions.STARTING_RED_RIGHT && spikeMark == Direction.RIGHT;
                boolean onBlueWithMarkInterception = startingPosition == StartingPositions.STARTING_BLUE_LEFT && spikeMark == Direction.LEFT;
                // We're in a dubious position where moving towards the backdrop will push the Spike Mark.
                // Will need to attach a short circumnavigation by moving backwards and strafing right -- the lineTo
                // will then move to the correct position.
                if (onRedWithMarkInterception || onBlueWithMarkInterception) {
                    redBuilder.back(0.5, FieldTiles);
                    redBuilder.strafeRight(0.5, FieldTiles);
                }
                // Move to the backdrop directly in one motion
                redBuilder.lineToLinearHeading(new Pose2d(35, -35, 0));
                redBuilder.setVelConstraint(atVelocity(0.1, MetersPerSecond));
                redBuilder.forward(6);
                redBuilder.resetConstraints();
            }

            redBuilder.waitFor(1, Seconds);

            // Build and mirror to blue
            TrajectorySequence red = redBuilder.build();

            // Go!
            return makeTrajectory()
                    .runSequence(startingPosition.isRed() ? red : blue.require())
                    .buildTask();
        }).withName("Navigate to Backdrop"));

        // Backdrop pose will be provided after onStart() and has been deferred until runtime
        addTask(new DynamicTask(
                () -> new DriveToPoseTask(Seconds.of(5), drive,
                        new Pose2d(backdropPose.get(0), backdropPose.get(1), 0),
                        new PIDController(0.1, 0, 0),
                        new PIDController(0.1, 0, 0),
                        new PIDController(0.1, 0, 0)
                )
                        .withMaxForwardSpeed(0.2)
                        .withMaxStrafeSpeed(0.2)
                        .withMaxRotationSpeed(0.1)
        ));

        // Place pixels and park to the left of the backdrop
        addTask(arm.tasks.delta(ARM_DELTA_BACKDROP).withName("Deploy Arm").withTimeout(Seconds.of(4)));
        addTask(claws.tasks.openRight().withName("Drop Pixel"));
        addTask(new WaitTask(Seconds.of(1)).withName("Wait for Pixels"));

        // Park
        double parkDistance = getParkingDirection() == Direction.LEFT ? PARK_LEFT_TILES : -PARK_RIGHT_TILES;
        addTask(new ParallelTaskGroup(
                makeTrajectory(new Pose2d(48, 36 * (startingPosition.isRed() ? -1 : 1), 0))
                        .back(7)
                        .strafeLeft(parkDistance, FieldTiles)
                        .buildTask(),
                claws.tasks.closeBoth(),
                arm.tasks.delta(-ARM_DELTA_BACKDROP).withTimeout(Seconds.of(4))
        ).withName("Stow and Move to Park"));

        makeTrajectory()
                // Avoid destroying the field wall
                .setVelConstraint(atVelocity(0.4, FieldTiles.per(Second)))
                .forward(0.8, FieldTiles)
                .withName("Finish Park")
                .addTask();

        // Tasks created, run vision initialisation
        teamProp = startingPosition.isRed() ? new RedTeamProp() : new BlueTeamProp();
        vision.init(aprilTag, teamProp);
        vision.start(teamProp);
        vision.startPreview();
        vision.setPreview(aprilTag);
        getTeamProp.setProcessor(teamProp);
    }

    @Override
    protected void onStart() {
        // Capture results when PLAY is pressed
        spikeMark = getTeamProp.getMappedPosition(Direction.FORWARD, Direction.RIGHT, Direction.LEFT);
        int id = SpikeMarkBackdropId.get(spikeMark, startingPosition);
        AprilTagMetadata aprilTagDetection = AprilTagGameDatabase.getCenterStageTagLibrary().lookupTag(id);
        if (aprilTagDetection == null) {
            telemetry.log("apriltag not found, seeing tag: %", id);
            return;
        }
        // Supply dynamic constructed information for AprilTag alignment tasks
        backdropPose = aprilTagDetection.fieldPosition;
        // Offset from the tag to the backdrop to not drive directly into the board
        backdropPose.subtract(new VectorF(APRILTAG_FORWARD_OFFSET, APRILTAG_SIDE_OFFSET, 0));

        // Team prop detections are done, switch over to AprilTag now for the rest of the match
        vision.stop(teamProp);
        vision.start(aprilTag);
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }
}
