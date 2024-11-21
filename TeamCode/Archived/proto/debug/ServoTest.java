package org.murraybridgebunyips.proto.debug;

import com.outoftheboxrobotics.photoncore.Photon;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * test for servo
 */
@TeleOp
@Photon
public class ServoTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        Servo servo = hardwareMap.get(Servo.class, "servo");
        servo.setPosition(1);
        waitForStart();
        while (opModeIsActive()) {
            servo.setPosition(0);
            sleep(500);
            servo.setPosition(1);
            sleep(500);
        }
    }
}

