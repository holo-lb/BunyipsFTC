package org.murraybridgebunyips.jerry.debug

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.bunyipslib.NullSafety
import org.murraybridgebunyips.jerry.components.JerryConfig
import org.murraybridgebunyips.jerry.components.JerryLift

/**
 * Manual arm control used for calibration purposes, using gamepad2 left stick.
 */
@TeleOp(name = "Manual Arm Control")
class JerryArmControl : BunyipsOpMode() {
    private var config = JerryConfig()
    private var arm: JerryLift? = null

    override fun onInit() {
        config.init()
        if (NullSafety.assertNotNull(config.armComponents)) {
            arm = JerryLift(
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
        telemetry.add("Lift Position: ${(config.arm1?.currentPosition!! + config.arm2?.currentPosition!!) / 2}")
        arm?.update()
    }
}