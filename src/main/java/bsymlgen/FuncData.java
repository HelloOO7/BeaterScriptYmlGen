/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsymlgen;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FuncData {

	public int opCode;
	public String name;
	public String brief;

	public FuncType type;
	public boolean isConditional = false;
	public boolean writesCondition = false;

	public String psClasspath = null;
	public String psName = null;

	public List<MethodArgument> arguments = new ArrayList<>();

	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		YAMLPrintStream out = new YAMLPrintStream(baos);

		out.print("0x");
		out.print(Integer.toHexString(opCode));
		out.println(":");

		out.incrementIndentLevel();

		out.printSimpleParam("Name", name);

		if (!arguments.isEmpty()) {
			out.beginBlock("Parameters");

			for (MethodArgument arg : arguments) {
				out.beginElem();
				out.printSimpleParam("Name", arg.name);

				out.printSimpleParam("Type", arg.type.cstype);
				if (arg.type.returnType != null) {
					out.printSimpleParam("IsReturn", "true");
				}

				out.endElem();
			}

			out.endBlock();
		}

		out.printSimpleParam("CommandType", type.commandTypeName);

		if (isConditional) {
			out.printSimpleParam("HasCondition", "true");
		}
		if (writesCondition) {
			out.printSimpleParam("WritesCondition", "true");
		}

		if (brief != null) {
			out.printSimpleParam("Brief", '"' + brief.replace("\"", "\\\"") + '"');
		}

		out.printSimpleParam("PSPackage", psClasspath);
		out.printSimpleParam("PSName", psName);

		out.decrementIndentLevel();

		return baos.toString();
	}

	public static class MethodArgument {

		public String name;
		public ArgType type;
	}
}
