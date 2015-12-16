package lu.uni.psod.corsanum.services;

import java.util.HashMap;
import java.util.Map;

import lu.uni.psod.corsanum.models.EnumDescription;

/**
 * Created by rlopez on 15/12/15.
 */
public class MessageType {
    public static final int MSG_LOGIN              = 1;
    public static final int MSG_LOGOUT             = 2;
    public static final int MSG_REGISTER_STEPCOUNT = 3;

    public static final int MSG_STEPCOUNT_DATA = 4;
    public static final int MSG_LOCATION_DATA  = 5;
    public static final int MSG_CALORIES_DATA  = 6;
    public static final int MSG_SPEED_DATA     = 7;

}
