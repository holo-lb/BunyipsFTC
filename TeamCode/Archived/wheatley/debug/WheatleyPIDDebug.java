package org.murraybridgebunyips.wheatley.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.murraybridgebunyips.bunyipslib.CommandBasedBunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.subsystems.HoldableActuator;
import org.murraybridgebunyips.wheatley.components.WheatleyConfig;

/**
 * TeleOp used to calibrate PID
 *
 * @author Lachlan Paul, 2024
 */
@Config
@TeleOp
@Disabled
public class WheatleyPIDDebug extends CommandBasedBunyipsOpMode {
    /**
     * proportional
     */
    public static double kP;
    /**
     * integral
     */
    public static double kI;
    /**
     * derivative
     */
    public static double kD;
    private final WheatleyConfig config = new WheatleyConfig();
    private HoldableActuator clawRotator;

    @Override
    protected void onInitialise() {
        config.init();
        clawRotator = new HoldableActuator(config.clawRotator);
        PIDFCoefficients coefficients = config.clawRotator.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
        kP = coefficients.p;
        kI = coefficients.i;
        kD = coefficients.d;

        useSubsystems(clawRotator);
    }

    @Override
    protected void assignCommands() {
        clawRotator.setDefaultTask(clawRotator.tasks.control(() -> -gamepad2.rsy));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void periodic() {
        config.clawRotator.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDFCoefficients(kP, kI, kD, 0, MotorControlAlgorithm.LegacyPID));
    }
}
