package com.defano.hypercard.gui.icons;

import com.defano.hypercard.gui.util.AlphaImageIcon;

import javax.swing.*;
import java.util.Arrays;

public enum HyperCardIcon implements ButtonIcon {

    // Arrows, hands and pointers
    UP_ARROW,
    DOWN_ARROW,
    NXT,
    PREV,
    FIRST_CARD_ARROW,
    BLACK_NEXT_ARROW,
    BLACK_RETURN_ARROW,
    BLACK_DOWN_ARROW,
    BLACK_PREV_ARROW,
    BLACK_UP_ARROW,
    BLK_NEXT_ARROW,
    BLK_PREV_ARROW,
    LT_ARROW_NEXT,
    LT_ARROW_PREV,
    MED_NEXT_HAND,
    MED_PREV_HAND,
    NEXT_ARROW,
    PREV_ARROW,
    NEXT_DOUBLE,
    PREV_DOUBLE,
    RTN_ARROW,
    SML_DOWN_ARROW,
    SML_NEXT_HAND,
    SML_PREV_HAND,
    SML_RETURN_ARROW,
    SML_FIRST_CARD_ARROW,
    SML_LAST_CARD_ARROW,
    SML_LEFT_ARROW,
    SML_NEXT_ARROW,
    SML_PREV_ARROW,
    SML_RIGHT_TRIANGLE,
    SML_UP_ARROW,
    FLEET_NEXT_ARROW,
    FLEET_PREV_ARROW,
    FLEET_RETURN_ARROW,
    LAST_CARD_ARROW,
    LEFT_TRI,
    RIGHT_TRI,
    LGE_NEXT_ARROW,
    LGE_NEXT_HAND,
    LGE_PREV_ARROW,
    LGE_PREV_HAND,
    LGE_UP_ARROW,
    LGE_DOWN_ARROW,
    LGE_FIRST_CARD_ARROW,
    LGE_RETURN_ARROW,
    LRG_LAST_CARD,
    XL_FIRST_CARD_ARROW,
    XL_LEFT_ARROW,
    XL_NEXT_ARROW,
    XL_RETURN_ARROW,
    XL_DOWN_ARROW,
    XL_PREV_ARROW,
    XL_UP_ARROW,
    EX_LGE_PREV_HAND,
    EX_LARGE_NEXT_HAND,

    // Home icons
    HOME_AGAIN,
    HOME_ALONE,
    HOME_BASE,
    HOME_BIG_2,
    HOME_BREW,
    HOME_LOAN,
    HOME_MADE,
    LARGE_HOME_BASE,
    TINY_HOME,
    SMALL_HOME_BASE,
    SML_BLACK_HOME,
    SML_WHITE_HOME,
    WHITE_HOME,
    HOME_BAKED,
    HOME,
    MINUTE_HOME,
    SML_HOME,

    // Calendar and dates
    APPOINTMENTS,
    DATEBOOK_DAY,
    DATEBOOK_YEAR,
    ADDRESS_CARD_A,
    ADDRESS,
    ADDRESS_CARD_B,
    ADDRESSES,
    CALENDAR_PAGE,
    DATEBOOK_DAY_GRAY,
    DATEBOOK_YR_GRAY,

    // Series and animations
    JUGGLER_1,
    JUGGLER_2,
    JUGGLER_3,
    JUGGLER_4,
    DICE_1,
    DICE_2,
    DICE_3,
    DICE_4,
    DICE_5,
    DICE_6,

    // Apps and docs
    HYPERCARD,
    MACPAINT,
    MACWRITE,
    STACK,
    MACDRAW,
    PAINT_DOC,
    WRITE_DOC,
    APPLICATION,
    DOCUMENT,
    DRAW_DOC,
    RESEDIT,

    IMAGEWRITER,
    LASERWRITER,
    MAC,

    // Help and question marks
    BLACK_QUESTION,
    GOOD_QUESTION,
    STACK_HELP,
    BLK_HELP,
    BOXED_QUESTION,
    GAME_QUESTION,
    HC_HELP,
    HELP_1,
    HELP_3,
    LGE_HELP,
    SML_HELP,
    MED_HELP,
    LGE_INTL_HELP,
    MED_INTL_HELP,
    LGE_TELL_ABOUT,
    MED_TELL_ABOUT,
    SML_TELL_ABOUT,
    WHITE_QUESTION,
    OUTLINE_QUESTION,
    MYSTERY_DOT,

    // Phones
    NATIONAL_DIRECTORY,
    PHONE,
    BIG_TONE,
    BILLS_ROLO,
    OLD_STYLE_PHONE,
    PHONE_DIALER,
    TOUCH_TONE,
    WRONG_NUMBER,
    TINY_PHONE,
    CORDY,
    ROBIN_STYLEE,

    // Cards and stacks
    SORT,
    STACK_INFO_RT_TRI,
    STACK_INFO,
    STACK_TEMPLATES,
    STACKY,
    GRAPH_MAKER,
    HC_TOUR,
    CARD_IDEAS,
    STACK_IDEAS,

    // Misc
    BILL,
    CLICKED_CLOSE_BOX,
    CLOSE_BOX,
    CLOSER_LOOK,
    EYE,
    LINK_TO,
    MARKER_CROSS,
    PUZZLE,
    SMALL_BUTTON,
    TO_DO,
    VISUAL_EFFECT,
    ART_BITS,
    ART_IDEAS,
    BACKGROUND_ART,
    BEANIE_COPTER,
    BUTTON_IDEAS,
    COMPASS,
    HT_REFERENCE,
    ICON_SUBSTITUTE,
    MEMO,
    MUSIC,
    NEW_HOME_PREFS,
    NEW_SORT,
    READYMADE_BUTTONS,
    READYMADE_FIELDS,
    SCAN,
    SCANNED_ART_2,
    TRAIN_SET;

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
