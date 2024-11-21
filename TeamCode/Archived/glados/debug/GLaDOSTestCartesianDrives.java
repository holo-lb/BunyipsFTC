package org.murraybridgebunyips.glados.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Direction;
import org.murraybridgebunyips.bunyipslib.Threads;
import org.murraybridgebunyips.bunyipslib.UserSelection;
import org.murraybridgebunyips.bunyipslib.drive.CartesianFieldCentricMecanumDrive;
import org.murraybridgebunyips.bunyipslib.drive.CartesianMecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.WaitUntilTask;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Test Cartesian stuff
 */
@Config
@TeleOp
public class GLaDOSTestCartesianDrives extends BunyipsOpMode {
    /**
     * assumed starting position, will be trusted as the source of truth
     */
    public static Direction ASSUMED_STARTING_DIRECTION = Direction.FORWARD;
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private CartesianMecanumDrive drive;

    @Override
    protected void onInit() {
        config.init();
        Threads.start(new UserSelection<>((s) -> {
            if ("Robot".equals(s)) {
                drive = new CartesianMecanumDrive(config.frontLeft, config.frontRight, config.backLeft, config.backRight);
            } else {
                drive = new CartesianFieldCentricMecanumDrive(config.frontLeft, config.frontRight, config.backLeft, config.backRight, config.imu, true, ASSUMED_STARTING_DIRECTION);
            }
        }, "Robot", "Field"));
        setInitTask(new WaitUntilTask(() -> drive != null));
    }

    @Override
    protected void activeLoop() {
        drive.setSpeedUsingController(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        drive.update();
    }
}
