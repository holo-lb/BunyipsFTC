package org.firstinspires.ftc.teamcode.glados.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.common.BunyipsOpMode;
import org.firstinspires.ftc.teamcode.common.RobotConfig;
import org.firstinspires.ftc.teamcode.glados.components.GLaDOSConfigCore;

@TeleOp(name="GLADOS: Rotator Test", group="GLADOS")
public class GLaDOSRotateTest extends BunyipsOpMode {
    private GLaDOSConfigCore config = new GLaDOSConfigCore();
    double target = 0.0;

    @Override
    protected void onInit() {
        config = (GLaDOSConfigCore) RobotConfig.newConfig(this, config, hardwareMap);
        config.sr.track();
        config.sr2.track();
        config.sr.setTargetPosition(0);
        config.sr2.setTargetPosition(0);
        config.sr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        config.sr2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        config.sr.setPower(0.3);
        config.sr2.setPower(0.3);
    }

    @Override
    protected void activeLoop() {
        target += gamepad1.left_stick_y * 2;
        if (gamepad1.a) {
            target = 0.0;
        }
        addTelemetry(String.valueOf(config.sr.getDegrees()));
        addTelemetry(String.valueOf(config.sr2.getDegrees()));
        config.sr.setDegrees(target);
        config.sr2.setDegrees(-target);
    }
}
