package com.bw.jtools.graph;

public enum LSystemGraphicCommand
{
    DRAW_FORWARD,
    DRAW_LEAF,
    MOVE_FORWARD,
    TURN_CLOCKWISE,
    TURN_COUNTERCLOCKWISE,
    /**
     * Push whole state on stack
     */
    PUSH_ON_STACK,
    /**
     * Pop whole state from stack
     */
    POP_FROM_STACK,
    /**
     * Pop only angle from stack
     */
    POP_ANGLE_FROM_STACK,
    /**
     * Pop only position from stack
     */
    POP_POS_FROM_STACK
}
