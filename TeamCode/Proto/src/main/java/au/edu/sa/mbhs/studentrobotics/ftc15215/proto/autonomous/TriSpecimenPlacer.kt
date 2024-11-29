package au.edu.sa.mbhs.studentrobotics.ftc15215.proto.autonomous

import au.edu.sa.mbhs.studentrobotics.bunyipslib.AutonomousBunyipsOpMode
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Reference
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Unit.Companion.of
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Degrees
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.FieldTilesPerSecond
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Inches
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.InchesPerSecond
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Milliseconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.external.units.Units.Seconds
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.SymmetricPoseMap
import au.edu.sa.mbhs.studentrobotics.bunyipslib.roadrunner.constraints.Vel
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.ParallelTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.tasks.groups.SequentialTaskGroup
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.blueRight
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.StartingConfiguration.redRight
import au.edu.sa.mbhs.studentrobotics.ftc15215.proto.Proto
import com.acmerobotics.roadrunner.IdentityPoseMap
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlin.math.PI

// TODO: optimise for time
//     considering A) sample push method
//                 B) starting location
@Autonomous(name = "3+0 Specimen Placer (Center, Right, Ob. Park)")
class TriSpecimenPlacer : AutonomousBunyipsOpMode() {
    private val robot = Proto()

    override fun onInitialise() {
        robot.init()
        robot.clawLift.withTolerance(25)
        setOpModes(
            blueRight().tile(3.5).backward(1 of Inches),
            redRight().tile(3.5).backward(1 of Inches)
        )
    }

    override fun onReady(selectedOpMode: Reference<*>?, selectedButton: Controls) {
        if (selectedOpMode == null) return
        val startLocation = selectedOpMode.require() as StartingConfiguration.Position

        val map = if (startLocation.isRed) SymmetricPoseMap() else IdentityPoseMap()
        val last = Reference.of(startLocation.toFieldPose())
        robot.drive.pose = last.require()

        val highChamberTicks = 1700
        val submersiblePlacingY = 31.0
        val samplePushY = 12.0
        val observationPushY = 50.0
        val homeDelay = 0.5 of Seconds
        val slowSpeed = 1 of FieldTilesPerSecond
        val wallPickup = Pose2d(-44.14, 53.88, PI / 2)
        var i = 0
        val wallPickupRoutine = {
            SequentialTaskGroup(
                robot.claws.tasks.openBoth() named "Prepare Claws",
                robot.clawRotator.tasks.setTo(0.5).forAtLeast(0.4, Seconds) named "Reach Specimen",
                robot.claws.tasks.closeBoth().forAtLeast(0.2, Seconds) named "Grip Specimen",
                robot.clawLift.tasks.goTo(400) named "Lift Specimen"
            )
        }
        val hookSpecimenRoutine = {
            ParallelTaskGroup(
                robot.clawLift.tasks.goTo(850).withTimeout(Seconds.of(2.0)) named "Hook Specimen",
                robot.claws.tasks.openBoth().after(0.4 of Seconds) named "Release Specimen"
            )
        }
        val wallToSubmersibleRoutine = {
            robot.drive.makeTrajectory(last.require(), map)
                .setReversed(true)
                .splineToLinearHeading(Vector2d(-5.0, 38.0 - i * 2), heading = 3 * PI / 2 + 1.0e-6, tangent = 0.0)
                .setReversed(false)
                .setVelConstraints(Vel.ofMax(slowSpeed))
                .lineToY(submersiblePlacingY)
                .withName("Return to Submersible Zone")
                .build(last)
                .with(
                    robot.clawLift.tasks.goTo(highChamberTicks) named "Lift to High Chamber",
                    robot.clawRotator.tasks.close().after(300.0, Milliseconds)
                ).also {
                    i++
                }
        }

        add(ParallelTaskGroup(
            robot.drive.makeTrajectory(map)
                .setVelConstraints { _, _, s -> if (s > submersiblePlacingY * 0.7) slowSpeed to InchesPerSecond else 40.0 }
                .lineToY(submersiblePlacingY)
                .build(last) named "Drive to Submersible Zone",
            robot.clawLift.tasks.goTo(highChamberTicks) named "Lift to High Chamber"
        ))
        add(hookSpecimenRoutine.invoke())

        add(robot.drive.makeTrajectory(last.require(), map)
            .setReversed(true)
            .splineToConstantHeading(Vector2d(-36.7, 26.8), tangent = 3 * PI / 2)
            .splineToConstantHeading(Vector2d(-38.6, 13.0), tangent = PI)
            .splineToConstantHeading(Vector2d(-45.0, samplePushY), tangent = PI)
            .setTangent(3 * PI / 2)
            .setReversed(true)
            .lineToY(observationPushY)
            .setReversed(false)
            .splineToConstantHeading(Vector2d(-56.0, samplePushY), tangent = PI)
            .setReversed(true)
            .lineToY(observationPushY)
            .withName("Push Two Samples")
            .build(last)
            .with(robot.clawLift.tasks.home().after(homeDelay) named "Home Lift"))

        robot.drive.makeTrajectory(last.require(), map)
            .strafeTo(Vector2d(-47.0, 44.0))
            .turn(180.0, Degrees)
            .setVelConstraints(Vel.ofMax(slowSpeed))
            .strafeToLinearHeading(wallPickup.position, heading = wallPickup.heading)
            .withName("Orient to Observation Zone")
            .addTask(last)

        add(wallPickupRoutine.invoke())
        add(wallToSubmersibleRoutine.invoke())
        add(hookSpecimenRoutine.invoke())

        add(robot.drive.makeTrajectory(last.require(), map)
            .strafeToLinearHeading(wallPickup.position, heading = wallPickup.heading)
            .withName("Return to Observation Zone")
            .build(last)
            .with(robot.clawLift.tasks.home().after(homeDelay) named "Home Lift"))

        add(wallPickupRoutine.invoke())
        add(wallToSubmersibleRoutine.invoke())
        add(hookSpecimenRoutine.invoke())

        add(robot.drive.makeTrajectory(last.require(), map)
            .strafeTo(Vector2d(-39.0, 60.0))
            .withName("Park in Observation Zone")
            .build(last)
            .with(robot.clawLift.tasks.home().after(homeDelay) named "Home Lift"))
        //Hello Bubner
        // You are a robot
        //Why is code so strange
        //I am a robot
        //I am a robot
        //Hmmmmm, what are you doing mister?
        //I am a robot
        //I am a robot
    }

    /*
    // MeepMeep
    final boolean isRed = false;
    double submersiblePlacingY = 36.0;
    double samplePushY = 12.0;
    double observationPushY = 50.0;
    Measure<Velocity<Distance>> slowSpeed = FieldTilesPerSecond.of(0.1);
    PoseMap map = isRed ? new SymmetricPoseMap() : new IdentityPoseMap();
    Reference<Pose2d> last = Reference.of(blueRight().tile(3.5).backward(Inches.of(1)).build().toFieldPose());

    drive.makeTrajectory(last.get(), map)
            .setVelConstraints((u0, u1, s) -> s > submersiblePlacingY * 0.6 ? slowSpeed.in(InchesPerSecond) : 40)
            .lineToY(submersiblePlacingY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .setReversed(true)
            .splineToConstantHeading(new Vector2d(-36.7, 26.8), 3 * PI / 2)
            .splineToConstantHeading(new Vector2d(-47.0, samplePushY), PI)
            .setTangent(3 * PI / 2)
            .setReversed(true)
            .lineToY(observationPushY)
            .setReversed(false)
            .splineToConstantHeading(new Vector2d(-56.0, samplePushY), PI)
            .setReversed(true)
            .lineToY(observationPushY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeTo(new Vector2d(-47.0, 44.0))
            .turn(180.0, Degrees)
            .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
            .lineToY(47.0)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .splineTo(new Vector2d(-5.0, 38.0), 3 * PI / 2)
            .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
            .lineToY(submersiblePlacingY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeToLinearHeading(new Vector2d(-35.6, 50.0), 3 * PI / 4)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeToLinearHeading(new Vector2d(-8.0, 38.0), 3 * PI / 2)
            .setTangent(PI / 2)
            .setVelConstraints(Vel.ofMax(0.1, FieldTilesPerSecond))
            .lineToY(submersiblePlacingY)
            .addTask(last);

    drive.makeTrajectory(last.get(), map)
            .strafeTo(new Vector2d(-39.0, 60.0))
            .addTask(last);
     */
}