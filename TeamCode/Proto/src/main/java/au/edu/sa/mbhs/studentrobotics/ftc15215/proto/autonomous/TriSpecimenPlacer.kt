package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.control.pid.PController
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.FieldTiles
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.InchesPerSecondPerSecond
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Second
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.constraints.Accel
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.constraints.Vel
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.MoveToContourTask
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redRight
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

@Autonomous(name = "3+0 Specimen Placer (Center, Right, Ob. Park)")
class TriSpecimenPlacer : AutonomousBunyipsOpMode() {
    private val robot = Proto()
//    private lateinit var sampleSensor: ColourThreshold

    override fun onInitialise() {
        robot.init()
        robot.clawLift.withTolerance(20, true)
        setOpModes(
            blueRight().tile(3.5).backward(5 of Inches),
            redRight().tile(3.5).backward(5 of Inches)
        )
        MoveToContourTask.DEFAULT_X_CONTROLLER = PController(0.05)
        MoveToContourTask.DEFAULT_R_CONTROLLER = PController(0.1)
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.require() as StartingConfiguration.Position
//        sampleSensor = if (startLocation.isRed) RedSample() else BlueSample()
//        robot.camera
//            .init(sampleSensor)
//            .start(sampleSensor)
//            .flip()
//            .startPreview()
        robot.drive.pose = startLocation.toFieldPose()

        /*
        drive.makeTrajectory(blueRight().tile(3.5).backward(Inches.of(5)).build().toFieldPose())
                .setAccelConstraints(Accel.ofMin(-10, InchesPerSecondPerSecond))
                .lineToY(36.0)
                .addTask();

        drive.makeTrajectory(new Pose2d(0.0, 38.0, 3 * PI / 2))
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(-36.7, 26.8), 3 * PI / 2)
                .splineToConstantHeading(new Vector2d(-47.0, 16.0), PI)
                .setTangent(3 * PI / 2)
                .setReversed(true)
                .lineToY(56.0)
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(-56.0, 16.0), PI)
                .setReversed(true)
                .lineToY(56.0)
                .addTask();

        drive.makeTrajectory(new Pose2d(-56.0, 56.0, 3 * PI / 2))
                .strafeTo(new Vector2d(-47.0, 44.0))
                .turn(180.0, Degrees)
                .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
                .lineToY(48.0)
                .addTask();

        drive.makeTrajectory(new Pose2d(-47.0, 48.0, PI / 2))
                .splineTo(new Vector2d(-5.0, 38.0), 3 * PI / 2)
                .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
                .lineToY(36.0)
                .addTask();
         */

        add(robot.claws.tasks.closeBoth())
        add(robot.clawRotator.tasks.open())
        add(ParallelTaskGroup(
            robot.drive.makeTrajectory()
                .setAccelConstraints(Accel.ofMin(-10.0, InchesPerSecondPerSecond))
                .lineToY(36.0)
                .build(),
            robot.clawLift.tasks.goTo(1700)
        ))
        add(robot.clawLift.tasks.goTo(1050).withTimeout(Seconds.of(2.0))
            .with(robot.claws.tasks.openBoth().after(0.4 of Seconds)))

        add(robot.drive.makeTrajectory(Pose2d(0.0, 38.0, 3 * PI / 2))
            .setReversed(true)
            .splineToConstantHeading(Vector2d(-36.7, 26.8), tangent = 3 * PI / 2)
            .splineToConstantHeading(Vector2d(-47.0, 16.0), tangent = PI)
            .setTangent(3 * PI / 2)
            .setReversed(true)
            .lineToY(56.0)
            .setReversed(false)
            .splineToConstantHeading(Vector2d(-56.0, 16.0), tangent = PI)
            .setReversed(true)
            .lineToY(56.0)
            .build()
            .with(robot.clawLift.tasks.home().after(0.5 of Seconds)))

        robot.drive.makeTrajectory(Pose2d(-56.0, 56.0, 3 * PI / 2))
            .strafeTo(Vector2d(-47.0, 44.0))
            .turn(180.0, Degrees)
            .setVelConstraints(Vel.ofMax(0.1, FieldTiles per Second))
            .lineToY(48.0)
            .addTask()
//        add(MoveToContourTask(robot.drive) { sampleSensor.data }
//            .withForwardErrorSupplier { 5.0 - it.areaPercent } timeout (3 of Seconds))
        add(robot.claws.tasks.openBoth())
        add(robot.clawRotator.tasks.setTo(0.1).forAtLeast(0.4, Seconds))
        add(robot.claws.tasks.closeBoth().forAtLeast(0.2, Seconds))
        add(robot.clawRotator.tasks.open())
        add(robot.drive.makeTrajectory(Pose2d(-47.0, 48.0, PI / 2))
            .splineTo(Vector2d(-5.0, 38.0), tangent = 3 * PI / 2)
            .setVelConstraints(Vel.ofMax(0.1, FieldTiles per Second))
            .lineToY(36.0)
            .build()
            .with(robot.clawLift.tasks.goTo(1700))
        )
        add(robot.clawLift.tasks.goTo(1050).withTimeout(Seconds.of(2.0))
            .with(robot.claws.tasks.openBoth().after(0.4 of Seconds)))
        //Hello Bubner
        // You are a robot
        //Why is code so strange
        //I am a robot
        //I am a robot
        //Hmmmmm, what are you doing mister?
        //I am a robot
        //I am a robot
    }
}