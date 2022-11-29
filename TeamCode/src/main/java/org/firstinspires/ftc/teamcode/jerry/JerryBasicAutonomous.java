package org.firstinspires.ftc.teamcode.jerry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.common.BunyipsOpMode;
import org.firstinspires.ftc.teamcode.common.CameraOp;
import org.firstinspires.ftc.teamcode.common.tasks.GetAprilTagTask;
import org.firstinspires.ftc.teamcode.common.tasks.MessageTask;
import org.firstinspires.ftc.teamcode.common.tasks.Task;
import org.firstinspires.ftc.teamcode.jerry.config.JerryArm;
import org.firstinspires.ftc.teamcode.jerry.config.JerryConfig;
import org.firstinspires.ftc.teamcode.jerry.config.JerryDrive;
import org.firstinspires.ftc.teamcode.common.tasks.GetQRSleeveTask;
import org.firstinspires.ftc.teamcode.jerry.tasks.JerryBaseDriveTask;
import org.firstinspires.ftc.teamcode.jerry.tasks.JerryDeadwheelDriveTask;

import java.util.ArrayDeque;

@Autonomous(name="<JERRY> POWERPLAY BASIC SIGNAL Autonomous")
public class JerryBasicAutonomous extends BunyipsOpMode {

    private JerryConfig config;
    private CameraOp cam;
    private JerryDrive drive;
    private JerryArm arm;
    private ArrayDeque<Task> tasks = new ArrayDeque<>();

    @Override
    protected void onInit() {
        config = JerryConfig.newConfig(hardwareMap, telemetry);
        try {
            drive = new JerryDrive(this, config.bl, config.br, config.fl, config.fr);
        } catch (Exception e) {
            telemetry.addLine("Failed to initialise Drive System.");
        }

        tasks.add(new JerryBaseDriveTask(this, 1, drive, 0, 1, 0));

        telemetry.addLine("Ready to go. Parking position has been set to: CENTER");
        telemetry.update();
    }

    @Override
    protected void activeLoop() throws InterruptedException {
        Task currentTask = tasks.peekFirst();
        if (currentTask == null) {
            return;
        }
        currentTask.run();
        if (currentTask.isFinished()) {
            tasks.removeFirst();
        }
        if (tasks.isEmpty()) {
            drive.deinit();
        }
    }
}