package au.edu.sa.mbhs.studentrobotics.cellphone.debug;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import au.edu.sa.mbhs.studentrobotics.bunyipslib.BunyipsOpMode;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.Sound;
import au.edu.sa.mbhs.studentrobotics.bunyipslib.transforms.Controls;
import au.edu.sa.mbhs.studentrobotics.cellphone.R;

/**
 * Random tests
 */
@TeleOp
public class CellphoneTests extends BunyipsOpMode {
    private final Sound sound = new Sound(R.raw.vineboom);

    @Override
    protected void onInit() {
    }

    @Override
    protected void activeLoop() {
        if (gamepad1.getDebounced(Controls.A))
            sound.play();
    }
}
