/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bsymlgen;

import java.awt.Color;

/**
 *
 */
public enum ArgType {
	U8(Color.RED, "byte"),
	U16(new Color(70, 189, 198), "const ushort"),
	S32(new Color(66, 133, 244), "int"),
	
	WK(new Color(52, 168, 83), "ref ushort"),
	FLEX(new Color(255, 153, 0), "ushort"),
	
	RETURN_INT(new Color(153, 0, 255), "ref ushort", "ushort"),
	RETURN_BOOL(new Color(255, 0, 255), "ref ushort", "bool");
	
	public final Color col;
	public final String cstype;
	public final String returnType;
	
	private ArgType(Color col, String cstype){
		this(col, cstype, null);
	}
	
	private ArgType(Color col, String cstype, String returnType){
		this.col = col;
		this.cstype = cstype;
		this.returnType = returnType;
	}
	
	public static ArgType valueOf(Color col){
		for (ArgType at : values()){
			if (at.col.equals(col)){
				return at;
			}
		}
		System.out.println("undetected color " + col);
		return FLEX;
	}
}
