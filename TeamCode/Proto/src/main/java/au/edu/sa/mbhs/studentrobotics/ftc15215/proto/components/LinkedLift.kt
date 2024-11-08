package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.components

import au.edu.sa.mbhs.studentrobotics.bunyipslib.subsystems.HoldableActuator
import com.qualcomm.robotcore.hardware.DcMotorEx

/**
 * Dual version of `HoldableActuator` for the Proto suspension lift.
 * Links one system controller to control two actuators.
 *
 * @author Lucas Bubner, 2024
 */
class LinkedLift(private val motor1: DcMotorEx, private val motor2: DcMotorEx) : HoldableActuator(motor1) {
    override fun periodic() {
        super.periodic()
        motor2.power = motor1.power
    }
}