package org.murraybridgebunyips.wheatley.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controller;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.common.centerstage.vision.RedTeamProp;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * TeleOp for silly guy (lock and pull) to play around with vision
 * without destroying our working TeleOp by being too silly.
 *
 * @author Lachlan Paul, 2024
 */
@TeleOp
public class WheatleyVisionTest extends CommandBasedBunyipsOpMode {
    private final WheatleyConfig config = new WheatleyConfig();
    private MecanumDrive drive;
    private Vision vision;
    private RedTeamProp redProp;
    // var robit;

    @Override
    protected void onInitialise() {
        config.init();
        drive = new MecanumDrive(config.driveConstants, config.mecanumCoefficients,
                config.imu, config.fl, config.fr, config.bl, config.br);
        vision = new Vision(config.webcam);
        redProp = new RedTeamProp();

        vision.init(redProp);
        vision.start(redProp);
        vision.startPreview();

        gamepad1.set(Controls.AnalogGroup.STICKS, Controller.SQUARE);
    }

    @Override
    protected void assignCommands() {
        drive.setDefaultTask(new HolonomicDriveTask(gamepad1, drive, () -> false));
    }
}
