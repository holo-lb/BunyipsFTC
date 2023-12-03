package org.firstinspires.ftc.team15215.jerry.debug

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.team15215.jerry.components.JerryConfig
import org.firstinspires.ftc.team15215.jerry.components.JerryLift
import org.murraybridgebunyips.ftc.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.ftc.bunyipslib.NullSafety

/**
 * Manual arm control used for calibration purposes, using gamepad2 left stick.
 */
@TeleOp(name = "Manual Arm Control")
class JerryArmControl : BunyipsOpMode() {
    private var config = JerryConfig()
    private var arm: JerryLift? = null

    override fun onInit() {
        config.init(this)
        if (NullSafety.assertNotNull(config.armComponents)) {
            arm = JerryLift(
                this,
                JerryLift.ControlMode.MANUAL,
                config.claw!!,
                config.arm1!!,
                config.arm2!!,
                config.limit!!
            )
        }

    }

    override fun activeLoop() {
        arm?.delta(gamepad2.left_stick_y.toDouble())
        // Calculates the average position of the lift motors
        addTelemetry("Lift Position: ${(config.arm1?.currentPosition!! + config.arm2?.currentPosition!!) / 2}")
        arm?.update()
    }
}