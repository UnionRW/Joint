package cn.tesseract.union.accessor;

import cn.tesseract.union.asm.Target;
import com.corrodinggames.rts.union.game.class_324;

@Target(class_324.class)
public interface PlayerAccessor {
    boolean get_muted();

    void set_muted(boolean f);
}
