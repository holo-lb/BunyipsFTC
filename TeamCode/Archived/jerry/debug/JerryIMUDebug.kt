package org.murraybridgebunyips.jerry.debug

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.murraybridgebunyips.bunyipslib.BunyipsOpMode
import org.murraybridgebunyips.bunyipslib.IMUEx
import org.murraybridgebunyips.jerry.components.JerryConfig

/**
 * Debug opmode for IMU readouts.
 */
@TeleOp(name = "IMU Debug")
class JerryIMUDebug : BunyipsOpMode() {
    private var config = JerryConfig()
    private var imu: IMUEx? = null

    override fun onInit() {
        config.init()
        imu = IMUEx(config.imu!!)
//        imu?.startCapture()
    }

    override fun activeLoop() {
//        addTelemetry(
//            config?.imu?.getAngularOrientation(
//                AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES
//            )?.thirdAngle.toString()
//        )
//        telemetry.add(imu?.heading.toString())
        imu?.run()
    }
}