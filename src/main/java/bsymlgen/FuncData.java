/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bsymlgen;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FuncData {
	public int opCode;
	public String name;
	public String brief;
	
	public boolean hasMovement;
	public boolean hasFunction;
	
	public String psClasspath = null;
	public String psName = null;
	
	public List<String> argNames = new ArrayList<>();
	public List<ArgType> argTypes = new ArrayList<>();
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("0x");
		sb.append(Integer.toHexString(opCode));
		sb.append(":\n");
		sb.append("    Name: ");
		sb.append(name);
		sb.append("\n");
		if (!argNames.isEmpty()){
			sb.append("    Parameters:\n");
			for (int i = 0; i < argNames.size(); i++){
				sb.append("        - ");
				sb.append(argTypes.get(i).cstype);
				sb.append("\n");
			}
			sb.append("    ParamNames:\n");
			for (int i = 0; i < argNames.size(); i++){
				sb.append("        - ");
				sb.append(argNames.get(i));
				sb.append("\n");
			}
		}
		if (hasFunction){
			sb.append("    HasFunction: true\n");
		}
		if (hasMovement){
			sb.append("    HasMovement: true\n");
		}
		if (!brief.trim().isEmpty()){
			sb.append("    Brief: \"");
			sb.append(brief);
			sb.append("\"");
			sb.append("\n");
		}
		
		if (!psClasspath.trim().isEmpty()){
			sb.append("    PSPackage: ");
			sb.append(psClasspath);
			sb.append("\n");
		}
		if (!psName.trim().isEmpty()){
			sb.append("    PSName: ");
			sb.append(psName);
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
