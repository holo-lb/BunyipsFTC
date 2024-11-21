package org.murraybridgebunyips.glados.debug;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.murraybridgebunyips.bunyipslib.BunyipsOpMode;
import org.murraybridgebunyips.bunyipslib.vision.Vision;
import org.murraybridgebunyips.common.centerstage.vision.BlueTeamProp;
import org.murraybridgebunyips.common.centerstage.vision.RedTeamProp;
import org.murraybridgebunyips.glados.components.GLaDOSConfigCore;

/**
 * Vision Showcase
 */
@Config
@TeleOp(name = "Team Prop Vision Showcase")
@Disabled
public class GLaDOSTeamPropShowcase extends BunyipsOpMode {
    private final GLaDOSConfigCore config = new GLaDOSConfigCore();
    private Vision vision;

    @Override
    protected void onInit() {
        config.init();
        vision = new Vision(config.webcam);
        RedTeamProp p1 = new RedTeamProp();
        BlueTeamProp p2 = new BlueTeamProp();
        vision.init(p1, p2);
        vision.start(p1, p2);
        vision.startPreview();
    }

    @Override
    protected void activeLoop() {
        telemetry.add(vision.getAllData());
        vision.update();
    }
}
