package bsymlgen;

import java.awt.Color;

public enum FuncType {
	NONE(0, 0, 0),
	SCRIPT_END(255, 0, 0, "IsEnd", "IsScriptEnd"),
	FUNCTION_CALL(0, 0, 255, "HasFunction"),
	FUNCTION_JUMP(74, 134, 232, "HasFunction", "IsJump"),
	FUNCTION_END(255, 153, 0, "IsEnd"),
	MOVEMENT_JUMP(153, 0, 255, "HasMovement")
	;
	
	private final Color col;
	public final String[] appliedBoolParams;

	private FuncType(int r, int g, int b, String... appliedBoolParams) {
		col = new Color(r, g, b);
		this.appliedBoolParams = appliedBoolParams;
	}

	public static FuncType identifyByColor(Color textColor) {
		for (FuncType f : values()){
			if (f.col.equals(textColor)){
				return f;
			}
		}
		return NONE;
	}
}
