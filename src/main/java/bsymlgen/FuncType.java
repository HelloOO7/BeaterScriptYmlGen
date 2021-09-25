package bsymlgen;

import java.awt.Color;

public enum FuncType {
	NONE(0, 0, 0, null),
	SCRIPT_END(255, 0, 0, "End"),
	FUNCTION_CALL(0, 0, 255, "Call"),
	FUNCTION_JUMP(74, 134, 232, "Jump"),
	FUNCTION_END(255, 153, 0, "Return"),
	ACTION_JUMP(153, 0, 255, "CallActionSeq")
	;
	
	private final Color col;
	public final String commandTypeName;

	private FuncType(int r, int g, int b, String commandTypeName) {
		col = new Color(r, g, b);
		this.commandTypeName = commandTypeName;
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
