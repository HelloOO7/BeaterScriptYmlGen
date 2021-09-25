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
	U8_C(Color.RED, "byte"),							//Unsigned 8-bits constant
	U16_C(new Color(70, 187, 198), "const ushort"),		//Unsigned 16-bits constant
	S32_C(new Color(66, 133, 244), "int"),				//Signed 32-bits constant
	
	WK(new Color(52, 168, 83), "ref ushort"),			//Unsigned 16-bit work value reference
	U16_H(new Color(255, 153, 0), "ushort"),			//Unsigned 16-bit hybrid constant or work value reference
	
	FX16(new Color(69, 129, 142), "fx16"),				//Signed FX16 fixed point decimal.
	FX32(new Color(19, 79, 92), "fx32"),				//Signed FX32 fixed point decimal.
	
	RETURN_INT(new Color(153, 0, 255), "ref ushort", "ushort"),	//EX Work value used as integer return storage.
	RETURN_BOOL(new Color(255, 0, 255), "ref ushort", "bool");	//EX Work value used as boolean return storage.
	
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
		//System.out.println("undetected color " + col);
		return null;
	}
}
