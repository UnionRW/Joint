package cn.tesseract.union.hook;

import cn.tesseract.union.asm.Hook;
import cn.tesseract.union.util.GameHelper;
import cn.tesseract.union.util.ScriptManager;
import com.corrodinggames.librocket.scripts.Root;
import com.corrodinggames.rts.union.gameFramework.class_1061;

public class ScriptHook {
    @Hook
    public static void method_3043(class_1061 c, String str) {
        if ("Init completed".equals(str))
            ScriptManager.reload();
    }

    @Hook(targetMethod = "<init>")
    public static void init(Root c) {
        GameHelper.root = c;
    }
}