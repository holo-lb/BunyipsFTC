package org.firstinspires.ftc.teamcode.bertie;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.common.BunyipsController;
import org.firstinspires.ftc.teamcode.common.BunyipsOpMode;

public class BertieArm extends BunyipsController {
    private static final int[] lift_positions = {0, 100, 400, 800, 1100};

    final private DcMotor liftMotor;
    private double liftPower;
    private int liftIndex = 0;

    @SuppressLint("DefaultLocale")
    public BertieArm(BunyipsOpMode opmode, DcMotor liftMotor) {
        super(opmode);
        this.liftMotor = liftMotor;
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setTargetPosition(0);
        liftPower = 0;
    }

        public void liftUp() {
            liftIndex++;
            if (liftIndex > lift_positions.length - 1) {
                liftIndex = lift_positions.length - 1;
            }
            liftMotor.setTargetPosition(lift_positions[liftIndex]);
        }

        public void liftDown() {
            liftIndex--;
            if (liftIndex < 0) {
                liftIndex = 0;
            }
            liftMotor.setTargetPosition(lift_positions[liftIndex]);
        }

        public void setPower(double power) {
            liftPower = power;
        }

        @SuppressLint("DefaultLocale")
        public void update() {
            getOpMode().telemetry.addLine(String.format("pos: %d", liftMotor.getCurrentPosition()));
            liftMotor.setPower(liftPower);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }
