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
	U16(Color.ORANGE, "const ushort"),
	S32(new Color(66, 133, 244), "int"),
	
	WK(new Color(163, 73, 164), "ref ushort"),
	FLEX(new Color(255, 153, 0), "ushort");
	
	public final Color col;
	public final String cstype;
	
	private ArgType(Color col, String cstype){
		this.col = col;
		this.cstype = cstype;
	}
	
	public static ArgType valueOf(Color col){
		for (ArgType at : values()){
			if (at.col.equals(col)){
				return at;
			}
		}
		return FLEX;
	}
}
