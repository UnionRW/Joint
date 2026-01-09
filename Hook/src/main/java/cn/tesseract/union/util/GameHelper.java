package cn.tesseract.union.util;

import com.corrodinggames.librocket.scripts.Root;
import com.corrodinggames.rts.union.game.b.class_299;
import com.corrodinggames.rts.union.game.class_324;
import com.corrodinggames.rts.union.game.units.class_426;
import com.corrodinggames.rts.union.game.units.class_706;
import com.corrodinggames.rts.union.gameFramework.class_1061;
import com.corrodinggames.rts.union.gameFramework.class_773;

public class GameHelper {
    public static final class_1061 game = class_1061.method_3076();
    public static Root root;

    public static void toast(String s) {
        System.out.println("[Union] " + s);
        //game.method_3056(s);
    }

    public static class_299 getMap() {
        return game.field_6339;
    }

    public static class_426 getUnitById(long id) {
        return class_773.method_1767(id, true);
    }

    public static class_426 getWaypointUnit(class_706 waypoint) {
        return waypoint.field_3934 == null ? getUnitById(waypoint.field_3933) : waypoint.field_3934;
    }

    public static class_324 getUserPlayer() {
        return game.field_6373;
    }

    public static boolean isUserPlayer(class_324 player) {
        return game.field_6373 == player;
    }

    public static String getCurrentMapName() {
        return game.field_6352.field_5874.field_6013;
    }

    public static void setCurrentMapName(String name) {
        //game.field_6352.field_5874.field_6013 = name;
    }

    public static void updateUI() {
    }

    public static void startGame() {
        root.multiplayer.multiplayerStart();
    }
}