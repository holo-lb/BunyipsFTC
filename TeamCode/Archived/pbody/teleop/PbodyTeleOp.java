package org.murraybridgebunyips.pbody.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.Controls;
import org.murraybridgebunyips.bunyipslib.drive.MecanumDrive;
import org.murraybridgebunyips.bunyipslib.subsystems.Cannon;
import org.murraybridgebunyips.bunyipslib.subsystems.DualServos;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.bunyipslib.tasks.HolonomicDriveTask;
import org.murraybridgebunyips.pbody.components.PbodyConfig;

/**
 * Primary TeleOp for Pbody.
 *
 * @author Lucas Bubner, 2024
 */
@TeleOp(name = "TeleOp")
public class PbodyTeleOp extends CommandBasedBunyipsOpMode {
    private final PbodyConfig config = new PbodyConfig();
    private DualServos claws;
    private MecanumDrive drive;
    private HoldableActuator arm;
    private Cannon plane;

    @Override
    protected void onInitialise() {
        config.init();
        claws = new DualServos(config.ls, config.rs, 0.6, 0.9, 0.7, 0.4);
        drive = new MecanumDrive(config.driveConstants, config.mecanumCoefficients, config.imu, config.fl, config.fr, config.bl, config.br);
        plane = new Cannon(config.pl, 0.0, 0.6);
        arm = new HoldableActuator(config.arm);

        gamepad2.set(Controls.Analog.LEFT_STICK_Y, (v) -> v * 0.6f);
    }

    @Override
    protected void assignCommands() {
//        operator().whenPressed(Controls.Y)
//                .run(claws.closeTask(DualServos.ServoSide.RIGHT));
//        operator().whenPressed(Controls.B)
//                .run(claws.openTask(DualServos.ServoSide.RIGHT));
//
//        operator().whenPressed(Controls.A)
//                .run(claws.closeTask(DualServos.ServoSide.LEFT));
//        operator().whenPressed(Controls.X)
//                .run(claws.openTask(DualServos.ServoSide.LEFT));

        operator().whenPressed(Controls.X)
                .run(claws.tasks.toggleLeft());
        operator().whenPressed(Controls.B)
                .run(claws.tasks.toggleRight());

        driver().when(Controls.Analog.RIGHT_TRIGGER, (v) -> v == 1.0)
                .run(plane.tasks.fire());
        driver().whenPressed(Controls.BACK)
                .run(plane.tasks.reset());

        arm.setDefaultTask(arm.tasks.control(() -> -gamepad2.lsy));
        drive.setDefaultTask(new HolonomicDriveTask(gamepad1, drive, () -> false));
    }
}
