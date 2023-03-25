package org.firstinspires.ftc.teamcode.jerry

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.teamcode.common.BunyipsOpMode
import org.firstinspires.ftc.teamcode.common.IMUOp
import org.firstinspires.ftc.teamcode.jerry.components.JerryConfig

@TeleOp(name="<JERRY> IMU Debug")
class IMUDebug: BunyipsOpMode() {
    private var config: JerryConfig? = null
    private var imu: IMUOp? = null

    override fun onInit() {
        config = JerryConfig.newConfig(hardwareMap, telemetry)
        imu = IMUOp(this,config?.imu)
        imu?.startCapture()
    }

    override fun activeLoop() {
//        telemetry.addLine(
//            config?.imu?.getAngularOrientation(
//                AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES
//            )?.thirdAngle.toString()
//        )
        telemetry.addLine(imu?.heading.toString())
        imu?.tick()
    }
}