package org.murraybridgebunyips.glados.teleop;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Centimeters;
import static org.murraybridgebunyips.bunyipslib.external.units.Units.Inches;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.RoadRunner;
import org.murraybridgebunyips.bunyipslib.StartingPositions;
import org.murraybridgebunyips.bunyipslib.Storage;
import org.murraybridgebunyips.bunyipslib.Text;
import org.murraybridgebunyips.bunyipslib.Threads;
import org.murraybridgebunyips.bunyipslib.UserSelection;
import org.murraybridgebunyips.bunyipslib.roadrunner.drive.RoadRunnerDrive;
import org.murraybridgebunyips.bunyipslib.tasks.DynamicTask;
import org.murraybridgebunyips.bunyipslib.vision.AprilTagPoseEstimator;
import org.murraybridgebunyips.bunyipslib.vision.processors.AprilTag;

/**
 * Extension of TeleOp with Vision and Driver-Assist Augmentations.
 *
 * @author Lucas Bubner, 2024
 */
@Config
@TeleOp(name = "TeleOp (Drive Augmentation)")
public class GLaDOSAugmentedDrive extends GLaDOSTeleOp implements RoadRunner {
    /**
     * Forward displacement from a known backdrop position.
     */
    public static double BACKDROP_FORWARD_OFFSET_CM = 20;

    private volatile StartingPositions lastKnownAlliance;

    @Override
    protected void configureVision() {
        AprilTag aprilTag = new AprilTag();
        AprilTagPoseEstimator atpe = new AprilTagPoseEstimator(aprilTag, drive)
                .setHeadingEstimate(true)
                .setCameraOffset(config.robotCameraOffset);
        onActiveLoop(atpe);

        vision.init(pixels, aprilTag);
        vision.start(pixels, aprilTag);

        // Will check to see if a last known alliance exists, and ask the user if there is none
        lastKnownAlliance = Storage.memory().lastKnownAlliance;
        if (lastKnownAlliance == null) {
            Threads.start(new UserSelection<>((selection) -> {
                if (selection == null) return;
                if (selection.equals("RED")) {
                    lastKnownAlliance = StartingPositions.STARTING_RED_LEFT;
                    return;
                }
                lastKnownAlliance = StartingPositions.STARTING_BLUE_LEFT;
            }, Text.html().color("red", "RED").toString(), Text.html().color("blue", "BLUE").toString()));
        }
    }

    private Vector2d getBackdrop() {
        if (lastKnownAlliance == null) {
            // No luck with getting our Alliance, will move to the middle of the two boards...
            return new Vector2d(48 - Inches.convertFrom(BACKDROP_FORWARD_OFFSET_CM, Centimeters), 0);
        }
        double x = 60.25 - Inches.convertFrom(BACKDROP_FORWARD_OFFSET_CM, Centimeters);
        if (lastKnownAlliance.isRed()) {
            return new Vector2d(x, 35.41);
        }
        // Blue
        return new Vector2d(x, -35.41);
    }

    @Override
    protected void assignCommands() {
        super.assignCommands();
        driver().whenPressed(Controls.A)
                .run(new DynamicTask(() ->
                        makeTrajectory(drive.getPoseEstimate())
                                // Drive in a line to the backdrop and align to it
                                .lineToLinearHeading(new Pose2d(getBackdrop(), 0.0))
                                .buildTask(false)))
                .finishingIf(() -> !gamepad1.a);
    }

    @NonNull
    @Override
    public RoadRunnerDrive getDrive() {
        return drive;
    }
}
