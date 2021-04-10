/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsymlgen;

import com.google.api.services.sheets.v4.model.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import turtleisaac.GoogleSheetsAPI;

public class BsYmlGen {

	public static GoogleSheetsAPI api;

	public static void main(String[] args) {
		try {
			api = new GoogleSheetsAPI("https://docs.google.com/spreadsheets/d/15n-9xDRZC8IgIILe4fWgREoA6zlIC13K5VhW4ccfM-0", ".");

			String[] sheets = api.getSheetNames();

			for (String sheet : sheets) {
				List<List<String>> values = api.getSpecifiedSheet(sheet);
				List<List<Color>> sheetColors = api.getSpecifiedSheetColors(sheet);

				int COL_OPCODE = -1;
				int COL_BSNAME = -1;
				int COL_BRIEF = -1;
				int COLID_HASFUNC = -1;
				int COLID_HASMOVEMENT = -1;
				int COLID_PSPKG = -1;
				int COLID_PSNAME = -1;

				int idx = 0;
				for (Object o : values.get(0)) {
					if (o instanceof String) {
						switch ((String) o) {
							case ColumnTags.CT_BRIEF:
								COL_BRIEF = idx;
								break;
							case ColumnTags.CT_BSNAME:
								COL_BSNAME = idx;
								break;
							case ColumnTags.CT_OPCODE:
								COL_OPCODE = idx;
								break;
							case ColumnTags.CT_HASFUNC:
								COLID_HASFUNC = idx;
								break;
							case ColumnTags.CT_HASMOVE:
								COLID_HASMOVEMENT = idx;
								break;
							case ColumnTags.CT_PSPKG:
								COLID_PSPKG = idx;
								break;
							case ColumnTags.CT_PSNAME:
								COLID_PSNAME = idx;
								break;
						}
					}
					idx++;
				}

				List<FuncData> funcs = new ArrayList<>();

				for (int i = 1; i < values.size(); i++) {
					List<String> rowStr = values.get(i);
					List<Color> colors = sheetColors.get(i);

					String opCode = rowStr.get(COL_OPCODE);
					if (!opCode.trim().isEmpty()) {
						FuncData fd = new FuncData();
						fd.opCode = Integer.parseInt(opCode, 16);
						fd.brief = rowStr.get(COL_BRIEF);
						fd.name = rowStr.get(COL_BSNAME);
						

						if (COLID_HASMOVEMENT != -1) {
							fd.hasMovement = !rowStr.get(COLID_HASMOVEMENT).trim().isEmpty();
						}
						if (COLID_HASFUNC != -1) {
							fd.hasFunction = !rowStr.get(COLID_HASFUNC).trim().isEmpty();
						}
						
						fd.psClasspath = rowStr.get(COLID_PSPKG);
						fd.psName = rowStr.get(COLID_PSNAME);

						for (int argIdx = COL_BSNAME + 1; argIdx < COL_BRIEF; argIdx++) {
							String argName = rowStr.get(argIdx);
							if (!argName.trim().isEmpty()) {
								fd.argNames.add(argName);
								Color typeColor = colors.get(argIdx);
								java.awt.Color col = getAWTColor(typeColor);

								ArgType t = ArgType.valueOf(col);
								fd.argTypes.add(t);
								
								if (t.returnType != null){
									fd.returnParamNames.add(argName);
									fd.returnParamTypes.add(t);
								}
							} else {
								break;
							}
						}
						funcs.add(fd);
					}
				}

				PrintStream out = new PrintStream(new File(System.getProperty("user.dir") + "/" + sheet + ".yml"));
				for (FuncData fd : funcs) {
					out.println(fd.toString());
				}
				out.close();
			}
		} catch (IOException | GeneralSecurityException ex) {
			Logger.getLogger(BsYmlGen.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static java.awt.Color getAWTColor(Color color) {
		int red = (int) (color.getRed() == null ? 0 : color.getRed() * 255);
		int blue = (int) (color.getBlue() == null ? 0 : color.getBlue() * 255);
		int green = (int) (color.getGreen() == null ? 0 : color.getGreen() * 255);

		return new java.awt.Color(red, green, blue);
	}
}
