package com.defano.hypercard.gui.icons;

import com.defano.hypercard.gui.util.AlphaImageIcon;

import javax.swing.*;
import java.util.Arrays;

public enum HyperCardIcon implements ButtonIcon {
    ADDRESS_CARD_A,
    ADDRESS,
    BILL,
    BLACK_NEXT_ARROW,
    BLACK_RETURN_ARROW,
    BLK_NEXT_ARROW,
    BLK_PREV_ARROW,
    CLICKED_CLOSE_BOX,
    CLOSE_BOX,
    CLOSER_LOOK,
    DATEBOOK_DAY_GRAY,
    DATEBOOK_YR_GRAY,
    DICE_1,
    DICE_2,
    DICE_3,
    DICE_4,
    DICE_5,
    DICE_6,
    DOWN_ARROW,
    EX_LGE_PREV_HAND,
    EYE,
    GOOD_QUESTION,
    GRAPH_MAKER,
    HC_TOUR,
    HOME_BAKED,
    HOME,
    HYPERCARD,
    IMAGEWRITER,
    LASERWRITER,
    LGE_DOWN_ARROW,
    LGE_FIRST_CARD_ARROW,
    LGE_RETURN_ARROW,
    LINK_TO,
    LRG_LAST_CARD,
    LT_ARROW_NEXT,
    LT_ARROW_PREV,
    MAC,
    MACPAINT,
    MACWRITE,
    MARKER_CROSS,
    MED_PREV_HAND,
    MED_TELL_ABOUT,
    NATIONAL_DIRECTORY,
    NEXT_ARROW,
    NEXT_DOUBLE,
    NXT,
    PAINT_DOC,
    PHONE,
    PREV_ARROW,
    PREV_DOUBLE,
    PREV,
    PUZZLE,
    RESEDIT,
    RTN_ARROW,
    SMALL_BUTTON,
    SML_DOWN_ARROW,
    SML_NEXT_HAND,
    SML_PREV_HAND,
    SML_RETURN_ARROW,
    STACK_HELP,
    STACK_IDEAS,
    STACK,
    TINY_HOME,
    TO_DO,
    VISUAL_EFFECT,
    WHITE_QUESTION,
    WRITE_DOC,
    XL_DOWN_ARROW,
    XL_PREV_ARROW,
    XL_UP_ARROW;

    @Override
    public int getId() {
        return Arrays.asList(HyperCardIcon.values()).indexOf(this);
    }

    @Override
    public String getName() {
        return name().toLowerCase().replaceAll("_", "-");
    }

    @Override
    public AlphaImageIcon getImage() {
        return new AlphaImageIcon(new ImageIcon(IconFactory.class.getResource("/button-icons/" + getName() + ".png")), 1.0f);
    }
}
